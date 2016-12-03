package com.jhcompany.android.libs.preference;

import com.google.common.base.Objects;

/**
 * {@link State}의 값이 변경되는 경우 이 이벤트가 EventBus를 통해 발생합니다.
 */
public class StateChangedEvent {

    public StateChangedEvent(State<?> changedState) {
        this.changedState = changedState;
    }

    private final State<?> changedState;

    public State<?> getChangedState() {
        return changedState;
    }

    public boolean isEventOf(State<?> state) {
        boolean isSameStatesName = Objects.equal(changedState.statesName(), state.statesName());
        boolean isSameName = Objects.equal(changedState.name(), state.name());
        return isSameStatesName && isSameName;
    }
}
