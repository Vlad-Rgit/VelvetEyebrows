package com.example.velveteyebrows.services;

import android.text.Editable;
import android.text.TextWatcher;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RemoveEmptyErrorWatcher implements TextWatcher {

    private final TextInputLayout _textInputLayout;
    private final TextInputEditText _editText;

    public RemoveEmptyErrorWatcher(TextInputLayout textInputLayout, TextInputEditText editText) {
        _textInputLayout = textInputLayout;
        _editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(!_editText.getText().toString().trim().isEmpty()){
            _textInputLayout.setError(null);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
