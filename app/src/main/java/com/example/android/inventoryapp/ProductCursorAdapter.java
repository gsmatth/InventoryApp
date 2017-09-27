package com.example.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by djp on 9/20/17.
 */

public class ProductCursorAdapter  extends CursorAdapter{

    public static final String LOG_TAG = ProductCursorAdapter.class.getSimpleName();


    public ProductCursorAdapter(Context context, Cursor cursor){
        super(context, cursor, 0);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.v(LOG_TAG, "entered View constructor");
        return LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
    }

    /**
     * This method binds the product data(in the row being pointed to in the cursor)
     * to the given list item layout.  For example, the name of the current product can be set on
     * the "item_product_name" text view in the list item layout
     * @param view          existing view
     * @param context       app context
     * @param cursor        The cursor from which to get the data.  The cursor is already  moved
     *                      to the correct row
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.v(LOG_TAG, "entered bindView");

        TextView productName = view.findViewById(R.id.item_product_name);
        TextView productQuantity = view.findViewById(R.id.item_product_quantity);
        TextView productPrice = view.findViewById(R.id.item_product_price);

        String name = cursor.getString(cursor.getColumnIndex("name"));
        String quantity = cursor.getString(cursor.getColumnIndex("quantity"));
        String price = cursor.getString(cursor.getColumnIndex("price"));

        productName.setText(name);
        productQuantity.setText(quantity);
        productPrice.setText(price);

    }
}
