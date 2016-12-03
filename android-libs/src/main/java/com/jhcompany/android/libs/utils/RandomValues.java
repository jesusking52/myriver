package com.jhcompany.android.libs.utils;

import java.util.Date;
import java.util.Random;

public final class RandomValues {
    private RandomValues() {}

    private static Random RAND = new Random();

    public static int randomAbsInt() {
        return Math.abs(RAND.nextInt());
    }

    public static int randomAbsInt(int amount) {
        return Math.abs(RAND.nextInt()) % amount;
    }

    public static Date randomPrevDate(int border) {
        int randDays = randomAbsInt(border);
        int amountMillis = randDays * 24 * 60 * 60 * 1000;
        long currentMillis = System.currentTimeMillis();
        return new Date(currentMillis - amountMillis);
    }
}
