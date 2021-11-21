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

public class NewWordDialog extends AppCompatDialogFragment {
    private EditText editTextWord, editTextDesc;
    private NewWordDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_new_word_dialog, null);
        AlertDialog dialog = new AlertDialog.Builder(requireActivity())
                .setTitle("New Word")
                .setPositiveButton("save", null)
                .setNegativeButton("cancel", null)
                .setView(view)
                .show();
        // on positive click
        dialog.getButton(Dialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String word = editTextWord.getText().toString();
            String desc = editTextDesc.getText().toString();
            listener.handleNewDialogData(word, desc);
            if (!word.equals(""))
                dialog.dismiss();
        });
        // on negative click
        dialog.getButton(Dialog.BUTTON_NEGATIVE).setOnClickListener(v -> dialog.dismiss());

        editTextWord = view.findViewById(R.id.editTextWord);
        editTextDesc = view.findViewById(R.id.editTextDesc);
        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (NewWordDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement NewWordDialogListener");
        }
    }

    public interface NewWordDialogListener {
        void handleNewDialogData(String word, String desc);
    }
}
