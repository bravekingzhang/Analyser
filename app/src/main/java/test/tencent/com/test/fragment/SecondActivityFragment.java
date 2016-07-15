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
 */
public class SecondActivityFragment extends Fragment {

    public static final String TAG = SecondActivityFragment.class.getSimpleName();

    TextView mTextView;

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
    }

    private void startAsyncTask() {
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

                while (i<3){
                    Thread.sleep(5000);
                    i++;
                    Log.e(TAG, "doInBackground: sleep time"+i);
                }
            } catch ( InterruptedException e ) {
                e.printStackTrace();
            }
            return 0;
        }
    }
}
