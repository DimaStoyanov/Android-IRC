package ru.ifmo.android_2016.irc.api.frankerfacez.emotes;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Consumer;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import ru.ifmo.android_2016.irc.api.FrankerFaceZApi;
import ru.ifmo.android_2016.irc.api.frankerfacez.FrankerFaceZParser;
import ru.ifmo.android_2016.irc.utils.FunctionUtils.CallableWithException;
import ru.ifmo.android_2016.irc.utils.FunctionUtils.Reference;

import static ru.ifmo.android_2016.irc.utils.FunctionUtils.fuckCheckedExceptions;
import static ru.ifmo.android_2016.irc.utils.FunctionUtils.getInputStream;
import static ru.ifmo.android_2016.irc.utils.FunctionUtils.tryWith;

/**
 * Created by ghost on 12/18/2016.
 */

public final class FfzEmotesLoader extends AsyncTask<Void, Void, Void> {
    private final boolean forceGlobalReload;
    private final String channel;
    private final Consumer<Set<Integer>> onLoad;

    public FfzEmotesLoader() {
        this(false, null, null);
    }

    public FfzEmotesLoader(boolean forceGlobalReload) {
        this(forceGlobalReload, null, null);
    }

    public FfzEmotesLoader(@Nullable String channel,
                           @Nullable Consumer<Set<Integer>> onLoad) {
        this(false, channel, onLoad);
    }

    public FfzEmotesLoader(boolean forceGlobalReload,
                           @Nullable String channel,
                           @Nullable Consumer<Set<Integer>> onLoad) {
        this.forceGlobalReload = forceGlobalReload;
        this.channel = channel;
        this.onLoad = onLoad;
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (!FfzEmotes.globalLoaded || forceGlobalReload) {
            load(FrankerFaceZApi::getGlobalEmotes);
        }
        if (channel != null && onLoad != null) {
            Set<Integer> map = load(() -> FrankerFaceZApi.getRoomInfo(channel));
            onLoad.accept(map);
        }
        return null;
    }

    private Set<Integer> load(CallableWithException<IOException, HttpURLConnection> callable) {
        Reference<Set<Integer>> ref = new Reference<>(Collections.emptySet());

        tryWith(callable).doOp(connection -> {
            getInputStream(connection).executeIfPresent(inputStream -> {
                ref.ref = fuckCheckedExceptions(() -> readJson(inputStream));
            });
        }).catchWith(IOException.class, (e) -> {
            e.printStackTrace();
        }).runUnchecked();

        return ref.ref;
    }

    private Set<Integer> readJson(InputStream inputStream) throws IOException {
        FrankerFaceZParser.Response response = FrankerFaceZParser.parse(inputStream);

        FfzEmotes.addEmotes(response.getSets());

        return Stream.of(response.getSets())
                .map(FrankerFaceZParser.Set::getId)
                .collect(Collectors.toSet());
    }
}