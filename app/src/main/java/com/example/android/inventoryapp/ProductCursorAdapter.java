package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.icu.text.NumberFormat;
import android.net.Uri;
import android.util.FloatProperty;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ProductContract;
import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

import java.util.Locale;

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
        Log.v(LOG_TAG, "entered View constructor in ProductCursorAdapter");
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
    public void bindView(View view, final Context context, Cursor cursor) {
        Log.v(LOG_TAG, "entered bindView in ProductCursorAdapter");

        TextView productName = view.findViewById(R.id.item_product_name);
        final TextView productQuantity = view.findViewById(R.id.item_product_quantity);
        TextView productPrice = view.findViewById(R.id.item_product_price);

        String name = cursor.getString(cursor.getColumnIndex("name"));
        final String quantity = cursor.getString(cursor.getColumnIndex("quantity"));
//        String price = cursor.getString(cursor.getColumnIndex("price"));
//        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
        Float price = cursor.getFloat(cursor.getColumnIndex("price"));
        Float priceFloat = (Float.valueOf(price)/100);
        String currencyString = String.format("%.2f", priceFloat);
//        String currencyString = Float.toString(priceFloat);
        Log.v(LOG_TAG, "value of priceFloat shoudl be a float: " + priceFloat);
        Log.v(LOG_TAG, "value of currencyString should be a string: " +currencyString);
//        Float priceInDollars = priceFloat/100;
        String _id = cursor.getString(cursor.getColumnIndex("_id"));

        productName.setText(name);
        productQuantity.setText(quantity);
        productPrice.setText("$" + currencyString);

        /**the saleButton functionality was accomplished with the assistance of:
         *
         * https://discussions.udacity.com/t/how-to-implement-sale-button/344691/10
         *   AND
         * https://stackoverflow.com/questions/15941374/how-do-i-call-onclick-listener-of-a-
         * button-which-resides-in-listview-item?rq=1
*/
        final Uri itemUri = Uri.withAppendedPath(ProductEntry.CONTENT_URI, _id);

        Button saleButton = (Button) view.findViewById(R.id.item_product_sale_button);
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = itemUri;
                int currentQuantity = Integer.parseInt(productQuantity.getText().toString());
                int reducedQuantity = currentQuantity - 1;
                if (reducedQuantity < 0) {
                    Toast.makeText(view.getContext(), "The quantitiy of product cannot be less than zero", Toast.LENGTH_LONG).show();
                } else {
                    ContentValues values = new ContentValues();
                    values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, reducedQuantity);
                    context.getContentResolver().update(
                            uri,
                            values,
                            null,
                            null
                    );
                }
            }


        });
}
}

