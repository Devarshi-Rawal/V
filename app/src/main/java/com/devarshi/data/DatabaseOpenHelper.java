package com.devarshi.data;

import static com.devarshi.data.DBConstants.DATABASE_CREATE;
import static com.devarshi.data.DBConstants.DB_NAME;
import static com.devarshi.data.DBConstants.DB_VERSION;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.devarshi.app.App;

class DatabaseOpenHelper extends SQLiteOpenHelper {

    static SQLiteDatabase getAppDatabase() {
        final DatabaseOpenHelper helper = new DatabaseOpenHelper(App.getInstance());
        return helper.getWritableDatabase();
    }

    private DatabaseOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        onCreate(database);
    }
}
