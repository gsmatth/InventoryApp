package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Loader;
import android.database.Cursor;
import android.icu.math.BigDecimal;
import android.icu.text.NumberFormat;
import android.icu.util.Currency;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ProductContract;
import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

import java.util.Locale;


/**
 * Created by djp on 9/20/17.
 */

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String LOG_TAG = EditorActivity.class.getSimpleName();

    private EditText mProductName;
    private EditText mProductQuantity;
    private EditText mProductSupplierName;
    private EditText mProductSupplierEmail;
    private EditText mProductImageSourceId;
    private EditText mProductPrice;

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mProductName = (EditText) findViewById(R.id.edit_product_name_text);
        mProductQuantity = (EditText) findViewById(R.id.edit_product_current_inventory_quantity_text);
        mProductSupplierName = (EditText) findViewById(R.id.edit_product_supplier_name_text);
        mProductSupplierEmail = (EditText) findViewById(R.id.edit_product_supplier_email_text);
        mProductImageSourceId = (EditText) findViewById(R.id.edit_product_image_source_id_text);
        mProductPrice = (EditText) findViewById(R.id.edit_product_price_text);

        mProductName.setOnTouchListener(mTouchListener);
        mProductQuantity.setOnTouchListener(mTouchListener);
        mProductSupplierName.setOnTouchListener(mTouchListener);
        mProductSupplierEmail.setOnTouchListener(mTouchListener);
        mProductImageSourceId.setOnTouchListener(mTouchListener);
        mProductPrice.setOnTouchListener(mTouchListener);


        /**
         * code snippet from Markus Eisele http://blog.eisele.net/2011/08/working-with-money-in-java.html
         */
//        double price = 100.12;
//        BigDecimal result2 = new BigDecimal(price).setScale(2, BigDecimal.ROUND_HALF_UP);
//        NumberFormat priceFormatted = NumberFormat.getCurrencyInstance(new Locale("usd"));
//        Log.v(LOG_TAG, "Amont formated into dollars: " + priceFormatted.format(result2));

}

private void saveProduct(){
    String productName = mProductName.getText().toString().trim();
    Integer productQuantity = Integer.parseInt(mProductQuantity.getText().toString().trim());
    Integer productPrice= Integer.parseInt(mProductPrice.getText().toString().trim());
    String productSupplierName = mProductSupplierName.getText().toString().trim();

    ContentValues productValues = new ContentValues();
    productValues.put(ProductEntry.COLUMN_PRODUCT_NAME, productName);
    productValues.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);
    productValues.put(ProductEntry.COLUMN_PRODUCT_PRICE_IN_CENTS, productPrice);
    productValues.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME, productSupplierName);

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
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
