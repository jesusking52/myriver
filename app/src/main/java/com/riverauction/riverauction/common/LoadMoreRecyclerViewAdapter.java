package com.riverauction.riverauction.common;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.common.collect.Lists;
import com.riverauction.riverauction.R;

import java.util.List;

public abstract class LoadMoreRecyclerViewAdapter<T> extends RecyclerView.Adapter {

    protected static final int TYPE_LOAD_MORE = 0;

    protected List<T> items = Lists.newArrayList();
    protected Integer nextToken;
    protected boolean isErrorLoadMore;

    @Override
    public int getItemViewType(int position) {
        // load more footer position
        int loadMorePosition = isExistHeader() ? items.size() + 1 : items.size();
        if (nextToken != null && position == loadMorePosition) {
            return TYPE_LOAD_MORE;
        } else {
            return getItemViewItemType(position);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (TYPE_LOAD_MORE == viewType) {
            return new LoadMoreFooterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading_footer, parent, false));
        }
        return onCreateViewItemHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int loadMorePosition = isExistHeader() ? items.size() + 1 : items.size();
        if (nextToken != null && position == loadMorePosition) {
            LoadMoreFooter loadMoreFooter = ((LoadMoreFooterHolder) holder).loadMoreFooter;
            if (isErrorLoadMore) {
                loadMoreFooter.setOnRetryButtonClickListener(v -> loadMore(nextToken));
                loadMoreFooter.showRetryButton();
            }

            if (nextToken != null && !isErrorLoadMore) {
                // can load more
                loadMoreFooter.showLoadingAnimation();
                loadMore(nextToken);
            }
        } else {
            onBindViewItemHolder(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        if (nextToken == null) {
            return isExistHeader() ? items.size() + 1 : items.size();
        } else {
            // load more footer 추가
            return isExistHeader() ? items.size() + 2 : items.size() + 1;
        }
    }

    public void setNextToken(Integer nextToken) {
        this.nextToken = nextToken;
    }

    public void setErrorLoadMore(boolean isErrorLoadMore) {
        this.isErrorLoadMore = isErrorLoadMore;
    }

    /**
     * true 면 header 존재
     * default : false
     */
    public boolean isExistHeader() {
        return false;
    }

    /**
     * {@link LoadMoreFooterHolder} 이외의 Holder 를 생성한다.
     */
    public abstract RecyclerView.ViewHolder onCreateViewItemHolder(ViewGroup parent, int viewType);

    /**
     * {@link LoadMoreFooterHolder} 이외의 Holder 에 Data 를 Binding 한다.
     */
    public abstract void onBindViewItemHolder(RecyclerView.ViewHolder holder, int position);

    /**
     * {@link LoadMoreFooterHolder} 이외의 ViewType 을 정한다.
     */
    public abstract int getItemViewItemType(int position);

    /**
     * item 을 더 불러온다.
     * @param nextToken
     */
    public abstract void loadMore(Integer nextToken);
}
