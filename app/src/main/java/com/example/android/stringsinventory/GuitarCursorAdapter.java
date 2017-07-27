package com.example.android.stringsinventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.stringsinventory.data.GuitarContract;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

/**
 * Created by Ikki on 27/07/2017.
 */

public class GuitarCursorAdapter extends CursorAdapter {

    public static final String LOG_TAG = GuitarCursorAdapter.class.getSimpleName();

    public static Context mContext;


    public GuitarCursorAdapter(Context context, Cursor cursor){
        super(context, cursor, 0);
        mContext = context;
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        ViewHolder holder;
        holder = new ViewHolder();

        holder.mManufacturerNameTextView = (TextView) view.findViewById(R.id.manufacturer_name);
        holder.mModelNameTextView = (TextView) view.findViewById(R.id.model_name);
        holder.mPriceTextView = (TextView) view.findViewById(R.id.price);
        holder.mQuantityTextView = (TextView) view.findViewById(R.id.quantity);
        holder.mSaleTextView = (TextView) view.findViewById(R.id.sale_text_view);

        int manufacturerNameColumnIndex = cursor.getColumnIndex(GuitarContract.GuitarEntry.COLUMN_MANUFACTURER_NAME);
        int modelNameColumnIndex = cursor.getColumnIndex(GuitarContract.GuitarEntry.COLUMN_MODEL);
        int priceColumnIndex = cursor.getColumnIndex(GuitarContract.GuitarEntry.COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(GuitarContract.GuitarEntry.COLUMN_QUANTITY);
        int idColumnIndex = cursor.getColumnIndex(GuitarContract.GuitarEntry._ID);

        final String manufacturerName = cursor.getString(manufacturerNameColumnIndex);
        final String modelName = cursor.getString(modelNameColumnIndex);
        final int price = cursor.getInt(priceColumnIndex);
        final int quantity = cursor.getInt(quantityColumnIndex);
        final long guitarId = cursor.getLong(idColumnIndex);
        final int newQuantity;

        holder.mManufacturerNameTextView.setText(manufacturerName);
        holder.mModelNameTextView.setText(modelName);
        holder.mPriceTextView.setText(Integer.toString(price));
        holder.mQuantityTextView.setText(Integer.toString(quantity));

        holder.mSaleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity >= 1){
                    Log.i(LOG_TAG, "TEST: On sale click Quantity is: " + quantity);
                    int newQuantity = quantity - 1;
                    Log.i(LOG_TAG, "TEST: One sale click Updated Quantity is: " + newQuantity);

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(GuitarContract.GuitarEntry.COLUMN_QUANTITY, newQuantity);
                    Uri recordUri = ContentUris.withAppendedId(GuitarContract.GuitarEntry.CONTENT_URI, guitarId);
                    Log.i(LOG_TAG, "TEST: On sale click ContentUri is: " + GuitarContract.GuitarEntry.CONTENT_URI);
                    Log.i(LOG_TAG, "TEST: On sale click ContentUri_ID is: " + recordUri);
                    Log.i(LOG_TAG, "TEST: On sale click Manufacturer's Name is: " + manufacturerName);

                    int numRowsUpdated = context.getContentResolver().update(recordUri,contentValues, null, null);
                    Log.i(LOG_TAG, "TEST: Number Rows Updated: " + numRowsUpdated);
                     if (!(numRowsUpdated > 0)) {
                         Log.e(LOG_TAG, "Error with udpating guitar");
                     }
                } else if (!(quantity >= 1)) {

                    int quantity = 0;
                    Toast.makeText(context, "Out of Stock.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
