package com.alex_sav.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alex_sav.inventoryapp.data.ProductContract.ProductEntry;

public class ProductCursorAdapter extends CursorAdapter {

    private Context mContext;

    public ProductCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_items, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        mContext = context;

        TextView textViewName = (TextView) view.findViewById(R.id.text_view_name);
        ImageView imageView = (ImageView) view.findViewById(R.id.image_list_item);
        TextView textViewQuantity = (TextView) view.findViewById(R.id.text_view_quantity);
        TextView textViewPrice = (TextView) view.findViewById(R.id.text_view_price);

        final String productName = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_NAME));
        final String productImage = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_IMAGE));
        final Integer productQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_QUANTITY));
        final Float productPrice = cursor.getFloat(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_PRICE));

        textViewName.setText(productName);
        textViewQuantity.setText(Integer.toString(productQuantity));
        textViewPrice.setText(Float.toString(productPrice));
        if (productImage != null) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageURI(Uri.parse(productImage));
        } else {
            imageView.setVisibility(View.INVISIBLE);
        }

        Button buttonSell = (Button) view.findViewById(R.id.button_sell);
        buttonSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view != null) {
                    Object object = view.getTag();
                    String string = object.toString();
                    ContentValues contentValues = new ContentValues();

                    contentValues.put(ProductEntry.COLUMN_PRODUCT_NAME, productName);
                    contentValues.put(ProductEntry.COLUMN_PRODUCT_IMAGE, productImage);
                    contentValues.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, productQuantity >= 1 ? productQuantity - 1 : 0);
                    contentValues.put(ProductEntry.COLUMN_PRODUCT_PRICE, productPrice);

                    Uri currentProductUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, Integer.parseInt(string));

                    int rowsAffected = mContext.getContentResolver().update(currentProductUri, contentValues, null, null);
                    if (rowsAffected == 0 || productQuantity == 0) {
                        Toast.makeText(mContext, "Product is Out Of Stock!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        Object object = cursor.getInt(cursor.getColumnIndex(ProductEntry._ID));
        buttonSell.setTag(object);
    }
}
