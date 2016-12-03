package com.jhcompany.android.libs.preference;


public interface State<T> extends ManagableState {

    /**
     * {@link State}의 이름을 리턴한다.
     * 기본적으로 한 {@link States}에는 하나의 이름으로만 등록이 되어야 한다.
     */
    String name();

    /**
     * 저장된 값을 가져온다.
     * 값을 설정한적 없다면 {@link State} 구현체의 인스턴스를 만들때 설정한 기본값이 나올 수 있다.
     */
    T get(StateCtx stateCtx);

    /**
     * 저장된 값을 조회한다.
     * 한번이라도 set하면 {@link #isSet(StateCtx)}함수에서 <code>true</code>를 리턴한다.
     */
    void set(StateCtx stateCtx, T value);

    /**
     * 값이 저장되어 있는지 확인한다.
     * 이 함수가 <code>false</code>를 리턴하더라도 {@link #get(StateCtx)}을 하면 기본값이 나올 수 있다.
     */
    boolean isSet(StateCtx stateCtx);

    /**
     * 설정된 기본 값을 가져온다.
     */
    T defaultValue();

    /**
     * 저장된 값의 Type 을 가져온다.
     */
    Class<T> type();
}
