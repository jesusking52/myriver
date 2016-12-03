package com.riverauction.riverauction.feature.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.jhcompany.android.libs.preference.StateCtx;
import com.jhcompany.android.libs.utils.Lists2;
import com.riverauction.riverauction.R;
import com.riverauction.riverauction.RiverAuctionApplication;
import com.riverauction.riverauction.api.model.CLesson;
import com.riverauction.riverauction.api.model.CStudent;
import com.riverauction.riverauction.api.model.CSubject;
import com.riverauction.riverauction.api.model.CTeacher;
import com.riverauction.riverauction.api.model.CUniversityStatus;
import com.riverauction.riverauction.api.model.CUser;
import com.riverauction.riverauction.api.model.CUserType;
import com.riverauction.riverauction.feature.photo.ProfileImageView;
import com.riverauction.riverauction.feature.utils.DataUtils;
import com.riverauction.riverauction.states.UserStates;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BasicInfoView extends LinearLayout {
    @Bind(R.id.basic_info_profile_image) ProfileImageView profileImageView;
    @Bind(R.id.basic_info_name_and_gender) TextView nameAndGenderView;
    @Bind(R.id.basic_info_university_and_major) TextView universityAndMajorView;
    @Bind(R.id.basic_info_university_status_view) TextView universityStatusView;
    @Bind(R.id.basic_info_location) TextView locationView;
    @Bind(R.id.basic_info_phone_number) TextView phoneNumberView;
    @Bind(R.id.basic_info_subject_curriculum_subject_container) View curriculumContainer;
    @Bind(R.id.basic_info_subject_curriculum_subject_text_view) TextView curriculumSubjectView;
    @Bind(R.id.basic_info_subject_foreign_language_container) View foreignLanguageContainer;
    @Bind(R.id.basic_info_subject_foreign_language_text_view) TextView foreignLanguageView;
    @Bind(R.id.basic_info_subject_music_container) View musicContainer;
    @Bind(R.id.basic_info_subject_music_text_view) TextView musicView;

    @Bind(R.id.basic_info_price_container) View priceContainer;
    @Bind(R.id.basic_info_price_title_view) TextView priceTitleView;
    @Bind(R.id.basic_info_preferred_price_view) TextView preferredPriceView;
    @Bind(R.id.basic_info_price_seperation_view) View priceSeperationView;
    @Bind(R.id.basic_info_my_price_view) TextView myPriceView;

    private StateCtx stateCtx;
    private CUser me;

    public BasicInfoView(Context context) {
        super(context);
        initialize(context);
    }

    public BasicInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public BasicInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_basic_info_view, this, true);
        stateCtx = RiverAuctionApplication.getApplication().getComponent().stateCtx();
        me = UserStates.USER.get(stateCtx);
        ButterKnife.bind(this);
    }

    /**
     * LessonDetailActivity 에서 사용
     * @param lesson
     */
    public void setContent(CLesson lesson) {
        if (lesson == null) {
            return;
        }

        profileImageView.loadProfileImage(lesson.getOwner());

        if (!Strings.isNullOrEmpty(lesson.getName()) || lesson.getGender() != null) {
            String nameAndGenderString = DataUtils.convertToAnonymousName(lesson.getName()) + " " + DataUtils.convertGenderToShortString(getContext(), lesson.getGender());
            nameAndGenderView.setText(nameAndGenderString);
        }
        if (lesson.getLocation() != null) {
            locationView.setText(DataUtils.convertLocationToString(lesson.getLocation()));
        }

        if (lesson.getGrade() == null) {
            universityAndMajorView.setText(DataUtils.convertStudentStatusToString(getContext(), lesson.getStudentStatus()));
        } else {
            String grade = getContext().getString(R.string.common_grade_unit, lesson.getGrade());
            universityAndMajorView.setText(DataUtils.convertStudentStatusToString(getContext(), lesson.getStudentStatus()) + " " + grade);
        }

        universityStatusView.setText(getContext().getString(R.string.common_student_department_type) + "-" + DataUtils.convertStudentDepartmentTypeToString(getContext(), lesson.getDepartment()));
        setSubjects(lesson.getAvailableSubjects());

        priceContainer.setVisibility(GONE);

        setPrice(lesson);

        setPhoneNumberView(lesson.getOwner(), lesson);
    }

    private void setPhoneNumberView(CUser user, CLesson lesson) {
        CUserType myType = me.getType();
        CUserType userType = user.getType();
        if (me.equals(user)) {
            // 내가 내자신을 조회할때
            phoneNumberView.setText(me.getPhoneNumber());
            phoneNumberView.setTextColor(getResources().getColor(R.color.river_auction_greyish_brown));
            phoneNumberView.setTextSize(15);
            return;
        }

        if (myType == userType) {
            // 같은 타입의 유저
            phoneNumberView.setText(R.string.common_phone_number);
            phoneNumberView.setTextColor(getResources().getColor(R.color.river_auction_greyish_brown));
            phoneNumberView.setTextSize(15);
        } else {
            // 다른 타입의 유저
            if (userType == CUserType.STUDENT) {
                // 대상이 학생, 내가 선생
                if (lesson == null) {
                    return;
                }
                CUser selectedTeacher = lesson.getSelectedTeacher();
                if (selectedTeacher == null) {
                    // 경매에 선택된 사람이 없을 때
                    phoneNumberView.setText(R.string.basic_info_student_no_phone_number);
                    phoneNumberView.setTextColor(getResources().getColor(R.color.river_auction_greyish));
                    phoneNumberView.setTextSize(11);
                } else {
                    if (me.getId().equals(selectedTeacher.getId())) {
                        // 경매에 내가 매칭됬을 때
                        phoneNumberView.setText(lesson.getOwner().getPhoneNumber());
                        phoneNumberView.setTextColor(getResources().getColor(R.color.river_auction_dodger_blue));
                        phoneNumberView.setTextSize(15);
                    } else {
                        // 경매에 내가 매칭되지 않았을 때
                        phoneNumberView.setText(R.string.basic_info_student_no_phone_number);
                        phoneNumberView.setTextColor(getResources().getColor(R.color.river_auction_greyish));
                        phoneNumberView.setTextSize(11);
                    }
                }
            } else {
                // 대상이 선생, 내가 학생
                if (!Strings.isNullOrEmpty(user.getPhoneNumber())) {
                    // 폰 번호가 있음
                    phoneNumberView.setText(user.getPhoneNumber());
                    phoneNumberView.setTextColor(getResources().getColor(R.color.river_auction_dodger_blue));
                    phoneNumberView.setTextSize(15);
                } else {
                    // 폰 번호가 없음
                    phoneNumberView.setText(R.string.basic_info_teacher_no_phone_number);
                    phoneNumberView.setTextColor(getResources().getColor(R.color.river_auction_greyish));
                    phoneNumberView.setTextSize(11);
                }
            }
        }
    }

    /**
     * 경매 만드는 화면에서 사용
     */
    public void setContent(CUser user, boolean showPrice) {
        setContent(user);
        if (showPrice) {
            setPrice(user);
            priceContainer.setVisibility(VISIBLE);
        } else {
            priceContainer.setVisibility(GONE);
        }
    }

    public void setContent(CUser user) {
        if (user == null) {
            return;
        }

        profileImageView.loadProfileImage(user);

        if (!Strings.isNullOrEmpty(user.getName()) || user.getGender() != null) {
            String nameAndGenderString = DataUtils.convertToAnonymousName(user.getName()) + " " + DataUtils.convertGenderToShortString(getContext(), user.getGender());
            nameAndGenderView.setText(nameAndGenderString);
        }
        if (user.getLocation() != null) {
            locationView.setText(DataUtils.convertLocationToString(user.getLocation()));
        }

        // 학생 / 선생님 분기
        if (user.getType() == CUserType.TEACHER) {
            setTeacherContent(user);
        } else {
            setStudentContent(user);
        }

        priceContainer.setVisibility(GONE);

        setPhoneNumberView(user, null);
    }

    private void setTeacherContent(CUser user) {
        CTeacher teacher = user.getTeacher();
        if (!Strings.isNullOrEmpty(teacher.getUniversity()) || !Strings.isNullOrEmpty(teacher.getMajor())) {
            universityAndMajorView.setText(teacher.getUniversity() + " " + teacher.getMajor());
        }
        if (CUniversityStatus.IN_SCHOOL == teacher.getUniversityStatus()) {
            universityStatusView.setText(R.string.common_in_school);
        } else if (CUniversityStatus.LEAVE_OF_ABSENCE == teacher.getUniversityStatus()) {
            universityStatusView.setText(R.string.common_leave_of_absence);
        } else if (CUniversityStatus.GRADUATION == teacher.getUniversityStatus()) {
            universityStatusView.setText(R.string.common_graduation);
        }
        setSubjects(teacher.getAvailableSubjects());
    }

    private void setStudentContent(CUser user) {
        CStudent student = user.getStudent();
        if (student.getGrade() == null) {
            universityAndMajorView.setText(DataUtils.convertStudentStatusToString(getContext(), student.getStudentStatus()));
        } else {
            String grade = getContext().getString(R.string.common_grade_unit, student.getGrade());
            universityAndMajorView.setText(DataUtils.convertStudentStatusToString(getContext(), student.getStudentStatus()) + " " + grade);
        }
        universityStatusView.setText(getContext().getString(R.string.common_student_department_type) + "-" + DataUtils.convertStudentDepartmentTypeToString(getContext(), student.getDepartment()));
        setSubjects(student.getAvailableSubjects());
    }

    /**
     * 희망 과목을 그려준다
     */
    private void setSubjects(List<CSubject> subjects) {
        List<CSubject> curriculum = Lists.newArrayList();
        List<CSubject> foreign = Lists.newArrayList();
        List<CSubject> music = Lists.newArrayList();
        for (CSubject subject : subjects) {
            if (subject.getGroupId() == null) {
                continue;
            }

            if (1 == subject.getGroupId()) {
                curriculum.add(subject);
            } else if (2 == subject.getGroupId()) {
                foreign.add(subject);
            } else if (3 == subject.getGroupId()) {
                music.add(subject);
            }
        }

        if (!Lists2.isNullOrEmpty(curriculum)) {
            curriculumContainer.setVisibility(VISIBLE);
            curriculumSubjectView.setText(DataUtils.convertSubjectToString(curriculum));
        } else {
            curriculumContainer.setVisibility(GONE);
        }

        if (!Lists2.isNullOrEmpty(foreign)) {
            foreignLanguageContainer.setVisibility(VISIBLE);
            foreignLanguageView.setText(DataUtils.convertSubjectToString(foreign));
        } else {
            foreignLanguageContainer.setVisibility(GONE);
        }

        if (!Lists2.isNullOrEmpty(music)) {
            musicContainer.setVisibility(VISIBLE);
            musicView.setText(DataUtils.convertSubjectToString(music));
        } else {
            musicContainer.setVisibility(GONE);
        }
    }

    private void setPrice(CUser user) {
        priceContainer.setVisibility(VISIBLE);
        priceTitleView.setText(R.string.lesson_detail_preferred_price);
        priceSeperationView.setVisibility(View.GONE);
        myPriceView.setVisibility(View.GONE);
        Integer price;
        if (user.getType() == CUserType.STUDENT) {
            price = user.getStudent().getPreferredPrice();
        } else {
            price = user.getTeacher().getPreferredPrice();
        }
        preferredPriceView.setText(getContext().getString(R.string.common_price_big_unit, price));
    }

    /**
     * setContent 에 Lesson 을 넘길때만 보여준다
     */
    private void setPrice(CLesson lesson) {
        priceContainer.setVisibility(VISIBLE);
        if (lesson.getIsBid() != null && !lesson.getIsBid()) {
            priceTitleView.setText(R.string.lesson_detail_preferred_price);
            priceSeperationView.setVisibility(View.GONE);
            myPriceView.setVisibility(View.GONE);
            preferredPriceView.setText(getContext().getString(R.string.common_price_big_unit, lesson.getPreferredPrice()));
        } else {
            priceTitleView.setText(R.string.lesson_detail_preferred_price_with_my_price);
            priceSeperationView.setVisibility(View.VISIBLE);
            myPriceView.setVisibility(View.VISIBLE);
            preferredPriceView.setText(getContext().getString(R.string.common_price_big_unit, lesson.getPreferredPrice()));
            myPriceView.setText(getContext().getString(R.string.common_price_big_unit, lesson.getBidding().getPrice()));
        }
    }
}
