package test.tencent.com.test.page;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import test.tencent.com.test.fragment.MainActivityFragment;
import test.tencent.com.test.R;
import test.tencent.com.test.fragment.SecondActivityFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }


    @Override
    protected void onResume() {
        super.onResume();
        getSupportFragmentManager().beginTransaction().replace(R.id.container,new MainActivityFragment()).commitAllowingStateLoss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action1:
                getSupportFragmentManager().beginTransaction().replace(R.id.container,new MainActivityFragment()).commitAllowingStateLoss();
                break;
            case R.id.action2:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new SecondActivityFragment()).commitAllowingStateLoss();
                break;
            case R.id.action3:
                startActivity(new Intent(this,SecondActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
