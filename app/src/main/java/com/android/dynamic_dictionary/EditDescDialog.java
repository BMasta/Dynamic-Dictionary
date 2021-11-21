package com.android.dynamic_dictionary;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class EditDescDialog extends AppCompatDialogFragment {
    private EditText editTextDesc;
    private EditWordDialogListener listener;
    private final String oldDescHint;
    private final int pos;

    public EditDescDialog(int position, String oldDesc) {
        super();
        oldDescHint = oldDesc;
        pos = position;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_edit_desc_dialog, null);
        builder.setView(view)
                .setTitle("Edit Notes")
                .setNegativeButton("Cancel", (dialog, which) -> {
                })
                .setPositiveButton("save", (dialog, which) -> {
                    String desc = editTextDesc.getText().toString();
                    listener.handleEditDialogData(pos, desc);
                });
        editTextDesc = view.findViewById(R.id.editTextEditDesc);
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

    public interface EditWordDialogListener {
        void handleEditDialogData(int position, String desc);
    }
}
