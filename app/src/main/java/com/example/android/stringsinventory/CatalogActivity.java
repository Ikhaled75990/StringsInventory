package com.example.android.stringsinventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.stringsinventory.data.GuitarContract;

import static android.R.attr.data;

public class CatalogActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{

    public static final String LOG_TAG = CatalogActivity.class.getSimpleName();

    private static final int GUITAR_LOADER = 0;

    GuitarCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView guitarListView = (ListView) findViewById(R.id.list);
        View emptyView = findViewById(R.id.empty_view);
        guitarListView.setEmptyView(emptyView);

        mCursorAdapter = new GuitarCursorAdapter(this, null);
        guitarListView.setAdapter(mCursorAdapter);

        guitarListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                Uri currentGuitarUri = ContentUris.withAppendedId(GuitarContract.GuitarEntry.CONTENT_URI, id);
                intent.setData(currentGuitarUri);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(GUITAR_LOADER, null, this);
    }

    private void insertGuitar(){
        Uri path = Uri.parse("android.resource://com.example.android.stringsinventory/drawable.fender_stratocaster");
        String imagePath = path.toString();

        ContentValues values = new ContentValues();
        values.put(GuitarContract.GuitarEntry.COLUMN_MANUFACTURER_NAME, "Fender");
        values.put(GuitarContract.GuitarEntry.COLUMN_MODEL, "Stratocaster");
        values.put(GuitarContract.GuitarEntry.COLUMN_PRICE, 699);
        values.put(GuitarContract.GuitarEntry.COLUMN_QUANTITY, 14);
        values.put(GuitarContract.GuitarEntry.COLUMN_GUITAR_COVER, imagePath);
        values.put(GuitarContract.GuitarEntry.COLUMN_SUPPLIER_NAME, "Dawson Music");
        values.put(GuitarContract.GuitarEntry.COLUMN_SUPPLIER_EMAIL, "customer.service@dawsons.co.uk");

        getContentResolver().insert(GuitarContract.GuitarEntry.CONTENT_URI, values);
    }

    private void deleteAllGuitars(){
        int rowsDeleted = getContentResolver().delete(GuitarContract.GuitarEntry.CONTENT_URI, null, null);
        Log.v(LOG_TAG, rowsDeleted + " rows have been deleted from guitars database");

    }

    private void showDeleteConfirmationDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAllGuitars();;
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog != null){
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case  R.id.action_insert_dummy_data:
                insertGuitar();
                return true;
            case R.id.action_delete_all_entries:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
      String[] projection = {
              GuitarContract.GuitarEntry._ID,
              GuitarContract.GuitarEntry.COLUMN_MANUFACTURER_NAME,
              GuitarContract.GuitarEntry.COLUMN_MODEL,
              GuitarContract.GuitarEntry.COLUMN_PRICE,
              GuitarContract.GuitarEntry.COLUMN_QUANTITY};

        return new CursorLoader(this,
                GuitarContract.GuitarEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);

    }
}
