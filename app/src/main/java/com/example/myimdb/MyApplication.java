package com.example.myimdb;

import android.app.Application;



import io.realm.Realm;
import io.realm.RealmConfiguration;
import okhttp3.OkHttpClient;


public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Init Stetho
        /*Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());*/

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
