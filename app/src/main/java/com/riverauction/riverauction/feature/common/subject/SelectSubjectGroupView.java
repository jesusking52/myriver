package com.riverauction.riverauction.feature.common.subject;

import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.jhcompany.android.libs.utils.DisplayUtils;
import com.riverauction.riverauction.R;
import com.riverauction.riverauction.api.model.CSubject;
import com.riverauction.riverauction.api.model.CSubjectGroup;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SelectSubjectGroupView extends LinearLayout {
    private static int ROW_HEIGHT;
    private static int SUBJECT_TEXT_VIEW_WIDTH;

    @Bind(R.id.subject_group_title) TextView subjectGroupTitleView;

    private LayoutInflater inflater;
    private List<CSubject> selectedSubjects;

    public SelectSubjectGroupView(Context context) {
        super(context);
        initialize(context);
    }

    public SelectSubjectGroupView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public SelectSubjectGroupView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        setOrientation(VERTICAL);
        inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        inflater.inflate(R.layout.layout_select_subject_group, this);
        ButterKnife.bind(this);
        selectedSubjects = Lists.newArrayList();

        ROW_HEIGHT = DisplayUtils.getPixelFromDP(context, 46);
        SUBJECT_TEXT_VIEW_WIDTH = DisplayUtils.getDisplayWidthPixel(context, 1) / 3;
    }

    /**
     * 과목 그룹과 과목들을 그린다
     * @param subjectGroup 과목 그룹
     * @param selectedSubjects 미리 선택된 과목들
     */
    public void setSubjectGroups(CSubjectGroup subjectGroup, List<CSubject> selectedSubjects) {
        subjectGroupTitleView.setText(subjectGroup.getName());
        List<CSubject> subjects = subjectGroup.getSubjects();

        // 제일 마지막에 있는 linearLayout;
        LinearLayout lastLinearLayout = null;
        for (int i = 0; i < subjects.size(); i++) {
            CSubject subject = subjects.get(i);
            if (i % 3 == 0) {
                lastLinearLayout = addLinearLayout();
            }
            addSubject(lastLinearLayout, subject, selectedSubjects);
        }
        addMarginBottomView();
    }

    public List<CSubject> getSelectedSubjects() {
        return selectedSubjects;
    }

    private LinearLayout addLinearLayout() {
        LinearLayout newLine = new LinearLayout(getContext());
        newLine.setOrientation(HORIZONTAL);
        newLine.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ROW_HEIGHT));
        newLine.setGravity(Gravity.CENTER_VERTICAL);
        this.addView(newLine);
        return newLine;
    }

    private void addSubject(LinearLayout linearLayout, CSubject subject, List<CSubject> selectedSubjects) {
        if (linearLayout == null) {
            return;
        }
        TextView subjectTextView = new TextView(getContext());
        subjectTextView.setBackgroundResource(R.drawable.bg_item_subejct);
        subjectTextView.setGravity(Gravity.CENTER);
        subjectTextView.setText(subject.getName());
        subjectTextView.setTextColor(getResources().getColorStateList(R.color.item_subject_text_color));
        subjectTextView.setLayoutParams(new LinearLayoutCompat.LayoutParams(SUBJECT_TEXT_VIEW_WIDTH, ViewGroup.LayoutParams.MATCH_PARENT));

        if (selectedSubjects.contains(subject)) {
            subjectTextView.setSelected(true);
            this.selectedSubjects.add(subject);
        }

        if (subject != null) {
            subjectTextView.setOnClickListener(v -> {
                if (subjectTextView.isSelected()) {
                    // 취소
                    subjectTextView.setSelected(false);
                    this.selectedSubjects.remove(subject);
                } else {
                    // 선택
                    subjectTextView.setSelected(true);
                    this.selectedSubjects.add(subject);
                }
            });
        }

        linearLayout.addView(subjectTextView);
    }

    private void addMarginBottomView() {
        View marginBottomView = new View(getContext());
        marginBottomView.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DisplayUtils.getPixelFromDP(getContext(), 10f)));
        marginBottomView.setBackgroundColor(getResources().getColor(R.color.river_auction_pale_grey));
        this.addView(marginBottomView);
    }
}
