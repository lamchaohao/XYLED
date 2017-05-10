package cn.com.hotled.xyled.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Lam on 2016/12/1.
 */

public class MainFragmentAdapter extends FragmentPagerAdapter {
    List<Fragment> mFragmentList;
    public MainFragmentAdapter(FragmentManager fm, List fgList) {
        super(fm);
        mFragmentList=fgList;
    }

    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.i(getClass().getName(),"instantiateItem-->"+position);
        return super.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container,position,object);
        Log.i(getClass().getName(),"destroyItem-->"+position);
    }
}
