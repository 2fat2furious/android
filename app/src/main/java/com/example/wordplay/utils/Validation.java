package com.example.wordplay.utils;

import android.text.TextUtils;
import android.util.Patterns;

public class Validation {

    public static boolean validateFields(String name){

        if (TextUtils.isEmpty(name)) {

            return false;

        } else {

            return true;
        }
    }

    public static boolean validateLogin(String string) {

        if (TextUtils.isEmpty(string)) {

            return false;

        } else {

            return  true;
        }
    }
}