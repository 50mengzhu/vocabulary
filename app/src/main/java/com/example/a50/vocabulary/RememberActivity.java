package com.example.a50.vocabulary;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.nineoldandroids.view.ViewHelper;

/**
 * Created by 50萌主 on 2017/8/7.
 */

public class RememberActivity extends Activity {

    RelativeLayout index;
//    LinearLayout wordCardEn;
//    LinearLayout wordCardCh;
    RelativeLayout layout;
    private ExplosionField mExplosionField;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
//            这个是设置系统UI的可见度
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            实际上这一行就是将状态栏的颜色设置为透明色
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }




//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.remember);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);




        layout = (RelativeLayout) findViewById(R.id.index);

//        final RefreshLayout refreshLayout = (RefreshLayout) findViewById(R.id.index);

        final RefreshLayout wordCardEn = (RefreshLayout) findViewById(R.id.wordCardEn);
        final RefreshLayout wordCardCh = (RefreshLayout) findViewById(R.id.wordCardCh);
//        wordCardEn = (LinearLayout) findViewById(R.id.wordCardEn);
//        wordCardCh = (LinearLayout) findViewById(R.id.wordCardCh);
//        layout = refreshLayout;

        if (wordCardEn != null) {
            // 刷新状态的回调
            wordCardEn.setRefreshListener(new RefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // 延迟3秒后刷新成功
                    wordCardEn.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            wordCardEn.refreshComplete();
//                            if (listView != null) {
//                                listView.setAdapter(new MainAdapter());
//                            }
                        }
                    }, 1000);
                }
            });
        }


        if (wordCardCh != null) {
            // 刷新状态的回调
            wordCardCh.setRefreshListener(new RefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // 延迟3秒后刷新成功
                    wordCardCh.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            wordCardCh.refreshComplete();
//                            if (listView != null) {
//                                listView.setAdapter(new MainAdapter());
//                            }
                        }
                    }, 1000);
                }
            });
        }

        QQRefreshHeader header  = new QQRefreshHeader(this);
        wordCardEn.setRefreshHeader(header);
//        wordCardCh.setRefreshHeader(header);
//        refreshLayout.autoRefresh();


        setCameraDistance();


        mExplosionField = ExplosionField.attach2Window(this);
        addListener(findViewById(R.id.index));


        wordCardEn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                wordCardCh.animate().alpha(1f).setDuration(500)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                wordCardCh.setVisibility(View.VISIBLE);
                            }
                        });

                ViewHelper.setRotationY(wordCardCh, 180f);

                Rotatable rotatable = new Rotatable.Builder(layout)
                        .sides(R.id.wordCardEn, R.id.wordCardCh)
                        .direction(Rotatable.ROTATE_Y)
                        .rotationCount(1)
                        .build();

                wordCardEn.animate().alpha(0f).setDuration(500)
                        .setListener(new AnimatorListenerAdapter() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                wordCardEn.setVisibility(View.GONE);
                            }
                        });

                rotatable.setTouchEnable(false);
                rotatable.rotate(Rotatable.ROTATE_Y, -180, 1500);

                return false;
            }
        });





//        index = (RelativeLayout) findViewById(R.id.index);
//        index.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mExplosionField.explode(v);
//                v.setOnClickListener(null);
//            }
//        });

        wordCardCh.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                wordCardEn.animate().alpha(1f).setDuration(500)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                wordCardEn.setVisibility(View.VISIBLE);
                            }
                        });

                Rotatable rotatable = new Rotatable.Builder(layout)
                        .sides(R.id.wordCardCh, R.id.wordCardEn)
                        .direction(Rotatable.ROTATE_Y)
                        .rotationCount(1)
                        .build();

                wordCardCh.animate().alpha(0f).setDuration(500)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                wordCardCh.setVisibility(View.GONE);
                            }
                        });

                rotatable.setTouchEnable(false);
                rotatable.rotate(Rotatable.ROTATE_Y, 0, 1500);
                return false;
            }
        });


    }

    private void addListener(View root) {
        if (root instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) root;
            for (int i = 0; i < parent.getChildCount(); i++) {
                addListener(parent.getChildAt(i));
            }
        } else {
            root.setClickable(true);
            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mExplosionField.explode(v);
                    v.setOnClickListener(null);
                }
            });
        }

    }

    private void setCameraDistance() {
        int distance = 10000;
        float scale = getResources().getDisplayMetrics().density * distance;
        layout.setCameraDistance(scale);
    }
}
