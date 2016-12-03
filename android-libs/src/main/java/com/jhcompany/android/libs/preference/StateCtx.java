package com.jhcompany.android.libs.preference;

import android.content.Context;

import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import de.greenrobot.event.EventBus;

public abstract class StateCtx {

    private final Context context;
    private final EventBus eventBus;
    private final Cache<String, Optional<?>> cache;

    public StateCtx(Context context) {
        this.context = context;
        this.eventBus = new EventBus();
        this.cache = CacheBuilder.newBuilder().maximumSize(1000).build();
    }

    public Context getContext() {
        return context;
    }

    public abstract <T> Object get(State<T> state);

    public abstract <T> void set(State<T> state, T value);

    public abstract <T> boolean isSet(State<T> state);

    public abstract <T> void clear(State<T> state);

    public EventBus getEventBus() {
        return eventBus;
    }

    public void registerChangedEventObserver(Object observer) {
        if (!getEventBus().isRegistered(observer)) {
            getEventBus().register(observer);
        }
    }

    public void unregisterChangedEventObserver(Object observer) {
        if (getEventBus().isRegistered(observer)) {
            getEventBus().unregister(observer);
        }
    }

    public void notifyChangedEvent(State<?> state) {
        getEventBus().post(new StateChangedEvent(state));
    }

    public void notifyChangedEvent(MapState<?> state, String key) {
        getEventBus().post(new MapStateChangedEvent(state, key));
    }

    public Cache<String, Optional<?>> getCache() {
        return cache;
    }
}
