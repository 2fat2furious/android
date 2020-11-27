package com.example.wordplay.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.wordplay.R;

public class WordBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "wordBase.db";
    private Context context;

    public WordBaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + WordDbSchema.WordTable.NAME + "(" + "_id integer primary key autoincrement, "
                + WordDbSchema.WordTable.Cols.WORD + ", " + WordDbSchema.WordTable.Cols.LANGUAGE + ")");

        initDb(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void initDb(SQLiteDatabase database){
        ContentValues values;
        for (String word: context.getResources().getStringArray(R.array.words_ru)){
            values = new ContentValues();
            values.put(WordDbSchema.WordTable.Cols.LANGUAGE, "ru");
            values.put(WordDbSchema.WordTable.Cols.WORD, word);
            database.insert(WordDbSchema.WordTable.NAME, null, values);
        }
        for (String word: context.getResources().getStringArray(R.array.words_en)){
            values = new ContentValues();
            values.put(WordDbSchema.WordTable.Cols.LANGUAGE, "en");
            values.put(WordDbSchema.WordTable.Cols.WORD, word);
            database.insert(WordDbSchema.WordTable.NAME, null, values);
        }
    }
}
