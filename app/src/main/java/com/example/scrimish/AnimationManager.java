package com.example.scrimish;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;

import java.util.ArrayList;

import static android.view.View.GONE;

public class AnimationManager {
    public static void animateFlip(final View v, final AnimatorListenerAdapter halfFlipCallback, AnimatorListenerAdapter fullFlipCallback) {
        final ObjectAnimator oa1 = ObjectAnimator.ofFloat(v, "scaleX", 1f, 0f);
        final ObjectAnimator oa2 = ObjectAnimator.ofFloat(v, "scaleX", 0f, 1f);
        oa1.setInterpolator(new DecelerateInterpolator());
        oa2.setInterpolator(new AccelerateDecelerateInterpolator());
        oa1.setDuration(450);
        oa2.setDuration(450);

        oa1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                halfFlipCallback.onAnimationEnd(animation);
                oa2.start();
            }
        });
        oa2.addListener(fullFlipCallback);
        oa1.start();
    }

    public static <T extends View> void animateExpand(ArrayList<T> mViews, boolean expand) {
        for (int i = 0; i < mViews.size(); i++) {
            int sign = expand ? 1 : 0;
            ObjectAnimator animation = ObjectAnimator.ofFloat(mViews.get(i), "translationY", sign * i * 16f);
            animation.setDuration(1000);
            animation.start();
        }
    }

    public static void animateFade(final View view) {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setStartOffset(500);
        fadeOut.setDuration(1000);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        view.startAnimation(fadeOut);
    }

    public static void rotate(View image, final Animation.AnimationListener callback) {
        RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(1000);
        rotate.setInterpolator(new AccelerateDecelerateInterpolator());

        rotate.setAnimationListener(callback);

        image.startAnimation(rotate);
    }
}
