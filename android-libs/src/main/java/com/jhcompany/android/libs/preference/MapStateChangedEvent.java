package com.jhcompany.android.libs.preference;

import com.google.common.base.Objects;

/**
 * {@link MapState}의 값이 변경되는 경우 이 이벤트가 EventBus를 통해 발생합니다.
 */
public class MapStateChangedEvent {

    public MapStateChangedEvent(MapState<?> changedState, String changedKey) {
        this.changedState = changedState;
        this.changedKey = changedKey;
    }

    private final MapState<?> changedState;
    private final String changedKey;

    public MapState<?> getChangedState() {
        return changedState;
    }

    public String getChangedKey() {
        return changedKey;
    }

    public boolean isEventOf(MapState<?> state) {
        boolean isSameStatesName = Objects.equal(changedState.statesName(), state.statesName());
        boolean isSameName = Objects.equal(changedState.name(), state.name());
        return isSameStatesName && isSameName;
    }

    public boolean isEventOf(MapState<?> state, String key) {
        final boolean isSameMapState = isEventOf(state);
        final boolean isSameKey = changedKey.equals(key);
        return isSameMapState && isSameKey;
    }
}
