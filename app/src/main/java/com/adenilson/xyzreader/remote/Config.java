package com.adenilson.xyzreader.remote;

import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

public class Config {
    public static final URL BASE_URL;

    static {
        URL url = null;
        try {
            url = new URL("https://nspf.github.io/XYZReader/data.json" );
        } catch (MalformedURLException ignored) {
            Log.e(Config.class.getSimpleName(), ignored.getMessage());
        }

        BASE_URL = url;
    }
}
