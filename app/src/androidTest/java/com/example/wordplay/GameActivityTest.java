package com.example.wordplay;

import android.content.Context;
import android.content.res.Resources;

import androidx.test.espresso.AmbiguousViewMatcherException;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasTextColor;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class GameActivityTest {

    @Rule
    public ActivityScenarioRule<GameActivity> activityRule = new ActivityScenarioRule<>(GameActivity.class);

    @Test
    public void testInterface(){
        onView(withId(R.id.word)).check(matches(isDisplayed()));
        onView(withId(R.id.letters)).check(matches(isDisplayed()));
        onView(withContentDescription(R.string.gallows)).check(matches(isDisplayed()));
        onView(withId(R.id.body)).check(matches(not(isDisplayed())));
        onView(withId(R.id.head)).check(matches(not(isDisplayed())));
        onView(withId(R.id.arm1)).check(matches(not(isDisplayed())));
        onView(withId(R.id.arm2)).check(matches(not(isDisplayed())));
        onView(withId(R.id.leg1)).check(matches(not(isDisplayed())));
        onView(withId(R.id.leg2)).check(matches(not(isDisplayed())));
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Resources res = appContext.getResources();
        String current = res.getConfiguration().locale.getDisplayLanguage();
        if(current.toLowerCase().contains("русский")) {
            for (int a = 0; a < 32; a++) {
                onView(allOf(withText("" + (char) (a + 'А')), withParent(withId(R.id.letters)))).check(matches(isDisplayed()));
            }
        }
        else{
            for (int a = 0; a < 26; a++) {
                onView(allOf(withText("" + (char) (a + 'A')), withParent(withId(R.id.letters)))).check(matches(isDisplayed()));
            }
        }
    }

    @Test
    public void testPlayGame() {
        int countMistake = 0;
        int i = 0;

        String current = InstrumentationRegistry.getInstrumentation().getTargetContext().getResources().getConfiguration().locale.getDisplayLanguage();

        if(current.toLowerCase().contains("русский")) {
            while(countMistake <= 6) {
                onView(allOf(withText("" + (char) (i + 'А')), withParent(withId(R.id.letters)))).perform(click());

                try {
                    onView(allOf(hasTextColor(R.color.black), withText("" + (char) (i + 'А')), isDescendantOfA(withId(R.id.word)))).check(matches(isDisplayed()));
                    onView(allOf(withText("" + (char) (i + 'А')), withParent(withId(R.id.letters)))).check(matches(not(isEnabled())));
                }
                catch (AmbiguousViewMatcherException ignored){ }
                catch (NoMatchingViewException e){
                    switch (countMistake){
                        case 0: {
                            onView(withId(R.id.head)).check(matches(isDisplayed()));
                            break;
                        }
                        case 1: {
                            onView(withId(R.id.head)).check(matches(isDisplayed()));
                            onView(withId(R.id.body)).check(matches(isDisplayed()));
                            break;
                        }
                        case 2: {
                            onView(withId(R.id.head)).check(matches(isDisplayed()));
                            onView(withId(R.id.body)).check(matches(isDisplayed()));
                            onView(withId(R.id.arm1)).check(matches(isDisplayed()));
                            break;
                        }
                        case 3: {
                            onView(withId(R.id.head)).check(matches(isDisplayed()));
                            onView(withId(R.id.body)).check(matches(isDisplayed()));
                            onView(withId(R.id.arm1)).check(matches(isDisplayed()));
                            onView(withId(R.id.arm2)).check(matches(isDisplayed()));
                            break;
                        }
                        case 4: {
                            onView(withId(R.id.head)).check(matches(isDisplayed()));
                            onView(withId(R.id.body)).check(matches(isDisplayed()));
                            onView(withId(R.id.arm1)).check(matches(isDisplayed()));
                            onView(withId(R.id.arm2)).check(matches(isDisplayed()));
                            onView(withId(R.id.leg1)).check(matches(isDisplayed()));
                            break;
                        }
                        case 5: {
                            onView(withId(R.id.head)).check(matches(isDisplayed()));
                            onView(withId(R.id.body)).check(matches(isDisplayed()));
                            onView(withId(R.id.arm1)).check(matches(isDisplayed()));
                            onView(withId(R.id.arm2)).check(matches(isDisplayed()));
                            onView(withId(R.id.leg1)).check(matches(isDisplayed()));
                            onView(withId(R.id.leg2)).check(matches(isDisplayed()));
                            break;
                        }
                    }
                    countMistake++;
                }
                i++;
            }
        }
        else {
            while(countMistake <= 6) {
                onView(allOf(withText("" + (char) (i + 'A')), withParent(withId(R.id.letters)))).perform(click());

                try {
                    onView(allOf(hasTextColor(R.color.black), withText("" + (char) (i + 'A')), isDescendantOfA(withId(R.id.word)))).check(matches(isDisplayed()));
                    onView(allOf(withText("" + (char) (i + 'A')), withParent(withId(R.id.letters)))).check(matches(not(isEnabled())));
                }
                catch (AmbiguousViewMatcherException ignored){ }
                catch (NoMatchingViewException e){
                    switch (countMistake){
                        case 0: {
                            onView(withId(R.id.head)).check(matches(isDisplayed()));
                            break;
                        }
                        case 1: {
                            onView(withId(R.id.head)).check(matches(isDisplayed()));
                            onView(withId(R.id.body)).check(matches(isDisplayed()));
                            break;
                        }
                        case 2: {
                            onView(withId(R.id.head)).check(matches(isDisplayed()));
                            onView(withId(R.id.body)).check(matches(isDisplayed()));
                            onView(withId(R.id.arm1)).check(matches(isDisplayed()));
                            break;
                        }
                        case 3: {
                            onView(withId(R.id.head)).check(matches(isDisplayed()));
                            onView(withId(R.id.body)).check(matches(isDisplayed()));
                            onView(withId(R.id.arm1)).check(matches(isDisplayed()));
                            onView(withId(R.id.arm2)).check(matches(isDisplayed()));
                            break;
                        }
                        case 4: {
                            onView(withId(R.id.head)).check(matches(isDisplayed()));
                            onView(withId(R.id.body)).check(matches(isDisplayed()));
                            onView(withId(R.id.arm1)).check(matches(isDisplayed()));
                            onView(withId(R.id.arm2)).check(matches(isDisplayed()));
                            onView(withId(R.id.leg1)).check(matches(isDisplayed()));
                            break;
                        }
                        case 5: {
                            onView(withId(R.id.head)).check(matches(isDisplayed()));
                            onView(withId(R.id.body)).check(matches(isDisplayed()));
                            onView(withId(R.id.arm1)).check(matches(isDisplayed()));
                            onView(withId(R.id.arm2)).check(matches(isDisplayed()));
                            onView(withId(R.id.leg1)).check(matches(isDisplayed()));
                            onView(withId(R.id.leg2)).check(matches(isDisplayed()));
                            break;
                        }
                    }
                    countMistake++;
                }
                i++;
            }
        }
        onView(withId(16908299)).check(matches(isDisplayed()));
    }
}