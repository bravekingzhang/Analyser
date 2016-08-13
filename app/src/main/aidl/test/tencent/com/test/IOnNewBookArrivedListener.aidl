// IOnNewBookArrivedListener.aidl
package test.tencent.com.test;
//也要注意这里引入book的包名
import test.tencent.com.test.model.Book;

// Declare any non-default types here with import statements

interface IOnNewBookArrivedListener {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
     void onNewBookArrived(in Book newBook);
}
