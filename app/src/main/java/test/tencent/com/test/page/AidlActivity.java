package test.tencent.com.test.page;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import test.tencent.com.test.IRemoteService;
import test.tencent.com.test.MockUtils;
import test.tencent.com.test.MyService;
import test.tencent.com.test.R;

public class AidlActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AidlActivity";

    TextView start, stop, add,list, delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidl);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        setupView();
    }

    private void setupView() {
        start = (TextView) findViewById(R.id.start);
        stop = (TextView) findViewById(R.id.stop);
        add = (TextView) findViewById(R.id.add);
        list = (TextView) findViewById(R.id.list);
        delete = (TextView) findViewById(R.id.delete);

        start.setOnClickListener(this);
        stop.setOnClickListener(this);
        add.setOnClickListener(this);
        list.setOnClickListener(this);
        delete.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.start:
                bindservie();
                break;
            case R.id.stop:
                break;
            case R.id.add:
                add();
                break;
            case R.id.list:
                list();
                break;
            case R.id.delete:
                delete();
                break;
        }
    }

    private void delete() {
        if (mIRemoteService != null) {
            try {
                Log.e(TAG, "list() called with: " + mIRemoteService.getPid());
                if(mIRemoteService.listBook().size()>0){
                    Log.e(TAG, "delete() called with: " + mIRemoteService.listBook().get(0).getId());
                    mIRemoteService.deleteBook(mIRemoteService.listBook().get(0).getId());
                }
            } catch ( RemoteException e ) {
                e.printStackTrace();
            }
        }
    }

    private void list() {
        if (mIRemoteService != null) {
            try {
                Log.e(TAG, "list() called with: " + mIRemoteService.getPid());
                Log.e(TAG, "list() called with: " + mIRemoteService.listBook());
            } catch ( RemoteException e ) {
                e.printStackTrace();
            }
        }
    }

    private void add() {
        if (mIRemoteService != null) {
            try {
                Log.e(TAG, "add() called with: " + mIRemoteService.getPid());
                mIRemoteService.addBook(MockUtils.mockBook());
            } catch ( RemoteException e ) {
                e.printStackTrace();
            }
        }
    }

    private void bindservie() {
        Intent intent = new Intent(this, MyService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        Log.e(TAG, "bindservie() called with: " + "");
    }

    private void unBindService() {
        if (mIRemoteService != null && mIRemoteService.asBinder().isBinderAlive()) {
            unbindService(mConnection);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unBindService();
    }

    IRemoteService mIRemoteService;
    private ServiceConnection mConnection = new ServiceConnection() {
        // Called when the connection with the service is established
        public void onServiceConnected(ComponentName className, IBinder service) {
            // Following the example above for an AIDL interface,
            // this gets an instance of the IRemoteInterface, which we can use to call on the service
            mIRemoteService = IRemoteService.Stub.asInterface(service);
        }

        // Called when the connection with the service disconnects unexpectedly
        public void onServiceDisconnected(ComponentName className) {
            Log.e(TAG, "Service has unexpectedly disconnected");
            mIRemoteService = null;
        }
    };

}
