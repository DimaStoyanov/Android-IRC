package irc.android_2016.ifmo.ru.irc.client;

/**
 * Created by ghost on 10/24/2016.
 */

public interface IClient {
    boolean connect(ClientSettings clientSettings);
    boolean isConnected();
    Exception getLastError();
    boolean joinChannel(String channel);
    boolean sendMessage(Message message);
    void close();
}
