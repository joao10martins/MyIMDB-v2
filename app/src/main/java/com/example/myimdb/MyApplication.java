package com.example.myimdb;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import okhttp3.OkHttpClient;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Init Stetho
        Stetho.initialize(Stetho.newInitializerBuilder(this)
            .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
            .build());

        // Initializing Realm (only has to be done once)
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);

        // #1
        /*Stetho.initializeWithDefaults(this);

        new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();*/
    }
}
