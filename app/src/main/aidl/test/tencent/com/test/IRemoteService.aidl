// IRemoteService.aidl
package test.tencent.com.test;
// Declare any non-default types here with import statements
import test.tencent.com.test.model.Book;

interface IRemoteService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    int getPid();

    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    void addBook(in Book book);

    List<Book> listBook();

    void deleteBook(in int id);

}