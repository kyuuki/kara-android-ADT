package jp.kyuuki.kara.android.fragment;

import jp.kyuuki.kara.android.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 승연。
 */
public class MemberSeungyeonFragment extends Fragment {
    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_seungyeon, container, false);
        return v;
    }
}
