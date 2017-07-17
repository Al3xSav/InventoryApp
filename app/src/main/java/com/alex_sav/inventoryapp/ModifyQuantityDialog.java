package com.alex_sav.inventoryapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.ButterKnife;

public class ModifyQuantityDialog extends DialogFragment {

    int mQuantity;
    private QuantityListener mListener;

    static ModifyQuantityDialog newInstance(int quantity) {
        ModifyQuantityDialog modifyQuantityDialog = new ModifyQuantityDialog();
        Bundle args = new Bundle();
        args.putInt("quantity", quantity);
        modifyQuantityDialog.setArguments(args);
        return modifyQuantityDialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (QuantityListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement QuantityListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.modify_quantity_dialog, null);
        mQuantity = getArguments().getInt("quantity");
        final TextView QuantityTextView = ButterKnife.findById(view, R.id.quantity_modify);
        final Button buttonPlus = ButterKnife.findById(view, R.id.button_plus);
        final Button buttonMinus = ButterKnife.findById(view, R.id.button_minus);
        QuantityTextView.setText(Integer.toString(mQuantity));

        buttonMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mQuantity > 0) {
                    mQuantity--;
                    QuantityTextView.setText(Integer.toString(mQuantity));
                }
            }
        });
        buttonPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mQuantity++;
                QuantityTextView.setText(Integer.toString(mQuantity));
            }
        });
        builder.setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String quantity = getString(R.string.zero);
                        QuantityTextView.setText(Integer.toString(mQuantity));
                        if (!TextUtils.isEmpty(QuantityTextView.getText().toString().trim()))
                            quantity = QuantityTextView.getText().toString().trim();
                        mListener.onFinishModifyQuantityDialog(quantity);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ModifyQuantityDialog.this.getDialog().cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        return alertDialog;
    }

    public static interface QuantityListener {
        void onFinishModifyQuantityDialog(String quantity);
    }
}

