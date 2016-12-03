package com.riverauction.riverauction.common;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public class LoadMoreFooterHolder extends RecyclerView.ViewHolder {

    public LoadMoreFooter loadMoreFooter;

    public LoadMoreFooterHolder(View itemView) {
        super(itemView);
        loadMoreFooter = (LoadMoreFooter) itemView;
    }
}
