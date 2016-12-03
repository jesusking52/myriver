package com.riverauction.riverauction.data;

import com.jhcompany.android.libs.preference.StateCtx;
import com.jhcompany.android.libs.utils.Lists2;
import com.riverauction.riverauction.api.model.CLesson;
import com.riverauction.riverauction.api.model.CLessonBidding;
import com.riverauction.riverauction.api.model.CLessonFavorite;
import com.riverauction.riverauction.api.model.CNotification;
import com.riverauction.riverauction.api.model.CReceipt;
import com.riverauction.riverauction.api.model.CReview;
import com.riverauction.riverauction.api.model.CSubject;
import com.riverauction.riverauction.api.model.CSubjectGroup;
import com.riverauction.riverauction.api.model.CUser;
import com.riverauction.riverauction.api.model.CUserFavorite;
import com.riverauction.riverauction.api.service.APISuccessResponse;
import com.riverauction.riverauction.api.service.auth.AuthService;
import com.riverauction.riverauction.api.service.auth.request.CertifyPhoneNumberRequest;
import com.riverauction.riverauction.api.service.auth.request.EmailCredentialRequest;
import com.riverauction.riverauction.api.service.auth.request.SignUpRequest;
import com.riverauction.riverauction.api.service.auth.request.TeacherReviewRequest;
import com.riverauction.riverauction.api.service.auth.response.IssueTokenResult;
import com.riverauction.riverauction.api.service.auth.response.SignUpResult;
import com.riverauction.riverauction.api.service.info.InfoService;
import com.riverauction.riverauction.api.service.lesson.LessonService;
import com.riverauction.riverauction.api.service.lesson.params.GetLessonsParams;
import com.riverauction.riverauction.api.service.lesson.request.LessonBiddingRequest;
import com.riverauction.riverauction.api.service.payment.PaymentService;
import com.riverauction.riverauction.api.service.review.ReviewService;
import com.riverauction.riverauction.api.service.review.params.GetReviewParams;
import com.riverauction.riverauction.api.service.teacher.TeacherService;
import com.riverauction.riverauction.api.service.teacher.params.GetTeachersParams;
import com.riverauction.riverauction.api.service.user.UserService;
import com.riverauction.riverauction.api.service.user.request.UserGCMAddRequest;
import com.riverauction.riverauction.api.service.user.request.UserPatchRequest;
import com.riverauction.riverauction.api.service.user.request.UserPreferencesRequest;
import com.riverauction.riverauction.states.UserStates;
import com.riverauction.riverauction.states.localmodel.MSubjectGroups;

import java.io.File;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit.mime.TypedFile;
import rx.Observable;

@Singleton
public class DataManager {

    private final StateCtx stateCtx;
    private final AuthService authService;
    private final TeacherService teacherService;
    private final LessonService lessonService;
    private final UserService userService;
    private final InfoService infoService;
    private final PaymentService paymentService;
    private final ReviewService reviewService;
    @Inject
    public DataManager(StateCtx stateCtx, AuthService authService, TeacherService teacherService, LessonService lessonService,
                       UserService userService, InfoService infoService, PaymentService paymentService, ReviewService reviewService) {
        this.stateCtx = stateCtx;
        this.authService = authService;
        this.teacherService = teacherService;
        this.lessonService = lessonService;
        this.userService = userService;
        this.infoService = infoService;
        this.paymentService = paymentService;
        this.reviewService = reviewService;
    }

    public Observable<SignUpResult> signUp(SignUpRequest request) {
        return authService.signUp(request).map(APISuccessResponse::getResult);
    }

    public Observable<IssueTokenResult> issuseToken(EmailCredentialRequest request) {
        return authService.issueToken(request).map(APISuccessResponse::getResult);
    }

    public Observable<Boolean> requestAuthNumber(CertifyPhoneNumberRequest request) {
        return authService.requestAuthNumber(request).map(APISuccessResponse::getResult);
    }

    public Observable<Boolean> certifyAuthNumber(CertifyPhoneNumberRequest request) {
        return authService.certifyAuthNumber(request).map(APISuccessResponse::getResult);
    }

    public Observable<APISuccessResponse<List<CUser>>> getTeachers(Integer userId, GetTeachersParams params) {
        return teacherService.getTeachers(userId, params);
    }

    // lesson service
    public Observable<APISuccessResponse<List<CLesson>>> getLessons(GetLessonsParams params) {
        return lessonService.getLessons(params);
    }

    public Observable<CLesson> postLesson() {
        return lessonService.postLesson("").map(APISuccessResponse::getResult);
    }

    public Observable<CLesson> cancelLesson(Integer lessonId) {
        return lessonService.cancelLesson(lessonId, "").map(APISuccessResponse::getResult);
    }

    public Observable<CLesson> getLesson(Integer lessonId) {
        return lessonService.getLesson(lessonId).map(APISuccessResponse::getResult);
    }

