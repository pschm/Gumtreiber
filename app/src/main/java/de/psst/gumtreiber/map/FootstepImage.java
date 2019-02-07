package de.psst.gumtreiber.map;

import android.content.Context;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import androidx.appcompat.widget.AppCompatImageView;

/**
 * Support class for the MovableMarker
 */
class FootstepImage extends AppCompatImageView implements Animation.AnimationListener {

    private Animation fadeOut, makeVisible;

    public FootstepImage(Context context) {
        super(context);

        fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setAnimationListener(this);
        fadeOut.setStartOffset(1200);
        fadeOut.setDuration(300);

        makeVisible = new AlphaAnimation(0, 1);
        makeVisible.setAnimationListener(this);
        makeVisible.setDuration(1);
    }


    public void startFadeOut() {
        setImageAlpha(255);
        clearAnimation();
        startAnimation(fadeOut);
    }

    public void makeVisible() {
        setImageAlpha(255);
        clearAnimation();
        startAnimation(makeVisible);
    }



    @Override
    public void onAnimationStart(Animation animation) {
        setVisibility(VISIBLE);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if(animation.equals(fadeOut)) setVisibility(GONE);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
