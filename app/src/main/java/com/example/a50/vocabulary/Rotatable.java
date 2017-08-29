package com.example.a50.vocabulary;

/**
 * Created by 50萌主 on 2017/8/7.
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.IntDef;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.DisplayMetrics;
import android.util.Property;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.CycleInterpolator;

import java.util.ArrayList;


public class Rotatable implements View.OnTouchListener {
    private static final int NULL_INT = -1;
    private final int FIT_ANIM_TIME = 300;

    public static final int DEFAULT_ROTATE_ANIM_TIME = 500;
    public static final int ROTATE_BOTH = 0;
    public static final int  ROTATE_X = 1;
    public static final int ROTATE_Y = 2;



    @IntDef({ROTATE_X, ROTATE_Y, ROTATE_BOTH})
    public @interface Direction{

    }

    public static final int FRONT_VIEW = 3;
    public static final int BACK_VIEW = 4;

    @IntDef({FRONT_VIEW, BACK_VIEW})
    public @interface Side{

    }

    private RotationListener rotationListener;
    private View rootView, frontView, backView;

    private boolean touchEnable = true;
    private boolean shouldSwapViews = false;

    private int rotation;
    private int screenWidth = NULL_INT, screenHeight = NULL_INT;
    private int currentVisibleView = FRONT_VIEW;

    private float rotationCount;
    private float rotationDistance;
    private float oldX, oldY, currentX, currentY;
    private float currentXRotation = 0, currentYRotation = 0;
    private float maxDistanceX = NULL_INT, maxDistanceY = NULL_INT;
    private float defaultPivotX = NULL_INT, defaultPivotY = NULL_INT;

    private Rotatable(Builder builder){
        this.rootView = builder.root;
        this.defaultPivotX = rootView.getPivotX();
        this.defaultPivotY = rootView.getPivotY();
        this.rotationListener = builder.listener;

        if (builder.pivotX != NULL_INT){
            this.rootView.setPivotX(builder.pivotX);
        }

        if (builder.pivotY != NULL_INT){
            this.rootView.setPivotY(builder.pivotY);
        }

        if (builder.frontId != NULL_INT){
            this.backView = rootView.findViewById(builder.backId);
        }

        this.rotation = builder.rotation;
        this.rotationCount = builder.rotationCount;
        this.rotationDistance = builder.rotationDistance;
        this.shouldSwapViews = frontView != null && backView != null;

        rootView.setOnTouchListener(this);
    }

    public void drop(){
        rootView.setPivotX(defaultPivotX);
        rootView.setPivotY(defaultPivotY);
        rootView.setOnClickListener(null);
        rootView = null;
        frontView = null;
        backView = null;
    }

    public void setDirection(@Direction int direction){
        if (!isRotationValid(direction)){
            throw new IllegalArgumentException("Cannot specify given value as rotation direction!");
        }
        this.rotation = direction;
    }

    public void setTouchEnable(boolean enable){
        this.touchEnable = enable;
    }

    public boolean isTouchEnable(){
        return touchEnable;
    }

    public void orientationChanged(int newOrientation){
        if (screenWidth == NULL_INT){
            calculateScreenDimensions();
        }

        measureScreenUpToOrientation(newOrientation);
        maxDistanceX = NULL_INT;
        maxDistanceY = NULL_INT;
    }

    public void takeAttention(){
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(rootView, View.ROTATION_X, 10);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(rootView, View.ROTATION_Y, -10);
        AnimatorSet set = new AnimatorSet();
        set.setDuration(DEFAULT_ROTATE_ANIM_TIME);
        set.setInterpolator(new CycleInterpolator(0.8f));
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                rootView.animate().rotationX(0).rotationY(0).setDuration(FIT_ANIM_TIME)
                        .setInterpolator(new FastOutSlowInInterpolator()).start();
            }
        });
        set.playTogether(animatorX, animatorY);
        set.start();
    }

    public void rotate(int direction, float degree){
        rotate(direction, degree, DEFAULT_ROTATE_ANIM_TIME);
    }

    public void rotate(int direction, float degree, int duration){
        rotate(direction, degree, duration, null);
    }

    public void rotate(int direction, float degree, int duration, Animator.AnimatorListener listener){
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(duration);
        animatorSet.setInterpolator(new FastOutSlowInInterpolator());

        ArrayList<Animator> animators = new ArrayList<>();

        if (direction == ROTATE_X || direction == ROTATE_Y){
            animators.add(getAnimatorForProperty(View.ROTATION_Y, direction, degree));
        }

        if (listener != null){
            animatorSet.addListener(listener);
        }

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                updateRotationValues(true);
            }
        });

        animatorSet.playTogether(animators);
        animatorSet.start();
    }

    public void  rotateOnce(){
        float toDegree;
        if (rotation == ROTATE_X){
            toDegree = rootView.getRotationX();
        }else if (rotation == ROTATE_Y){
            toDegree = rootView.getRotationY();
        }else{
            toDegree = rootView.getRotation();
        }

        toDegree += 180;
        rotate(rotation, toDegree);
    }

    public boolean isFront(){
        return getCurrentVisibleView() == FRONT_VIEW;
    }

    public
    @Side
    int getCurrentVisibleView(){
        return currentVisibleView;
    }

    public float getCurrentXRotation(){
        return currentXRotation;
    }

    public float getCurrentYRotation(){
        return currentYRotation;
    }

    private Animator getAnimatorForProperty(Property property, final int direction, float degree){
        ObjectAnimator animator = ObjectAnimator.ofFloat(rootView, property, degree);

        if (shouldSwapViews){
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    updateRotationValues(false);
                    swapViews(direction);
                }
            });
        }
        return animator;
    }

    private void updateRotationValues(boolean notifyListener){
        currentXRotation = rootView.getRotationX();
        currentYRotation = rootView.getRotationY();

        if (notifyListener){
            notifyListenerRotationChanged();
        }
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (touchEnable){
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:{
                    restoreOldPositions(event);
                    break;
                }
                case MotionEvent.ACTION_MOVE:{
                    restoreNewPositions(event);
                    handleRotation();

                    if (shouldSwapViews){
                        swapViews(rotation);
                    }
                    notifyListenerRotationChanged();
                    break;
                }
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:{
                    fitRotation();
                    break;
                }
            }
            return true;
        }else {
            return false;
        }
    }

    private void restoreOldPositions(MotionEvent event){
        if (shouldRotateX()){
            oldY = getYValue(event.getRawY());
        }

        if (shouldRotateY()) {
            oldX = getXValue(event.getRawX());
        }
    }

    private float getXValue(float rawX){
        if (rotationCount != NULL_INT && maxDistanceX != NULL_INT){
            return rawX * rotationCount * 180 / maxDistanceX;
        }

        if (rotationDistance != NULL_INT){
            return rawX * 180 / rotationDistance;
        }

        return rawX;
    }

    private float getYValue(float rawY){
        if (rotationCount != NULL_INT && maxDistanceY != NULL_INT){
            return rawY * rotationCount * 180 / maxDistanceY;
        }

        if (rotationDistance != NULL_INT){
            return rawY * 180 / rotationDistance;
        }

        return rawY;
    }

    private void restoreNewPositions(MotionEvent event){
        if (shouldRotateX()) {
            if (rotationCount != NULL_INT && maxDistanceY == NULL_INT){
                maxDistanceY = (event.getRawY() - oldY) > 0 ? (getScreenHeight() - oldY) : oldY;
                oldY = getYValue(oldY);
            }
            currentY = getYValue(event.getRawY());
        }

        if (shouldRotateY()) {
            if (rotationCount != NULL_INT && maxDistanceX == NULL_INT){
                maxDistanceX = (event.getRawX() - oldX) > 0 ? (getScreenWidth() - oldX) : oldX;
                oldX = getXValue(oldX);
            }
            currentX = getXValue(event.getRawX());
        }
    }

    private int getScreenWidth(){
        if (screenWidth == NULL_INT){
            calculateScreenDimensions();
        }
        return screenWidth;
    }

    private int getScreenHeight(){
        if (screenHeight == NULL_INT){
            calculateScreenDimensions();
        }
        return screenHeight;
    }

    private void calculateScreenDimensions(){
        Display display = ((WindowManager) rootView.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        screenHeight = metrics.heightPixels;
        screenWidth = metrics.widthPixels;
    }

    private void measureScreenUpToOrientation(int screenOrientation){
        int tempWidth = screenWidth;
        int tempHeight = screenHeight;

        if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE){
            screenHeight = Math.min(tempHeight, tempWidth);
            screenWidth = Math.max(tempHeight, tempWidth);
        }else{
            screenWidth = Math.min(tempHeight, tempWidth);
            screenHeight = Math.max(tempHeight, tempWidth);
        }
    }

    private boolean shouldRotateX(){
        return rotation == ROTATE_X || rotation == ROTATE_BOTH;
    }

    private boolean shouldRotateY(){
        return rotation == ROTATE_Y || rotation == ROTATE_BOTH;
    }

    private void handleRotation(){
        if (shouldRotateX()){
            float newXRotation = (rootView.getRotationX() + (oldY - currentY)) % 360;
            rootView.setRotationX(newXRotation);
            currentXRotation = newXRotation;
            oldY = currentY;
        }

        if (shouldRotateY()){
            float newYRotation;
            if (isInFrontArea(currentXRotation)){
                newYRotation = (rootView.getRotationY() + (currentY - oldY)) % 360;
            }else{
                newYRotation = (rootView.getRotationY() - (currentY - oldY)) % 360;
            }

            rootView.setRotationY(newYRotation);
            currentYRotation = newYRotation;
            oldX = currentX;
        }
    }

    private boolean isInFrontArea(float value){
        return (-270 >= value && value >= -360)
                ||(-90 <= value && value <= 90)
                ||(270 <= value && value <= 360);
    }

    private void swapViews(int rotation){
        boolean isFront = false;
        if (rotation == ROTATE_Y){
            isFront = isInFrontArea(currentYRotation);

            if (!isInFrontArea(currentXRotation)){
                isFront = !isFront;
            }
        }

        if (rotation == ROTATE_X){
            isFront = isInFrontArea(currentXRotation);

            if (!isInFrontArea(currentYRotation)){
                isFront = !isFront;
            }
        }

        if (rotation == ROTATE_BOTH) {
            isFront = (currentXRotation > -90 && currentXRotation < 90) && (currentYRotation > -90 && currentYRotation < 90)

                    || (currentXRotation > -90 && currentXRotation < 90) && (currentYRotation > -360 && currentYRotation < -270)
                    || (currentXRotation > -360 && currentXRotation < -270) && (currentYRotation > -90 && currentYRotation < 90)

                    || (currentXRotation > -90 && currentXRotation < 90) && (currentYRotation > 270 && currentYRotation < 360)
                    || (currentXRotation > 270 && currentXRotation < 360) && (currentYRotation > -90 && currentYRotation < 90)

                    || (currentXRotation > 90 && currentXRotation < 270) && (currentYRotation > -270 && currentYRotation < -90)
                    || (currentXRotation > -270 && currentXRotation < -90) && (currentYRotation > 90 && currentYRotation < 270)

                    || (currentXRotation > 90 && currentXRotation < 270) && (currentYRotation > 90 && currentYRotation < 270)
                    || (currentXRotation > -270 && currentXRotation < -90) && (currentYRotation > -270 && currentYRotation < -90);
        }

        boolean shouldSwap = (isFront && currentVisibleView == BACK_VIEW) || (!isFront && currentVisibleView == FRONT_VIEW);
        if (shouldSwap){
            frontView.setVisibility(isFront ? View.VISIBLE : View.GONE);
            backView.setVisibility(isFront ? View.GONE : View.VISIBLE);
            currentVisibleView = isFront ? FRONT_VIEW : BACK_VIEW;
        }
    }

    private void notifyListenerRotationChanged(){
        if (rotationListener != null){
            rotationListener.onRotationChanged(currentXRotation, currentYRotation);
        }
    }

    private void fitRotation(){
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(FIT_ANIM_TIME);
        animatorSet.setInterpolator(new FastOutSlowInInterpolator());

        ArrayList<Animator> animators = new ArrayList<>();

        if (shouldRotateY()){
            animators.add(ObjectAnimator.ofFloat(rootView, View.ROTATION_Y, getRequiredRotation(rootView.getRotationY())));
        }

        if (shouldRotateX()){
            animators.add(ObjectAnimator.ofFloat(rootView, View.ROTATION_X, getRequiredRotation(rootView.getRotationX())));
        }

        animatorSet.playTogether(animators);
        animatorSet.addListener(new AnimatorListenerAdapter(){
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                updateRotationValues(true);
            }
        });

        animatorSet.start();
        maxDistanceX = NULL_INT;
        maxDistanceY = NULL_INT;
    }

    private float getRequiredRotation(float currentXRotation){
        float requiredRotation;
        if (currentXRotation < -270){
            requiredRotation = -360;
        }else if (currentXRotation < -90 && currentXRotation > -270){
            requiredRotation = -180;
        }else if (currentXRotation > -90 && currentXRotation < 90){
            requiredRotation = 0;
        }else if (currentXRotation > 90 && currentXRotation < 270){
            requiredRotation = 180;
        }else {
            currentXRotation = 360;
        }

        return currentXRotation;
    }

    public interface RotationListener{
        void onRotationChanged(float newRotationX, float newRotationY);
    }

    public static class Builder{
        private View root;
        private RotationListener listener;
        private int rotation = NULL_INT;
        private int frontId = NULL_INT;
        private int backId = NULL_INT;
        private int pivotX = NULL_INT;
        private int pivotY = NULL_INT;
        private float rotationCount = NULL_INT;
        private float rotationDistance = NULL_INT;

        public Builder(View viewToRotate){
            this.root = viewToRotate;
        }

        public Builder listener(RotationListener listener){
            this.listener = listener;
            return this;
        }

        public Builder sides(int frontId, int backId){
            this.frontId = frontId;
            this.backId = backId;
            return this;
        }

        public Builder direction(@Direction int rotation){
            this.rotation = rotation;
            return this;
        }

        public Builder rotationCount(float count){
            if (rotationDistance != NULL_INT){
                throw new IllegalArgumentException("You cannot specify both distance and count for rotation limitation.");
            }
            this.rotationCount = count;
            return this;
        }

        public Builder rotationDistance(float distance){
            if (rotationCount != NULL_INT){
                throw new IllegalArgumentException("You cannot specify both distance and count for rotation limitation.");
            }
            this.rotationDistance = distance;
            return this;
        }

        public Builder pivot(int pivotX, int pivotY){
            this.pivotX = pivotX;
            this.pivotY = pivotY;
            return this;
        }

        public Builder pivotX(int pivotX){
            this.pivotX = pivotX;
            return this;
        }

        public Builder pivotY(int pivotY){
            this.pivotY = pivotY;
            return this;
        }

        public Rotatable build(){
            if (rotation == NULL_INT && !isRotationValid(rotation)){
                throw new IllegalArgumentException("You cannot specify a direction.");
            }
            return new Rotatable(this);
        }
    }

    private static boolean isRotationValid(int value){
        return value == ROTATE_X || value == ROTATE_Y || value == ROTATE_BOTH;
    }
}
