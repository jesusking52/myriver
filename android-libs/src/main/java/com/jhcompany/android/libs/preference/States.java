package com.jhcompany.android.libs.preference;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import java.util.List;

public class States {

    private static final List<ManagableState> STATES = Lists.newArrayList();

    protected States() {
    }

    protected static State<Integer> defineInt(String statesName, String key, Integer defaultValue) {
        final State<Integer> state = new StateImpl<>(statesName, key, defaultValue, Integer.class);
        STATES.add(state);
        return state;
    }

    protected static State<Long> defineLong(String statesName, String key, Long defaultValue) {
        final State<Long> state = new StateImpl<>(statesName, key, defaultValue, Long.class);
        STATES.add(state);
        return state;
    }

    protected static State<String> defineString(String statesName, String key, String defaultValue) {
        final State<String> state = new StateImpl<>(statesName, key, defaultValue, String.class);
        STATES.add(state);
        return state;
    }

    protected static State<Boolean> defineBoolean(String statesName, String key, Boolean defaultValue) {
        final State<Boolean> state = new StateImpl<>(statesName, key, defaultValue, Boolean.class);
        STATES.add(state);
        return state;
    }

    protected static State<Float> defineFloat(String statesName, String key, Float defaultValue) {
        final State<Float> state = new StateImpl<>(statesName, key, defaultValue, Float.class);
        STATES.add(state);
        return state;
    }

    protected static <T> State<T> defineObject(String statesName, String key, T defaultValue, Class<T> type) {
        final State<T> cachingState2 = cache(new StateImpl<>(statesName, key, defaultValue, type));
        STATES.add(cachingState2);
        return cachingState2;
    }

    protected static <T> MapState<T> defineMap(String statesName, String key, Class<T> type) {
        final MapState<T> cachingMapState = cache(new MapStateImpl<>(statesName, key, type));
        STATES.add(cachingMapState);
        return cachingMapState;
    }

    /**
     * (ex. UserStates 의 경우 "state_user" 가 State 들의 공통된 statesName 이다.)
     */
    public static void clear(StateCtx stateCtx, String statesName) {
        for (ManagableState state : STATES) {
            if (Objects.equal(state.statesName(), statesName)) {
                state.clear(stateCtx);
            }
        }
    }

    private static <T> State<T> cache(State<T> state) {
        return new CachingState<T>(state);
    }

    private static <T> MapState<T> cache(MapState<T> state) {
        return new CachingMapState<T>(state);
    }
}
