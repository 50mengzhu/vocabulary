package com.example.a50.vocabulary;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by 50萌主 on 2017/8/30.
 */

public class SwitchLayout extends ViewGroup {

    public SwitchLayout(Context context) {
        super(context);
        init();
    }

    public SwitchLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwitchLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childLeft = 0;
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; ++ i){
            final View childView = getChildAt(i);
            final int childWidth = childView.getMeasuredWidth();
            final int childHeight = childView.getMeasuredHeight();
            childView.layout(childLeft, 0, childWidth + childLeft, childHeight);
//            childLeft += childWidth;
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = getChildCount();

        for (int i = 0; i < count; ++ i){
//            if (getChildAt(i).getVisibility() != GONE){
                getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);

        }
    }


    public void init(){
        View enCardView = LayoutInflater.from(this.getContext()).inflate(R.layout.english, null);
        addView(enCardView);
        View cnCardView = LayoutInflater.from(this.getContext()).inflate(R.layout.translate, null);
//        cnCardView.setVisibility(INVISIBLE);
        addView(cnCardView);
    }
}
