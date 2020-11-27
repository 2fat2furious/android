package com.example.wordplay.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.wordplay.R;

public class WordLab {

    private SQLiteDatabase database;
    private Context context;
    private String currentLocale;

    public WordLab(Context context){
        this.context = context;
        database = new WordBaseHelper(context).getWritableDatabase();
        currentLocale = context.getResources().getConfiguration().locale.getDisplayLanguage();
    }

    private static ContentValues getContentValues(Word word){
        ContentValues values = new ContentValues();
        values.put(WordDbSchema.WordTable.Cols.LANGUAGE, word.getLanguage());
        values.put(WordDbSchema.WordTable.Cols.WORD, word.getWord());

        return values;
    }

    public void addWord(Word word){
        ContentValues values = getContentValues(word);

        database.insert(WordDbSchema.WordTable.NAME, null, values);
    }

    public String getRandomWord(){
        if(currentLocale != context.getResources().getConfiguration().locale.getDisplayLanguage()){
            currentLocale = context.getResources().getConfiguration().locale.getDisplayLanguage();
        }
        Cursor cursor;
        if(currentLocale.toLowerCase().contains("русский")) {
            cursor = database.query(WordDbSchema.WordTable.NAME, new String[] {WordDbSchema.WordTable.Cols.WORD},
                    WordDbSchema.WordTable.Cols.LANGUAGE + "= ?", new String[] {"ru"},
                    null, null, "Random()", "1");
        }
        else{
            cursor = database.query(WordDbSchema.WordTable.NAME, new String[] {WordDbSchema.WordTable.Cols.WORD},
                    WordDbSchema.WordTable.Cols.LANGUAGE + "= ?", new String[] {"en"},
                    null, null, "Random()", "1");
        }

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getString(cursor.getColumnIndex(WordDbSchema.WordTable.Cols.WORD));
        }
        finally {
            cursor.close();
        }
    }
}
