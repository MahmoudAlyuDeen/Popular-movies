package com.afterapps.movies;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/*
 * Created by Mahmoud.AlyuDeen on 7/13/2016.
 */
public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //create the Realm configuration
        //deleting realm if migration needed
        RealmConfiguration config = new RealmConfiguration.Builder(this).deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(config);
    }
}