package com.riverauction.riverauction.feature.common.subject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.common.collect.Lists;
import com.jhcompany.android.libs.utils.DisplayUtils;
import com.jhcompany.android.libs.utils.ParcelableWrappers;
import com.riverauction.riverauction.R;
import com.riverauction.riverauction.api.model.CErrorCause;
import com.riverauction.riverauction.api.model.CSubject;
import com.riverauction.riverauction.api.model.CSubjectGroup;
import com.riverauction.riverauction.base.BaseActivity;
import com.riverauction.riverauction.states.UserStates;
import com.riverauction.riverauction.states.localmodel.MSubjectGroups;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;

public class SelectSubjectsActivity extends BaseActivity implements SelectSubjectsMvpView {
    private static final String EXTRA_PREFIX = "com.riverauction.riverauction.feature.common.subject.SelectSubjectsActivity";
    public static final String EXTRA_SELECTED_SUBJECTS = EXTRA_PREFIX + "extra_selected_subjects";

    @Inject SelectSubjectsPresenter presenter;

    @Bind(R.id.select_subject_root_view) LinearLayout rootView;

    private List<SelectSubjectGroupView> selectSubjectViews = Lists.newArrayList();
    private List<CSubject> selectedSubjects = Lists.newArrayList();

    @Override
    public int getLayoutResId() {
        return R.layout.activity_select_subjects;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDataFromBundle(getIntent().getExtras());
        getActivityComponent().inject(this);
        presenter.attachView(this, this);

        getSupportActionBar().setTitle(R.string.select_subjects_action_bar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (isExistSubjectGroups()) {
            // show ui
            MSubjectGroups subjectGroups = UserStates.SUBJECT_GROUPS.get(stateCtx);
            makeSelectViews(subjectGroups.getSubjectGroups());
        } else {
            // downLoad
            presenter.getSubjectGroups();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_common_confirm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_common_confirm) {
            selectedSubjects = getSelectedSubjects();
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra(EXTRA_SELECTED_SUBJECTS, ParcelableWrappers.wrap(selectedSubjects));
            setResult(RESULT_OK, intent);
            finish();
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    private void getDataFromBundle(Bundle bundle) {
        ArrayList<Parcelable> parcelableArrayList = getIntent().getParcelableArrayListExtra(EXTRA_SELECTED_SUBJECTS);
        if (parcelableArrayList != null) {
            selectedSubjects = ParcelableWrappers.unwrap(parcelableArrayList);
        }
    }

    private boolean isExistSubjectGroups() {
        MSubjectGroups subjectGroups = UserStates.SUBJECT_GROUPS.get(stateCtx);
        if (subjectGroups == null) {
            return false;
        } else {
            List<CSubjectGroup> subjectGroupList = subjectGroups.getSubjectGroups();
            return subjectGroupList != null && !subjectGroupList.isEmpty();
        }
    }

    /**
     * View 를 만든다
     */
    private void makeSelectViews(List<CSubjectGroup> subjectGroups) {
        for (CSubjectGroup subjectGroup : subjectGroups) {
            SelectSubjectGroupView selectSubjectGroupView = new SelectSubjectGroupView(context);
            selectSubjectGroupView.setSubjectGroups(subjectGroup, selectedSubjects);
            rootView.addView(selectSubjectGroupView);
            rootView.addView(makeMarginView());
            selectSubjectViews.add(selectSubjectGroupView);
        }
    }

    private View makeMarginView() {
        View marginView = new View(context);
        int height = DisplayUtils.getPixelFromDP(context, 10);
        marginView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
        return marginView;
    }

    private List<CSubject> getSelectedSubjects() {
        List<CSubject> selectedSubjects = Lists.newArrayList();
        for (SelectSubjectGroupView selectSubjectGroupView : selectSubjectViews) {
            selectedSubjects.addAll(selectSubjectGroupView.getSelectedSubjects());
        }

        return selectedSubjects;
    }

    @Override
    public void successSubjectGroups(List<CSubjectGroup> subjectGroups) {
        makeSelectViews(subjectGroups);
    }

    @Override
    public boolean failSubjectGroups(CErrorCause errorCause) {
        return false;
    }
}
