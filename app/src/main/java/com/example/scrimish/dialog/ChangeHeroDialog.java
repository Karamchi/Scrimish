package com.example.scrimish.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.LayoutRes;

import com.example.scrimish.R;
import com.example.scrimish.model.Carta;
import com.example.scrimish.view.CardView;
import com.example.scrimish.view.MazoView;

public class ChangeHeroDialog extends AlmostSingletonDialog {

    private OnFinishListener mListener;

    private @LayoutRes int[] mViews = {R.id.king_1, R.id.king_2, R.id.king_3, R.id.king_4};

    public ChangeHeroDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.change_hero_dialog);
        setListeners();
    }

    private void setListeners() {
        for (int i = 0; i < mViews.length; i++) {
            final MazoView.King king = MazoView.King.values()[i];
            CardView view = findViewById((mViews[i]));
            view.setKing(king);
            view.setCard(new Carta(Carta.Type.KING));
            view.showFront(true);

            findViewById(mViews[i]).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setSelected(true);
                    if (mListener != null) {
                        dismiss();
                        mListener.onFinish(king);
                    }
                }
            });
        }
    }

    public void setOnFinishListener(OnFinishListener listener) {
        mListener = listener;
    }

    public interface OnFinishListener {
        void onFinish(MazoView.King k);
    }
}
