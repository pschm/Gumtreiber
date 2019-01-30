package de.psst.gumtreiber.map;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

/**
 * Support class for the MovableMarker
 */
class FadingImage extends AppCompatImageView implements Animation.AnimationListener {

    private Animation fadeOut, makeVisible, makeInvisible;

    public FadingImage(Context context) {
        super(context);

        fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setAnimationListener(this);
        fadeOut.setStartOffset(1200);
        fadeOut.setDuration(300);

        makeVisible = new AlphaAnimation(0, 1);
        makeVisible.setAnimationListener(this);
        makeVisible.setDuration(1);

        makeInvisible = new AlphaAnimation(1, 0);
        makeInvisible.setAnimationListener(this);
        makeInvisible.setDuration(1);
    }


    public void startFadeOut() {
        setImageAlpha(255);
        clearAnimation();
        startAnimation(fadeOut);
    }

    public void makeVisible() {
        if(getAnimation() == null && getVisibility() == VISIBLE) return;

        setImageAlpha(255);
        clearAnimation();
        startAnimation(makeVisible);
    }

    public void makeInvisible() {
        if(getAnimation() == null && getVisibility() == INVISIBLE) return;

        setImageAlpha(0);
        clearAnimation();
        startAnimation(makeVisible);
    }



    @Override
    public void onAnimationStart(Animation animation) {
        setVisibility(VISIBLE);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if(animation.equals(fadeOut) || animation.equals(makeInvisible)) setVisibility(GONE);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
