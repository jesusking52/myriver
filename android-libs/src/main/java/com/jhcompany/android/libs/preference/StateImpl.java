package com.jhcompany.android.libs.preference;

public class StateImpl<T> implements State<T> {

    private final String statesName;
    private final String key;
    private final T defaultValue;
    private final Class<T> type;

    public StateImpl(String statesName, String key, T defaultValue, Class<T> type) {
        this.statesName = statesName;
        this.key = key;
        this.defaultValue = defaultValue;
        this.type = type;
    }

    @Override
    public String statesName() {
        return statesName;
    }

    @Override
    public String name() {
        return key;
    }

    @Override
    public T get(StateCtx stateCtx) {
        return (T) stateCtx.get(this);
    }

    @Override
    public void set(StateCtx stateCtx, T value) {
        stateCtx.set(this, value);
        stateCtx.notifyChangedEvent(this);
    }

    @Override
    public boolean isSet(StateCtx stateCtx) {
        return stateCtx.isSet(this);
    }

    @Override
    public T defaultValue() {
        return defaultValue;
    }

    @Override
    public void clear(StateCtx stateCtx) {
        stateCtx.clear(this);
        stateCtx.notifyChangedEvent(this);
    }

    @Override
    public Class<T> type() {
        return type;
    }
}
