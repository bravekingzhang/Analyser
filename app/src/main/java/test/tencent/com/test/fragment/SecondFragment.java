package test.tencent.com.test.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import test.tencent.com.test.R;

/**
 * A placeholder fragment containing a simple view.
 * 验证 AsyncTask  内存泄露。
 */
public class SecondFragment extends Fragment {

    public static final String TAG = SecondFragment.class.getSimpleName();

    TextView mTextView;

    MyAsyncTask myAsyncTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.second_fragment, container, false);
        mTextView = (TextView) view.findViewById(R.id.text);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        startAsyncTask();
    }

    @Override
    public void onAttach(Context context) {
        Log.e(TAG, "onAttach: ");
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e(TAG, "onDetach: excuted!");
        //myAsyncTask.cancel(true);

    }

    private void startAsyncTask() {
        //myAsyncTask = new MyAsyncTask();
        //myAsyncTask.execute(1);
        //fragment onDetach 的时候，不结束AsyncTask，是会造成良性的内存泄露的，只有等任务执行完之后，才能回收。如果按照上面写，且在onDetach中去cancel这个不结束AsyncTask，是不会造成泄漏的。
        new MyAsyncTask().execute(1);
    }

    class MyAsyncTask extends AsyncTask<Integer, Void, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            Log.e(TAG, "onPostExecute: ");
            mTextView.setText("任务这里已经执行完成");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(Integer integer) {
            super.onCancelled(integer);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            // do long time task,
            try {
                int i = 0;
                while (i < 3) {
                    Thread.sleep(5000);
                    i++;
                    Log.e(TAG, "doInBackground: sleep time" + i);
                }
            } catch ( InterruptedException e ) {
                e.printStackTrace();
            }
            return 0;
        }
    }
}
