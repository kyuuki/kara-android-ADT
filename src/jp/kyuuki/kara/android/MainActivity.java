package jp.kyuuki.kara.android;

import jp.kyuuki.kara.android.fragment.MemberFragmentStatePagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

public class MainActivity extends BaseActivity {
    /*
     * 画面要素
     */
    private ViewPager mViewPager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // 画面要素 (レイアウト) と変数を結びつける
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(new MemberFragmentStatePagerAdapter(getSupportFragmentManager()));
    }
}
