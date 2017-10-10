package com.example.android.inventoryapp;


import android.annotation.TargetApi;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import static android.net.Uri.parse;


/**
 * Created by djp on 9/22/17.
 */

public class ProductDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = ProductDetailActivity.class.getSimpleName();
    private Uri currentProductUri;
    ProductCursorAdapter mCursorAdapter;
    private static final int URL_LOADER = 0;
    private boolean mProductHasChanged = false;

    private String mProductName;
    private Integer mProductQuantity;
    private Float mProductPrice;
    private String mProductSupplierName;
    private String mProductSupplierEmail;
    private String mProductImageUri;

    private static final int SELECT_IMAGE_REQUEST = 0;
    private ImageView productImageView;
    private TextView productImageTextView;
    private Uri productImageUri;

    public void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "entered onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_product);
        Intent intent = getIntent();
        currentProductUri = intent.getData();
        Log.v(LOG_TAG, "value of currentProductUri should not be null:  " + currentProductUri);
        productImageView = (ImageView) findViewById(R.id.product_detail_image);


        final Button increaseQuantityButton = (Button) findViewById(R.id.increase_quantity_button);
        increaseQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(LOG_TAG, "called onClickListener for increase quantity button");
                int numberOfRowsUpdated = increaseProductQuantity(currentProductUri);
            }
        });

        final Button decreaseQuantityButton = (Button) findViewById(R.id.decrease_quantity_button);
        decreaseQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(LOG_TAG, "called onClickListener for increase quantity button");
                int numberOfRowsUpdated = decreaseProductQuantity(currentProductUri);
            }
        });

        final Button deleteProductButton = (Button) findViewById(R.id.delete_product_button);
        deleteProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog();
                Log.v(LOG_TAG, "entered onClick for deleteProductButton");
            }
        });

        final Button orderProductButton = (Button) findViewById(R.id.order_product_button);
        orderProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderProduct(view);

            }
        });

        // //calls onCreateLoader on initial load of activity
        if (currentProductUri != null) {
            getLoaderManager().initLoader(URL_LOADER, null, this);
        }
    }

    ;

    private void showDeleteConfirmationDialog() {
        Log.v(LOG_TAG, " entered showDeleteConfirmationDialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Log.v(LOG_TAG, "row 1");
        builder.setMessage(R.string.delete_product_dialog_msg);
        Log.v(LOG_TAG, "row2");
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.v(LOG_TAG, "entered onCLick in showDeleteConfirmationDIalog");
                deleteProduct(currentProductUri);
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                Log.v(LOG_TAG, "enterd dialog dismiss in setNegativeButton");
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void orderProduct(View view) {
        Log.v(LOG_TAG, "entered orderProduct method");
        TextView productNameView = (TextView) findViewById(R.id.detailed_product_name_text);
        String productName = productNameView.getText().toString().trim();
        TextView supplierEmailView = (TextView) findViewById(R.id.detailed_product_supplier_email_text);
        String[] ccAddresses = new String[]{"gsmatth@icloud.com"};
        String supplierEmail = supplierEmailView.getText().toString().trim();
        String[] addresses = new String[]{supplierEmail};
        String subject = "Request for estimate on part: " + productName;
        String emailBody = "Please respond to this email address with an estimate of the cost " +
                "to purchase one " + productName + ". The estimate should include any applicable " +
                "sales tax and shipping costs.";
        Log.v(LOG_TAG, "email body: " + emailBody);
        composeEmail(addresses, ccAddresses, subject, emailBody);
    }

    public void composeEmail(String[] addresses, String[] ccAddresses, String subject, String emailBody) {
        Log.v(LOG_TAG, "entered compose email method");
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_CC, ccAddresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, emailBody);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.v(LOG_TAG, "unable to start activity because resolve activity is null");
        }
    }

    private int increaseProductQuantity(Uri uri) {
        Log.v(LOG_TAG, "entered increaseProductQuantity");
        TextView currentQuantityView = (TextView) findViewById(R.id.detailed_product_current_inventory_text);
        int currentQuantity = Integer.parseInt(currentQuantityView.getText().toString());
        Log.v(LOG_TAG, "value of current quantity as integer: " + currentQuantity);
        int newQuantity = currentQuantity + 1;
        Log.v(LOG_TAG, "value of new quantity as an int: " + newQuantity);

        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);

        int mRowsUpdated = getContentResolver().update(
                uri,
                values,
                null,
                null
        );
        return mRowsUpdated;
    }

    private int deleteProduct(Uri uri) {
        Log.v(LOG_TAG, "entered deleteProduct");
        int mRowsDeleted = getContentResolver().delete(
                uri,
                null,
                null);
        Log.v(LOG_TAG, "value of mRowsDeleted returned to  deleteProducts mehtod: " + mRowsDeleted);
        return mRowsDeleted;
    }

    private int decreaseProductQuantity(Uri uri) {
        int mRowsUpdated = 0;
        Log.v(LOG_TAG, "entered increaseProductQuantity");
        TextView currentQuantityView = (TextView) findViewById(R.id.detailed_product_current_inventory_text);
        int currentQuantity = Integer.parseInt(currentQuantityView.getText().toString());
        Log.v(LOG_TAG, "value of current quantity as integer: " + currentQuantity);
        int newQuantity = currentQuantity - 1;
        if (newQuantity < 0) {
            Toast.makeText(this, "The quantity can not be less than zero", Toast.LENGTH_LONG).show();
        } else {
            Log.v(LOG_TAG, "value of new quantity as an int: " + newQuantity);

            ContentValues values = new ContentValues();
            values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);

            mRowsUpdated = getContentResolver().update(
                    uri,
                    values,
                    null,
                    null
            );
        }
        return mRowsUpdated;
    }

    public Bitmap getBitMapFromUri(Uri productImageUri) {

        Log.v(LOG_TAG, "entered getBitMapFromUri");

        if (productImageUri == null || productImageUri.toString().isEmpty()) {
            Log.v(LOG_TAG, "productImageUri is null or is empty");
            return null;
        }

        int targetW = productImageView.getWidth();
        int targetH = productImageView.getHeight();

        InputStream input = null;
        try {
            Log.v(LOG_TAG, "entered try block");
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
            if (bitmap == null) {
                Log.v(LOG_TAG, "bitmap is null");
            }
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
        Log.e(LOG_TAG, "getting ready to return null at end of getBitmapfromUri");
        return null;
    }

    /**
     * Creates a CursorLoader and defines the data you want to query.  Then, off the main thread
     * it queries through the ContentResolver to the ProductProvider.query().  Upon completion
     * of the query, the LoaderManager calls the onLoadFinished and passes the Cursor with
     * the returned data
     *
     * @param id   the row id of the cursor
     * @param args
     * @return Cursor object with data or null if no row id passed in
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "entered onCreateLoader");
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

    /**
     * data, in the form of a Cursor, is passed in from CursorLoader created in onCreateLoader above.
     * That data is passed to the CursorAdapter, which interacts with the UI to update view of data.
     *
     * @param loader CursorLoader created in onCreateLoader
     * @param data   Cursor object with data
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "entered onLoadFinished");
        Log.v(LOG_TAG, "value of cursor: " + data);
        if (data.moveToFirst()) {
            data.moveToFirst();
            mProductName = data.getString(data.getColumnIndex("name"));
            mProductQuantity = data.getInt(data.getColumnIndex("quantity"));
            mProductPrice = data.getFloat(data.getColumnIndex("price"));
            Float priceFloat = (Float.valueOf(mProductPrice / 100));
            String currencyString = String.format("%.2f", priceFloat);


            mProductSupplierName = data.getString(data.getColumnIndex("supplier"));
            mProductSupplierEmail = data.getString(data.getColumnIndex("supplier_email"));
            mProductImageUri = data.getString(data.getColumnIndex("image_uri"));
            Log.v(LOG_TAG, "value of name in cursor: " + mProductName);
            Log.v(LOG_TAG, "value of quantity in cursor: " + mProductQuantity);
            Log.v(LOG_TAG, "value of price in cursor: " + currencyString);
            Log.v(LOG_TAG, "value of supplier in cursor: " + mProductSupplierName);
            Log.v(LOG_TAG, "value of email in cursor: " + mProductSupplierEmail);
            Log.v(LOG_TAG, "value of imageSourceId in cursor: " + mProductImageUri);

            TextView productNameText = (TextView)
                    findViewById(R.id.detailed_product_name_text);
            productNameText.setText(mProductName);

            TextView productQuantityText = (TextView)
                    findViewById(R.id.detailed_product_current_inventory_text);
            productQuantityText.setText(String.valueOf(mProductQuantity));

            TextView productPriceText = (TextView)
                    findViewById(R.id.detailed_product_price_text);
//            productPriceText.setText(String.valueOf(mProductPrice));
            productPriceText.setText("$" + currencyString);

            TextView productSupplierNameText = (TextView)
                    findViewById(R.id.detailed_product_supplier_name_text);
            productSupplierNameText.setText(mProductSupplierName);

            TextView productSupplierEmailText = (TextView)
                    findViewById(R.id.detailed_product_supplier_email_text);
            productSupplierEmailText.setText(mProductSupplierEmail);

            ViewTreeObserver viewTreeObserver = productImageView.getViewTreeObserver();
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onGlobalLayout() {
                    productImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    productImageView.setImageBitmap(getBitMapFromUri(parse(mProductImageUri)));
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(LOG_TAG, "entered onLoadReset");
        TextView productNameText = (TextView)
                findViewById(R.id.detailed_product_name_text);
        productNameText.setText("");

        TextView productQuantityText = (TextView)
                findViewById(R.id.detailed_product_current_inventory_text);
        productQuantityText.setText("");

        TextView productPriceText = (TextView)
                findViewById(R.id.detailed_product_price_text);
        productQuantityText.setText("");

        TextView productSupplierNameText = (TextView)
                findViewById(R.id.detailed_product_supplier_name_text);
        productSupplierNameText.setText("");

        TextView productSupplierEmailText = (TextView)
                findViewById(R.id.detailed_product_supplier_email_text);
        productSupplierEmailText.setText("");

        /**
         * attribution     https://github.com/crlsndrsjmnz/MyShareImageExample/blob/master/app/src/
         * main/java/co/carlosandresjimenez/android/myshareimageexample/MainActivity.java
         */

        ViewTreeObserver viewTreeObserver = productImageView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                productImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                productImageView.setImageBitmap(getBitMapFromUri(parse(mProductImageUri)));
            }
        });


    }

}
