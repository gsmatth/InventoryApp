package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
//import android.support.v4.app.LoaderManager;
//import android.support.v4.content.Loader;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

public class InventoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = InventoryActivity.class.getSimpleName();
    ProductCursorAdapter mCursorAdapter;
    private static final int URL_LOADER = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        ListView productItems = (ListView) findViewById(R.id.product_listview);

        View emptyView = findViewById(R.id.empty_view);
        productItems.setEmptyView(emptyView);

        mCursorAdapter = new ProductCursorAdapter(this, null);
        productItems.setAdapter(mCursorAdapter);

        productItems.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(InventoryActivity.this, ProductDetailActivity.class);

                Uri currentProductUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);

                intent.setData(currentProductUri);

                startActivity(intent);

            }
        });

        Button addNewProductButton = (Button) findViewById(R.id.add_new_product_button);
        addNewProductButton.setOnClickListener(new View.OnClickListener(){
            @Override
                    public void onClick(View view){
                Intent intent = new Intent(InventoryActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(URL_LOADER, null, this);
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
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_PRICE_IN_CENTS,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL,
                ProductEntry.COLUMN_PRODUCT_IMAGE_SOURCE_ID
        };
        switch(id){
            case URL_LOADER:
                return new CursorLoader(
                        this,
                        ProductEntry.CONTENT_URI,
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
        mCursorAdapter.swapCursor(data);

    }

    /**
     * clears out adapter refence to the data (Cursor), which prevents memory leaks
     * @param loader
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);

    }

    private void reduceProductQuantity(){

    }
}
