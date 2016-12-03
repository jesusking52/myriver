package com.jhcompany.android.libs.preference;

import android.util.Log;

import com.google.common.base.Optional;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class CachingState<T> extends ForwardingState<T> {

    private final State<T> delegate;

    public CachingState(State<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    protected State<T> delegate() {
        return delegate;
    }

    @Override
    public String statesName() {
        return delegate().statesName();
    }

    @Override
    public T get(final StateCtx stateCtx) {
        T result = null;
        try {
            Optional<?> optional = stateCtx.getCache().get(cacheKey(), new Callable<Optional<T>>() {
                @Override
                public Optional<T> call() throws Exception {
                    T result = delegate().get(stateCtx);
                    return Optional.fromNullable(result);
                }
            });
            result = (T) optional.orNull();
        } catch (ExecutionException e) {
            Log.w("CoupleState", e.getMessage());
            stateCtx.getCache().invalidate(cacheKey());
        }
        return result;
    }

    @Override
    public void set(StateCtx stateCtx, T value) {
        super.set(stateCtx, value);
        stateCtx.getCache().put(cacheKey(), Optional.fromNullable(value));
    }

    @Override
    public boolean isSet(StateCtx stateCtx) {
        Optional<?> optional = stateCtx.getCache().getIfPresent(cacheKey());
        if (optional != null) {
            return optional.isPresent();
        }
        return super.isSet(stateCtx);
    }

    @Override
    public void clear(StateCtx stateCtx) {
        super.clear(stateCtx);
        stateCtx.getCache().invalidate(cacheKey());
    }

    private String cacheKey() {
        StringBuilder builder = new StringBuilder();
        builder.append(delegate().statesName());
        builder.append(NamingPolicy.SEPARATOR);
        builder.append(delegate().name());
        return builder.toString();
    }
}
