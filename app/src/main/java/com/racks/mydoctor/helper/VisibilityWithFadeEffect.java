package com.racks.mydoctor.helper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

public class VisibilityWithFadeEffect {
    public void slideUpFadeIn(View view) {
        view.setAlpha(0f); // Start with transparent
        view.setTranslationY(view.getHeight()); // Start from below
        view.setVisibility(View.VISIBLE); // Make the view visible
        view.animate()
                .alpha(1f) // Fade in
                .translationY(0) // Slide up to its original position
                .setDuration(300) // Animation duration in milliseconds
                .setListener(null);
    }

    public void slideDownFadeOut(View view) {
        view.animate()
                .alpha(0f) // Fade out
                .translationY(view.getHeight()) // Slide down out of view
                .setDuration(300) // Animation duration in milliseconds
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.INVISIBLE); // Hide the view after animation
                    }
                });
    }

    public void slideDownFadeIn(View view) {
        view.setAlpha(0f); // Start with transparent
        view.setTranslationY(-view.getHeight()); // Start from above
        view.setVisibility(View.VISIBLE); // Make the view visible
        view.animate()
                .alpha(1f) // Fade in
                .translationY(0) // Slide down to its original position
                .setDuration(300) // Animation duration in milliseconds
                .setListener(null);
    }

    public void slideUpFadeOut(View view) {
        view.animate()
                .alpha(0f) // Fade out
                .translationY(-view.getHeight()) // Slide up out of view
                .setDuration(300) // Animation duration in milliseconds
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.INVISIBLE); // Hide the view after animation
                    }
                });
    }


}
