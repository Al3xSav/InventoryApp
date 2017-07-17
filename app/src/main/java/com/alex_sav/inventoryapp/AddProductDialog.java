package com.alex_sav.inventoryapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alex_sav.inventoryapp.data.ProductContract.ProductEntry;

import butterknife.ButterKnife;

public class AddProductDialog extends DialogFragment {

    String mImageURI;

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        final View addView = layoutInflater.inflate(R.layout.add_product_dialog, null);

        Button buttonSelectImage = ButterKnife.findById(addView, R.id.button_image);
        // set up click listener for the select image button
        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission(getActivity())) {
                    startActivityForResult(new Intent
                                    (Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI),
                            PermissionUtilities.MY_PERMISSIONS_READ_EXTERNAL_STORAGE);
                }
            }
        });

        final Dialog addDialog = builder.setView(addView).setPositiveButton(R.string.add_product, null)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int id) {
                        AddProductDialog.this.getDialog().cancel();
                    }
                }).create();

        // set on the listener for the positive button of the dialog
        addDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button positiveButton = ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_POSITIVE);

                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // add closingDialog to prevent the dialog from closing when the
                        // information is not completely filled out
                        Boolean closingDialog = false;

                        EditText editTextName = ButterKnife.findById(addView, R.id.dialog_edit_text_name);
                        EditText editTextQuantity = ButterKnife.findById(addView, R.id.dialog_edit_text_quantity);
                        EditText editTextPrice = ButterKnife.findById(addView, R.id.dialog_edit_text_price);

                        String name = editTextName.getText().toString().trim();
                        String quantityString = editTextQuantity.getText().toString().trim();
                        String priceString = editTextPrice.getText().toString().trim();

                        // validate all the required information
                        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(quantityString) || TextUtils.isEmpty(priceString)) {

                            Toast.makeText(getActivity(), "Please fill out all the required information", Toast.LENGTH_SHORT).show();

                        } else {
                            Integer quantity = Integer.parseInt(editTextQuantity.getText().toString().trim());
                            Float price = Float.parseFloat(editTextPrice.getText().toString().trim());
                            insertProduct(name, quantity, price, mImageURI);
                            closingDialog = true;
                        }

                        // after successfully inserting product, dismiss the dialog
                        if (closingDialog) {
                            addDialog.dismiss();
                        }
                    }
                });
            }
        });

        return addDialog;
    }

    private void insertProduct(String name, Integer quantity, Float price, String imageURI) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(ProductEntry.COLUMN_PRODUCT_NAME, name);
        contentValues.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);
        contentValues.put(ProductEntry.COLUMN_PRODUCT_PRICE, price);
        if (!"".equals(imageURI)) {
            contentValues.put(ProductEntry.COLUMN_PRODUCT_IMAGE, imageURI);
        }
        getActivity().getContentResolver().insert(ProductEntry.CONTENT_URI, contentValues);
    }

    // get result data from selecting an image
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == PermissionUtilities.MY_PERMISSIONS_READ_EXTERNAL_STORAGE && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = intent.getData();
            mImageURI = selectedImage.toString();
        }
    }

    public boolean checkPermission(final Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission
                    (context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {

                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                        PermissionUtilities.showPermissionDialog(
                                context.getString(R.string.external_storage),
                                context,
                                Manifest.permission.READ_EXTERNAL_STORAGE);
                    } else {
                        // No explanation needed, we can request the permission.
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                PermissionUtilities.MY_PERMISSIONS_READ_EXTERNAL_STORAGE);
                    }
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionUtilities.MY_PERMISSIONS_READ_EXTERNAL_STORAGE: {
                // if request is cancelled, the result arrays are empty
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT,
                                    android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI),
                            PermissionUtilities.MY_PERMISSIONS_READ_EXTERNAL_STORAGE);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }
}
