package com.jhcompany.android.libs.utils;

import com.google.common.base.Strings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Validator {
    private static final String VALID_PATTERN_EMAIL = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";
    private static final String VALID_PATTERN_PASSWORD = "[A-Z0-9a-z!@#$%&*()_+-=\\[\\]\\{\\}|;':^\",./<>?\\\\]{6,20}";

    private Validator() {}

    public static boolean validateEmail(String value) {
        return !Strings.isNullOrEmpty(value) && checkRegex(value, VALID_PATTERN_EMAIL);
    }

    public static boolean validatePassword(String value) {
        return !Strings.isNullOrEmpty(value) && checkRegex(value, VALID_PATTERN_PASSWORD);
    }

    public static boolean validateLength(String value, int minLen, int maxLen) {
        return isValidLength(value, minLen, maxLen);
    }

    public static boolean isValidLength(String str, int shortestLen, int longestLen) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        return length > shortestLen - 1 && length < longestLen + 1;
    }

    public static boolean checkRegex(String str, String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(str);
        return m.matches();
    }
}
