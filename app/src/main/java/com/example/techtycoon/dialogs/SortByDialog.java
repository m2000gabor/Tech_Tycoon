package com.example.techtycoon.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.example.techtycoon.Device;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class SortByDialog extends DialogFragment {
    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface SortByDialogListener {
        /*
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);*/
        void selectedAttributeId(Device.DeviceAttribute id);
    }

    // Use this instance of the interface to deliver action events
    private SortByDialog.SortByDialogListener listener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (SortByDialog.SortByDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement SortByDialogListener");
        }
    }

    private final static List<String> OPTIONS= Arrays.asList(
            "Name","ID","Performance","Storage","Body","Income","Price","Sold pieces","Profit per item",
            "Ram", "Memory","Design","Material","Color","IP","Bezels"
    );

    public SortByDialog(){}

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Sort devices by ...")
                .setItems(OPTIONS.toArray(new String[0]), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        Device.DeviceAttribute attribute=null;
                        switch (which){
                            case 0:attribute=Device.DeviceAttribute.NAME;break;
                            case 1:attribute=Device.DeviceAttribute.DEVICE_ID;break;
                            case 2:attribute=Device.DeviceAttribute.PERFORMANCE_OVERALL;break;
                            case 3:attribute=Device.DeviceAttribute.SCORE_STORAGE;break;
                            case 4:attribute=Device.DeviceAttribute.SCORE_BODY;break;
                            case 5:attribute=Device.DeviceAttribute.INCOME;break;
                            case 6:attribute=Device.DeviceAttribute.PRICE;break;
                            case 7:attribute=Device.DeviceAttribute.SOLD_PIECES;break;
                            case 8:attribute=Device.DeviceAttribute.PROFIT;break;
                            case 9:attribute=Device.DeviceAttribute.STORAGE_RAM;break;
                            case 10:attribute=Device.DeviceAttribute.STORAGE_MEMORY;break;
                            case 11:attribute=Device.DeviceAttribute.BODY_DESIGN;break;
                            case 12:attribute=Device.DeviceAttribute.BODY_MATERIAL;break;
                            case 13:attribute=Device.DeviceAttribute.BODY_COLOR;break;
                            case 14:attribute=Device.DeviceAttribute.BODY_IP;break;
                            case 15:attribute=Device.DeviceAttribute.BODY_BEZEL;break;
                        }

                        listener.selectedAttributeId(attribute);
                    }
                });
        return builder.create();
    }
}
