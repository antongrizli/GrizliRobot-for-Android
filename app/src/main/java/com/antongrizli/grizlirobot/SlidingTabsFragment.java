package com.antongrizli.grizlirobot;

import android.support.v4.app.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.antongrizli.grizlirobot.tabs.DeletingTab;
import com.antongrizli.grizlirobot.tabs.MainTab;
import com.antongrizli.grizlirobot.tabs.SearchingTab;
import com.antongrizli.grizlirobot.view.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

public class SlidingTabsFragment extends Fragment{
    private static final int SCENE_SEARCH = 0x0100;
    private static final int SCENE_MAIN = 0x0200;
    private static final int SCENE_DELETE = 0x0300;

    static class SamplePagerItem {
        private final CharSequence mTitle;
        private final int mIndicatorColor = Color.BLUE;
        private final int sceneView;

        SamplePagerItem(CharSequence title, int sceneView) {
            mTitle = title;
            this.sceneView = sceneView;
        }

        Fragment createFragment() {
            switch (sceneView) {
                case SCENE_SEARCH:
                    return SearchingTab.newInstance(mTitle, sceneView);
                case SCENE_MAIN:
                    return MainTab.newInstance(mTitle, mIndicatorColor);
                case SCENE_DELETE:
                    return DeletingTab.newInstance(mTitle, mIndicatorColor);
                default:
                    return null;
            }
        }

        CharSequence getTitle() {
            return mTitle;
        }

        int getIndicatorColor() {
            return mIndicatorColor;
        }
    }

    static final String LOG_TAG = "SlidingTabsColorsFragment";

    private SlidingTabLayout mSlidingTabLayout;

    private ViewPager mViewPager;

    private List<SamplePagerItem> mTabs = new ArrayList<SamplePagerItem>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTabs.add(new SamplePagerItem("Search", SCENE_SEARCH));

        mTabs.add(new SamplePagerItem("Main", SCENE_MAIN));

        mTabs.add(new SamplePagerItem("Delete", SCENE_DELETE));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sample, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new SampleFragmentPagerAdapter(getChildFragmentManager()));

        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);

        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {

            @Override
            public int getIndicatorColor(int position) {
                return mTabs.get(position).getIndicatorColor();
            }
        });
    }

    class SampleFragmentPagerAdapter extends FragmentPagerAdapter {

        SampleFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return mTabs.get(i).createFragment();
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabs.get(position).getTitle();
        }
    }
}