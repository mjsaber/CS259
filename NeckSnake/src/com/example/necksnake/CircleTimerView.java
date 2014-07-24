package com.example.necksnake;

import android.content.Context;
//import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


public class CircleTimerView extends View {

    private int mRedColor;
    private int mWhiteColor;
    private long mIntervalTime = 0;
    private long mIntervalStartTime = -1;
    private long mMarkerTime = -1;
    private long mCurrentIntervalTime = 0;
    private long mAccumulatedTime = 0;
    private boolean mPaused = false;
    private boolean mAnimate = false;
    private static float mCircleXCenterLeftPadding = 0;
    private static float mStrokeSize = 4;
    private static float mDiamondStrokeSize = 12;
    private static float mMarkerStrokeSize = 2;
    private final Paint mPaint = new Paint();
    private final Paint mFill = new Paint();
    private final RectF mArcRect = new RectF();
    private float mRectHalfWidth = 6f;
    private Resources mResources;
    private float mRadiusOffset;   // amount to remove from radius to account for markers on circle
    private float mScreenDensity;

    // Class has 2 modes:
    // Timer mode - counting down. in this mode the animation is counter-clockwise and stops at 0
    // Stop watch mode - counting up - in this mode the animation is clockwise and will keep the
    //                   animation until stopped.
    private boolean mTimerMode = true; // default is stop watch view

    public CircleTimerView(Context context) {
        this(context, null);
    }

    public CircleTimerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void setIntervalTime(long t) {
        if (mAccumulatedTime == 0) {
        	mIntervalTime = t;
        	postInvalidate();
        }    
    }

    public void setMarkerTime(long t) {
        mMarkerTime = t;
        postInvalidate();
    }

    public void reset() {
        mIntervalStartTime = -1;
        mMarkerTime = -1;
        postInvalidate();
    }
    public void startIntervalAnimation() {
        mIntervalStartTime = Utils.getTimeNow();
        mAccumulatedTime = GlobalParameters.accumulatedTime;
        //mAccumulatedTime =  (long) Math.floor(GlobalParameters.startNew - GlobalParameters.startTime)/1000*1000;
        mAnimate = true;
        invalidate();
        mPaused = false;
    }
    public void stopIntervalAnimation() {
        mAnimate = false;
        mIntervalStartTime = -1;
        mAccumulatedTime = 0;
    }

    public boolean isAnimating() {
        return (mIntervalStartTime != -1);
    }

    public void pauseIntervalAnimation() {
        mAnimate = false;
       // mAccumulatedTime += Utils.getTimeNow() - mIntervalStartTime;
        mPaused = true;
    }

    public void abortIntervalAnimation() {
        mAnimate = false;
    }

    public void setPassedTime(long time, boolean drawRed) {
        // The onDraw() method checks if mIntervalStartTime has been set before drawing any red.
        // Without drawRed, mIntervalStartTime should not be set here at all, and would remain at -1
        // when the state is reconfigured after exiting and re-entering the application.
        // If the timer is currently running, this drawRed will not be set, and will have no effect
        // because mIntervalStartTime will be set when the thread next runs.
        // When the timer is not running, mIntervalStartTime will not be set upon loading the state,
        // and no red will be drawn, so drawRed is used to force onDraw() to draw the red portion,
        // despite the timer not running.
        mCurrentIntervalTime = mAccumulatedTime = time;
        if (drawRed) {
            mIntervalStartTime = Utils.getTimeNow();
        }
        postInvalidate();
    }



    private void init(Context c) {

        mResources = c.getResources();
        mCircleXCenterLeftPadding = (mResources.getDimension(R.dimen.timer_circle_width)
                - mResources.getDimension(R.dimen.timer_circle_diameter)) / 2;
        mStrokeSize = mResources.getDimension(R.dimen.circletimer_circle_size);
        mDiamondStrokeSize = mResources.getDimension(R.dimen.circletimer_diamond_size);
        mMarkerStrokeSize = mResources.getDimension(R.dimen.circletimer_marker_size);
        mRadiusOffset = Utils.calculateRadiusOffset(
                mStrokeSize, mDiamondStrokeSize, mMarkerStrokeSize);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mWhiteColor = mResources.getColor(R.color.clock_white);
        mRedColor = mResources.getColor(R.color.clock_red);
        mScreenDensity = mResources.getDisplayMetrics().density;
        mFill.setAntiAlias(true);
        mFill.setStyle(Paint.Style.FILL);
        mFill.setColor(mRedColor);
        mRectHalfWidth = mDiamondStrokeSize / 2f;
    }

    public void setTimerMode(boolean mode) {
        mTimerMode = mode;
    }

    @Override
    public void onDraw(Canvas canvas) {
        int xCenter = getWidth() / 2 + 1;
        int yCenter = getHeight() / 2;

        mPaint.setStrokeWidth(mStrokeSize);
        float radius = Math.min(xCenter, yCenter) - mRadiusOffset;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            xCenter = (int) (radius + mRadiusOffset);
            if (mTimerMode) {
                xCenter += mCircleXCenterLeftPadding;
            }
        }

