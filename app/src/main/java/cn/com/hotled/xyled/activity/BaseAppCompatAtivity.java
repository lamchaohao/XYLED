package cn.com.hotled.xyled.activity;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import java.util.Locale;

import cn.com.hotled.xyled.global.Global;

/**
 * 保存语言
 * Created by Lam on 2017/3/16.
 */

public class BaseAppCompatAtivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = getSharedPreferences(Global.SP_SYSTEM_CONFIG, MODE_PRIVATE);
        int lang = sp.getInt(Global.KEY_LANGUAGE, 0);
        switchLanguage(lang);
    }

    private void switchLanguage(int language){
        Resources resources = getResources();
        Configuration config= resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        switch (language){
            case 0:
                config.locale=Locale.getDefault();
                break;
            case 1:
                config.locale=Locale.SIMPLIFIED_CHINESE;
                break;
            case 2:
                config.locale=Locale.TRADITIONAL_CHINESE;
                break;
            case 3:
                config.locale=Locale.ENGLISH;
                break;
        }

        resources.updateConfiguration(config,displayMetrics);
    }

}
