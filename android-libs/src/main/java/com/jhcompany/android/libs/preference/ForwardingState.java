package com.jhcompany.android.libs.preference;

import com.google.common.collect.ForwardingObject;

public abstract class ForwardingState<T> extends ForwardingObject implements State<T> {

    @Override
    protected abstract State<T> delegate();

    @Override
    public String statesName() {
        return delegate().statesName();
    }

    @Override
    public String name() {
        return delegate().name();
    }

    @Override
    public T get(StateCtx stateCtx) {
        return delegate().get(stateCtx);
    }

    @Override
    public void set(StateCtx stateCtx, T value) {
        delegate().set(stateCtx, value);
    }

    @Override
    public boolean isSet(StateCtx stateCtx) {
        return delegate().isSet(stateCtx);
    }

    @Override
    public T defaultValue() {
        return delegate().defaultValue();
    }

    @Override
    public void clear(StateCtx stateCtx) {
        delegate().clear(stateCtx);
    }

    @Override
    public Class<T> type() {
        return delegate().type();
    }
}
