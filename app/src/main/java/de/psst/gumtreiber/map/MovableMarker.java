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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import de.psst.gumtreiber.data.Vector2;
import de.psst.gumtreiber.*;

/**
 * Visualisation for a point (Footstep) on an activity.
 */
public class MovableMarker {

    private static final int LEFT_PRINT_RES_ID = R.drawable.footstep_left;
    private static final int RIGHT_PRINT_RES_ID = R.drawable.footstep_right;
    private static final int NAME_LABEL_RES_ID = R.drawable.banner;
    private static final int DEFAULT_SIZE = 64;
    private static final Vector2 DEFAULT_NAME_OFFSET = new Vector2(-30, -60);

    private Activity activity;
    private Thread moveThread;
    private boolean allowMovingThread;
    private Vector2 curPos, targetPos;

    private ImageView nameImg;
    private ArrayList<FootstepImage> leftPrints, rightPrints;
    private int lastLeftIndex, lastRightIndex;

    private Vector2 nameOffset = DEFAULT_NAME_OFFSET;
    private float stepSpeed = 30;
    private float animationSpeed = 2.2f;
    private float rotOffset = 0f;
    private float scaleStepSpeedFactor = 1f;

    /**
     * Creates a new movable marker.
     * @param activity The Activity on which all ImageViews will be added to display them.
     * @param label Name label on top of the marker.
     */
    public MovableMarker(Activity activity, String label) {
        this(activity, label, false);
    }

    /**
     * Creates a new movable marker.
     * @param activity The Activity on witch all ImageViews will be added to display them.
     * @param label Name label on top of the marker.
     * @param markAsFriend Set to true to change the color appearance of the marker.
     */
    public MovableMarker(Activity activity, String label, boolean markAsFriend) {
        this.activity = activity;
        this.curPos = new Vector2();

        initAnimationImages(markAsFriend);
        initLabel(label, markAsFriend);

        activity.addContentView(nameImg, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        setPosition(curPos);
    }

    private void initAnimationImages(boolean markAsFriend) {
        leftPrints = new ArrayList<>(4);
        rightPrints = new ArrayList<>(4);
        for(int i = 0; i < 8; i++) {
            FootstepImage image = new FootstepImage(activity);
            image.setAdjustViewBounds(true);
            image.setMaxWidth(DEFAULT_SIZE);

            //TODO friend: to be continued

            if(i == 3 || i == 7) image.setVisibility(View.VISIBLE);
            else image.setVisibility(View.GONE);

            if(i < 4) {
                image.setImageResource(LEFT_PRINT_RES_ID);
                leftPrints.add(image);
            } else {
                image.setImageResource(RIGHT_PRINT_RES_ID);
                rightPrints.add(image);
            }

            activity.addContentView(image, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    private void initLabel(String label, boolean markAsFriend) {
        nameImg = new ImageView(activity);
        nameImg.setAdjustViewBounds(true);
        nameImg.setMaxWidth(DEFAULT_SIZE * 2);

        //TODO friend: to be continued

        changeLabel(label);
    }

    private FootstepImage getNextPrint(boolean leftPrint) {
        if(leftPrint) {
            lastLeftIndex = (lastLeftIndex + 1) % (leftPrints.size() - 1);
            return leftPrints.get(lastLeftIndex);
        } else {
            lastRightIndex = (lastRightIndex + 1) % (rightPrints.size() - 1);
            return rightPrints.get(lastRightIndex);
        }
    }

    private FootstepImage getLeftFixPrint() {
        return leftPrints.get(3);
    }

    private FootstepImage getRightFixPrint() {
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
     * @param markAsFriend Set true if the marker symbols a friend.
     */
    public void markAsFriend(boolean markAsFriend) {
        //TODO to be continued
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

        getLeftFixPrint().setX(x);
        getLeftFixPrint().setY(y);
        getLeftFixPrint().makeVisible();

        getRightFixPrint().setX(x);
        getRightFixPrint().setY(y);
        getRightFixPrint().makeVisible();

        nameImg.setX(x + nameOffset.x);
        nameImg.setY(y + nameOffset.y);
    }

    private void setPosition(Vector2 position) {
        setPosition(position.x, position.y);
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

        for(FootstepImage img : leftPrints) {
            img.setScaleX(scaleFactor);
            img.setScaleY(scaleFactor);
        }
        for(FootstepImage img : rightPrints) {
            img.setScaleX(scaleFactor);
            img.setScaleY(scaleFactor);
        }

        nameImg.setScaleX(scaleFactor);
        nameImg.setScaleY(scaleFactor);
        nameOffset = new Vector2(DEFAULT_NAME_OFFSET.x, DEFAULT_NAME_OFFSET.y * scaleFactor);
        nameImg.setX(curPos.x + nameOffset.x);
        nameImg.setY(curPos.y + nameOffset.y);
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

                final FootstepImage stepImgL = getLeftFixPrint();
                final FootstepImage stepImgR = getRightFixPrint();

                boolean isSecondTime = false;
                boolean isLeftStep = false;
                float rotation;
                FootstepImage stepImg;


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
                            stepImg.setX(curPos.x);
                            stepImg.setY(curPos.y);
                            stepImg.setRotation(rotation);
                            stepImg.startFadeOut();

                            isLeftStep = !isLeftStep;
                            counter = 0;
                        }

                    } else {

                        if(counter >= stepSpeed * scaleStepSpeedFactor && !isSecondTime) {
                            rotation = direction.angle() + 90f;

                            stepImgL.setX(targetPos.x);
                            stepImgL.setY(targetPos.y);
                            stepImgL.setRotation(rotation);

                            stepImgR.setX(targetPos.x);
                            stepImgR.setY(targetPos.y);
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

                    nameImg.setX(curPos.x + nameOffset.x);
                    nameImg.setY(curPos.y + nameOffset.y);
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

}