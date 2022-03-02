package com.devarshi.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

public class InfoRepository {

    Context context;

    public InfoRepository(Context context) {
        this.context = context;
    }

    private final SQLiteDatabase db = DatabaseOpenHelper.getAppDatabase();

    /*public void writeInfo(@NonNull String info) {
        ContentValues values = new ContentValues();
        values.put(DBConstants.INFO_FIELD_TEXT, info);
        db.insert(DBConstants.TABLE_INFO, null, values);
        db.close();
    }*/

    @Nullable
    public String getInfo() {
        String info = "";
        final String[] cols = new String[]{DBConstants.INFO_FIELD_TEXT};
        try (Cursor cursor = db.query(
                true,
                DBConstants.TABLE_INFO,
                cols,
                null,
                null,
                null,
                null,
                null,
                null)) {
            cursor.moveToLast();
            info = cursor.getString(0);
        } catch (CursorIndexOutOfBoundsException e) {
            Toast.makeText(context, "No items uploaded to Drive.", Toast.LENGTH_SHORT).show();
        }
        return info;
    }
}
