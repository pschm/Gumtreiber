package de.psst.gumtreiber.map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import de.psst.gumtreiber.R;
import de.psst.gumtreiber.data.Vector2;

/**
 * Visualisation for a point (Footstep) on an activity.
 */
public class MovableMarker {

    /**
     * Representation of different color compositions for the MovableMarker.
     */
    public enum Look {DEFAULT, FRIEND, BOT}

    private static final int LEFT_PRINT_RES_ID = R.drawable.footstep_left;
    private static final int RIGHT_PRINT_RES_ID = R.drawable.footstep_right;
    private static final int NAME_LABEL_RES_ID = R.drawable.banner;
    private static final int DEFAULT_SIZE = 64;
    private static final Vector2 LBL_CNTR_OFFSET = new Vector2(-60, -90); //Offset for centering the image (0,0 -> left top corner)
    private static final Vector2 IMG_CNTR_OFFSET = new Vector2(-30, -30);

    private Activity activity;
    private Thread moveThread;
    private boolean allowMovingThread;
    private Vector2 curPos, targetPos;

    private FadingImage nameImg;
    private ArrayList<FadingImage> leftPrints, rightPrints;
    private int lastLeftIndex, lastRightIndex;

    private Vector2 nameCntrOffset = LBL_CNTR_OFFSET; //Centering offset based on scaling
    private float stepSpeed = 30;
    private float animationSpeed = 2.2f;
    private float rotOffset = 0f;
    private float scaleStepSpeedFactor = 1f;

    private boolean alreadyDrawn = false;

    /**
     * Creates a new movable marker.
     * @param activity The Activity on which all ImageViews will be added to display them.
     * @param label Name label on top of the marker.
     */
    public MovableMarker(Activity activity, String label) {
        this(activity, label, Look.DEFAULT);
    }

