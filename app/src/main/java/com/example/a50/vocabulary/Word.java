package com.example.a50.vocabulary;

import java.io.Serializable;

/**
 * Created by 50萌主 on 2017/8/29.
 */

public class Word implements Serializable {
    private String vocabulary;
    private String translate;
    private String sentence;
    private boolean flag;

    public Word(String vocabulary){
        this.vocabulary = vocabulary;
        this.sentence = "";
        this.translate = "this vocabulary has not an translate yet";
        this.flag = false;
    }

    public Word(String vocabulary, String sentence){
        this.vocabulary = vocabulary;
        this.sentence = sentence;
        this.translate = "this vocabulary has not an translate yet";
        this.flag = false;
    }

    public Word(String vocabulary, String sentence, String translate){
        this.vocabulary = vocabulary;
        this.sentence = sentence;
        this.translate = translate;
        this.flag = false;
    }

    public String getTranslate() {
        return this.translate;
    }

    public String getVocabulary() {
        return this.vocabulary;
    }

    public String getSentence() {
        return this.sentence;
    }

    public boolean getFlag(){
        return this.flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
