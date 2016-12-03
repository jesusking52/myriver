package com.riverauction.riverauction.feature.teacher;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.riverauction.riverauction.R;
import com.riverauction.riverauction.api.model.CLessonBidding;
import com.riverauction.riverauction.api.model.CTeacher;
import com.riverauction.riverauction.api.model.CUser;
import com.riverauction.riverauction.feature.photo.ProfileImageView;
import com.riverauction.riverauction.feature.utils.DataUtils;
import com.riverauction.riverauction.feature.utils.DateUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TeacherItemView extends LinearLayout {
    @Bind(R.id.item_teacher_profile_image) ProfileImageView profileImageView;
    @Bind(R.id.item_teacher_name_and_gender) TextView nameAndGenderView;
    @Bind(R.id.item_teacher_university_and_major) TextView universityAndMajorView;
    @Bind(R.id.item_teacher_available_subjects) TextView availableSubjectsView;
    @Bind(R.id.item_teacher_class_available_count) TextView classAvailableCountView;
    @Bind(R.id.item_teacher_location) TextView locationView;
    @Bind(R.id.item_teacher_price_title) TextView priceTitleView;
    @Bind(R.id.item_teacher_preferred_price) TextView priceView;

    public TeacherItemView(Context context) {
        super(context);
        initialize(context);
    }

    public TeacherItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public TeacherItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_item_teacher_list, this, true);
        ButterKnife.bind(this);
        setGravity(Gravity.CENTER_VERTICAL);
    }

    public void setContent(CLessonBidding bidding) {
        setContent(bidding.getUser());

        // bidding 이 있으면 희망 금액이 아니라 입찰 금액과 입찰 시점이 나온다
        priceTitleView.setText(R.string.teacher_item_bidding_price);
        classAvailableCountView.setText(DateUtils.convertBiddingDateToString(getContext(), bidding.getCreatedAt()));
        String price = getContext().getString(R.string.common_price_big_unit, bidding.getPrice());
        SpannableStringBuilder builder = new SpannableStringBuilder(price);
        builder.setSpan(new TextAppearanceSpan(getContext(), R.style.PriceSmallText), price.length() - 2, price.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        priceView.setTextColor(getContext().getResources().getColor(R.color.river_auction_leafy_green));
        priceView.setText(builder);
        classAvailableCountView.setText(android.text.format.DateUtils.getRelativeTimeSpanString(bidding.getCreatedAt()));
    }

    public void setContent(CUser user) {
        if (user == null || user.getTeacher() == null) {
            return;
        }
        CTeacher teacher = user.getTeacher();

        profileImageView.loadProfileImage(user);

        if (!Strings.isNullOrEmpty(user.getName()) || user.getGender() != null) {
            String nameAndGenderString = DataUtils.convertToAnonymousName(user.getName()) + " " + DataUtils.convertGenderToShortString(getContext(), user.getGender());
            nameAndGenderView.setText(nameAndGenderString);
        }

        if (!Strings.isNullOrEmpty(teacher.getUniversity()) || !Strings.isNullOrEmpty(teacher.getMajor())) {
            universityAndMajorView.setText(teacher.getUniversity() + " " + teacher.getMajor());
        }

        String availableSubjectString = DataUtils.convertSubjectToString(teacher.getAvailableSubjects());
        if (!Strings.isNullOrEmpty(availableSubjectString)) {
            availableSubjectsView.setText(availableSubjectString);
        }
        if (teacher.getClassAvailableCount() != null) {
            classAvailableCountView.setText(getResources().getString(R.string.common_class_available_count_unit, teacher.getClassAvailableCount()));
        }
        if (user.hasAddress()) {
            locationView.setText(DataUtils.convertLocationToString(user.getLocation()));
        }

        if (teacher.getPreferredPrice() != null) {
            String price = getContext().getString(R.string.common_price_big_unit, teacher.getPreferredPrice());
            SpannableStringBuilder builder = new SpannableStringBuilder(price);
            builder.setSpan(new TextAppearanceSpan(getContext(), R.style.PriceSmallText), price.length() - 2, price.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            priceView.setText(builder);
        }
    }
}
