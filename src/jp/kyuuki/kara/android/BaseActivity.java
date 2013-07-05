package jp.kyuuki.kara.android;

import java.util.Arrays;
import java.util.List;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.FacebookOperationCanceledException;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

/**
 * 全画面共通。
 */
public abstract class BaseActivity extends FragmentActivity {

    // ログの TAG をどうするかは悩みどころ。
    // http://stackoverflow.com/questions/8355632/how-do-you-usually-tag-log-entries-android
    private static final String TAG = BaseActivity.class.getName();
    
    // 使用する Facebook 機能
    private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
    
    UiLifecycleHelper mUiLifecycleHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFacebook(savedInstanceState);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        mUiLifecycleHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mUiLifecycleHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mUiLifecycleHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        mUiLifecycleHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUiLifecycleHelper.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_facebook:
            if (checkPostFacebook()) {
                postFacebook();
            }
            break;
        case R.id.action_about:
            PackageManager pm = this.getPackageManager();
            String versionName = "";
            PackageInfo packageInfo;
            try {
                packageInfo = pm.getPackageInfo(this.getPackageName(), PackageManager.GET_ACTIVITIES);
                versionName = "Version " + packageInfo.versionName;
            } catch (NameNotFoundException e) {
                e.printStackTrace();
                versionName = "";
            }
            
            AlertDialog.Builder ab = new AlertDialog.Builder(this);
            AlertDialog ad = ab.create();
            ad.setTitle(getString(R.string.action_about));
            ad.setMessage(getString(R.string.app_name) + " " + versionName);
            ad.show();
            break;
        }
        
        return true;
    }
    
    // onCreate で呼ばれる前提
    private void initFacebook(Bundle savedInstanceState) {
        mUiLifecycleHelper = new UiLifecycleHelper(this, new StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                BaseActivity.this.onSessionStateChange(session, state, exception);
            }
        });
        mUiLifecycleHelper.onCreate(savedInstanceState);

        // キーハッシュが正しいかどうか出力するコード
//      try {
//          PackageInfo info = getPackageManager().getPackageInfo("jp.kyuuki.kara.android",
//                  PackageManager.GET_SIGNATURES);
//          for (Signature signature :info.signatures) {
//              MessageDigest md = MessageDigest.getInstance("SHA");
//              md.update(signature.toByteArray());
//              Log.d(TAG, Base64.encodeToString(md.digest(), Base64.DEFAULT));
//              }
//      } catch (NameNotFoundException e) {
//          e.printStackTrace();
//      } catch (NoSuchAlgorithmException e) {
//          e.printStackTrace();
//      }
    }
    
    boolean isWaitingNewPermissionsRequest = false;
    FacebookState facebookState = FacebookState.NOTHING;
    private enum FacebookState {
        NOTHING,                            // 投稿する必要なし
        OPENING_ACTIVE_SESSION,             // セッションオープン中
        REQUESTING_NEW_PUBLISH_PERMISSIONS  // 投稿権限取得中
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        Log.v(TAG, "onSessionStateChange");
        Log.v(TAG, "  session   = " + session);
        Log.v(TAG, "  state     = " + state);
        Log.v(TAG, "  exception = " + exception);
        Log.v(TAG, "  facebookState = " + facebookState);

        // キャンセルしたときは投稿ルーチンをやめる
        if (exception instanceof FacebookOperationCanceledException) {
            facebookState = FacebookState.NOTHING;
            return;
        }

        switch (facebookState) {
        case NOTHING:
            break;
        case OPENING_ACTIVE_SESSION:
        case REQUESTING_NEW_PUBLISH_PERMISSIONS:
            if (checkPostFacebook()) {
                postFacebook();
                facebookState = FacebookState.NOTHING;
            }
            break;
        }
    }

    private void loginFacebook() {
        Log.v(TAG, "loginFacebook()");

        // ログイン時にパーミッションを同時に取得するため少し特殊なやり方
        // http://www.notice.co.jp/archives/2505 を参考にした。
//        OpenRequest openRequest = new OpenRequest(this).setCallback(new StatusCallback() {
//            @Override
//            public void call(Session session, SessionState state, Exception exception) {
//                Log.v(TAG, "openRequest.call");
//                Log.v(TAG, "session = " + session);
//                if (session != null && session.isOpened()) {
//                    postFacebook();
//                }
//            }
//        });
//        openRequest.setPermissions(PERMISSIONS);
//
//        Session session = new Builder(this).build();
//        Session.setActiveSession(session);
//        session.openForPublish(openRequest);

        Session.openActiveSession(this, true, null);
    }
    
    // Facebook に投稿できるかチェックし、できなければそのための処理を行う。
    private boolean checkPostFacebook() {
        Log.v(TAG, "checkPostFacebook()");

        Session session = Session.getActiveSession();
        Log.v(TAG, "  session = " + session);
        
        // ログインできていない
        if (session == null || session.isOpened() == false) {
            if (facebookState != FacebookState.OPENING_ACTIVE_SESSION) {
                loginFacebook();
                facebookState = FacebookState.OPENING_ACTIVE_SESSION;
            }
            return false;
        }
        
        // 投稿権限がない
        if (hasPublishPermission(session) == false) {
            if (facebookState != FacebookState.REQUESTING_NEW_PUBLISH_PERMISSIONS) {
                Session.getActiveSession().requestNewPublishPermissions(
                        new Session.NewPermissionsRequest(this, PERMISSIONS));
                facebookState = FacebookState.REQUESTING_NEW_PUBLISH_PERMISSIONS;
            }
            return false;
        }
        
        return true;
    }
    
    private void postFacebook() {
        Log.v(TAG, "postFacebook()");

        Request request = Request.newGraphPathRequest(Session.getActiveSession(), "me/feed", new Request.Callback() {
            @Override
            public void onCompleted(Response response) {
                Log.v(TAG, "newGraphPathRequest.onCompleted()");
                Log.v(TAG, response.toString());
                
                Toast.makeText(BaseActivity.this, "Posted", Toast.LENGTH_SHORT).show();
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("message", "Check out KARA. #KARA");
        parameters.putString("picture", "http://cdn-jp.umgi.net/products/um/umck-9632_01_m.jpg");
        parameters.putString("link", "http://www.karaweb.jp/");
        parameters.putString("caption", "***** KARA *****");
        parameters.putString("description", "This is the KARA Official Web site in Japan.");
        request.setParameters(parameters);
        request.setHttpMethod(HttpMethod.POST);
        Request.executeBatchAsync(request);
    }

    private static boolean hasPublishPermission(Session session) {
        return session != null && session.getPermissions().containsAll(PERMISSIONS);
    }
}
