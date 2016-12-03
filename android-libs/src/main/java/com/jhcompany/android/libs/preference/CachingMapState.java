package com.jhcompany.android.libs.preference;

import android.util.Log;

import com.google.common.base.Optional;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class CachingMapState<T> extends ForwardingMapState<T> {

    private final MapState<T> delegate;

    public CachingMapState(MapState<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    protected MapState<T> delegate() {
        return delegate;
    }

    @Override
    public T get(final StateCtx stateCtx, final String key) {
        T result = null;
        try {
            Optional<?> optional = stateCtx.getCache().get(cacheKey(key), new Callable<Optional<T>>() {
                @Override
                public Optional<T> call() throws Exception {
                    T result = delegate().get(stateCtx, key);
                    return Optional.fromNullable(result);
                }
            });
            result = (T) optional.orNull();
        } catch (ExecutionException e) {
            Log.w("CoupleState", e.getMessage());
            stateCtx.getCache().invalidate(cacheKey(key));
        }
        return result;
    }

    @Override
    public void put(StateCtx stateCtx, String key, T value) {
        super.put(stateCtx, key, value);
        stateCtx.getCache().put(cacheKey(key), Optional.fromNullable(value));
    }

    @Override
    public void remove(StateCtx stateCtx, String key) {
        super.remove(stateCtx, key);
        stateCtx.getCache().put(cacheKey(key), Optional.absent());
    }

    @Override
    public String statesName() {
        return delegate().statesName();
    }

    @Override
    public void clear(StateCtx stateCtx) {
        super.clear(stateCtx);
        for (String key : keys(stateCtx)) {
            stateCtx.getCache().put(cacheKey(key), Optional.absent());
        }
    }

    private String cacheKey(String key) {
        StringBuilder builder = new StringBuilder();
        builder.append(statesName());
        builder.append(NamingPolicy.SEPARATOR);
        builder.append(name());
        builder.append(NamingPolicy.SEPARATOR);
        builder.append(key);
        return builder.toString();
    }
}
