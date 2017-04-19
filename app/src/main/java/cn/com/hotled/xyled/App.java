package cn.com.hotled.xyled;

import android.app.Application;

import org.greenrobot.greendao.database.Database;

import cn.com.hotled.xyled.dao.DaoMaster;
import cn.com.hotled.xyled.dao.DaoSession;

/**
 * Created by Lam on 2016/12/14.
 */

public class App extends Application {
    public static final boolean ENCRYPTED = false;
    private DaoSession daoSession;
    private Database mDb;

    @Override
    public void onCreate() {
        super.onCreate();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, ENCRYPTED ? "screen-db-encrypted" : "screen-db");
        mDb = ENCRYPTED ? helper.getEncryptedWritableDb("super-secret") : helper.getWritableDb();

        daoSession = new DaoMaster(mDb).newSession();

    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public Database getDb() {
        return mDb;
    }
}
