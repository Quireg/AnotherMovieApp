package ua.in.quireg.anothermovieapp.movieList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import ua.in.quireg.anothermovieapp.R;
import ua.in.quireg.anothermovieapp.common.Constants;

public class SortOrderPicker extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.ui_favourites_sort_order)
                .setItems(R.array.sort_orders_array, new DialogInterface.OnClickListener() {
                    @SuppressLint("CommitPrefEdits")
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences preferences =
                                PreferenceManager.getDefaultSharedPreferences(getContext());
                        preferences.edit().putInt(Constants.SORT_ORDER, which).apply();
                        getTargetFragment().onActivityResult(
                                getTargetRequestCode(), Activity.RESULT_OK,
                                getActivity().getIntent());
                    }
                });
        return builder.create();

    }
}
