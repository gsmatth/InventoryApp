package com.example.android.inventoryapp;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
;
import static android.R.attr.id;



/**
 * Created by djp on 9/20/17.
 */

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = EditorActivity.class.getSimpleName();

    private EditText mProductName;
    private EditText mProductQuantity;
    private EditText mProductSupplierName;
    private EditText mProductSupplierEmail;
    private EditText mProductImageUri;
    private EditText mProductPrice;
    private ImageView mProductImageView;

    private Uri productImageUri;
    private static final int SELECT_IMAGE_REQUEST = 0;


    private Uri currentProductUri;
    private static final int URL_LOADER = 0;
    private boolean mProductHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Intent intent = getIntent();

        getLoaderManager().initLoader(URL_LOADER, null, this);

        mProductName = (EditText) findViewById(R.id.edit_product_name_text);
        mProductQuantity = (EditText) findViewById(R.id.edit_product_current_inventory_quantity_text);
        mProductSupplierName = (EditText) findViewById(R.id.edit_product_supplier_name_text);
        mProductSupplierEmail = (EditText) findViewById(R.id.edit_product_supplier_email_text);
        mProductImageUri = (EditText) findViewById(R.id.edit_product_image_source_id_text);
        mProductPrice = (EditText) findViewById(R.id.edit_product_price_text);
        mProductImageView = (ImageView) findViewById(R.id.product_image);

        mProductName.setOnTouchListener(mTouchListener);
        mProductQuantity.setOnTouchListener(mTouchListener);
        mProductSupplierName.setOnTouchListener(mTouchListener);
        mProductSupplierEmail.setOnTouchListener(mTouchListener);
        mProductImageUri.setOnTouchListener(mTouchListener);
        mProductPrice.setOnTouchListener(mTouchListener);


        final Button selectProductImageButton = (Button) findViewById(R.id.select_product_image_button);
        selectProductImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageSelector();
            }
        });

        Button addNewProductButton = (Button) findViewById(R.id.add_new_product_button);
        addNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProduct();
            }
        });
    }


    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //user clicked "Keep Editing" button so dismiss dialog and keep editing
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openImageSelector() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_IMAGE_REQUEST);
    }

    //called when photo is clicked on in openImageSelector method
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        if (requestCode == SELECT_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                productImageUri = resultData.getData();
                mProductImageUri.setText(productImageUri.toString());
                mProductImageView.setImageBitmap(getBitMapFromUri(productImageUri));
            }
        }
    }

    public Bitmap getBitMapFromUri(Uri productImageUri) {

        if (productImageUri == null || productImageUri.toString().isEmpty()) {
            return null;
        }

        int targetW = mProductImageView.getWidth();
        int targetH = mProductImageView.getHeight();

        InputStream input = null;
        try {
            input = this.getContentResolver().openInputStream(productImageUri);
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, bitmapOptions);
            input.close();


            //The resulting width of the bitmap.
            int photoW = bitmapOptions.outWidth;
            //The resulting height of the bitmap.
            int photoH = bitmapOptions.outHeight;

            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            bitmapOptions.inJustDecodeBounds = false;
            //If set to a value > 1, requests the decoder to subsample the original image,
            // returning a smaller image to save memory.
            bitmapOptions.inSampleSize = scaleFactor;

            input = this.getContentResolver().openInputStream(productImageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
            input.close();
            return bitmap;

        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, "failed to load image: ", e);
            return null;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Failed to load image.", e);
        } finally {
            try {
                input.close();
            } catch (IOException e) {
            }
        }
        return null;
    }

    private void saveProduct() {

        List<String> mNullValues = new ArrayList<String>();
        String joined = "";
        String productName = mProductName.getText().toString().trim();

        int productQuantity = 0;
        int productPrice = 0;
        try {
            productQuantity = Integer.parseInt(mProductQuantity.getText().toString().trim());
        } catch (NumberFormatException e) {
            mNullValues.add("quantity");
            joined = TextUtils.join(", ", mNullValues);
        }

        try {
            productPrice = Integer.parseInt(mProductPrice.getText().toString().trim());
        } catch (NumberFormatException e) {
            mNullValues.add("price");
            joined = TextUtils.join(", ", mNullValues);
        }

        String productSupplierName = mProductSupplierName.getText().toString().trim();
        String productSupplierEmail = mProductSupplierEmail.getText().toString().trim();
        String productImageUri = mProductImageUri.getText().toString().trim();
        if (productName.isEmpty()) {
            mNullValues.add("product name");
            joined = TextUtils.join(", ", mNullValues);
        }

        if (productSupplierName.isEmpty()) {
            mNullValues.add("supplier name");
            joined = TextUtils.join(", ", mNullValues);
        }

        if (productSupplierEmail.isEmpty()) {
            mNullValues.add("supplier email");
            joined = TextUtils.join(", ", mNullValues);
        }

        if (productImageUri.isEmpty()) {
            mNullValues.add("image uri");
            joined = TextUtils.join(", ", mNullValues);

        }

        if (mNullValues.size() > 0) {
            Toast.makeText(this, "The following items are blank and have to be updated before" +
                    " you can save the new product: " + joined, Toast.LENGTH_LONG).show();

        } else {
            ContentValues productValues = new ContentValues();
            productValues.put(ProductEntry.COLUMN_PRODUCT_NAME, productName);
            productValues.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);
            productValues.put(ProductEntry.COLUMN_PRODUCT_PRICE_IN_CENTS, productPrice);
            productValues.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME, productSupplierName);
            productValues.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL, productSupplierEmail);
            productValues.put(ProductEntry.COLUMN_PRODUCT_IMAGE_URI, productImageUri);

            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, productValues);

            if (newUri == null) {
                Toast.makeText(this, "Error with saving product, uri is null", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Product saved with uri: " + newUri, Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_PRICE_IN_CENTS,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL,
                ProductEntry.COLUMN_PRODUCT_IMAGE_URI

        };
        switch (id) {
            case URL_LOADER:
                return new CursorLoader(
                        this,
                        currentProductUri,
                        projection,
                        null,
                        null,
                        null
                );
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            data.moveToFirst();
            String mProductName = data.getString(data.getColumnIndex("name"));
            int mProductQuantity = data.getInt(data.getColumnIndex("quantity"));

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
