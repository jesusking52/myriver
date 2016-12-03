package com.jhcompany.android.libs.preference;

/**
 * {@link States}에서 관리되는 인터페이스
 */
public interface ManagableState {
    String DEFAULT_STATES_NAME = "com.riverauction.riverauction.app";

    /**
     * {@link States} 의 이름을 리턴한다.
     *
     * @return
     */
    String statesName();

    /**
     * {@link State}의 이름을 리턴한다.
     * 기본적으로 한 {@link States}에는 하나의 이름으로만 등록이 되어야 한다.
     */
    String name();

    /**
     * 저장된 값을 초기화 시킨다.
     */
    void clear(StateCtx stateCtx);
}
