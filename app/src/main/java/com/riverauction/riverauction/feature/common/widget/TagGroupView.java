package com.riverauction.riverauction.feature.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.riverauction.riverauction.R;

import java.util.List;


/**
 * layout_width 로 wrap_content 를 사용하면 안된다.
 * wrap_content 를 사용하면 {@link TagGroupView#addTagTextView(View)} 를 호출시에 넓이 계산이 제대로 안될수있다.
 */
public class TagGroupView extends LinearLayout {

    private List<View> mTagViews = Lists.newArrayList();
    private List<LinearLayout> mLinearLayoutLines = Lists.newArrayList();
    private List<View> mSelectedTagViews = Lists.newArrayList();

    private OnTagSelectListener onTagSelectListener;
    private int mMaxSelectCount = 0;
    private boolean canMultiSelect = true;

    private int mRowPaddingTop;
    private int mRowPaddingBottom;
    private int mRowGravity;

    public TagGroupView(Context context) {
        super(context);
        initialize(context, null, 0);
    }

    public TagGroupView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0);
    }

    public TagGroupView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr) {
        setOrientation(VERTICAL);

        // Attribute initialization
        if (attrs != null) {
            final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TagGroupView, defStyleAttr, 0);
            mRowPaddingTop = (int) a.getDimension(R.styleable.TagGroupView_rowPaddingTop, 0);
            mRowPaddingBottom = (int) a.getDimension(R.styleable.TagGroupView_rowPaddingBottom, 0);
            mRowGravity = a.getInt(R.styleable.TagGroupView_rowGravity, Gravity.CENTER_VERTICAL);

            a.recycle();
        }

        addLinearLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        LinearLayout firstLinearLayout = (LinearLayout) getChildAt(mLinearLayoutLines.size() - 1);

        int tagViewCount = firstLinearLayout.getChildCount();
        for (int i = 0; i < tagViewCount; i++) {
            final View child = firstLinearLayout.getChildAt(i);
            // LinearLayout 에 TextView(TagView) 가 들어 갈 때 MeasureSpec.UNSPECIFIED 로 measure 하지 않으면 TextView 의 line 이 2줄로 늘어나서 들어가는 경우가 있다.
            // 따라서 TextView 가 1줄일 때 넓이를 알려면 MeasureSpec.UNSPECIFIED 로 measure 해야 한다.
            child.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        boolean shouldRearrange = true;
        boolean isFinish = false;

        // LinearLayout 의 넓이 구하기
        LinearLayout linearLayout = mLinearLayoutLines.get(mLinearLayoutLines.size() - 1);
        final int linearLayoutWidth = linearLayout.getMeasuredWidth();

        while (shouldRearrange) {
            LinearLayout lastLinearLayout = mLinearLayoutLines.get(mLinearLayoutLines.size() - 1);

            // lastLinearLayout 안에 있는 tagView 에 대하여
            int tagViewTotalWidth = 0;
            int tagViewCount = lastLinearLayout.getChildCount();
            for (int i = 0; i < tagViewCount; i++) {
                View tagView = lastLinearLayout.getChildAt(i);
                int measuredWidth = tagView.getMeasuredWidth();
                LayoutParams layoutParams = (LayoutParams) tagView.getLayoutParams();
                int marginWidth = layoutParams.rightMargin;
                tagViewTotalWidth += measuredWidth;
                tagViewTotalWidth += marginWidth;

                // tagView 다음 줄로 이동한다. (새로운 LinearLayout 을 붙이고 거기고 tagView 들을 이동시킨다.)
                if (linearLayoutWidth < tagViewTotalWidth) {
                    addLinearLayout();
                    for (int j = i; j < tagViewCount; j++) {
                        // getChildAt(j) 가 아니라 i 이다. 아랫줄에서 removeView 를 하기 때문에 index 가 증가하는 j 를 사용하면 안된다.
                        View movedTagView = lastLinearLayout.getChildAt(i);
                        rearrangeTagTextView(movedTagView);
                    }
                    break;
                }

                // 마지막 tagView 까지 아무이상없이 잘 그림 -> Finish
                if (tagViewCount == i + 1) {
                    isFinish = true;
                }
            }

            if (isFinish || tagViewCount == 0) {
                shouldRearrange = false;
            }
        }
    }

    public void setCanMultiSelect(boolean canMultiSelect) {
        this.canMultiSelect = canMultiSelect;
    }

    public void addTagTextView(final View tagView) {
        LinearLayout lastLinearLayout = mLinearLayoutLines.get(mLinearLayoutLines.size() - 1);
        lastLinearLayout.addView(tagView);
        mTagViews.add(tagView);

        // click listener
        tagView.setOnClickListener(v -> {
            if (mSelectedTagViews.contains(tagView)) {
                unselectTagView(tagView);
                if (onTagSelectListener != null) {
                    onTagSelectListener.onUnselect();
                }
            } else {
                if (canMultiSelect) {
                    // 여러개의 태그를 선택할수 있다.
                    selectTagView(tagView);

                    if (onTagSelectListener != null) {
                        onTagSelectListener.onSelect();
                    }

                    while (0 < mMaxSelectCount && mMaxSelectCount < mSelectedTagViews.size()) {
                        View removed = mSelectedTagViews.remove(0);
                        if (null != removed) {
                            removed.setSelected(false);
                        }
                    }
                } else {
                    // 단 하나의 태그만 선택할 수 있다.
                    clearAllSelectedTag();

                    selectTagView(tagView);
                    if (onTagSelectListener != null) {
                        onTagSelectListener.onSelect();
                    }
                }
            }
        });
    }

    /**
     * 선택된 모든 tag 를 clear 한다.
     */
    public void clearAllSelectedTag() {
        if (mSelectedTagViews.isEmpty()) {
            return;
        }

        for (View selectTagView : mSelectedTagViews) {
            selectTagView.setSelected(false);
        }

        mSelectedTagViews.clear();
    }

    /**
     * param 으로 넘긴 tagView 를 제외한 모든 tagView 를 선택 취소시킨다.
     * @param tagView 선택할 태그 뷰
     */
    public void clearAllSelectedTagWithoutMe(TextView tagView) {
        clearAllSelectedTag();

        selectTagView(tagView);
    }

    /**
     * 태그 뷰를 선택한다.
     */
    public void selectTagView(View tagView) {
        mSelectedTagViews.add(tagView);
        tagView.setSelected(true);
    }

    /**
     * 태그 뷰를 취소한다.
     */
    public void unselectTagView(View tagView) {
        mSelectedTagViews.remove(tagView);
        tagView.setSelected(false);
    }

    public void removeTagTextViewAll() {
        if (mTagViews.isEmpty()) {
            return;
        }

        mTagViews.clear();
        mSelectedTagViews.clear();

        removeAllViews();
        redrawTagViews();
    }

    public void removeTagTextView(View tagView) {
        mTagViews.remove(tagView);
        mSelectedTagViews.remove(tagView);
        tagView.setOnClickListener(null);

        LinearLayout parent = (LinearLayout) tagView.getParent();
        LinearLayout lastLinearLayout = mLinearLayoutLines.get(mLinearLayoutLines.size() - 1);

        if (lastLinearLayout.equals(parent)) {
            parent.removeView(tagView);
            if (parent.getChildCount() == 0 && mLinearLayoutLines.size() > 1) {
                // mLinearLayoutLines 가 1개 만 있으면 LinearLayout 을 제거하지 않는다.
                removeView(parent);
                mLinearLayoutLines.remove(parent);
            }
        } else {
            // lastLinearLayout 이 아니라 중간 지점에서 TagView 가 지워져야 되는 경우.
            removeAllViews();
            redrawTagViews();
        }
    }

    /**
     * TagView 가 LinearLayout 가로 길이를 넘어가서 붙으면 remove 시킨뒤 하단의 LinearLayout 에 붙인다.
     *
     * @param tagView rearrange 할 TagView
     */
    private void rearrangeTagTextView(View tagView) {
        LinearLayout parent = (LinearLayout) tagView.getParent();
        tagView.setOnClickListener(null);
        parent.removeView(tagView);
        mTagViews.remove(tagView);

        addTagTextView(tagView);
    }

    /**
     * {@code mTagViews} 에 있는 TagView 들을 {@link TagGroupView} 에 다시 그려준다.
     * {@code mLinearLayouts} 중 마지막이 아닌 LinearLayout 에서 TagView 가 지워지면 빈공간을 다시 계산해서 TagView 를 그리기 위해서 reDraw 해야된다.
     */
    private void redrawTagViews() {
        mLinearLayoutLines = Lists.newArrayList();

        addLinearLayout();

        List<View> tagViews = Lists.newArrayList(mTagViews);
        mTagViews = Lists.newArrayList();

        for (View tagView : tagViews) {
            if (tagView.getParent() != null) {
                ((LinearLayout) tagView.getParent()).removeView(tagView);
            }

            addTagTextView(tagView);
        }
    }

    /**
     * TagView 를 붙일 LinearLayout 을 하나 더 붙인다.
     */
    private void addLinearLayout() {
        LinearLayout newLine = new LinearLayout(getContext());
        newLine.setOrientation(HORIZONTAL);
        newLine.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        newLine.setGravity(mRowGravity);
        newLine.setPadding(0, mRowPaddingTop, 0, mRowPaddingBottom);
        this.addView(newLine);

        mLinearLayoutLines.add(newLine);
    }

    @NonNull
    public List<View> getSelectedTagViews() {
        return mSelectedTagViews;
    }

    public int getMaxSelectCount() {
        return mMaxSelectCount;
    }

    /**
     * @param count 0 보다 커야 유효. 0 이하이면 무효.
     */
    public void setMaxSelectCount(int count) {
        this.mMaxSelectCount = count;
    }

    public void setOnTagSelectListener(OnTagSelectListener onTagSelectListener) {
        this.onTagSelectListener = onTagSelectListener;
    }

    /**
     *  모든 TagView 들이 공통적으로 작동하는 select/unselect listener
     */
    public interface OnTagSelectListener {
        void onSelect();
        void onUnselect();
    }
}
