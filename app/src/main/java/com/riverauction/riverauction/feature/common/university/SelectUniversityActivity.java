package com.riverauction.riverauction.feature.common.university;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jhcompany.android.libs.rxjava.BasicSubscriber;
import com.jhcompany.android.libs.utils.DisplayUtils;
import com.jhcompany.android.libs.utils.FileUtils;
import com.jhcompany.android.libs.utils.Lists2;
import com.jhcompany.android.libs.utils.ParcelableWrappers;
import com.jhcompany.android.libs.widget.recyclerview.HorizontalDividerItemDecoration;
import com.riverauction.riverauction.R;
import com.riverauction.riverauction.base.BaseActivity;
import com.riverauction.riverauction.feature.common.widget.TagGroupView;
import com.riverauction.riverauction.widget.recyclerview.DividerItemDecoration;
import com.riverauction.riverauction.widget.recyclerview.DividerUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.Bind;
import rx.android.schedulers.AndroidSchedulers;

import static com.riverauction.riverauction.R.id.item_university_tag_view_name;

public class SelectUniversityActivity extends BaseActivity implements SelectUniversityMvpView {
    private static final String EXTRA_PREFIX = "com.riverauction.riverauction.feature.common.university.SelectUniversityActivity";
    public static final String EXTRA_UNIVERSITIES = EXTRA_PREFIX + "extra_universities";
    public static final String EXTRA_SELECT_ONLY_ONE = EXTRA_PREFIX + "extra_select_only_one";

    @Inject SelectUniversityPresenter presenter;

    @Bind(R.id.select_university_description) View descriptionView;
    @Bind(R.id.select_university_tag_group_view) TagGroupView tagGroupView;
    @Bind(R.id.select_university_edit_text) EditText universityEditText;
    @Bind(R.id.select_university_search_button) View searchButton;
    @Bind(R.id.select_university_recycler_view) RecyclerView recyclerView;

    // client 에 내장된 대학교 목록
    private List<String> universities;
    private List<String> searchedUniversities = Lists.newArrayList();

    private List<String> selectedUniversities = Lists.newArrayList();

    private UniversityAdapter adapter;
    // true 이면 대학교 1개만 선택할 수 있고 false 이면 복수선택 가능
    private boolean shouldSelectOnlyOne;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_select_university;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        presenter.attachView(this, this);
        getDataFromBundle(getIntent().getExtras());

        getSupportActionBar().setTitle(R.string.select_university_action_bar_title);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        universities = FileUtils.readFromAssests(context, "university.txt");

        adapter = new UniversityAdapter(searchedUniversities);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        RecyclerView.ItemDecoration itemDecoration = DividerUtils.getHorizontalDividerItemDecoration(context);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        RxTextView.textChanges(universityEditText)
                .throttleWithTimeout(300, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BasicSubscriber<CharSequence>() {
                    @Override
                    public void onNext(CharSequence charSequence) {
                        super.onNext(charSequence);
                        String keyword = charSequence.toString();
                        if (!Strings.isNullOrEmpty(keyword)) {
                            searchedUniversities.clear();
                            searchedUniversities.addAll(search(keyword));
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
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
            Intent intent = new Intent();

            if (Lists2.isNullOrEmpty(selectedUniversities)) {
                new AlertDialog.Builder(context)
                        .setTitle(R.string.common_error_title)
                        .setMessage(R.string.select_university_description)
                        .setPositiveButton(R.string.common_button_confirm, null)
                        .show();
                return false;
            }

            intent.putExtra(EXTRA_UNIVERSITIES, ParcelableWrappers.wrap(selectedUniversities));
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
        if (bundle == null) {
            return;
        }
        shouldSelectOnlyOne = bundle.getBoolean(EXTRA_SELECT_ONLY_ONE, false);
    }

    /**
     * 키워드가 들어간 대학교 목록 검색
     */
    private List<String> search(String keyword) {
        List<String> result = Lists.newArrayList();

        for (String university : universities) {
            if (!Strings.isNullOrEmpty(university) && university.contains(keyword)) {
                result.add(university);
            }
        }
        return result;
    }

    public static class UniversityItemHolder extends RecyclerView.ViewHolder {
        public TextView universityNameView;

        public UniversityItemHolder(View itemView) {
            super(itemView);
            universityNameView = (TextView) itemView.findViewById(R.id.item_university_name);
        }
    }

    private class UniversityAdapter extends RecyclerView.Adapter {
        private List<String> universities;

        public UniversityAdapter(List<String> universities) {
            this.universities = universities;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new UniversityItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_university, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String university = universities.get(position);
            UniversityItemHolder universityItemHolder = (UniversityItemHolder) holder;
            universityItemHolder.universityNameView.setText(university);
            universityItemHolder.itemView.setOnClickListener(v -> {
                if (universityItemHolder.itemView.isSelected()) {
                    universityItemHolder.itemView.setSelected(false);
                } else {
                    universityItemHolder.itemView.setSelected(true);
                    if (shouldSelectOnlyOne) {
                        // 하나만 선택 가능
                        new AlertDialog.Builder(context)
                                .setTitle(R.string.select_university_action_bar_title)
                                .setMessage(R.string.select_university_confirm_dialog_message)
                                .setPositiveButton(R.string.common_button_confirm, (dialog, which) -> {
                                    Intent intent = new Intent();
                                    intent.putExtra(EXTRA_UNIVERSITIES, ParcelableWrappers.wrap(Lists.newArrayList(university)));
                                    setResult(RESULT_OK, intent);
                                    finish();
                                })
                                .show();
                    } else {
                        descriptionView.setVisibility(View.GONE);
                        tagGroupView.setVisibility(View.VISIBLE);

                        if (!selectedUniversities.contains(university)) {
                            selectedUniversities.add(university);
                            tagGroupView.addTagTextView(makeUniversityTagView(university));
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return universities.size();
        }
    }

    private View makeUniversityTagView(String university) {
        View tagView = getLayoutInflater().inflate(R.layout.layout_university_tag_view, null, false);
        TextView universityNameView = (TextView) tagView.findViewById(item_university_tag_view_name);
        universityNameView.setText(university);
        tagView.setTag(university);
        tagView.findViewById(R.id.item_university_tag_view_cancel_button).setOnClickListener(v -> {
            // cancel
            if (selectedUniversities.contains(university)) {
                tagGroupView.removeTagTextView(tagView);
                selectedUniversities.remove(university);
                if (selectedUniversities.size() == 0) {
                    descriptionView.setVisibility(View.VISIBLE);
                    tagGroupView.setVisibility(View.GONE);
                }
            }
        });

        return tagView;
    }
}
