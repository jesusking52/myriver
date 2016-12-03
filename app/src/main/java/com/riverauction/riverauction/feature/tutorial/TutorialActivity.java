package com.riverauction.riverauction.feature.tutorial;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.common.collect.Lists;
import com.jhcompany.android.libs.widget.viewpager.ViewPagerIndicator;
import com.riverauction.riverauction.R;
import com.riverauction.riverauction.api.model.CUser;
import com.riverauction.riverauction.api.model.CUserType;
import com.riverauction.riverauction.base.BaseActivity;
import com.riverauction.riverauction.states.UserStates;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;

public class TutorialActivity extends BaseActivity implements TutorialMvpView {

    @Inject
    TutorialPresenter presenter;

    @Bind(R.id.tutorial_view_pager) ViewPager viewPager;
    @Bind(R.id.tutorial_skip_button) TextView skipButton;
    @Bind(R.id.tutorial_start_button) TextView startButton;
    @Bind(R.id.tutorial_view_pager_indicator) ViewPagerIndicator viewPagerIndicator;

    private TutorialAdapter adapter;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_tutorial_student;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        presenter.attachView(this, this);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(final int position) {
                if (position == adapter.getCount() - 1) {
                    // "시작하기"
                    startButton.setText(R.string.tutorial_start_button);
                } else {
                    // "다음"
                    startButton.setText(R.string.tutorial_next_button);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        adapter = new TutorialAdapter(getTutorialItems());
        viewPager.setAdapter(adapter);
        viewPagerIndicator.setViewPager(viewPager);

        skipButton.setOnClickListener(v -> finish());
        startButton.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() == adapter.getCount() - 1) {
                // 마지막 item
                finish();
            } else {
                // 다음 페이지로 이동
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
//        UserStates.TUTORIAL_SHOWN.set(stateCtx, true);
    }

    private List<TutorialItem> getTutorialItems() {
        List<TutorialItem> tutorialItems = Lists.newArrayList();
        CUser me = UserStates.USER.get(stateCtx);
        if (me.getType() == CUserType.STUDENT) {
            // 학생
            tutorialItems.add(new TutorialItem(R.drawable.contents_01, R.drawable.st_text_01));
            tutorialItems.add(new TutorialItem(R.drawable.contents_02, R.drawable.st_text_02));
            tutorialItems.add(new TutorialItem(R.drawable.st_contents_03, R.drawable.st_text_03));
        } else {
            // 선생
            tutorialItems.add(new TutorialItem(R.drawable.contents_01, R.drawable.tc_text_01));
            tutorialItems.add(new TutorialItem(R.drawable.contents_02, R.drawable.tc_text_02));
            tutorialItems.add(new TutorialItem(R.drawable.tc_contents_03, R.drawable.tc_text_03));
        }

        return tutorialItems;
    }

    /**
     * ViewPager 에 붙을 Adapter
     */
    public class TutorialAdapter extends PagerAdapter {
        List<TutorialItem> tutorialItems;

        public TutorialAdapter(List<TutorialItem> tutorialItems) {
            this.tutorialItems = tutorialItems;
        }

        @Override
        public int getCount() {
            return tutorialItems.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return object == view;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(context).inflate(R.layout.layout_item_tutorial, null, false);
            ImageView imageView = (ImageView) view.findViewById(R.id.item_tutorial_image);
            ImageView textImageView = (ImageView) view.findViewById(R.id.item_tutorial_text);

            TutorialItem item = tutorialItems.get(position);

            Glide.with(context)
                    .load(item.getImageResId())
                    .asBitmap()
                    .into(imageView);

            Glide.with(context)
                    .load(item.getTextImageResId())
                    .asBitmap()
                    .into(textImageView);

            container.addView(view);

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
