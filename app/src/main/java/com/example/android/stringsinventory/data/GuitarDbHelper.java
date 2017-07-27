package com.example.android.stringsinventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ikki on 27/07/2017.
 */

public class GuitarDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = GuitarDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "guitars.db";

    private static final int DATABASE_VERSION = 1;

    public GuitarDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_GUITARS_TABLE = "CREATE TABLE " + GuitarContract.GuitarEntry.TABLE_NAME + " ("
                + GuitarContract.GuitarEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + GuitarContract.GuitarEntry.COLUMN_MANUFACTURER_NAME + " TEXT NOT NULL, "
                + GuitarContract.GuitarEntry.COLUMN_MODEL + " TEXT NOT NULL, "
                + GuitarContract.GuitarEntry.COLUMN_PRICE + " INTEGER NOT NULL DEFAULT 0, "
                + GuitarContract.GuitarEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + GuitarContract.GuitarEntry.COLUMN_GUITAR_COVER + " TEXT NOT NULL, "
                + GuitarContract.GuitarEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                + GuitarContract.GuitarEntry.COLUMN_SUPPLIER_EMAIL + " TEXT NOT NULL);";

        db.execSQL(SQL_CREATE_GUITARS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
