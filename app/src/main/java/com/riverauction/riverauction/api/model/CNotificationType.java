package com.riverauction.riverauction.api.model;

/**
 *
 * - 상대방이 찜한 경우
 * - {name}님이 회원님을 찜했습니다.
 *
 * - 경매 시작
 * - 경매 시작한 학생에게 '경매를 시작하였습니다.'
 *
 * - 입찰 시
 * - 경매 시작한 학생에게 '{name}님이 입찰하였습니다.'
 *
 * - 경매 취소
 * - 참여한 선생님들에게 '{name}의 경매가 취소되었습니다.'
 *
 * - 경매 종료
 * - 참여한 선생님들에게 '{name}의 경매가 마감되었습니다.'
 * - 선택된 선생님에게 '{name}님이 회원님을 최종 선택하였습니다.'
 *
 * - 연락처 확인
 * - 선생님에게 '{name}님이 회원님의 연락처를 확인했습니다.'
 * - 학생에게 '{name}님이 회원님의 연락처를 확인했습니다.'
 */
public enum CNotificationType {
    USER_FAVORITED,
    START_BIDDING,
    BIDDING_LESSON,
    LESSON_CANCELED,
    LESSON_FINISHED,
    USER_SELECTED,
    PHONE_NUMBER_CHECKED;
}
