package com.example.scrimish.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;

import com.example.scrimish.R;
import com.example.scrimish.model.Carta;

@SuppressWarnings("deprecation")
public class CardView extends LinearLayout {

    private boolean mFront;
    private MazoView.King mKing;
    private @DrawableRes int drawableFront;
    private @DrawableRes int drawableBack;
    private Carta mCarta;


    public CardView(Context context) {
        super(context);
        init(context);
    }

    public CardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.card_view, this);
    }

    public void setKing(MazoView.King king) {
        mKing = king;
        if (mCarta == null)
            mCarta = new Carta(Carta.Type.UNKNOWN);
        setCard(mCarta);
    }

    public void setCard(Carta card) {
        drawableFront = card.getImage(mKing);
        drawableBack = card.getBack(mKing);
        mCarta = card;
        showFront(mFront);
    }

    @Override
    public void setSelected(boolean selected) {
        findViewById(R.id.overlay).setVisibility(selected ? VISIBLE : GONE);
        findViewById(R.id.overlay).setBackgroundColor(Color.WHITE);
        super.setSelected(selected);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setAllParentsClip(this, false);
    }

    private void setAllParentsClip(View view, boolean enabled) {
        while (view.getParent()!= null && view.getParent() instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view.getParent();
            viewGroup.setClipChildren(enabled);
            viewGroup.setClipToPadding(enabled);
            view = viewGroup;
        }
    }

    public void showFront(boolean front) {
        mFront = front;
        @DrawableRes int frontD = front ? drawableFront : drawableBack;
        ((ImageView) findViewById(R.id.card)).setImageDrawable(getResources().getDrawable(frontD));
        findViewById(R.id.progress_bar).setVisibility(front && mCarta.type.equals(Carta.Type.UNKNOWN) ? VISIBLE : GONE);
    }

}
