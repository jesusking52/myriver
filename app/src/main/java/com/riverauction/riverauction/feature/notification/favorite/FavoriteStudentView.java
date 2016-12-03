package com.riverauction.riverauction.feature.notification.favorite;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.jhcompany.android.libs.preference.StateCtx;
import com.riverauction.riverauction.R;
import com.riverauction.riverauction.RiverAuctionApplication;
import com.riverauction.riverauction.api.model.CUser;
import com.riverauction.riverauction.api.model.CUserFavorite;
import com.riverauction.riverauction.common.LoadMoreRecyclerViewAdapter;
import com.riverauction.riverauction.feature.MoreLoadable;
import com.riverauction.riverauction.feature.teacher.TeacherDetailActivity;
import com.riverauction.riverauction.feature.teacher.TeacherItemView;
import com.riverauction.riverauction.states.UserStates;
import com.riverauction.riverauction.widget.StatusView;
import com.riverauction.riverauction.widget.recyclerview.DividerItemDecoration;
import com.riverauction.riverauction.widget.recyclerview.DividerUtils;

import java.util.List;

public abstract class FavoriteStudentView extends StatusView implements MoreLoadable {
    private FavoriteAdapter adapter;

    private List<CUserFavorite> favorites = Lists.newArrayList();
    private Integer nextToken;
    private StateCtx stateCtx;
    // 로그인한 사용자
    private CUser user;

    public FavoriteStudentView(Context context) {
        super(context);
        initialize(context);
    }

    public FavoriteStudentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public FavoriteStudentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        stateCtx = RiverAuctionApplication.getApplication().getComponent().stateCtx();
        user = UserStates.USER.get(stateCtx);

        TextView emptyView = (TextView) inflater.inflate(R.layout.status_empty_view, this, false);
        emptyView.setText(R.string.favorite_student_empty_view);
        addView(emptyView);
        setEmptyView(emptyView);

        View errorView = inflater.inflate(R.layout.status_error_view, this, false);
        addView(errorView);
        setErrorView(errorView);

        View loadingView = inflater.inflate(R.layout.status_loading_view, this, false);
        addView(loadingView);
        setLoadingView(loadingView);

        View recyclerView = inflater.inflate(R.layout.recycler_view, this, false);
        addView(recyclerView);
        setResultView(recyclerView);

        findViewById(R.id.error_retry_button).setOnClickListener(v -> FavoriteStudentView.this.loadMore(null));

        adapter = new FavoriteAdapter(favorites);
        RecyclerView mRecyclerView = (RecyclerView) getResultView();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView.ItemDecoration itemDecoration = DividerUtils.getHorizontalDividerItemDecoration(context);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);
    }

    public void setContent(List<CUserFavorite> newFavorites, Integer newNextToken) {
        if (favorites.size() == 0 && newFavorites.size() == 0) {
            showEmptyView();
            return;
        }
        showResultView();

        favorites.addAll(newFavorites);
        nextToken = newNextToken;

        adapter.setNextToken(nextToken);
        adapter.setErrorLoadMore(false);
        adapter.notifyDataSetChanged();
    }

    public void setError() {
        if (favorites.size() == 0) {
            showErrorView();
        } else {
            // 더 불러오기 실패
            adapter.setErrorLoadMore(true);
            adapter.notifyItemChanged(favorites.size());
        }
    }

    public void setLoading() {
        if (favorites.size() == 0) {
            showLoadingView();
        }
    }

    public static class FavoriteHolder extends RecyclerView.ViewHolder {
        public TeacherItemView teacherItemView;

        public FavoriteHolder(View itemView) {
            super(itemView);
            teacherItemView = (TeacherItemView) itemView;
        }
    }

    private class FavoriteAdapter extends LoadMoreRecyclerViewAdapter {
        private static final int TYPE_ITEM = 1;

        public FavoriteAdapter(List<CUserFavorite> favorites) {
            Preconditions.checkNotNull(favorites);
            this.items = favorites;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewItemHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_ITEM) {
                return new FavoriteHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_teacher_list, parent, false));
            }
            return null;
        }

        @Override
        public void onBindViewItemHolder(RecyclerView.ViewHolder holder, int position) {
            CUserFavorite favorite = favorites.get(position);
            FavoriteHolder favoriteHolder = ((FavoriteHolder) holder);
            favoriteHolder.teacherItemView.setContent(favorite.getToUser());
            favoriteHolder.teacherItemView.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), TeacherDetailActivity.class);
                intent.putExtra(TeacherDetailActivity.EXTRA_USER_ID, favorite.getToUser().getId());
                getContext().startActivity(intent);
            });
        }

        @Override
        public int getItemViewItemType(int position) {
            return TYPE_ITEM;
        }

        @Override
        public void loadMore(Integer nextToken) {
            FavoriteStudentView.this.loadMore(nextToken);
        }
    }
}
