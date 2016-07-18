package test.tencent.com.test.page;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import test.tencent.com.test.R;
import test.tencent.com.test.util.RxUtils;

public class SecondActivity extends AppCompatActivity {

    private static final String TAG = SecondActivity.class.getSimpleName();

    CompositeSubscription  _subscription = new CompositeSubscription();

    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        textView = (TextView) findViewById(R.id.text);
    }

    @Override
    protected void onResume() {
        super.onResume();
        _subscription.add(new RxUtils().testRx()
                .asObservable()
                //.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        Log.e(TAG, "onNext: "+s);
                        textView.setText(s);
                    }
                }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!_subscription.isUnsubscribed()){
            _subscription.unsubscribe();
        }
    }
}
