package com.jhcompany.android.libs.log;

/**
 * Tag 를 이용하여 {@link Logger} 인스턴스를 만든다.
 */
public final class LoggerFactory {
    private LoggerFactory() {}

    public static Logger getLogger(String tag) {
        return new Logger(tag);
    }

    public static Logger getLogger(Class<?> clazz) {
        return new Logger(clazz.getSimpleName());
    }
}
