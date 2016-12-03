package com.jhcompany.android.libs.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.common.base.Objects;
import com.google.common.primitives.Primitives;

import javax.inject.Inject;

/**
 * {@link StateCtx} 정보를 {@link SharedPreferences} 에 저장해 관리해주는 클래스이다.
 */
public class PreferenceStateCtx extends StateCtx {

    @Inject
    public PreferenceStateCtx(Context context) {
        super(context);
    }

    @Override
    public <T> T get(State<T> state) {
        WrappedSharedPreference preference = getPreferences(state);
        return preference.get(state.name(), state.defaultValue(), state.type());
    }

    @Override
    public <T> void set(State<T> state, T value) {
        WrappedSharedPreference preferences = getPreferences(state);
        WrappedSharedPreference.WrappedEditor editor = preferences.edit();
        editor.put(state.name(), value, state.type());
        editor.commit();
    }

    @Override
    public <T> boolean isSet(State<T> state) {
        WrappedSharedPreference preferences = getPreferences(state);
        return preferences.contains(state.name());
    }

    @Override
    public <T> void clear(State<T> state) {
        WrappedSharedPreference preferences = getPreferences(state);
        WrappedSharedPreference.WrappedEditor editor = preferences.edit();
        editor.remove(state.name());
        editor.commit();
    }

    private <T> WrappedSharedPreference getPreferences(State<T> state) {
        if (Objects.equal(state.statesName(), ManagableState.DEFAULT_STATES_NAME)) {
            return new WrappedSharedPreference(PreferenceManager.getDefaultSharedPreferences(getContext()));
        }
        String preferencesName = NamingPolicy.getSharedPreferencesName(state.statesName());
        if (!Primitives.isWrapperType(state.type())) {
            preferencesName = NamingPolicy.getSharedPreferencesName(state.statesName(), state.name());
        }
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        return new WrappedSharedPreference(sharedPreferences);
    }
}
