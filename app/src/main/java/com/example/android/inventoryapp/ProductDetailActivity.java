package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.inventoryapp.data.ProductContract;
import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

import static android.R.attr.id;
import static android.R.attr.name;
import static android.webkit.ConsoleMessage.MessageLevel.LOG;

/**
 * Created by djp on 9/22/17.
 */

public class ProductDetailActivity  extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String LOG_TAG = ProductDetailActivity.class.getSimpleName();
    private Uri currentProductUri;
    ProductCursorAdapter mCursorAdapter;
    private static final int URL_LOADER = 0;
    private boolean mProductHasChanged = false;

    private String mProductName;
    private Integer mProductQuantity;
    private String mProductSupplierName;
    private String mProductSupplierEmail;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener(){
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent){
            mProductHasChanged = true;
            return false;
        }
    };

    public void onCreate(Bundle savedInstanceState){
        Log.v(LOG_TAG, "entered onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_product);
        Intent intent = getIntent();
        currentProductUri = intent.getData();
        Log.v(LOG_TAG, "value of currentProductUri should not be null:  " + currentProductUri);

        final Button increaseQuantityButton = (Button) findViewById(R.id.increase_quantity_button);
        increaseQuantityButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.v(LOG_TAG, "called onClickListener for increase quantity button");
                increaseQuantityButton(currentProductUri);
            }
        });



        // //calls onCreateLoader on initial load of activity
        if(currentProductUri != null){
            getLoaderManager().initLoader(URL_LOADER, null, this);
        }

    }

    /**
     * Creates a CursorLoader and defines the data you want to query.  Then, off the main thread
     * it queries through the ContentResolver to the ProductProvider.query().  Upon completion
     * of the query, the LoaderManager calls the onLoadFInished and passes the Cursor with
     * the returned data
     * @param id            the row id of the cursor
     * @param args
     * @return              Cursor object with data or null if no row id passed in
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "entered onCreateLoader");
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL
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

    /**
     * data, in the form of a Cursor, is passed in from CursorLoader created in onCreateLoader above.
     * That data is passed to the CursorAdapter, which interacts with the UI to update view of data.
     * @param loader        CursorLoader created in onCreateLoader
     * @param data          Cursor object with data
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "entered onLoadFinished");
        Log.v(LOG_TAG, "value of cursor: " + data);
        if(data.moveToFirst()){
            data.moveToFirst();
            mProductName = data.getString(data.getColumnIndex("name"));
            mProductQuantity = data.getInt(data.getColumnIndex("quantity"));
            mProductSupplierName = data.getString(data.getColumnIndex("supplier"));
            mProductSupplierEmail = data.getString(data.getColumnIndex("supplier_email"));
            Log.v(LOG_TAG, "value of name in cursor: " + mProductName);
            Log.v(LOG_TAG, "value of quantity in cursor: " + mProductQuantity);
            Log.v(LOG_TAG, "value of supplier in cursor: " + mProductSupplierName);
            Log.v(LOG_TAG, "value of email in cursor: " + mProductSupplierEmail);

            TextView productNameText = (TextView)
                    findViewById(R.id.detailed_product_name_text);
            productNameText.setText(mProductName);

            TextView productQuantityText = (TextView)
                    findViewById(R.id.detailed_product_current_inventory_text);
            productQuantityText.setText(String.valueOf(mProductQuantity));

            TextView productSupplierNameText = (TextView)
                    findViewById(R.id.detailed_product_supplier_name_text);
            productSupplierNameText.setText(mProductSupplierName);

            TextView productSupplierEmailText = (TextView)
                    findViewById(R.id.detailed_product_supplier_email_text);
            productSupplierEmailText.setText(mProductSupplierEmail);
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
        TextView productSupplierNameText = (TextView)
                findViewById(R.id.detailed_product_supplier_name_text);
        productSupplierNameText.setText("");
        TextView productSupplierEmailText = (TextView)
                findViewById(R.id.detailed_product_supplier_email_text);
        productSupplierEmailText.setText("");


    }

    private void increaseProductQuantity(Uri uri){
        Log.v(LOG_TAG, "entered increaseProductQuantity");


    }
}
