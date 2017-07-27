package com.example.android.stringsinventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.android.stringsinventory.data.GuitarContract.GuitarEntry;

import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Ikki on 27/07/2017.
 */

public class GuitarProvider extends ContentProvider {

    public static final String LOG_TAG = GuitarProvider.class.getSimpleName();

    private static final int GUITARS = 100;

    private static final int GUITAR_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(GuitarContract.CONTENT_AUTHORITY, GuitarContract.PATH_GUITARS, GUITARS);
        sUriMatcher.addURI(GuitarContract.CONTENT_AUTHORITY, GuitarContract.PATH_GUITARS + "/#", GUITAR_ID);

    }

    private GuitarDbHelper mDbHelper;


    @Override
    public boolean onCreate() {
        mDbHelper = new GuitarDbHelper(getContext());
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case GUITARS:
                cursor = database.query(GuitarEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case GUITAR_ID:
                selection = GuitarContract.GuitarEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(GuitarEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case GUITARS:
                return GuitarEntry.CONTENT_LIST_TYPE;
            case GUITAR_ID:
                return GuitarEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " +uri + "with match" + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case GUITARS:
                return insertGuitar(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);

        }
    }

    private Uri insertGuitar(Uri uri, ContentValues values) {
        if (values == null) {
            Toast.makeText(getContext(), "Guitar cannot be empty", Toast.LENGTH_LONG).show();
            throw new IllegalArgumentException("Guitar cannot be empty");
        }
        String manufacturerName = values.getAsString(GuitarEntry.COLUMN_MANUFACTURER_NAME);
        if (manufacturerName == null) {
            Toast.makeText(getContext(), "Guitar requires a manufacturer name", Toast.LENGTH_LONG).show();
            throw new IllegalArgumentException("Guitar requires a manufacturer's name");
        }

        String modelName = values.getAsString(GuitarEntry.COLUMN_MODEL);
        if (modelName == null) {
            Toast.makeText(getContext(), "Guitar requires a model name", Toast.LENGTH_LONG).show();
            throw new IllegalArgumentException("Guitar requires a model name");
        }

        Integer price = values.getAsInteger(GuitarEntry.COLUMN_PRICE);
        if (price != null && price < 0) {
            Toast.makeText(getContext(), "Guitar requires a valid price", Toast.LENGTH_LONG).show();
            throw new IllegalArgumentException("Guitar requires a valid price");
        }

        Integer quantity = values.getAsInteger(GuitarEntry.COLUMN_QUANTITY);
        if (quantity == null && quantity < 0) {
            Toast.makeText(getContext(), "Guitar requires a quantity", Toast.LENGTH_LONG).show();
            throw new IllegalArgumentException("Guitar requires a quantity");
        }

        String guitarCover = values.getAsString(GuitarEntry.COLUMN_GUITAR_COVER);
        if (guitarCover == null) {
            Toast.makeText(getContext(), "Guitar requires an image", Toast.LENGTH_LONG).show();
            throw new IllegalArgumentException("Guitar requires an image");
        }

        String supplierName = values.getAsString(GuitarEntry.COLUMN_SUPPLIER_NAME);
        if (supplierName == null) {
            Toast.makeText(getContext(), "Guitar requires a supplier contact name", Toast.LENGTH_LONG).show();
            throw new IllegalArgumentException("Guitar requires a supplier contact name");
        }

        String supplierEmail = values.getAsString(GuitarEntry.COLUMN_SUPPLIER_EMAIL);
        if (supplierEmail == null) {
            Toast.makeText(getContext(), "Guitar requires a supplier email", Toast.LENGTH_LONG).show();
            throw new IllegalArgumentException("Guitar requires a supplier email");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(GuitarEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
        }
        return ContentUris.withAppendedId(uri, id);

    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case GUITARS:
                return updateGuitar(uri, contentValues, selection, selectionArgs);
            case GUITAR_ID:
                selection = GuitarEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateGuitar(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateGuitar(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        if (contentValues == null) {
            if (contentValues.containsKey(GuitarEntry.COLUMN_MANUFACTURER_NAME)) {
                Toast.makeText(getContext(), "This field cannot be empty", Toast.LENGTH_LONG).show();
                throw new IllegalArgumentException("This field cannot be empty");
            }
            if (contentValues.containsKey(GuitarEntry.COLUMN_MODEL)) {
                Toast.makeText(getContext(), "Guitar requires a model name", Toast.LENGTH_LONG).show();
                throw new IllegalArgumentException("Guitar requires a model name");
            }


            Integer price = contentValues.getAsInteger(GuitarEntry.COLUMN_PRICE);
            if (price == null && price < 0) {
                throw new IllegalArgumentException("Guitar requires a valid price");
            }

            Integer quantity = contentValues.getAsInteger(GuitarEntry.COLUMN_QUANTITY);
            if (quantity == null && quantity < 0) {
                throw new IllegalArgumentException("Guitar requires a quantity");
            }

            if (contentValues.containsKey(GuitarEntry.COLUMN_GUITAR_COVER)) {
                throw new IllegalArgumentException("Guitar requires an image");
            }

            if (contentValues.containsKey(GuitarEntry.COLUMN_SUPPLIER_NAME)) {
                throw new IllegalArgumentException("Guitar requires a supplier name");
            }

            if (contentValues.containsKey(GuitarEntry.COLUMN_SUPPLIER_EMAIL)) {
                throw new IllegalArgumentException("Guitar requires a supplier's e-mail");
            }
            if (contentValues.size() == 0) {
                return 0;
            }
        }


        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        int rowsUpdated = database.update(GuitarEntry.TABLE_NAME, contentValues, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
       SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match){
            case GUITARS:
                rowsDeleted = database.delete(GuitarEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case GUITAR_ID:
                selection = GuitarEntry._ID + "=?";
                selectionArgs = new String[]{ String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(GuitarEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);

        }
        if (rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }


}
