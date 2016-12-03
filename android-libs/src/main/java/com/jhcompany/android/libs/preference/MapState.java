package com.jhcompany.android.libs.preference;

import java.util.Set;

/**
 * 서로 다른 키로 여러개의 값이 저장되어야할때 이용됩니다. 기존 {@link State}는 하나의 값만 저장이 가능하지만,
 * {@link MapState}는 키에 따라 다양한 값을 저장이 가능합니다.
 */
public interface MapState<T> extends ManagableState {

    /**
     * 저장된 모든 키값을 가져온다.
     */
    public Set<String> keys(StateCtx stateCtx);

    /**
     * 특정 키로 저장된 값을 가져온다.
     * 값을 저장한적이 없다면 <code>null</code>이 리턴하게 된다.
     * null을 저장한 경우에도 <code>null</code>이 리턴된다.
     */
    public T get(StateCtx stateCtx, String key);

    /**
     * 특정 키와 연관된 값을 저장한다.
     * 한번이라도 put하면 {@link #contains(StateCtx, String)} 메서드에서 <code>true</code>를 리턴한다.
     */
    public void put(StateCtx stateCtx, String key, T value);

    /**
     * 값을 삭제한다. 값을 삭제하게 되면,
     * {@link #contains(StateCtx, String)} 메서드에서 <code>false</code>를 리턴한다.
     */
    public void remove(StateCtx stateCtx, String key);

    /**
     * 값이 저장되어 있는지 확인한다.
     */
    public boolean contains(StateCtx stateCtx, String key);
}
