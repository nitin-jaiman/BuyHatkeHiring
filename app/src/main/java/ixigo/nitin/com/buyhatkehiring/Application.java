package ixigo.nitin.com.buyhatkehiring;

import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by apple on 26/02/17.
 */

public class Application extends android.app.Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


}
