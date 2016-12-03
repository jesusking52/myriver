package com.jhcompany.android.libs.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.common.base.Objects;
import com.google.common.primitives.Primitives;
import com.jhcompany.android.libs.preference.WrappedSharedPreference.WrappedEditor;

import java.util.Set;

class MapStateImpl<T> implements MapState<T> {

    private static final String INDEX_PREFERENCES_KEY = "!index!";

    private final String statesName;
    private final String name;
    private final Class<T> type;

    public MapStateImpl(String statesName, String name, Class<T> type) {
        this.statesName = statesName;
        this.name = name;
        this.type = type;
    }

    @Override
    public Set<String> keys(StateCtx stateCtx) {
        WrappedSharedPreference index = getIndexPreferences(stateCtx);
        return index.keys();
    }

    @Override
    public T get(StateCtx stateCtx, String key) {
        if (!contains(stateCtx, key)) {
            return null;
        }
        WrappedSharedPreference preferences = getPreferences(stateCtx, key);
        return preferences.get(key, null, type);
    }

    @Override
    public void put(StateCtx stateCtx, String key, T value) {
        WrappedSharedPreference index = getIndexPreferences(stateCtx);
        WrappedEditor indexEditor = index.edit();
        indexEditor.putBoolean(key, value != null);
        indexEditor.commit();

        WrappedSharedPreference preferences = getPreferences(stateCtx, key);
        WrappedEditor editor = preferences.edit();
        editor.put(key, value, type);
        editor.commit();

        stateCtx.notifyChangedEvent(this, key);
    }

    @Override
    public void remove(StateCtx stateCtx, String key) {
        if (!contains(stateCtx, key)) {
            return;
        }
        WrappedSharedPreference index = getIndexPreferences(stateCtx);
        WrappedEditor indexEditor = index.edit();
        indexEditor.remove(key);
        indexEditor.commit();

        WrappedSharedPreference preferences = getPreferences(stateCtx, key);
        WrappedEditor editor = preferences.edit();
        editor.remove(key);
        editor.commit();

        stateCtx.notifyChangedEvent(this, key);
    }

    @Override
    public boolean contains(StateCtx stateCtx, String key) {
        WrappedSharedPreference index = getIndexPreferences(stateCtx);
        return index.contains(key);
    }

    @Override
    public String statesName() {
        return statesName;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public void clear(StateCtx stateCtx) {
        final Set<String> keys = keys(stateCtx);
        WrappedSharedPreference index = getIndexPreferences(stateCtx);
        WrappedEditor indexEditor = index.edit();
        indexEditor.clear();
        indexEditor.commit();

        for (String key : keys) {
            WrappedSharedPreference preferences = getPreferences(stateCtx, key);
            WrappedEditor editor = preferences.edit();
            editor.clear();
            editor.commit();

            stateCtx.notifyChangedEvent(this, key);
        }
    }

    private WrappedSharedPreference getIndexPreferences(StateCtx stateCtx) {
        if (Objects.equal(statesName(), DEFAULT_STATES_NAME)) {
            return new WrappedSharedPreference(PreferenceManager.getDefaultSharedPreferences(stateCtx.getContext()));
        }
        String preferencesName = NamingPolicy.getSharedPreferencesName(statesName(), name, INDEX_PREFERENCES_KEY);
        SharedPreferences sharedPreferences = stateCtx.getContext().getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        return new WrappedSharedPreference(sharedPreferences);
    }

    private WrappedSharedPreference getPreferences(StateCtx stateCtx, String key) {
        if (Objects.equal(statesName(), DEFAULT_STATES_NAME)) {
            return new WrappedSharedPreference(PreferenceManager.getDefaultSharedPreferences(stateCtx.getContext()));
        }
        String preferencesName = determinePreferencesNameWithStateName(stateCtx, key);
        SharedPreferences sharedPreferences = stateCtx.getContext().getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        return new WrappedSharedPreference(sharedPreferences);
    }

    private String determinePreferencesNameWithStateName(StateCtx stateCtx, String key) {
        String preferencesName = NamingPolicy.getSharedPreferencesName(statesName(), name);
        if (!Primitives.isWrapperType(type)) {
            preferencesName = NamingPolicy.getSharedPreferencesName(statesName(), name, key);
        }
        return preferencesName;
    }
}
