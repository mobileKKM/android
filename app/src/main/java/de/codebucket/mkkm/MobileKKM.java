package de.codebucket.mkkm;

import android.app.Application;

public class MobileKKM extends Application {

    private static MobileKKM instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static MobileKKM getInstance() {
        return instance;
    }
}
