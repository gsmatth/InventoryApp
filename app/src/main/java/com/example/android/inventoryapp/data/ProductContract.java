package com.example.android.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by djp on 9/20/17.
 */

public class ProductContract {

    /**
     * By making contract constructor private, you prevent and instance of contract being instantiated
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
        public static final String COLUMN_PRODUCT_PRICE_IN_CENTS = "price";
        public static final String COLUMN_PRODUCT_SUPPLIER_NAME = "supplier";
        public static final String COLUMN_PRODUCT_SUPPLIER_EMAIL = "supplier_email";
        public static final String COLUMN_PRODUCT_IMAGE_URI = "image_uri";

        public static final String SQL_CREATE_PRODUCTS_TABLE =
                "CREATE TABLE " + ProductEntry.TABLE_NAME + " (" +
                        ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        ProductEntry.COLUMN_PRODUCT_NAME + " TEXT," +
                        ProductEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER," +
                        ProductEntry.COLUMN_PRODUCT_PRICE_IN_CENTS + " INTEGER," +
                        ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME + " TEXT," +
                        ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL + " TEXT," +
                        ProductEntry.COLUMN_PRODUCT_IMAGE_URI + " TEXT" + ");";

        public static final String SQL_DELETE_PRODUCT_TABLE =
                "DROP TABLE IF EXISTS " + ProductEntry.TABLE_NAME;
    }
}
