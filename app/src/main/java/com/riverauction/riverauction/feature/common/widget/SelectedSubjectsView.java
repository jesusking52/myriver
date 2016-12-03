package com.riverauction.riverauction.feature.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jhcompany.android.libs.preference.StateCtx;
import com.jhcompany.android.libs.utils.Lists2;
import com.riverauction.riverauction.R;
import com.riverauction.riverauction.RiverAuctionApplication;
import com.riverauction.riverauction.api.model.CSubject;
import com.riverauction.riverauction.feature.utils.DataUtils;
import com.riverauction.riverauction.states.UserStates;
import com.riverauction.riverauction.states.localmodel.MSubjectGroups;

import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SelectedSubjectsView extends FrameLayout {
    @Bind(R.id.selected_subject_view) View selectedSubjectView;
    @Bind(R.id.selected_subject_result_container) LinearLayout selectedSubjectResultContainer;

    private LayoutInflater inflater;
    private StateCtx stateCtx;
    private List<CSubject> selectedSubjects = Lists.newArrayList();

    private OnClickSelectSubjectViewListener onClickSelectSubjectViewListener;

    public SelectedSubjectsView(Context context) {
        super(context);
        initialize(context);
    }

    public SelectedSubjectsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public SelectedSubjectsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_selected_subjects, this, true);
        stateCtx = RiverAuctionApplication.getApplication().getComponent().stateCtx();
        ButterKnife.bind(this);
    }

    public void setSelectedSubjects(List<CSubject> selectedSubjects) {
        this.selectedSubjects = selectedSubjects;
        if (!Lists2.isNullOrEmpty(selectedSubjects)) {
            // 선택된 과목이 있다
            selectedSubjectView.setVisibility(View.GONE);
            selectedSubjectResultContainer.setVisibility(View.VISIBLE);
            selectedSubjectResultContainer.removeAllViews();

            // 선택된 subject 들을 group 으로 나눈다
            HashMap<Integer, List<CSubject>> selectedSubjectGroupMap = Maps.newHashMap();
            for (CSubject subject : selectedSubjects) {
                Integer key = subject.getGroupId();
                if (selectedSubjectGroupMap.containsKey(key)) {
                    // 이미 키가 있다
                    List<CSubject> subjects = selectedSubjectGroupMap.get(key);
                    subjects.add(subject);
                } else {
                    selectedSubjectGroupMap.put(key, Lists.newArrayList(subject));
                }
            }

            // 그룹으로 나눈 subject 를 그린다
            for (Integer groupId : selectedSubjectGroupMap.keySet()) {
                View subjectGroupView = makeSelectedGroupView(getSubjectGroupName(groupId), selectedSubjectGroupMap.get(groupId));
                subjectGroupView.setOnClickListener(v -> {
                    if (onClickSelectSubjectViewListener != null) {
                        onClickSelectSubjectViewListener.onClick(selectedSubjects);
                    }
                });
                selectedSubjectResultContainer.addView(subjectGroupView);
            }
        } else {
            // 선택된 과목이 없다
            selectedSubjectView.setVisibility(View.VISIBLE);
            selectedSubjectResultContainer.setVisibility(View.GONE);
        }
    }

    private String getSubjectGroupName(Integer groupId) {
        MSubjectGroups subjectGroups = UserStates.SUBJECT_GROUPS.get(stateCtx);
        return subjectGroups.getSubjectGroupName(groupId);
    }

    private View makeSelectedGroupView(String subjectGroupName, List<CSubject> selectedSubjects) {
        View selectedGroupView = inflater.inflate(R.layout.layout_selected_subject_group_view, selectedSubjectResultContainer, false);
        TextView subjectGroupView = (TextView) selectedGroupView.findViewById(R.id.selected_subject_group_view);
        TextView subjectsView = (TextView) selectedGroupView.findViewById(R.id.selected_subjects_view);
        subjectGroupView.setText(subjectGroupName);
        subjectsView.setText(DataUtils.convertSubjectToString(selectedSubjects));
        return selectedGroupView;
    }

    public void setOnClickSelectSubjectViewListener(OnClickSelectSubjectViewListener onClickSelectSubjectViewListener) {
        this.onClickSelectSubjectViewListener = onClickSelectSubjectViewListener;
        if (onClickSelectSubjectViewListener != null) {
            selectedSubjectView.setOnClickListener(v -> onClickSelectSubjectViewListener.onClick(null));
        }
    }

    public interface OnClickSelectSubjectViewListener {
        void onClick(List<CSubject> selectedSubjects);
    }
}
