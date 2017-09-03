package com.example.a50.vocabulary;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by 50萌主 on 2017/9/1.
 */

public class MyViewPager extends ViewPager {
    private boolean canScroll;

    public MyViewPager(Context context) {
        super(context);
        canScroll = true;
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        canScroll = true;
    }

    public void setCanScroll(boolean isScroll){
        this.canScroll = isScroll;
    }

    public boolean isCanScroll() {
        return canScroll;
    }

    /*重写这个方法能够实现对viewPager的滑动进行控制, 如果允许滑动则直接继承父类的方法就好，若不允许滑动则是返回false使事件不在继续向下传递*/
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isCanScroll()){
            return super.onTouchEvent(ev);
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isCanScroll()){
            return super.onInterceptTouchEvent(ev);
        }
        return false;
    }
}
