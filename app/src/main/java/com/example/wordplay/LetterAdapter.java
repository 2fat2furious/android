package com.example.wordplay;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import java.util.Locale;

public class LetterAdapter extends BaseAdapter {

    private String[] letters;
    private LayoutInflater letterInf;

    public LetterAdapter(Context c) {
        Resources res = c.getResources();
        String current = res.getConfiguration().locale.getDisplayLanguage();
        if(current.toLowerCase().contains("русский")) {
            letters = new String[32];
            for (int a = 0; a < letters.length; a++) {
                letters[a] = "" + (char) (a + 'А');
            }
        }
        else{
            letters = new String[26];
            for (int a = 0; a < letters.length; a++) {
                letters[a] = "" + (char) (a + 'A');
            }
        }

        letterInf = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return letters.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //create a button for the letter at this position in the alphabet
        Button letterBtn;
        if (convertView == null) {
            //inflate the button layout
            letterBtn = (Button)letterInf.inflate(R.layout.letter, parent, false);
        } else {
            letterBtn = (Button) convertView;
        }
        //set the text to this letter
        letterBtn.setText(letters[position]);
        return letterBtn;
    }
}
