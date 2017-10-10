package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.android.inventoryapp.data.ProductContract.ProductEntry.SQL_CREATE_PRODUCTS_TABLE;
import static com.example.android.inventoryapp.data.ProductContract.ProductEntry.SQL_DELETE_PRODUCT_TABLE;

/**
 * Created by djp on 9/20/17.
 */

public class ProductDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = ProductDbHelper.class.getSimpleName();
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ProductEntry.db";

    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
    }

    /**
     * Use this function to push changes to the db schema.  I do not have any changes at this
     * point so this is more of a place holder that does not do anything other than drop the
     * current table and create a new one.
     *
     * @param db         database instance
     * @param oldVersion version number of older schema
     * @param newVersion version number of newer schema
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_PRODUCT_TABLE);
        onCreate(db);
    }

    /**
     * Use this function to revert back to older version schema.I do not have any schema changes
     * to implement at this point, so it is more of a place holder.
     *
     * @param db         database instance
     * @param oldVersion version number of older schema
     * @param newVersion version number of newer schema
     */
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
