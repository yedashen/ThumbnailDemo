package tyj.com.thumbnaildemo;

import android.app.Application;
import android.content.Context;

/**
 * @author ChenYe
 *         created by on 2018/1/10 0010. 10:16
 **/

public class App extends Application {

    public static Context mShareInstance = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mShareInstance = getApplicationContext();
    }
}
