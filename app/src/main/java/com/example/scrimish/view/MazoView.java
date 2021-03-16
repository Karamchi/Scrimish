package com.example.scrimish.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.example.scrimish.AnimationManager;
import com.example.scrimish.model.Carta;
import com.example.scrimish.R;

import java.util.ArrayList;

public class MazoView extends LinearLayout {

    private int[] views = {R.id.mazo_view_1, R.id.mazo_view_2, R.id.mazo_view_3, R.id.mazo_view_4, R.id.mazo_view_5};
    private ArrayList<CardView> mViews = new ArrayList<>();
    private int mTop = views[4];
    public boolean mIsAnimating;
    private ArrayList<Carta> cartas = new ArrayList<>();
    private int mStack = 5;
    private boolean mExpanded;

    public MazoView(Context context) {
        super(context);
        init(context);
    }

    public MazoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MazoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.mazo_view_2, this);
        for (int view : views) {
            mViews.add((CardView) findViewById(view));
        }
    }

    public void setKing(King king) {
        for (CardView v : mViews) {
            v.setKing(king);
        }
    }

    public void setCartas(ArrayList<Carta> cartas) {
        this.cartas = new ArrayList<>(cartas);
        setStack(cartas.size(), true);
        for (int i = 0; i < mStack; i++) {
            mViews.get(i).setCard(cartas.get(mStack - i - 1));
        }
    }

    public void setStack(final int stack, final boolean immediate) {
        if (mStack == stack) return;
        if (immediate) setSelected(false);

        mTop = stack > 0 ? views[stack - 1] : -1;

        for (int i = 0; i < stack; i++) {
            mViews.get(i).setVisibility(VISIBLE);
        }
        for (int i = stack; i < views.length; i++) {
            if (i == stack && !immediate) {
                AnimationManager.animateFade(mViews.get(stack));
            } else {
                mViews.get(i).setVisibility(GONE);
            }
        }
        mStack = stack;
    }

    public void showTop(Carta carta) {
        if (mTop == -1) return;
        ((CardView) findViewById(mTop)).setCard(carta);
        if (mIsAnimating) return;
        ((CardView) findViewById(mTop)).showFront(true);
    }

    public void showTop(boolean show) {
        if (mTop == -1) return;
        ((CardView) findViewById(mTop)).showFront(show);
    }

    @Override
    public void setSelected(boolean selected) {
        if (mTop == -1) return;
        if (mIsAnimating) return;
        findViewById(mTop).setSelected(selected);
    }

    @Override
    public boolean isSelected() {
        return mTop != -1 && findViewById(mTop).isSelected();
    }

    public void doAnimate(AnimatorListenerAdapter callback) {
        doAnimate(findViewById(mTop), callback);
    }

    private void doAnimate(final View v, AnimatorListenerAdapter callback) {
        AnimatorListenerAdapter halfFlipCallback = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mIsAnimating = false;
                showTop(true);
            }
        };

        AnimationManager.animateFlip(v, halfFlipCallback, callback);
        mIsAnimating = true;
    }

    public void expand(boolean expand) {
        if (expand != mExpanded) {
            mExpanded = expand;
            animateExpand(expand);
        }
    }

    public void animateExpand(boolean expand) {
        AnimationManager.animateExpand(mViews, expand);
    }

    public void showFrontOfAll(boolean show) {
        for (CardView v : mViews) {
            v.showFront(show);
        }
    }

    //These are for building the deck and won't be called later because the deck doesn't change
    public void addCard(Carta carta) {
        cartas.add(0, carta);
        setStack(mStack + 1, true);
        ((CardView) findViewById(mTop)).setCard(carta);
    }

    public void removeTop() {
        cartas.remove(0);
        setStack(mStack - 1, true);
    }

    public boolean isExpanded() {
        return mExpanded;
    }

    public enum King {
        EDER,
        MAIA,
        PALLEGINA,
        ALOTH
    }

    /*public interface NonRetardedLongClickListener extends OnLongClickListener {
        void onLongClickFinished();
    }

    @Override
    public void setOnClickListener(@Nullable final OnClickListener l) {
        super.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsLongClicking) {
                    mLongClickListener.onLongClickFinished();
                } else {
                    if (l!= null) l.onClick(view);
                }
            }
        });
    }

    public void setOnLongClickListener(final NonRetardedLongClickListener l) {
        mLongClickListener = new NonRetardedLongClickListener() {
            @Override
            public void onLongClickFinished() {
                mIsLongClicking = false;
                l.onLongClickFinished();
            }

            @Override
            public boolean onLongClick(View view) {
                l.onLongClick(view);
                return false;
            }
        };
        super.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mIsLongClicking = true;
                return mLongClickListener.onLongClick(view);
            }
        });
        if (!hasOnClickListeners()) {
            setOnClickListener(null);
        }
    }*/
}
