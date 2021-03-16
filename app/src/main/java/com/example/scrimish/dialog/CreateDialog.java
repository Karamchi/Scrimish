package com.example.scrimish.dialog;

import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.example.scrimish.R;

public class CreateDialog extends AlmostSingletonDialog {
    private OnFinishListener mListener;
    private EditText editText;

    public CreateDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.create_dialog);
        editText = findViewById(R.id.dialog_txt_edit);
        editText.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        setListeners();
    }

    private void setListeners() {
        findViewById(R.id.dialog_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    dismiss();
                    mListener.onFinish(editText.getText().toString());
                }
            }
        });
    }

    public void setOnFinishListener(OnFinishListener listener) {
        mListener = listener;
    }

    public interface OnFinishListener {
        void onFinish(String s);
    }
}