        if (mIntervalStartTime == -1) {
            // just draw a complete white circle, no red arc needed
            mPaint.setColor(mWhiteColor);
            canvas.drawCircle (xCenter, yCenter, radius, mPaint);
            if (mTimerMode) {
                drawRedDiamond(canvas, 0f, xCenter, yCenter, radius);
            }
        } else {
            if (mAnimate) { 
                mCurrentIntervalTime = Utils.getTimeNow() - mIntervalStartTime + mAccumulatedTime;
            }
            //draw a combination of red and white arcs to create a circle
            mArcRect.top = yCenter - radius;
            mArcRect.bottom = yCenter + radius;
            mArcRect.left =  xCenter - radius;
            mArcRect.right = xCenter + radius;
            float redPercent = (float)mCurrentIntervalTime / (float)mIntervalTime;
            // prevent timer from doing more than one full circle
            redPercent = (redPercent > 1 && mTimerMode) ? 1 : redPercent;

            float whitePercent = 1 - (redPercent > 1 ? 1 : redPercent);
            // draw red arc here
            mPaint.setColor(mRedColor);
            if (mTimerMode){
                canvas.drawArc (mArcRect, 270, - redPercent * 360 , false, mPaint);
            } else {
                canvas.drawArc (mArcRect, 270, + redPercent * 360 , false, mPaint);
            }

            // draw white arc here
            mPaint.setStrokeWidth(mStrokeSize);
            mPaint.setColor(mWhiteColor);
            if (mTimerMode) {
                canvas.drawArc(mArcRect, 270, + whitePercent * 360, false, mPaint);
            } else {
                canvas.drawArc(mArcRect, 270 + (1 - whitePercent) * 360,
                        whitePercent * 360, false, mPaint);
            }

            if (mMarkerTime != -1 && radius > 0 && mIntervalTime != 0) {
                mPaint.setStrokeWidth(mMarkerStrokeSize);
                float angle = (float)(mMarkerTime % mIntervalTime) / (float)mIntervalTime * 360;
                // draw 2dips thick marker
                // the formula to draw the marker 1 unit thick is:
                // 180 / (radius * Math.PI)
                // after that we have to scale it by the screen density
                canvas.drawArc (mArcRect, 270 + angle, mScreenDensity *
                        (float) (360 / (radius * Math.PI)) , false, mPaint);
            }
            drawRedDiamond(canvas, redPercent, xCenter, yCenter, radius);
        }
        if (mAnimate) {
            invalidate();
        }
   }

    protected void drawRedDiamond(
            Canvas canvas, float degrees, int xCenter, int yCenter, float radius) {
        mPaint.setColor(mRedColor);
        float diamondPercent;
        if (mTimerMode) {
            diamondPercent = 270 - degrees * 360;
        } else {
            diamondPercent = 270 + degrees * 360;
        }

        canvas.save();
        final double diamondRadians = Math.toRadians(diamondPercent);
        canvas.translate(xCenter + (float) (radius * Math.cos(diamondRadians)),
                yCenter + (float) (radius * Math.sin(diamondRadians)));
        canvas.rotate(diamondPercent + 45f);
        canvas.drawRect(-mRectHalfWidth, -mRectHalfWidth, mRectHalfWidth, mRectHalfWidth, mFill);
        canvas.restore();
    }

//    public static final String PREF_CTV_PAUSED  = "_ctv_paused";
//    public static final String PREF_CTV_INTERVAL  = "_ctv_interval";
//    public static final String PREF_CTV_INTERVAL_START = "_ctv_interval_start";
//    public static final String PREF_CTV_CURRENT_INTERVAL = "_ctv_current_interval";
//    public static final String PREF_CTV_ACCUM_TIME = "_ctv_accum_time";
//    public static final String PREF_CTV_TIMER_MODE = "_ctv_timer_mode";
//    public static final String PREF_CTV_MARKER_TIME = "_ctv_marker_time";
//
//    // Since this view is used in multiple places, use the key to save different instances
//    public void writeToSharedPref(SharedPreferences prefs, String key) {
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putBoolean (key + PREF_CTV_PAUSED, mPaused);
//        editor.putLong (key + PREF_CTV_INTERVAL, mIntervalTime);
//        editor.putLong (key + PREF_CTV_INTERVAL_START, mIntervalStartTime);
//        editor.putLong (key + PREF_CTV_CURRENT_INTERVAL, mCurrentIntervalTime);
//        editor.putLong (key + PREF_CTV_ACCUM_TIME, mAccumulatedTime);
//        editor.putLong (key + PREF_CTV_MARKER_TIME, mMarkerTime);
//        editor.putBoolean (key + PREF_CTV_TIMER_MODE, mTimerMode);
//        editor.apply();
//    }
//
//    public void readFromSharedPref(SharedPreferences prefs, String key) {
//        mPaused = prefs.getBoolean(key + PREF_CTV_PAUSED, false);
//        mIntervalTime = prefs.getLong(key + PREF_CTV_INTERVAL, 0);
//        mIntervalStartTime = prefs.getLong(key + PREF_CTV_INTERVAL_START, -1);
//        mCurrentIntervalTime = prefs.getLong(key + PREF_CTV_CURRENT_INTERVAL, 0);
//        mAccumulatedTime = prefs.getLong(key + PREF_CTV_ACCUM_TIME, 0);
//        mMarkerTime = prefs.getLong(key + PREF_CTV_MARKER_TIME, -1);
//        mTimerMode = prefs.getBoolean(key + PREF_CTV_TIMER_MODE, false);
//        mAnimate = (mIntervalStartTime != -1 && !mPaused);
//    }
//
//    public void clearSharedPref(SharedPreferences prefs, String key) {
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.remove (Stopwatches.PREF_START_TIME);
//        editor.remove (Stopwatches.PREF_ACCUM_TIME);
//        editor.remove (Stopwatches.PREF_STATE);
//        editor.remove (key + PREF_CTV_PAUSED);
//        editor.remove (key + PREF_CTV_INTERVAL);
//        editor.remove (key + PREF_CTV_INTERVAL_START);
//        editor.remove (key + PREF_CTV_CURRENT_INTERVAL);
//        editor.remove (key + PREF_CTV_ACCUM_TIME);
//        editor.remove (key + PREF_CTV_MARKER_TIME);
//        editor.remove (key + PREF_CTV_TIMER_MODE);
//        editor.apply();
//    }
}
