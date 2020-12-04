package com.example.wordplay.database;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.wordplay.R;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class WordLabTest {

    private WordLab wordLab;

    @Before
    public void setUp(){
        wordLab = new WordLab(InstrumentationRegistry.getInstrumentation().getTargetContext());
        wordLab.deleteAll();
    }

    @Test
    public void testPreConditions() {
        assertNotNull(wordLab);
    }

    @Test
    public void testDeleteAll() {
        wordLab.deleteAll();
        List<Word> words = wordLab.getAllWords();

        assertThat(words.size(), is(0));
    }

    @Test
    public void testAdd() throws Exception {
        wordLab.deleteAll();
        Word word = new Word("ТЕСТИРОВАНИЕ", "ru");
        wordLab.addWord(word);
        List<Word> words = wordLab.getAllWords();

        assertThat(words.size(), is(1));
        assertEquals("ТЕСТИРОВАНИЕ", words.get(0).getWord());
        assertEquals("ru", words.get(0).getLanguage());
    }

    @Test
    public void getRandomWord() {
        wordLab.deleteAll();
        Word word1 = new Word("ТЕСТИРОВАНИЕ", "ru");
        Word word2 = new Word("TESTING", "en");
        Word word3 = new Word("ПРОГРАММИРОВАНИЕ", "ru");
        Word word4 = new Word("PROGRAMMING", "en");
        wordLab.addWord(word1);
        wordLab.addWord(word2);
        wordLab.addWord(word3);
        wordLab.addWord(word4);

        String randomWord = wordLab.getRandomWord();
        assertThat(randomWord, Matchers.anyOf(is(word1.getWord()), is(word2.getWord()), is(word3.getWord()), is(word4.getWord())));
    }

    @After
    public void after(){
        wordLab.deleteAll();
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        for (String word: context.getResources().getStringArray(R.array.words_ru)){
            wordLab.addWord(new Word(word, "ru"));
        }
        for (String word: context.getResources().getStringArray(R.array.words_en)){
            wordLab.addWord(new Word(word, "en"));
        }
    }
}