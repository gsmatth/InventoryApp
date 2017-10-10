package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ProductContract;
import com.example.android.inventoryapp.data.ProductContract.ProductEntry;


/**
 * Created by djp on 9/20/17.
 */

public class ProductCursorAdapter extends CursorAdapter {

    public static final String LOG_TAG = ProductCursorAdapter.class.getSimpleName();


    public ProductCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
    }

    /**
     * This method binds the product data(in the row being pointed to in the cursor)
     * to the given list item layout.  For example, the name of the current product can be set on
     * the "item_product_name" text view in the list item layout
     *
     * @param view    existing view
     * @param context app context
     * @param cursor  The cursor from which to get the data.  The cursor is already  moved
     *                to the correct row
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        TextView productName = view.findViewById(R.id.item_product_name);
        final TextView productQuantity = view.findViewById(R.id.item_product_quantity);
        TextView productPrice = view.findViewById(R.id.item_product_price);

        String name = cursor.getString(cursor.getColumnIndex("name"));
        final String quantity = cursor.getString(cursor.getColumnIndex("quantity"));
        Float price = cursor.getFloat(cursor.getColumnIndex("price"));
        Float priceFloat = (Float.valueOf(price) / 100);
        String currencyString = String.format("%.2f", priceFloat);
        String _id = cursor.getString(cursor.getColumnIndex("_id"));

        productName.setText(name);
        productQuantity.setText(quantity);
        productPrice.setText("$" + currencyString);

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

