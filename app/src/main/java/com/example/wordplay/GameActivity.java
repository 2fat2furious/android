package com.example.wordplay;


import android.app.AlertDialog;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.example.wordplay.database.WordLab;

public class GameActivity extends AppCompatActivity {

    //body part images
    private ImageView[] bodyParts;
    //number of body parts
    private final int numParts=6;
    //current part - will increment when wrong answers are chosen
    private int currPart;
    //number of characters in current word
    private int numChars;
    //number correctly guessed
    private int numCorr;

    private String currWord;
    private LinearLayout wordLayout;
    private TextView[] charViews;
    private GridView letters;
    private LetterAdapter ltrAdapt;
    private AlertDialog helpAlert;
    private WordLab wordLab;
    private SoundPool sounds;
    private int sWin;
    private int sLose;
    private int sSure;

    public GameActivity(Context context){
        sounds = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
        sWin = sounds.load(context, R.raw.pobeda,1);
        sLose = sounds.load(context, R.raw.lose, 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currWord = "";
        wordLayout = findViewById(R.id.word);
        wordLab = new WordLab(this);

        letters = findViewById(R.id.letters);

        bodyParts = new ImageView[numParts];
        bodyParts[0] = findViewById(R.id.head);
        bodyParts[1] = findViewById(R.id.body);
        bodyParts[2] = findViewById(R.id.arm1);
        bodyParts[3] = findViewById(R.id.arm2);
        bodyParts[4] = findViewById(R.id.leg1);
        bodyParts[5] = findViewById(R.id.leg2);

        playGame();
    }

    private void playGame() {
        //play a new game
        // Выбираем случайным образом новое слово
        String newWord = wordLab.getRandomWord();
        // Если слово совпадает с текущим, то повторяем еще раз, пока не выберем новое слово
        while (newWord.equals(currWord)) newWord = wordLab.getRandomWord();
        currWord = newWord; // выбрали
        // программно создадим столько TextView, сколько символов в слове
        charViews = new TextView[currWord.length()];
        // но сначала удалим все элементы от прошлой игры
        wordLayout.removeAllViews();

        // создаём массив новых TextView
        for (int c = 0; c < currWord.length(); c++) {
            charViews[c] = new TextView(this);
            charViews[c].setText("" + currWord.charAt(c));

            charViews[c].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            charViews[c].setGravity(Gravity.CENTER);
            charViews[c].setTextColor(Color.WHITE);
            charViews[c].setBackgroundResource(R.drawable.letter_bg);
            // добавляем в разметку
            wordLayout.addView(charViews[c]);
        }

        ltrAdapt = new LetterAdapter(this);
        letters.setAdapter(ltrAdapt);
        currPart = 0;
        numChars = currWord.length();
        numCorr = 0;

        for (int p = 0; p < numParts; p++) {
            bodyParts[p].setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_help:
                showHelp();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void letterPressed(View view) {
        //user has pressed a letter to guess
        String ltr = ((Button) view).getText().toString();
        char letterChar = ltr.charAt(0);
        view.setEnabled(false);
        view.setBackgroundResource(R.drawable.letter_down);
        boolean correct = false;
        for (int k = 0; k < currWord.length(); k++) {
            if (currWord.charAt(k) == letterChar) {
                correct = true;
                numCorr++;
                charViews[k].setTextColor(Color.BLACK);
            }
        }

        if (correct) {
            // удачная попытка
            if (numCorr == numChars) {
                // блокируем кнопки
                disableBtns();

                // выводим диалоговое окно
                AlertDialog.Builder winBuild = new AlertDialog.Builder(this);
//                sounds.play(sWin, 1.0f, 1.0f, 0, 0, 1.5f);
                winBuild.setTitle(getString(R.string.congrat));
                winBuild.setMessage(getString(R.string.win) + currWord);
                winBuild.setPositiveButton(getString(R.string.playAgain),
                        (dialog, id) -> GameActivity.this.playGame()
                );

                winBuild.setNegativeButton(getString(R.string.exit),
                        (dialog, id) -> GameActivity.this.finish()
                );

                winBuild.show();
            }
        } else if (currPart < numParts) {
            //some guesses left
            bodyParts[currPart].setVisibility(View.VISIBLE);
            currPart++;
        }else{
            //user has lost
            disableBtns();

            // Display Alert Dialog
            AlertDialog.Builder loseBuild = new AlertDialog.Builder(this);
 //           sounds.play(sLose, 1.0f, 1.0f, 0, 0, 1.5f);
            loseBuild.setTitle(getString(R.string.alas));
            loseBuild.setMessage(getString(R.string.lose) + currWord);
            loseBuild.setPositiveButton(getString(R.string.playAgain),
                    (dialog, id) -> GameActivity.this.playGame());

            loseBuild.setNegativeButton(getString(R.string.exit),
                    (dialog, id) -> GameActivity.this.finish());

            loseBuild.show();
        }
    }

    public void disableBtns() {
        int numLetters = letters.getChildCount();
        for (int l = 0; l < numLetters; l++) {
            letters.getChildAt(l).setEnabled(false);
        }
    }

    public void showHelp() {
        AlertDialog.Builder helpBuild = new AlertDialog.Builder(this);

        helpBuild.setTitle(getString(R.string.help));
        helpBuild.setMessage(getString(R.string.helpMess));
        helpBuild.setPositiveButton("OK",
                (dialog, id) -> helpAlert.dismiss());
        helpAlert = helpBuild.create();

        helpBuild.show();
    }
}
