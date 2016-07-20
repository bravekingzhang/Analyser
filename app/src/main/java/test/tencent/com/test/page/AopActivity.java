package test.tencent.com.test.page;

import android.Manifest;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import test.tencent.com.aop.anonation.DebugTrace;
import test.tencent.com.aop.anonation.PermissionCheck;
import test.tencent.com.test.R;

public class AopActivity extends AppCompatActivity {
    private static final String TAG = "AopActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aop);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                testAopMethod();
                                xxxTakePhoto();
                            }
                        }).show();
            }
        });
    }

    @DebugTrace
    private void testAopMethod() {
        try {
            Thread.sleep(10);
        } catch ( InterruptedException e ) {
            e.printStackTrace();
        }
    }

    @PermissionCheck(permession = {Manifest.permission.WRITE_EXTERNAL_STORAGE
            , Manifest.permission.READ_EXTERNAL_STORAGE
            , Manifest.permission.ACCESS_NETWORK_STATE
            , Manifest.permission.RECORD_AUDIO})
    private void xxxTakePhoto() {
        Log.e(TAG, " test xxxTakePhoto() called with: " + "");
    }

}
