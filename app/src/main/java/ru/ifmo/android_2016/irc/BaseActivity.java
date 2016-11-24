package ru.ifmo.android_2016.irc;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static ru.ifmo.android_2016.irc.constant.PreferencesConstant.TEXTSIZE_KEY;
import static ru.ifmo.android_2016.irc.constant.PreferencesConstant.THEME_DARK_KEY;
import static ru.ifmo.android_2016.irc.constant.PreferencesConstant.THEME_KEY;
import static ru.ifmo.android_2016.irc.constant.PreferencesConstant.THEME_LIGHT_KEY;

public abstract class BaseActivity extends AppCompatActivity {

    SharedPreferences prefs;
    private SharedPreferences.OnSharedPreferenceChangeListener listener =
            this::onSharedPreferenceChanged;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getDefaultSharedPreferences(getApplicationContext());
        prefs.registerOnSharedPreferenceChangeListener(listener);
        setThemeFromPref();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getStartPreferences();
    }

    protected void getStartPreferences() {
    }


    private void setThemeFromPref() {
        int themeID = 0;
        switch (prefs.getString(THEME_KEY, "")) {
            case THEME_LIGHT_KEY:
                themeID = R.style.AppTheme;
                break;

            case THEME_DARK_KEY:
                themeID = R.style.AppTheme_Dark;
                break;
        }
        setTheme(themeID);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        switch (s) {
            case THEME_KEY:
            case TEXTSIZE_KEY:
                recreate();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        prefs.unregisterOnSharedPreferenceChangeListener(listener);
    }
}
