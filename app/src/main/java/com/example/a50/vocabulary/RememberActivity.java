package com.example.a50.vocabulary;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nineoldandroids.view.ViewHelper;

import java.io.Serializable;
import java.sql.Ref;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 50萌主 on 2017/8/7.
 */

public class RememberActivity extends Activity {
    /*version 1中多余的代码*/
//    RelativeLayout layout;
    private ExplosionField mExplosionField;
    ImageButton addItem;


    /*version 2的版本的新增代码*/
//    ViewPager viewPager;
    MyViewPager viewPager;
    PagerAdapter pagerAdapter;
    List<View> viewPages;
    List<Word> words;
    ViewPager layout;

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


        /*version 2的版本的新增代码*/
        viewPages = new ArrayList<>();
        words = new ArrayList<>();

        viewPager = (MyViewPager) findViewById(R.id.indexViewPager);
        layout = viewPager;


        addItem = (ImageButton) findViewById(R.id.addItem);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RememberActivity.this, "click", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RememberActivity.this, AddActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("words", (Serializable) words);
                intent.putExtras(bundle);
                startActivityForResult(intent, 10086);
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10086 && resultCode == RESULT_OK){
            Bundle bundle = data.getExtras();
            words = (List<Word>) bundle.getSerializable("words");
        }
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

    @Override
    protected void onResume() {
        super.onResume();
        for (Word word : words){
            if (!word.getFlag()){
                word.setFlag(true);
                SwitchLayout switchLayout = new SwitchLayout(this);
                viewPages.add(switchLayout);

                final RefreshLayout wordCardEn = (RefreshLayout) switchLayout.findViewById(R.id.wordCardEn);
                final RefreshLayout wordCardCh = (RefreshLayout) switchLayout.findViewById(R.id.wordCardCh);
                TextView vocabulary = (TextView) wordCardEn.findViewById(R.id.word);
                TextView sentence = (TextView) wordCardEn.findViewById(R.id.sentence);
                TextView translate = (TextView) wordCardCh.findViewById(R.id.translate);
                TextView trSentence = (TextView) wordCardCh.findViewById(R.id.trsentence);

                vocabulary.setText(word.getVocabulary());
                sentence.setText(word.getSentence());
                translate.setText(word.getTranslate());
                trSentence.setText(word.getSentence());

                addListener(switchLayout);




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
                /*version 1中删除的代码*/
//                addListener(findViewById(R.id.index));
//                addListener(findViewById(R.id.indexViewPager));


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
                                        viewPager.setCanScroll(false);
                                    }
                                });

                        rotatable.setTouchEnable(false);
                        rotatable.rotate(Rotatable.ROTATE_Y, -180, 1500);

                        return false;
                    }
                });





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
                                        viewPager.setCanScroll(true);
                                    }
                                });

                        rotatable.setTouchEnable(false);
                        rotatable.rotate(Rotatable.ROTATE_Y, 0, 1500);
                        return false;
                    }
                });

            }
        }

        pagerAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return viewPages.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(viewPages.get(position));
                return viewPages.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(viewPages.get(position));
            }
        };
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);

    }
}
