package com.jhcompany.android.libs.preference;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Strings;
import com.jhcompany.android.libs.jackson.Jackson;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;


/**
 * {@link StateImpl}를 구현하기 위해 만든 클래스. 원래는 {@link SharedPreferences}
 * 를 사용하여야 하지만, 구현상 편의를 위해 primitive 타입이 아닌 box 타입들을 받을 수 있도록 한 클래스이다.
 */
public class WrappedSharedPreference {

    private SharedPreferences preferences;

    WrappedSharedPreference(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public boolean contains(String key) {
        return preferences.contains(key);
    }

    @SuppressLint("CommitPrefEdits")
    public WrappedEditor edit() {
        return new WrappedEditor(preferences.edit());
    }

    public Set<String> keys() {
        return Collections.unmodifiableSet(preferences.getAll().keySet());
    }

    public Boolean getBoolean(String key, Boolean defValue) {
        if (!preferences.contains(key)) {
            return defValue;
        }
        return preferences.getBoolean(key, false);
    }

    public Float getFloat(String key, Float defValue) {
        if (!preferences.contains(key)) {
            return defValue;
        }
        return preferences.getFloat(key, 0);
    }

    public Integer getInt(String key, Integer defValue) {
        if (!preferences.contains(key)) {
            return defValue;
        }
        return preferences.getInt(key, 0);
    }

    public Long getLong(String key, Long defValue) {
        if (!preferences.contains(key)) {
            return defValue;
        }
        return preferences.getLong(key, 0);
    }

    public Double getDouble(String key, Double defValue) {
        if (!preferences.contains(key)) {
            return defValue;
        }
        try {
            return Double.valueOf(preferences.getString(key, null));
        } catch (Exception e) {
            return defValue;
        }
    }

    public String getString(String key, String defValue) {
        return preferences.getString(key, defValue);
    }

    public <T> T getObject(String key, T defValue, Class<T> type) {
        String value = preferences.getString(key, null);
        if (!Strings.isNullOrEmpty(value)) {
            try {
                return Jackson.stringToObject(value, type);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return defValue;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, T defValue, Class<T> type) {
        T result = null;
        if (type == Boolean.class) {
            result = (T) getBoolean(key, (Boolean) defValue);
        } else if (type == Float.class) {
            result = (T) getFloat(key, (Float) defValue);
        } else if (type == Integer.class) {
            result = (T) getInt(key, (Integer) defValue);
        } else if (type == Double.class) {
            result = (T) getDouble(key, (Double) defValue);
        } else if (type == Long.class) {
            result = (T) getLong(key, (Long) defValue);
        } else if (type == String.class) {
            result = (T) getString(key, (String) defValue);
        } else {
            result = getObject(key, defValue, type);
        }
        return result;
    }

    public static class WrappedEditor {

        private final Editor editor;

        public WrappedEditor(Editor editor) {
            this.editor = editor;
        }

        public WrappedEditor clear() {
            editor.clear();
            return this;
        }

        public boolean commit() {
            /*
             Gingerbread 부터는 apply() 를 사용하면 Async 하게 변경사항을 저장할 있지만,
             Process kill 에 취약하기에 commit 을 사용한다.
             */
            return editor.commit();
        }

        public WrappedEditor putBoolean(String key, Boolean value) {
            if (value == null) {
                editor.clear();
                return this;
            }
            editor.putBoolean(key, value);
            return this;
        }

        public WrappedEditor putFloat(String key, Float value) {
            if (value == null) {
                editor.clear();
                return this;
            }
            editor.putFloat(key, value);
            return this;
        }

        public WrappedEditor putInt(String key, Integer value) {
            if (value == null) {
                editor.clear();
                return this;
            }
            editor.putInt(key, value);
            return this;
        }

        public WrappedEditor putLong(String key, Long value) {
            if (value == null) {
                editor.clear();
                return this;
            }
            editor.putLong(key, value);
            return this;
        }

        public WrappedEditor putDouble(String key, Double value) {
            if (value == null) {
                editor.clear();
                return this;
            }
            editor.putString(key, String.valueOf(value));
            return this;
        }

        public WrappedEditor putString(String key, String value) {
            editor.putString(key, value);
            return this;
        }

        public <T> WrappedEditor putObject(String key, T value, Class<T> type) {
            try {
                if (value != null) {
                    Object targetValue = (Object) value;
                    @SuppressWarnings("rawtypes")
                    Class targetClass = value.getClass();
                    @SuppressWarnings("unchecked")
                    String jsonString = Jackson.objectToString(targetValue, targetClass);
                    editor.putString(key, jsonString);
                } else {
                    editor.putString(key, null);
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return this;
        }

        public <T> WrappedEditor put(String key, T value, Class<T> type) {
            if (type == Boolean.class) {
                putBoolean(key, (Boolean) value);
            } else if (type == Float.class) {
                putFloat(key, (Float) value);
            } else if (type == Integer.class) {
                putInt(key, (Integer) value);
            } else if (type == Long.class) {
                putLong(key, (Long) value);
            } else if (type == Double.class) {
                putDouble(key, (Double) value);
            } else if (type == String.class) {
                putString(key, (String) value);
            } else {
                putObject(key, (T) value, type);
            }
            return this;
        }

        public WrappedEditor remove(String key) {
            editor.remove(key);
            return this;
        }
    }
}
