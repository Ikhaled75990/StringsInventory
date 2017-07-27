package com.example.android.stringsinventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.stringsinventory.data.GuitarContract;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.attr.data;
import static android.R.attr.displayOptions;
import static android.content.ContentValues.TAG;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static com.example.android.stringsinventory.R.id.quantity;

/**
 * Created by Ikki on 27/07/2017.
 */

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = EditorActivity.class.getSimpleName();

    public static final int IMAGE_GALLERY_REQUEST = 20;

    private static final int EXISTING_GUITAR_LOADER = 0;

    private static final String STATE_IMAGE_URI = "STATE_IMAGE_URI";

    final Context mContext = this;
    @BindView(R.id.edit_manufacturer_name)
    EditText mManufacturerNameEditText;
    @BindView(R.id.edit_model)
    EditText mModelNameEditText;
    @BindView(R.id.edit_price)
    EditText mPriceEditText;
    @BindView(R.id.edit_quantity)
    EditText mQuantityEditText;
    @BindView(R.id.image_cover)
    ImageView mGuitarCover;
    @BindView(R.id.edit_supplier_name)
    EditText mSupplierNameEditText;
    @BindView(R.id.edit_supplier_email)
    EditText mSupplierEmailEditText;
    @BindView(R.id.add_image)
    Button mAddImage;
    @BindView(R.id.email_button)
    Button mOrder;
    @BindView(R.id.plus)
    Button mAddStock;
    @BindView(R.id.minus)
    Button mMinusStock;
    private Uri mImageUri;
    private String mImagePath;
    private Bitmap image;
    private Uri mCurrentGuitarUri;
    private boolean mGuitarHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mGuitarHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        mCurrentGuitarUri = intent.getData();

        if (mCurrentGuitarUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_guitar));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_guitar));
            getLoaderManager().initLoader(EXISTING_GUITAR_LOADER, null, this);
        }

        mManufacturerNameEditText.setOnTouchListener(mTouchListener);
        mModelNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mGuitarCover.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierEmailEditText.setOnTouchListener(mTouchListener);
        mOrder.setOnTouchListener(mTouchListener);

        mAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openPhotoGallery = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                String pictureDirectoryPath = pictureDirectory.getPath();
                Uri data = Uri.parse(pictureDirectoryPath);
                openPhotoGallery.setDataAndType(data, "image/*");
                startActivityForResult(openPhotoGallery, IMAGE_GALLERY_REQUEST);
            }
        });

        mOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);

                String to = mSupplierEmailEditText.getText().toString();
                String manufacturerName = mManufacturerNameEditText.getText().toString();
                String modelName = mModelNameEditText.getText().toString();
                String subject = "Order: " + modelName + "by: " + manufacturerName;
                String supplier = mSupplierNameEditText.getText().toString();
                String sep = System.getProperty("line.separator");
                String message = "Dear " + supplier + "," + sep + "I would like to order 10 more pieces of " + modelName + "by, " + manufacturerName + "." + sep + "Regards, " + sep + "Ikramul.";
                emailIntent.setData(Uri.parse("mailto:" + to));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                emailIntent.putExtra(Intent.EXTRA_TEXT, message);

                try {
                    startActivity(emailIntent);
                    finish();
                    Log.i(LOG_TAG, "Finished sending email...");
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(EditorActivity.this, "There is no email client installed.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mImageUri != null) {
            outState.putString(STATE_IMAGE_URI, mImageUri.toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(STATE_IMAGE_URI) &&
                !savedInstanceState.get(STATE_IMAGE_URI).equals("")) {
            mImageUri = Uri.parse(savedInstanceState.getString(STATE_IMAGE_URI));

            ViewTreeObserver viewTreeObserver = mGuitarCover.getViewTreeObserver();
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mGuitarCover.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mGuitarCover.setImageBitmap(getBitmapFromUri(mImageUri, mContext, mGuitarCover));
                }
            });
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_GALLERY_REQUEST && (resultCode == RESULT_OK)) {
            try {
                mImageUri = data.getData();
                int takeFlags = data.getFlags();
                takeFlags &= (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                mImagePath = mImageUri.toString();
                InputStream inputStream;

                inputStream = getContentResolver().openInputStream(mImageUri);
                image = BitmapFactory.decodeStream(inputStream);
                mGuitarCover.setImageBitmap(image);
                mImagePath = mImageUri.toString();
                try {
                    getContentResolver().takePersistableUriPermission(mImageUri, takeFlags);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
                mGuitarCover.setImageBitmap(getBitmapFromUri(mImageUri, mContext, mGuitarCover));
            } catch (Exception e) {
                e.printStackTrace();
                ;
                Toast.makeText(EditorActivity.this, "Unable to open image", Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    public Intent getSupportParentActivityIntent() {
        Intent intent = super.getSupportParentActivityIntent();
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        return intent;
    }

    public Bitmap getBitmapFromUri(Uri uri, Context context, ImageView imageView) {
        if (uri == null || uri.toString().isEmpty())
            return null;

        int targetWidth = imageView.getWidth();
        int targetHeight = imageView.getHeight();

        InputStream input = null;
        try {
            input = this.getContentResolver().openInputStream(uri);

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, bmOptions);
            if (input != null) {
                input.close();
            }

            int photoWidth = bmOptions.outWidth;
            int photoHeight = bmOptions.outHeight;

            int scaleFactor = Math.min(photoWidth / targetWidth, photoHeight / targetHeight);
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            input = this.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, bmOptions);
            if (input != null) {
                input.close();
            }
            return bitmap;
        } catch (FileNotFoundException fne) {
            Log.e(LOG_TAG, "Failed to load image.", fne);
            return null;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to load image.", e);
            return null;
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException ioe) {

            }
        }
    }

    private void saveGuitar() {

        String manufacturerNameString = mManufacturerNameEditText.getText().toString().trim();
        String modelNameString = mModelNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String supplierNameString = mSupplierNameEditText.getText().toString().trim();
        String supplierEmailString = mSupplierEmailEditText.getText().toString().trim();

        if (TextUtils.isEmpty(manufacturerNameString) || TextUtils.isEmpty(modelNameString) ||
                TextUtils.isEmpty(priceString) || TextUtils.isEmpty(quantityString) ||
                TextUtils.isEmpty(supplierNameString) || TextUtils.isEmpty(supplierEmailString) || mImageUri == null) {
            Toast.makeText(getApplicationContext(), "Please fill in all the missing fields.", Toast.LENGTH_LONG).show();
        }

        mImagePath = mImageUri.toString();

        Log.i(LOG_TAG, "TEST: Guitar cover string is: " + mImagePath);

        ContentValues values = new ContentValues();
        values.put(GuitarContract.GuitarEntry.COLUMN_MANUFACTURER_NAME, manufacturerNameString);
        values.put(GuitarContract.GuitarEntry.COLUMN_MODEL, modelNameString);
        int price = 0;
        if (!TextUtils.isEmpty(priceString)) {
            price = Integer.parseInt(priceString);
        }
        values.put(GuitarContract.GuitarEntry.COLUMN_PRICE, price);

        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }
        values.put(GuitarContract.GuitarEntry.COLUMN_QUANTITY, quantity);
        values.put(GuitarContract.GuitarEntry.COLUMN_GUITAR_COVER, mImagePath);
        values.put(GuitarContract.GuitarEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        values.put(GuitarContract.GuitarEntry.COLUMN_SUPPLIER_EMAIL, supplierEmailString);

        if (mCurrentGuitarUri == null) {
            Uri newUri = getContentResolver().insert(GuitarContract.GuitarEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_guitar_failed), Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, getString(R.string.editor_insert_guitar_successful), Toast.LENGTH_SHORT).show();

            }
        } else {

            int rowsAffected = getContentResolver().update(mCurrentGuitarUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_guitar_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_guitar_successful), Toast.LENGTH_SHORT).show();


            }
            finish();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentGuitarUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveGuitar();
                Intent intent = new Intent(EditorActivity.this, CatalogActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_delete:
                showDeleteConfirmationDialog();
                ;
                return true;

            case android.R.id.home:
                if (!mGuitarHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mGuitarHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtononClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtononClickListener);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        String[] projection = {
                GuitarContract.GuitarEntry._ID,
                GuitarContract.GuitarEntry.COLUMN_MANUFACTURER_NAME,
                GuitarContract.GuitarEntry.COLUMN_MODEL,
                GuitarContract.GuitarEntry.COLUMN_PRICE,
                GuitarContract.GuitarEntry.COLUMN_QUANTITY,
                GuitarContract.GuitarEntry.COLUMN_GUITAR_COVER,
                GuitarContract.GuitarEntry.COLUMN_SUPPLIER_NAME,
                GuitarContract.GuitarEntry.COLUMN_SUPPLIER_EMAIL};
        return new CursorLoader(this,
                mCurrentGuitarUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        ViewTreeObserver viewTreeObserver = mGuitarCover.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mGuitarCover.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mGuitarCover.setImageBitmap(getBitmapFromUri(mImageUri, mContext, mGuitarCover));


            }
        });

        if (cursor.moveToFirst()) {
            int idColumnIndex = cursor.getColumnIndex(GuitarContract.GuitarEntry._ID);
            int manufacturerNameColumnIndex = cursor.getColumnIndex(GuitarContract.GuitarEntry.COLUMN_MANUFACTURER_NAME);
            int modelNameColumnIndex = cursor.getColumnIndex(GuitarContract.GuitarEntry.COLUMN_MODEL);
            int priceColumnIndex = cursor.getColumnIndex(GuitarContract.GuitarEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(GuitarContract.GuitarEntry.COLUMN_QUANTITY);
            int imageColumnIndex = cursor.getColumnIndex(GuitarContract.GuitarEntry.COLUMN_GUITAR_COVER);
            int supplierNameColumnIndex = cursor.getColumnIndex(GuitarContract.GuitarEntry.COLUMN_SUPPLIER_NAME);
            int supplierEmailColumnIndex = cursor.getColumnIndex(GuitarContract.GuitarEntry.COLUMN_SUPPLIER_EMAIL);

            final long guitarID = cursor.getLong(idColumnIndex);
            String manufacturerName = cursor.getString(manufacturerNameColumnIndex);
            String modelName = cursor.getString(modelNameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            final int quantity = cursor.getInt(quantityColumnIndex);
            final String cover = cursor.getString(imageColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierEmail = cursor.getString(supplierEmailColumnIndex);

            mManufacturerNameEditText.setText(manufacturerName);
            mModelNameEditText.setText(modelName);
            mPriceEditText.setText(Integer.toString(price));
            mQuantityEditText.setText(Integer.toString(quantity));
            mSupplierNameEditText.setText(supplierName);
            mSupplierEmailEditText.setText(supplierEmail);
            mGuitarCover.setImageBitmap(getBitmapFromUri(Uri.parse(cover), mContext, mGuitarCover));
            mImageUri = Uri.parse(cover);

            mAddStock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (quantity >= 0) {
                        int newQuantity = quantity + 1;
                        ContentValues values = new ContentValues();
                        values.put(GuitarContract.GuitarEntry.COLUMN_QUANTITY, newQuantity);
                        Uri guitarUri = ContentUris.withAppendedId(GuitarContract.GuitarEntry.CONTENT_URI, guitarID);
                        int numRowsUpdated = EditorActivity.this.getContentResolver().update(guitarUri, values, null, null);
                        if (!(numRowsUpdated > 0)) {
                            Log.e(TAG, EditorActivity.this.getString(R.string.editor_update_guitar_failed));
                        }

                    }
                }
            });

            mMinusStock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (quantity >= 1) {
                        int newQuantity = quantity - 1;
                        ContentValues values = new ContentValues();
                        values.put(GuitarContract.GuitarEntry.COLUMN_QUANTITY, newQuantity);
                        Uri guitarUri = ContentUris.withAppendedId(GuitarContract.GuitarEntry.CONTENT_URI, guitarID);
                        int numRowsUpdated = EditorActivity.this.getContentResolver().update(guitarUri, values, null, null);
                        if (!(numRowsUpdated > 0)) {
                            Log.e(TAG, EditorActivity.this.getString(R.string.editor_update_guitar_failed));
                        } else if (!(quantity >= 1)) {
                            Toast.makeText(EditorActivity.this, getString(R.string.negative_stock), Toast.LENGTH_SHORT).show();

                        }
                    }
                }
            });
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mManufacturerNameEditText.setText("");
        mModelNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierNameEditText.setText("");
        mSupplierEmailEditText.setText("");
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_message);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteRecord();
            }
        });
        builder.setNegativeButton(R.string.cancel, null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteRecord() {
        if (mCurrentGuitarUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentGuitarUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_guitar_failed), Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, getString(R.string.editor_delete_guitar_successful), Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
}
