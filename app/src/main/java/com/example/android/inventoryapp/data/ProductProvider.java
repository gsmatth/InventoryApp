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

import static android.R.attr.data;
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
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Log.v(LOG_TAG, "entered query method");

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;
        int match = sUriMatcher.match(uri);

        switch(match){
            case PRODUCTS:
                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case PRODUCTS_ID:
                selection = ProductEntry._ID + "=?";
                //the value of the string in selectionArgs below, will be substituted for the "?" above
                //the parseId() will convert the last path segment in the uri to a long data type
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("cannot query unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
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

        Log.v(LOG_TAG, "values of values: " + name + ", " + quantity + ", "  + price + ", "
                + supplierName + ", " + supplierEmail + ", " + imageSourceId);

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
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.v(LOG_TAG, "entered update in ProductProvider");
        final int match = sUriMatcher.match(uri);
        switch(match){
            case PRODUCTS:
                return updateProduct(uri, values, selection, selectionArgs);
            case PRODUCTS_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update to product is not supported for:  " + uri);
        }
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs){
        Log.v(LOG_TAG, "entered updateProduct in ProductProvider");
        writeDatabase = mDbHelper.getWritableDatabase();
        int numberOfRowsUpdated = writeDatabase.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);
        Log.v(LOG_TAG, "number of rows updated by updateProduct method:  " + numberOfRowsUpdated);
        if(numberOfRowsUpdated > 0){
            Log.v(LOG_TAG, "calling notifyChange method in updateProduct");
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numberOfRowsUpdated;
    }

}
