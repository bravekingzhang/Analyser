package test.tencent.com.test.page;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import test.tencent.com.test.DrawView;
import test.tencent.com.test.R;

/**
 * Created by hoollyzhang on 16/7/16.
 * Description :
 */
public class SurfaceViewActivity  extends AppCompatActivity{


    private DrawView drawView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface_test);

        drawView = (DrawView) findViewById(R.id.surfaceview);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
