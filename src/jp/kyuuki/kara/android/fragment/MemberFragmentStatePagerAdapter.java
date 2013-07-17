package jp.kyuuki.kara.android.fragment;

import java.util.Arrays;
import java.util.List;

import jp.kyuuki.kara.android.model.Member;
import jp.kyuuki.kara.android.model.Member.CurrentMember;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class MemberFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

    private class MemberAndFragment {
        public CurrentMember member;
        public Fragment fragment;
        
        public MemberAndFragment(CurrentMember member, Fragment fragment) {
            this.member = member;
            this.fragment = fragment;
        }
    }
    
    List<MemberAndFragment> list = Arrays.asList(
            new MemberAndFragment(CurrentMember.GYURI,     new MemberGyuriFragment()), 
            new MemberAndFragment(CurrentMember.SEUNGYEON, new MemberSeungyeonFragment()), 
            new MemberAndFragment(CurrentMember.NICOLE,    new MemberNicoleFragment()), 
            new MemberAndFragment(CurrentMember.HARA,      new MemberHaraFragment()), 
            new MemberAndFragment(CurrentMember.JIYOUNG,   new MemberJiyoungFragment()));

    public MemberFragmentStatePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        MemberAndFragment memberAndFragment = list.get(i);
        return memberAndFragment.fragment;
    }

    @Override
    public int getCount() {
        return list.size();
    }
    
    @Override
    public CharSequence getPageTitle(int position) {
        MemberAndFragment memberAndFragment = list.get(position);
        Member member = Member.currentMemberMap.get(memberAndFragment.member);
        return member.stage_name_japanese;
    }
}
