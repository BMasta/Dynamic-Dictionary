package com.android.dynamic_dictionary;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class EditWordDialog extends AppCompatDialogFragment {
    private EditText editTextWord, editTextDesc;
    private EditWordDialogListener listener;
    private String oldWordHint, oldDescHint;
    private int pos;

    public EditWordDialog(int position, String oldWord, String oldDesc) {
        super();
        oldWordHint = oldWord;
        oldDescHint = oldDesc;
        pos = position;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);
        builder.setView(view)
                .setTitle("Edit Word")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String word = editTextWord.getText().toString();
                        String desc = editTextDesc.getText().toString();
                        listener.handleEditDialogData(pos, word, desc);
                    }
                });
        editTextWord = view.findViewById(R.id.editTextWord);
        editTextDesc = view.findViewById(R.id.editTextDesc);
        editTextWord.setText(oldWordHint);
        editTextDesc.setText(oldDescHint);

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (EditWordDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement EditWordDialogListener");
        }
    }

    public interface EditWordDialogListener{
        void handleEditDialogData(int position, String word, String desc);
    }
}
