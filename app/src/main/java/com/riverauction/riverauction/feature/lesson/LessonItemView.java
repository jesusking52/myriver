package com.riverauction.riverauction.feature.lesson;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.riverauction.riverauction.R;
import com.riverauction.riverauction.api.model.CLesson;
import com.riverauction.riverauction.api.model.CLessonStatus;
import com.riverauction.riverauction.feature.photo.ProfileImageView;
import com.riverauction.riverauction.feature.utils.DataUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LessonItemView extends LinearLayout {
    @Bind(R.id.item_lesson_profile_image) ProfileImageView profileImageView;
    @Bind(R.id.item_lesson_name_and_gender) TextView nameAndGenderView;
    @Bind(R.id.item_lesson_available_subjects) TextView availableSubjectsView;
    @Bind(R.id.item_lesson_location) TextView locationView;
    @Bind(R.id.item_lesson_preferred_price) TextView preferredPriceView;

    @Bind(R.id.item_lesson_bidding) View biddingStatusView;
    @Bind(R.id.item_lesson_dealing) View dealingStatusView;
    @Bind(R.id.item_lesson_canceled) View canceledStatusView;
    @Bind(R.id.item_lesson_finished) View finishedStatusView;
    @Bind(R.id.item_lesson_bidding_remain_time) TextView biddingRemainTimeView;
    @Bind(R.id.item_lesson_dealing_remain_time) TextView dealingRemainTimeView;
    @Bind(R.id.item_lesson_canceled_created_at) TextView canceledCreatedAtView;
    @Bind(R.id.item_lesson_finished_created_at) TextView finishedCreatedAtView;

    public LessonItemView(Context context) {
        super(context);
        initialize(context);
    }

    public LessonItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public LessonItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_item_lesson_list, this, true);
        ButterKnife.bind(this);
        setGravity(Gravity.CENTER_VERTICAL);
    }

    public void setContent(CLesson lesson) {
        if (lesson == null || lesson.getOwner() == null) {
            return;
        }
        profileImageView.loadProfileImage(lesson.getOwner());

        if (!Strings.isNullOrEmpty(lesson.getName()) || lesson.getGender() != null) {
            String nameAndGenderString = DataUtils.convertToAnonymousName(lesson.getName()) + " " + DataUtils.convertGenderToShortString(getContext(), lesson.getGender());
            nameAndGenderView.setText(nameAndGenderString);
        }

        String availableSubjectString = DataUtils.convertSubjectToString(lesson.getAvailableSubjects());
        if (!Strings.isNullOrEmpty(availableSubjectString)) {
            availableSubjectsView.setText(availableSubjectString);
        }
        if (lesson.hasAddress()) {
            locationView.setText(DataUtils.convertLocationToString(lesson.getLocation()));
        }

        if (lesson.getPreferredPrice() != null) {
            preferredPriceView.setText(getResources().getString(R.string.item_lesson_preferred_price, lesson.getPreferredPrice()));
        }

        setLessonStatusAndTime(lesson);
    }

    private void setLessonStatusAndTime(CLesson lesson) {
        CLessonStatus lessonStatus = lesson.getStatus();
        if (lessonStatus == null) {
            return;
        }
        biddingStatusView.setVisibility(GONE);
        dealingStatusView.setVisibility(GONE);
        canceledStatusView.setVisibility(GONE);
        finishedStatusView.setVisibility(GONE);
        switch (lessonStatus) {
            case BIDDING: {
                biddingStatusView.setVisibility(VISIBLE);
                biddingRemainTimeView.setText(getContext().getString(R.string.common_remain_unit, DataUtils.convertRemainTimeToString(getContext(), lesson.getExpiresIn())));
                break;
            }
            case DEALING: {
                dealingStatusView.setVisibility(VISIBLE);
                dealingRemainTimeView.setText(getContext().getString(R.string.common_remain_unit, DataUtils.convertRemainTimeToString(getContext(), lesson.getExpiresIn())));
                break;
            }
            case CANCELED: {
                canceledStatusView.setVisibility(VISIBLE);
                canceledCreatedAtView.setText(DateUtils.getRelativeTimeSpanString(lesson.getCreatedAt()));
                break;
            }
            case FINISHED: {
                finishedStatusView.setVisibility(VISIBLE);
                finishedCreatedAtView.setText(DateUtils.getRelativeTimeSpanString(lesson.getCreatedAt()));
                break;
            }
        }
    }
}
