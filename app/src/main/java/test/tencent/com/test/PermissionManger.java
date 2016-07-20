package test.tencent.com.test;

import android.content.Context;

/**
 * Created by hoollyzhang on 16/7/20.
 * Description :
 */
public class PermissionManger {

    private static PermissionManger _permissionManger = null;

    //getApplicationContext
    private PermissionManger(Context context) {
    }

    public static PermissionManger getInstance(Context context){
        if (_permissionManger!=null){
            return _permissionManger;
        }else{
            return new PermissionManger(context);
        }

    }

}