    /**
     * Creates a new movable marker.
     * @param activity The Activity on witch all ImageViews will be added to display them.
     * @param label Name label on top of the marker.
     * @param look The color composition to change to. See {@link Look}.
     */
    public MovableMarker(final Activity activity, final String label, final Look look) {

        this.activity = activity;
        this.curPos = new Vector2();

        Runnable init = new Runnable() {
            @Override
            public void run() {
                ConstraintLayout layout = activity.findViewById(R.id.mapConstraintLayout);

                initAnimationImages(layout, look);
                initLabel(label, look);

                layout.addView(nameImg);
                //activity.addContentView(nameImg, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                setPosition(curPos);
                synchronized(this) {
                    notify();
                }
            }
        };


        if(isUiThread()) {
            init.run();
        } else {

            synchronized(init) {
                activity.runOnUiThread(init);
                try {
                    init.wait(); //Block thread until the stuff is done on ui thread
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void initAnimationImages(ConstraintLayout layout, Look look) {
        leftPrints = new ArrayList<>(4);
        rightPrints = new ArrayList<>(4);
        for(int i = 0; i < 8; i++) {
            FadingImage image = new FadingImage(activity);
            image.setAdjustViewBounds(true);
            image.setMaxWidth(DEFAULT_SIZE);

            if(look == Look.FRIEND) image.setColorFilter(ContextCompat.getColor(activity, R.color.colorStepsFriend));
            else if(look == Look.BOT) image.setColorFilter(ContextCompat.getColor(activity, R.color.colorStepsBot));
            else image.setColorFilter(ContextCompat.getColor(activity, R.color.colorStepsDefault));

            if(i == 3 || i == 7) image.setVisibility(View.VISIBLE);
            else image.setVisibility(View.GONE);

            if(i < 4) {
                image.setImageResource(LEFT_PRINT_RES_ID);
                leftPrints.add(image);
            } else {
                image.setImageResource(RIGHT_PRINT_RES_ID);
                rightPrints.add(image);
            }

            layout.addView(image);
            //activity.addContentView(image, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    private void initLabel(String label, Look look) {
        nameImg = new FadingImage(activity);
        nameImg.setAdjustViewBounds(true);
        nameImg.setMaxWidth(DEFAULT_SIZE * 2);

        if(look == Look.FRIEND) nameImg.setColorFilter(ContextCompat.getColor(activity, R.color.colorLabelFriend));
        else if(look == Look.BOT) nameImg.setColorFilter(ContextCompat.getColor(activity, R.color.colorLabelBot));
        else nameImg.setColorFilter(ContextCompat.getColor(activity, R.color.colorLabelDefault));

        changeLabel(label);
    }

    private boolean isUiThread() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Looper.getMainLooper().isCurrentThread();
        } else {
            return Looper.getMainLooper().equals(Looper.myLooper());
        }
    }

    private FadingImage getNextPrint(boolean leftPrint) {
        if(leftPrint) {
            lastLeftIndex = (lastLeftIndex + 1) % (leftPrints.size() - 1);
            return leftPrints.get(lastLeftIndex);
        } else {
            lastRightIndex = (lastRightIndex + 1) % (rightPrints.size() - 1);
            return rightPrints.get(lastRightIndex);
        }
    }

    private FadingImage getLeftFixPrint() {
        return leftPrints.get(3);
    }

    private FadingImage getRightFixPrint() {
        return rightPrints.get(3);
    }

    /**
     * Replaces the current label text.
     * @param label The new label text.
     */
    public void changeLabel(String label) {
        BitmapDrawable bd = writeTextOnDrawable(NAME_LABEL_RES_ID, label, 56);
        nameImg.setImageDrawable(bd);
    }

    /**
     * Changes the color appearance.
     * @param look The color composition to change to. See {@link Look}.
     */
    public void changeLook(Look look) {
        for(FadingImage image : leftPrints) {
            if(look == Look.FRIEND) image.setColorFilter(ContextCompat.getColor(activity, R.color.colorStepsFriend));
            else if(look == Look.BOT) image.setColorFilter(ContextCompat.getColor(activity, R.color.colorStepsBot));
            else image.setColorFilter(ContextCompat.getColor(activity, R.color.colorStepsDefault));
        }

        for(FadingImage image : rightPrints) {
            if(look == Look.FRIEND) image.setColorFilter(ContextCompat.getColor(activity, R.color.colorStepsFriend));
            else if(look == Look.BOT) image.setColorFilter(ContextCompat.getColor(activity, R.color.colorStepsBot));
            else image.setColorFilter(ContextCompat.getColor(activity, R.color.colorStepsDefault));
        }

        if(look == Look.FRIEND) nameImg.setColorFilter(ContextCompat.getColor(activity, R.color.colorLabelFriend));
        else if(look == Look.BOT) nameImg.setColorFilter(ContextCompat.getColor(activity, R.color.colorLabelBot));
        else nameImg.setColorFilter(ContextCompat.getColor(activity, R.color.colorLabelDefault));
    }


    /**
     * Makes this MovableMarker and its footsteps immediately invisible.
     * @param setVisible Set true if the marker should be visible.
     */
    public void setVisibility(boolean setVisible) {
        if(setVisible) {
            getLeftFixPrint().makeVisible();
            getRightFixPrint().makeVisible();
            nameImg.makeVisible();

        } else {
            for(FadingImage img : leftPrints) {
                img.makeInvisible();
            }
            for(FadingImage img : leftPrints) {
                img.makeInvisible();
            }
            nameImg.makeInvisible();
        }
    }

    /**
     * Stops all footstep animations.
     */
    public void stopFadeAnimation() {
        for(FadingImage img : leftPrints) {
            if(img.equals(getLeftFixPrint())) continue;
            img.makeInvisible();
        }
        for(FadingImage img : leftPrints) {
            if(img.equals(getRightFixPrint())) continue;
            img.makeInvisible();
        }
    }


    /**
     * Set the marker position on the given activity.
     * This will cancel a running transition from a {@link #moveTo} call.
     * @param x The visual x position of this marker, in pixels.
     * @param y The visual y position of this marker, in pixels.
     */
    public void setPosition(float x, float y) {
        allowMovingThread = false;
        curPos = new Vector2(x, y);

        nameImg.setX(x + nameCntrOffset.x);
        nameImg.setY(y + nameCntrOffset.y);

        x += IMG_CNTR_OFFSET.x;
        y += IMG_CNTR_OFFSET.y;

        getLeftFixPrint().setX(x);
        getLeftFixPrint().setY(y);
        getLeftFixPrint().makeVisible();

        getRightFixPrint().setX(x);
        getRightFixPrint().setY(y);
        getRightFixPrint().makeVisible();
    }

    private void setPosition(Vector2 position) {
        setPosition(position.x, position.y);
    }

    public Vector2 getPosition() {
        return curPos;
    }

    /**
     * Set the rotation offset angle in degree of the label.
     * @param offset Offset in degree.
     */
    public void setRotationOffset(float offset) {
        rotOffset = offset;
    }

    /**
     * Set the scale of the marker.
     * @param scaleFactor Scaling factor, 1 means no scaling.
     */
    public void setScale(float scaleFactor) {
        //if(scaleFactor >= 2) scaleStepSpeedFactor = scaleFactor * .5f;
        scaleStepSpeedFactor = scaleFactor; //TODO Checken ob das immer noch doof aussieht auf echter Karte

        for(FadingImage img : leftPrints) {
            img.setScaleX(scaleFactor);
            img.setScaleY(scaleFactor);
        }
        for(FadingImage img : rightPrints) {
            img.setScaleX(scaleFactor);
            img.setScaleY(scaleFactor);
        }

        nameImg.setScaleX(scaleFactor);
        nameImg.setScaleY(scaleFactor);
        nameCntrOffset = new Vector2(LBL_CNTR_OFFSET.x, LBL_CNTR_OFFSET.y * scaleFactor);
        nameImg.setX(curPos.x + nameCntrOffset.x);
        nameImg.setY(curPos.y + nameCntrOffset.y);
    }

    /**
     * Set the rotation/orientation of the marker.
     * The rotation offset set by {@link #setRotationOffset} is applied.
     * @param angle Rotation angle in degree.
     */
    public void setRotation(float angle) {
        getLeftFixPrint().setRotation(angle);
        getRightFixPrint().setRotation(angle);
        nameImg.setRotation(angle + rotOffset);
    }

    /**
     * Moves the marker to the given position on the activity. It will slowly transition to this position.
     * If called while a transition is running, it will override the old target position and will
     * start transition to the new given position from its current position.
     * The transition is also canceled if {@link #setPosition} is called.
     * @param x The visual target x position of this marker, in pixels.
     * @param y The visual target y position of this marker, in pixels.
     */
    public void moveTo(float x, float y) {
        targetPos = new Vector2(x, y);
        if(moveThread != null && moveThread.isAlive()) return;
        if(curPos.equals(targetPos)) return;

        allowMovingThread = true;
        moveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //This is were the magic happens!
                Log.d("MoveThread","Thread started!");
                long counter = 0;

                final FadingImage stepImgL = getLeftFixPrint();
                final FadingImage stepImgR = getRightFixPrint();

                boolean isSecondTime = false;
                boolean isLeftStep = false;
                float rotation;
                FadingImage stepImg;


                stepImgL.startFadeOut();
                stepImgR.startFadeOut();

                while(!curPos.equals(targetPos) && allowMovingThread) {
                    //Log.d("Loop1", "Counter: " + counter);
                    Vector2 direction = Vector2.sub(targetPos, curPos);
                    direction = direction.normalize();
                    curPos.x = curPos.x + direction.x * animationSpeed;
                    curPos.y = curPos.y + direction.y * animationSpeed;

                    if(targetPos.distance(curPos) > animationSpeed) {
                        if (counter >= stepSpeed * scaleStepSpeedFactor) {
                            //Log.d("curPos", curPos.toString());
                            rotation = direction.angle() + 90f;

                            stepImg = getNextPrint(isLeftStep);
                            stepImg.setX(curPos.x + IMG_CNTR_OFFSET.x);
                            stepImg.setY(curPos.y + IMG_CNTR_OFFSET.y);
                            stepImg.setRotation(rotation);
                            stepImg.startFadeOut();

                            isLeftStep = !isLeftStep;
                            counter = 0;
                        }

                    } else {

                        if(counter >= stepSpeed * scaleStepSpeedFactor && !isSecondTime) {
                            rotation = direction.angle() + 90f;

                            stepImgL.setX(targetPos.x + IMG_CNTR_OFFSET.x);
                            stepImgL.setY(targetPos.y + IMG_CNTR_OFFSET.y);
                            stepImgL.setRotation(rotation);

                            stepImgR.setX(targetPos.x + IMG_CNTR_OFFSET.x);
                            stepImgR.setY(targetPos.y + IMG_CNTR_OFFSET.y);
                            stepImgR.setRotation(rotation);

                            if (isLeftStep) stepImgL.makeVisible();
                            else stepImgR.makeVisible();

                            counter = 0;
                            isSecondTime = true;
                        }


                        if(counter >= stepSpeed * scaleStepSpeedFactor && isSecondTime) {

                            stepImgL.makeVisible();
                            stepImgR.makeVisible();

                            Log.d("2nd if", "curPos = targetPos");
                            curPos = targetPos;
                            counter = 0;
                        }

                    }

                    nameImg.setX(curPos.x + nameCntrOffset.x);
                    nameImg.setY(curPos.y + nameCntrOffset.y);
                    nameImg.setRotation(rotOffset);

                    try {
                        Thread.sleep(15);
                        counter++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                Log.d("MoveThread","Thread finished!");
            }
        });
        moveThread.start();

    }


    //https://stackoverflow.com/questions/11100428/add-text-to-image-in-android-programmatically
    private BitmapDrawable writeTextOnDrawable(int drawableId, String text, int size) {

        Bitmap bm = BitmapFactory.decodeResource(activity.getResources(), drawableId)
                .copy(Bitmap.Config.ARGB_8888, true);

        Typeface tf = Typeface.create("Helvetica", Typeface.NORMAL);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        paint.setTypeface(tf);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(convertToPixels(activity.getApplicationContext(), size));

        Rect textRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textRect);

        Canvas canvas = new Canvas(bm);

        //If the text is bigger than the canvas , reduce the font size
        while(textRect.width() >= (canvas.getWidth() - 200)) {    //the padding on either sides is considered as 4, so as to appropriately fit in the text
            size -= 4;
            paint.setTextSize(convertToPixels(activity, size));        //Scaling needs to be used for different dpi's
            paint.getTextBounds(text, 0, text.length(), textRect);
            //Log.d("Size-Loop", "" + size);
        }


        /* Old scaling code
        if(textRect.width() >= (canvas.getWidth() - 4))     //the padding on either sides is considered as 4, so as to appropriately fit in the text
            paint.setTextSize(convertToPixels(activity, 16));        //Scaling needs to be used for different dpi's
        */

        //Calculate the positions
        int xPos = (canvas.getWidth() / 2) - 2;     //-2 is for regulating the x position offset

        //"- ((paint.descent() + paint.ascent()) / 2)" is the distance from the baseline to the center.
        int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2)) ;

        canvas.drawText(text, xPos, yPos, paint);

        return new BitmapDrawable(activity.getResources(), bm);
    }

    private static int convertToPixels(Context context, int nDP) {
        final float conversionScale = context.getResources().getDisplayMetrics().density;

        return (int) ((nDP * conversionScale) + 0.5f) ;
    }

    public boolean isAlreadyDrawn() {
        return alreadyDrawn;
    }

    public void setAlreadyDrawn(boolean alreadyDrawn) {
        this.alreadyDrawn = alreadyDrawn;
    }
}