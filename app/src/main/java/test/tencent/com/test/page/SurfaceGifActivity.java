package test.tencent.com.test.page;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import test.tencent.com.test.R;
import test.tencent.com.test.widget.GifView;

public class SurfaceGifActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface_gif_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupViews();
    }

    private void setupViews() {
        GifView gifView = (GifView) findViewById(R.id.gif_view);
        gifView.setZOrderOnTop(true);
        gifView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
    }

}
