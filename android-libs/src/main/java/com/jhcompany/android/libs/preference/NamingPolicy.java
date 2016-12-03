package com.jhcompany.android.libs.preference;

import com.google.common.base.Preconditions;

import java.util.regex.Pattern;

public final class NamingPolicy {
    public static final String PREFIX = "state";
    public static final String SEPARATOR = ":";
    public static final String NAME_REGEX = "[A-Za-z0-9-_\\.]+";
    public static final Pattern NAME_PATTERN = Pattern.compile(NAME_REGEX);

    private NamingPolicy() {
    }

    public static void ensureName(String name) {
        Preconditions.checkNotNull(name);
        Preconditions.checkArgument(NAME_PATTERN.matcher(name).matches());
    }

    public static String getSharedPreferencesName(String... names) {
        Preconditions.checkArgument(names.length > 0);
        StringBuilder sb = new StringBuilder(PREFIX);
        for (String name : names) {
            sb.append(SEPARATOR);
            sb.append(name);
        }
        return sb.toString();
    }
}
