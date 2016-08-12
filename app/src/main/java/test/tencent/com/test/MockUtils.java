package test.tencent.com.test;

import java.util.Random;

import test.tencent.com.test.model.Book;


/**
 * Created by hoollyzhang on 16/8/12.
 * Description :
 */
public class MockUtils {

    private static final String books[] = new String[] {"大海的女儿","青青子衿","人生如意","就是你","冬日天下"};
    public static final Book mockBook(){
        return new Book(new Random().nextInt(100),books[new Random().nextInt(books.length)]);
    }
}
