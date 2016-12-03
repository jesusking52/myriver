package com.jhcompany.android.libs.preference;

import com.google.common.collect.ForwardingObject;

import java.util.Set;

public abstract class ForwardingMapState<T> extends ForwardingObject implements MapState<T> {

    @Override
    protected abstract MapState<T> delegate();

    @Override
    public Set<String> keys(StateCtx stateCtx) {
        return delegate().keys(stateCtx);
    }

    @Override
    public T get(StateCtx stateCtx, String key) {
        return delegate().get(stateCtx, key);
    }

    @Override
    public void put(StateCtx stateCtx, String key, T value) {
        delegate().put(stateCtx, key, value);
    }

    @Override
    public void remove(StateCtx stateCtx, String key) {
        delegate().remove(stateCtx, key);
    }

    @Override
    public boolean contains(StateCtx stateCtx, String key) {
        return delegate().contains(stateCtx, key);
    }

    @Override
    public String name() {
        return delegate().name();
    }

    @Override
    public void clear(StateCtx stateCtx) {
        delegate().clear(stateCtx);
    }
}
