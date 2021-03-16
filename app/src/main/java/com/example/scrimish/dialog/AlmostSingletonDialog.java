package com.example.scrimish.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;

public abstract class AlmostSingletonDialog extends Dialog {

    private static AlmostSingletonDialog sCurrentshowingInstance;

    public AlmostSingletonDialog(Context context) {
        super(context);
    }

    @Override
    public void show() {
        if (sCurrentshowingInstance != null) return;
        sCurrentshowingInstance = this;
        super.show();
    }

    @Override
    protected void onStop() {
        sCurrentshowingInstance = null;
        super.onStop();
    }

}
