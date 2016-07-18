package test.tencent.com.test.page;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import test.tencent.com.test.R;
import test.tencent.com.test.util.CommenUtils;
import test.tencent.com.test.util.SobelUtils;
import test.tencent.com.test.widget.PainterView;

import static test.tencent.com.test.util.CommenUtils.getPicFromBytes;
import static test.tencent.com.test.util.CommenUtils.readStream;

public class PainterActivity extends AppCompatActivity {


    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STROAGE  = 101;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STROAGE = 102;
    Bitmap paintBm;

    ImageView   imageView;
    PainterView drawOutlineView;

    private CompositeSubscription _compositeSubscription = new CompositeSubscription();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_painter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageView = (ImageView) findViewById(R.id.source);
        drawOutlineView = (PainterView) findViewById(R.id.des);

        paintBm = CommenUtils.getRatioBitmap(this, R.drawable.paint, 10, 20);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "选择一副图像", Snackbar.LENGTH_LONG)
                        .setAction("Action", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                selectPhoto();
                            }
                        }).show();
            }
        });
    }


    private void selectPhoto() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择照片");
        builder.setPositiveButton("相机",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (ContextCompat.checkSelfPermission(PainterActivity.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(PainterActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STROAGE);
                        } else {
                            StartTakePhoto();
                        }
                    }
                });
        builder.setNegativeButton("相册",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (ContextCompat.checkSelfPermission(PainterActivity.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(PainterActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STROAGE);
                        } else {
                            startChooseAlbue();
                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    //从群聊选择
    private void StartTakePhoto() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "image.jpg"));
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(openCameraIntent, 0);
    }

    //从相册选择
    private void startChooseAlbue() {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * 因为两种方式都用到了startActivityForResult方法，这个方法执行完后都会执行onActivityResult方法
         * ， 所以为了区别到底选择了那个方式获取图片要进行判断
         * ，这里的requestCode跟startActivityForResult里面第二个参数对应 1== 相册 2 ==相机
         */
        if (requestCode == 1 && data != null) {

            try {
                ContentResolver resolver = getContentResolver();
                // 获得图片的uri
                Uri originalUri = data.getData();
                // 将图片内容解析成字节数组
                byte[] mContent = readStream(resolver.openInputStream(Uri.parse(originalUri.toString())));
                // 将字节数组转换为ImageView可调用的Bitmap对象
                Bitmap myBitmap = getPicFromBytes(mContent, null);
                // //把得到的图片绑定在控件上显示
                imageView.setImageBitmap(myBitmap);
            } catch ( Exception e ) {
                System.out.println(e.getMessage());
            }

        } else if (requestCode == 0) {

            Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/image.jpg");//这里的照片略微清晰很多
            if (bitmap == null) {
                return;
            }
            FileOutputStream fileOutputStream = null;
            File directory = new File(Environment.getExternalStorageDirectory(), "test/image");
            directory.mkdirs();// 创建文件夹，名称为myimage

            // 照片的命名，目标文件夹下，以当前时间数字串为名称，即可确保每张照片名称不相同。
            String fileName = directory.getAbsolutePath() + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".jpg";
            try {
                fileOutputStream = new FileOutputStream(fileName);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);// 把数据写入文件
                Bitmap bitmapCamera = CommenUtils.createScaleBitmap(bitmap, 200 * 3, false);
                imageView.setImageBitmap(bitmapCamera);
                bitmap.recycle();//将原图赶紧回收，避免oom
            } catch ( FileNotFoundException e ) {
                e.printStackTrace();
            } finally {
                try {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
        }
        //一旦原始图片设置好，之后，看是生成图片轮廓，然后，在去绘图
        if (imageView.getDrawable() instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            //将Bitmap压缩处理，防止OOM
            Bitmap bm = CommenUtils.createScaleBitmap(bitmap, 200 * 3, false);
            //返回的是处理过的Bitmap
            _compositeSubscription.add(createSobelBitmap(bm)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Bitmap>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(Bitmap sobelBm) {
                            drawOutlineView.setPaintBm(paintBm);
                            drawOutlineView.setSobelBitMap(sobelBm);
                        }
                    }));
        }
    }

    private Observable<Bitmap> createSobelBitmap(final Bitmap bm) {
        return Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                Bitmap bitmap = SobelUtils.Sobel(bm);
                subscriber.onNext(bitmap);
                subscriber.onCompleted();
            }
        });
    }


    @Override
    protected void onDestroy() {
        if (!_compositeSubscription.isUnsubscribed()) {
            _compositeSubscription.unsubscribe();
        }
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STROAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startChooseAlbue();
                } else {
                    // Permission Denied
                    Toast.makeText(PainterActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STROAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    StartTakePhoto();
                } else {
                    // Permission Denied
                    Toast.makeText(PainterActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
}
