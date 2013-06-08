package jp.kyuuki.kara.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;

public class MainActivity extends Activity {
    private ViewFlipper vf;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        vf = (ViewFlipper) findViewById(R.id.viewFlipper1);
        setViewFlipper(vf);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // �^�b�`�C�x���g�n���h��
    private float old_x;
    
    private void setViewFlipper(ViewFlipper vf) {
        vf.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                Log.v("kara", "event = " + event);
                ViewFlipper vfvf = (ViewFlipper) view;

                switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    old_x = event.getX();
                    break;
                case MotionEvent.ACTION_UP:
                    float new_x = event.getX();
                    if (old_x < new_x) {
                        vfvf.setInAnimation(AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.slide_in_left));
                        vfvf.setOutAnimation(AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.slide_out_right));
                        vfvf.showNext();
                    } else if (old_x > new_x) {
                        // android.jar に入っているアニメーションは、コピーして持ってくるのが正しいやり方？
                        vfvf.setInAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_in_right));
                        vfvf.setOutAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_out_left));
                        vfvf.showPrevious();
                    }
                    break;
                }

                return true;
            }
        });
    }
}
