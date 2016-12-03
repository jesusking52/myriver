package com.riverauction.riverauction.feature.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.riverauction.riverauction.R;
import com.riverauction.riverauction.api.model.CLesson;
import com.riverauction.riverauction.api.model.CStudent;
import com.riverauction.riverauction.api.model.CTeacher;
import com.riverauction.riverauction.api.model.CUser;
import com.riverauction.riverauction.api.model.CUserType;
import com.riverauction.riverauction.feature.utils.DataUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LessonInfoView extends LinearLayout {

    @Bind(R.id.lesson_info_level_container) View levelContainer;
    @Bind(R.id.lesson_info_career_container) View careerContainer;

    @Bind(R.id.lesson_info_level_view) TextView levelView;
    @Bind(R.id.lesson_info_career_view) TextView careerView;
    @Bind(R.id.lesson_info_available_days_of_week_view) TextView availableDayOfWeekView;
    @Bind(R.id.lesson_info_class_available_count_view) TextView lessonInfoclassAvailableCountView;
    @Bind(R.id.lesson_info_class_time_view) TextView classTimeView;

    public LessonInfoView(Context context) {
        super(context);
        initialize(context);
    }

    public LessonInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public LessonInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_lesson_info_view, this, true);

        ButterKnife.bind(this);
    }

    public void setContent(CUser user) {
        if (user == null) {
            return;
        }

        if (CUserType.STUDENT == user.getType()) {
            setStudentContent(user.getStudent());
        } else {
            setTeacherContent(user.getTeacher());
        }
    }

    public void setContent(CLesson lesson) {
        if (lesson == null) {
            return;
        }

        careerContainer.setVisibility(GONE);
        if (lesson.getLevel() != null) {
            levelView.setText(DataUtils.convertStudentLevelToString(getContext(), lesson.getLevel()));
        }

        if (lesson.getAvailableDaysOfWeek() != null) {
            availableDayOfWeekView.setText(DataUtils.convertAvailableDaysOfWeekToString(getContext(), lesson.getAvailableDaysOfWeek()));
        }

        if (lesson.getClassAvailableCount() != null) {
            lessonInfoclassAvailableCountView.setText(getResources().getString(R.string.common_class_available_count_unit, lesson.getClassAvailableCount()));
        }

        if (lesson.getClassTime() != null) {
            classTimeView.setText(String.valueOf(lesson.getClassTime()) + getContext().getString(R.string.common_count_time));
        }
    }

    private void setTeacherContent(CTeacher teacher) {
        levelContainer.setVisibility(GONE);
        if (teacher.getCareer() != null) {
            careerView.setText(DataUtils.convertCareerToString(teacher.getCareer()));
        }

        if (teacher.getAvailableDaysOfWeek() != null) {
            availableDayOfWeekView.setText(DataUtils.convertAvailableDaysOfWeekToString(getContext(), teacher.getAvailableDaysOfWeek()));
        }

        if (teacher.getClassAvailableCount() != null) {
            lessonInfoclassAvailableCountView.setText(getResources().getString(R.string.common_class_available_count_unit, teacher.getClassAvailableCount()));
        }

        if (teacher.getClassTime() != null) {
            classTimeView.setText(String.valueOf(teacher.getClassTime()) + getContext().getString(R.string.common_count_time));
        }
    }

    private void setStudentContent(CStudent student) {
        careerContainer.setVisibility(GONE);
        if (student.getLevel() != null) {
            levelView.setText(DataUtils.convertStudentLevelToString(getContext(), student.getLevel()));
        }

        if (student.getAvailableDaysOfWeek() != null) {
            availableDayOfWeekView.setText(DataUtils.convertAvailableDaysOfWeekToString(getContext(), student.getAvailableDaysOfWeek()));
        }

        if (student.getClassAvailableCount() != null) {
            lessonInfoclassAvailableCountView.setText(getResources().getString(R.string.common_class_available_count_unit, student.getClassAvailableCount()));
        }

        if (student.getClassTime() != null) {
            classTimeView.setText(String.valueOf(student.getClassTime()) + getContext().getString(R.string.common_count_time));
        }
    }
}
