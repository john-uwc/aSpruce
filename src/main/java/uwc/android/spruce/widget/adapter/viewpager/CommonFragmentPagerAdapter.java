package uwc.android.spruce.widget.adapter.viewpager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * created by steven
 */
public class CommonFragmentPagerAdapter extends FragmentPagerAdapter {
    private List<? extends Fragment> mFragmentList = new ArrayList<>();

    public CommonFragmentPagerAdapter(FragmentManager fm, List<? extends Fragment> fragmentList) {
        super(fm);
        mFragmentList = fragmentList;
    }

    @Override
    public int getCount() {
        return mFragmentList!=null? mFragmentList.size(): 0;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }
}
