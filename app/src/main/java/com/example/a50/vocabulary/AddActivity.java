package com.example.a50.vocabulary;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 50萌主 on 2017/9/1.
 */

public class AddActivity extends ActionBarActivity {
    EditText addWord;
    EditText addTranslate;
    EditText addSentence;
    List<Word> words;
    String word;
    String translate;
    String sentence;
    Word vocabulary;
    Toolbar toolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.addword);

        addWord = (EditText) findViewById(R.id.addWord);
        addTranslate = (EditText) findViewById(R.id.addTranslate);
        addSentence = (EditText) findViewById(R.id.addSentence);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);


        words = new ArrayList<>();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        words = (List<Word>) bundle.getSerializable("words");

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.save:
                        word = addWord.getText().toString();
                        translate = addTranslate.getText().toString();
                        sentence = addSentence.getText().toString();

                        if (!"".equals(word)){
                            if ("".equals(translate) && "".equals(sentence)){
                                vocabulary = new Word(word);
                            }else if ("".equals(translate) && !"".equals(sentence)){
                                vocabulary = new Word(word, sentence);
                            }else if (!"".equals(translate) && !"".equals(sentence)){
                                vocabulary = new Word(word, sentence, translate);
                            }

                            words.add(vocabulary);
                            Intent intent = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("words", (Serializable) words);
                            intent.putExtras(bundle);
                            setResult(RESULT_OK, intent);
                            finish();

                        }else{
                            Toast.makeText(AddActivity.this, "请添加单词！", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
