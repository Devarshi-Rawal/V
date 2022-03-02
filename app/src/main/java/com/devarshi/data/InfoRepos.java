package com.devarshi.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

public class InfoRepos {

    private final SQLiteDatabase db = DatabaseOpenHelper.getAppDatabase();

    public void writeInfo(@NonNull String info) {
        ContentValues values = new ContentValues();
        values.put(DBConstants.INFO_FIELD_TEXT, info);
        db.insert(DBConstants.TABLE_INFO, null, values);
        db.close();
    }
}
