package com.example.android.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by djp on 9/20/17.
 */

public class ProductContract {

    /**
     * By making contract constructor private, you prevent the contract from being instantiated
     */
    private ProductContract(){}

    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String  PATH_PRODUCTS = "products";

    public static class ProductEntry implements BaseColumns{
        public static final String TABLE_NAME = "products";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.ANY_CURSOR_ITEM_TYPE + "/"  + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "name";
        public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
        public static final String COLUMN_PRODUCT_PRICE = "price";
        public static final String COLUMN_PRODUCT_SUPPLIER_CONTACT = "supplier_contact";
        public static final String COLUMN_PRODUCT_IMAGE_SOURCE_ID = "image_source_id";

        public static final String SQL_CREATE_PRODUCTS_TABLE =
                "CREATE TABLE " + ProductEntry.TABLE_NAME + " (" +
                        ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL," +
                        ProductEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL," +
                        ProductEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL," +
                        ProductEntry.COLUMN_PRODUCT_IMAGE_SOURCE_ID + "INTEGER," +
                        ProductEntry.COLUMN_PRODUCT_SUPPLIER_CONTACT + "TEXT NOT NULL)";

        public static final String SQL_DELETE_PRODUCT_TABLE =
                "DROP TABLE IF EXISTS " + ProductEntry.TABLE_NAME;
    }
}
