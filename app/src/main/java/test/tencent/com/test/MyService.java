package test.tencent.com.test;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import test.tencent.com.test.model.Book;


/**
 * Created by hoollyzhang on 16/8/12.
 * Description :
 */
public class MyService extends Service {


    private final List<Book> bookList = new ArrayList<>();

    private static final String TAG = "MyService";


    private final IRemoteService.Stub mBinder = new IRemoteService.Stub() {
        public int getPid() {
            return Process.myPid();
        }

        public void basicTypes(int anInt, long aLong, boolean aBoolean,
                               float aFloat, double aDouble, String aString) {
            // Does nothing
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            synchronized (bookList) {
                bookList.add(book);
            }
        }

        @Override
        public List<Book> listBook() throws RemoteException {
            synchronized (bookList){
                return bookList;
            }
        }

        @Override
        public void deleteBook(int id) throws RemoteException {
            synchronized (bookList){
                for (int i=0;i<bookList.size();i++){
                    if (bookList.get(i).getId() == id){
                        bookList.remove(i);
                        break;
                    }
                }
            }
        }

    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
