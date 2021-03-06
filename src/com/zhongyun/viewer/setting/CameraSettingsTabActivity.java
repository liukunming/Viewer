/*
 * Copyright (C) 2015 iChano incorporation's Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhongyun.viewer.setting;

import java.util.ArrayList;
import java.util.List;

import com.zhongyun.viewer.MyViewPagerAdapter;
import com.zhongyun.viewer.R;
import com.zhongyun.viewer.R.id;
import com.zhongyun.viewer.R.layout;
import com.zhongyun.viewer.utils.Constants;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.Toolbar.OnMenuItemClickListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class CameraSettingsTabActivity extends AppCompatActivity implements
        ViewPager.OnPageChangeListener, OnClickListener {
    private CoordinatorLayout mCoordinatorLayout;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    // TabLayout中的tab标题
    private int[] mTitles;
    // 填充到ViewPager中的Fragment
    private List<Fragment> mFragments;

    MyViewPagerAdapter mViewPagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        // 初始化各种控件
         initViews();

        // 初始化mTitles、mFragments等ViewPager需要的数据
        // 这里的数据都是模拟出来了，自己手动生成的，在项目中需要从网络获取数据
        initData();

        // 对各种控件进行设置、适配、填充数据
        configViews();

    }

    private void initData() {

        long cid = getIntent().getLongExtra(Constants.INTENT_CID, 0);
        // Tab的标题采用string-array的方法保存，在res/values/arrays.xml中写
        mTitles = new int[] { R.string.menu_alarm_settings_label, R.string.menu_scheduled_recording_label, R.string.menu_change_password_label };
        
        mToolbar.setTitle(mTitles[0]);
        // 初始化填充到ViewPager中的Fragment集合
        mFragments = new ArrayList<>();
        Bundle mBundle = new Bundle();
        mBundle.putString(Constants.INTENT_CID, String.valueOf(cid));
        for (int i = 0; i < mTitles.length; i++) {
            if(i==0){
                AlarmFragment fm = new AlarmFragment();
                fm.setArguments(mBundle);
                mFragments.add(i, fm);
            }else if(i==1){
                TimeRecordFragment fm = new TimeRecordFragment();
                fm.setArguments(mBundle);
                mFragments.add(i, fm);
            }else if(i==2){
                CameraSettingsInfoFragment fm = new CameraSettingsInfoFragment();
                fm.setArguments(mBundle);
                mFragments.add(i, fm);
            }
        }

    }

    private void configViews() {

        // 设置显示Toolbar
        setSupportActionBar(mToolbar);
        
        mToolbar.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            
            @Override
            public boolean onMenuItemClick(MenuItem arg0) {
                int id = arg0.getItemId();

                if (id == R.id.save) {
                    BaseSettingFragment fm =  (BaseSettingFragment)mViewPagerAdapter.getItem(mViewPager.getCurrentItem());
                    if(fm!=null)fm.onSave();
                    return true;
                }
                return false;
            }
        });
        // 初始化ViewPager的适配器，并设置给它
        mViewPagerAdapter = new MyViewPagerAdapter(this,getSupportFragmentManager(),
                mTitles, mFragments);
        mViewPager.setAdapter(mViewPagerAdapter);
        // 设置ViewPager最大缓存的页面个数
        mViewPager.setOffscreenPageLimit(3);
        // 给ViewPager添加页面动态监听器（为了让Toolbar中的Title可以变化相应的Tab的标题）
        mViewPager.addOnPageChangeListener(this);

        mTabLayout.setTabMode(TabLayout.MODE_FIXED);//MODE_SCROLLABLE  MODE_FIXED
        // 将TabLayout和ViewPager进行关联，让两者联动起来
        mTabLayout.setupWithViewPager(mViewPager);
        // 设置Tablayout的Tab显示ViewPager的适配器中的getPageTitle函数获取到的标题
        mTabLayout.setTabsFromPagerAdapter(mViewPagerAdapter);
    }

    private void initViews() {
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.id_coordinatorlayout);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.id_appbarlayout);
        mToolbar = (Toolbar) findViewById(R.id.id_toolbar);
        mTabLayout = (TabLayout) findViewById(R.id.id_tablayout);
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	Fragment fg = mViewPagerAdapter.getItem(mViewPager.getCurrentItem());
    	if(fg instanceof CameraSettingsInfoFragment){
    		
    	}
    	return super.onPrepareOptionsMenu(menu);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_save, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.save) {
            BaseSettingFragment fm =  (BaseSettingFragment)mViewPagerAdapter.getItem(mViewPager.getCurrentItem());
            if(fm!=null)fm.onSave();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onPageSelected(int position) {
        mToolbar.setTitle(mTitles[position]);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
            int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {

    }

}
