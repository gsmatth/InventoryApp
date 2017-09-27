package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

import static android.R.attr.value;

/**
 * Created by djp on 9/20/17.
 */

public class ProductProvider extends ContentProvider {
    public static final String LOG_TAG = ProductProvider.class.getSimpleName();
    private ProductDbHelper mDbHelper;
    private static final int PRODUCTS = 100;
    private static final int PRODUCTS_ID = 101;

    SQLiteDatabase writeDatabase;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static{
        sUriMatcher.addURI("com.example.android.inventoryapp", "/products", PRODUCTS);
        sUriMatcher.addURI("com.example.android.inventoryapp", "/products/#", PRODUCTS_ID);

    }



    @Override
    public boolean onCreate() {
        mDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri mNewProductRowUri;
        final int match = sUriMatcher.match(uri);
        switch(match){
            case PRODUCTS:
                mNewProductRowUri = insertProduct(uri, values);
                break;
            default:
                throw new IllegalArgumentException("Cannot insert new row with uri: " + uri);
        }
        return mNewProductRowUri;
    }


    private Uri insertProduct(Uri uri, ContentValues values){
        Log.v(LOG_TAG, "entered insertProducts method");
        String name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
        Log.v(LOG_TAG, "insertProduct name value: " + name);
        int quantity = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        int price = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_PRICE_IN_CENTS);
        String supplierName = values.getAsString(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
        String supplierEmail = values.getAsString(ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL);
        String imageSourceId = values.getAsString(ProductEntry.COLUMN_PRODUCT_IMAGE_SOURCE_ID);

        Log.v(LOG_TAG, "values of values: " + name + ", " + quantity + ", " + supplierName +
        ", " + supplierEmail + ", " + imageSourceId);

        writeDatabase = mDbHelper.getWritableDatabase();
        long newRowId = writeDatabase.insert(ProductEntry.TABLE_NAME, null, values);

        if(newRowId == -1){
            Log.e(LOG_TAG, "Failed to insert row for uri: " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, newRowId);
    };

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