    public Observable<APISuccessResponse<List<CLessonBidding>>> getLessonBiddings(Integer lessonId, Integer nextToken) {
        return lessonService.getLessonBiddings(lessonId, nextToken);
    }

    public Observable<CLessonBidding> postLessonBiddings(Integer lessonId, LessonBiddingRequest request) {
        return lessonService.postLessonBiddings(lessonId, request).map(APISuccessResponse::getResult);
    }

    public Observable<CLessonFavorite> postLessonFavorites(Integer lessonId) {
        return lessonService.postLessonFavorites(lessonId, "").map(APISuccessResponse::getResult);
    }

    public Observable<Void> deleteLessonFavorites(Integer lessonId) {
        return lessonService.deleteLessonFavorites(lessonId).map(APISuccessResponse::getResult);
    }

    public Observable<CUser> postSelectTeacher(Integer lessonId, Integer teacherId) {
        return lessonService.postSelectTeacher(lessonId, teacherId, "").map(APISuccessResponse::getResult);
    }

    // user service
    public Observable<CUser> getUser(Integer userId) {
        return userService.getUser(userId).map(APISuccessResponse::getResult);
    }

    public Observable<CUser> getUserProfile(Integer userId, Boolean phoneNumber) {
        return userService.getUserProfile(userId, phoneNumber).map(APISuccessResponse::getResult);
    }

    public Observable<APISuccessResponse<List<CNotification>>> getNotifications(Integer userId, Integer nextToken) {
        return userService.getNotifications(userId, nextToken);
    }

    public Observable<CUser> patchUser(Integer userId, UserPatchRequest request) {
        return userService.patchUser(userId, request).map(response -> {
            CUser user = response.getResult();
            UserStates.USER.set(stateCtx, user);
            return user;
        });
    }

    public Observable<APISuccessResponse<List<CLesson>>> getActiveLessons(Integer userId, Integer nextToken) {
        return userService.getActiveLessons(userId, nextToken);
    }

    public Observable<APISuccessResponse<List<CLesson>>> getHistoryLessons(Integer userId, Integer nextToken) {
        return userService.getHistoryLessons(userId, nextToken);
    }

    public Observable<APISuccessResponse<List<CUserFavorite>>> getUserFavorites(Integer userId, Integer nextToken) {
        return userService.getUserFavorites(userId, nextToken);
    }

    public Observable<APISuccessResponse<List<CLessonFavorite>>> getLessonFavorites(Integer userId, Integer nextToken) {
        return userService.getLessonsFavorites(userId, nextToken);
    }

    public Observable<CUserFavorite> postUserFavorites(Integer userId) {
        return userService.postUserFavorites(userId, "").map(APISuccessResponse::getResult);
    }

    public Observable<Void> deleteUserFavorites(Integer userId) {
        return userService.deleteUserFavorites(userId).map(APISuccessResponse::getResult);
    }

    public Observable<Boolean> postGCM(Integer userId, UserGCMAddRequest request) {
        return userService.postGCM(userId, request).map(APISuccessResponse::getResult);
    }

    public Observable<CUser> checkPhoneNumber(Integer userId) {
        return userService.checkPhoneNumber(userId, "").map(APISuccessResponse::getResult);
    }

    public Observable<CUser> postPreferences(Integer userId, UserPreferencesRequest request) {
        return userService.postPreferences(userId, request).map(APISuccessResponse::getResult);
    }

    public Observable<CUser> postProfilePhoto(Integer userId, final File localFile) {
        TypedFile file = new TypedFile("multipart/form-data", localFile);
        return userService.postProfilePhoto(userId, file).map(APISuccessResponse::getResult);
    }

    // InfoService
    public Observable<List<CSubjectGroup>> getSubjectGroups() {
        return infoService.getSubjectGroups().map(listAPISuccessResponse -> {
            List<CSubjectGroup> subjectGroups = listAPISuccessResponse.getResult();
            if (!Lists2.isNullOrEmpty(subjectGroups)) {
                for (CSubjectGroup subjectGroup : subjectGroups) {
                    for (CSubject subject : subjectGroup.getSubjects()) {
                        subject.setSubjectGroupName(subjectGroup.getName());
                    }
                }
                UserStates.SUBJECT_GROUPS.set(stateCtx, new MSubjectGroups(subjectGroups));
            }
            return subjectGroups;
        });
    }

    // PaymentService
    public Observable<Boolean> purchaseCoin(CReceipt receipt) {
        return paymentService.purchaseCoin(receipt).map(APISuccessResponse::getResult);
    }
    //리뷰쓰기
    public Observable<Boolean> writeReview(Integer userId, TeacherReviewRequest request) {
        return userService.writeReview(userId, request).map(APISuccessResponse::getResult);
    }

    //리뷰리스트
    public Observable<APISuccessResponse<List<CReview>>> getReviews(Integer teacherId, GetReviewParams params) {
        return reviewService.getReviews(teacherId, params);
    }
}
