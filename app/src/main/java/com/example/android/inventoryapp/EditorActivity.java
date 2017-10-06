package com.example.android.inventoryapp;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import static android.R.attr.id;


/**
 * Created by djp on 9/20/17.
 */

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

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

    private View.OnTouchListener mTouchListener = new View.OnTouchListener(){
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent){
            mProductHasChanged = true;
            return false;
        }
    };
    protected void onCreate(Bundle savedInstanceState){
        Log.v(LOG_TAG, "entered onCreate");
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
        selectProductImageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.v(LOG_TAG, "selectProductImageButton onClick called");
                openImageSelector();
            }
        });


        /**
         * code snippet from Markus Eisele http://blog.eisele.net/2011/08/working-with-money-in-java.html
         */
//        double price = 100.12;
//        BigDecimal result2 = new BigDecimal(price).setScale(2, BigDecimal.ROUND_HALF_UP);
//        NumberFormat priceFormatted = NumberFormat.getCurrencyInstance(new Locale("usd"));
//        Log.v(LOG_TAG, "Amont formated into dollars: " + priceFormatted.format(result2));

        Button addNewProductButton = (Button) findViewById(R.id.add_new_product_button);
        addNewProductButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.v(LOG_TAG, "entered onClick, calling saveProduct method");
                saveProduct();
            }
        });
}

    public void openImageSelector(){
        Log.v(LOG_TAG, "entered openImageSelector");
        Intent intent;
        if(Build.VERSION.SDK_INT < 19){
            Log.v(LOG_TAG, "using sdk < 19");
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            Log.v(LOG_TAG, "using sdk >= 19");
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("image/*");
        Log.v(LOG_TAG, "calling startActivity");
        startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_IMAGE_REQUEST);
    }

    //called when photo is clicked on in openImageSelector
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData){

        Log.v(LOG_TAG, "entered onActivityResults");
        if(requestCode == SELECT_IMAGE_REQUEST && resultCode == Activity.RESULT_OK){
            Log.v(LOG_TAG, "value of resultData: " + resultData.getData());
            if(resultData != null){
                productImageUri = resultData.getData();
                Log.v(LOG_TAG, "productImageUri value in onActivityResult: " + productImageUri);
                mProductImageUri.setText(productImageUri.toString());
                mProductImageView.setImageBitmap(getBitMapFromUri(productImageUri));

            }
        }
    }

    public Bitmap getBitMapFromUri(Uri productImageUri) {

        Log.v(LOG_TAG, "entered getBitMapFromUri");
//
        if (productImageUri == null || productImageUri.toString().isEmpty()) {
            Log.v(LOG_TAG, "productImageUri is null or is empty");
            return null;
        }
//
        int targetW = mProductImageView.getWidth();
        int targetH = mProductImageView.getHeight();
//        return null;
//
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


    private void saveProduct(){
    Log.v(LOG_TAG, "entered saveProduct");
    String productName = mProductName.getText().toString().trim();
    Integer productQuantity = Integer.parseInt(mProductQuantity.getText().toString().trim());
    Integer productPrice= Integer.parseInt(mProductPrice.getText().toString().trim());
    String productSupplierName = mProductSupplierName.getText().toString().trim();
    String productSupplierEmail = mProductSupplierEmail.getText().toString().trim();
    String productImageUri = mProductImageUri.getText().toString().trim();

    ContentValues productValues = new ContentValues();
    productValues.put(ProductEntry.COLUMN_PRODUCT_NAME, productName);
    productValues.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);
    productValues.put(ProductEntry.COLUMN_PRODUCT_PRICE_IN_CENTS, productPrice);
    productValues.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME, productSupplierName);
    productValues.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL, productSupplierEmail);
    productValues.put(ProductEntry.COLUMN_PRODUCT_IMAGE_URI, productImageUri);

    Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, productValues);

    Log.v(LOG_TAG, "insertProduct() created row with uri:  " + newUri);
    if(newUri == null){
        Toast.makeText(this, "Error with saving product, uri is null", Toast.LENGTH_SHORT).show();
    } else {
        Toast.makeText(this, "Product saved with uri: " + newUri, Toast.LENGTH_SHORT).show();
    }

}

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
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
        switch(id){
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
        Log.v(LOG_TAG, "entered onLoadFinished");
        if(data.moveToFirst()){
            data.moveToFirst();
            String mProductName = data.getString(data.getColumnIndex("name"));
            int mProductQuantity = data.getInt(data.getColumnIndex("quantity"));

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
