package com.example.android.stringsinventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


/**
 * Created by Ikki on 27/07/2017.
 */

public final class GuitarContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.stringsinventory";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_GUITARS = "guitars";

    private GuitarContract() {

    }

    public static final class GuitarEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_GUITARS);

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GUITARS;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GUITARS;

        public static final String TABLE_NAME = "guitars";
        public static final String COLUMN_MANUFACTURER_NAME = "manufacturer_name";
        public static final String COLUMN_MODEL = "model_name";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QUANTITY = "quantity";
        public final static String COLUMN_GUITAR_COVER = "cover";
        public static final String COLUMN_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_SUPPLIER_EMAIL = "supplier_email";
        public static String _ID = BaseColumns._ID;

    }
}
