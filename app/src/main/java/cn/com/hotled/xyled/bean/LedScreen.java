package cn.com.hotled.xyled.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.ArrayList;
import java.util.List;

import cn.com.hotled.xyled.dao.DaoSession;
import cn.com.hotled.xyled.dao.LedScreenDao;
import cn.com.hotled.xyled.dao.ProgramDao;

/**
 * Created by Lam on 2016/12/14.
 */
@Entity
public class LedScreen implements Parcelable {

    @Id(autoincrement = true)
    private long id;
    private String screenName;
    private int width;
    private int height;
    private String cardName;
    private String location;
    @ToMany(referencedJoinProperty = "screenId")
    private List<Program> programList;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 466806085)
    private transient LedScreenDao myDao;
    @Generated(hash = 492642423)
    public LedScreen(long id, String screenName, int width, int height,
            String cardName, String location) {
        this.id = id;
        this.screenName = screenName;
        this.width = width;
        this.height = height;
        this.cardName = cardName;
        this.location = location;
    }

    public LedScreen() {
        setId(System.currentTimeMillis());
    }

    public LedScreen(String screenName, int width, int height, String cardName, String location) {
        setId(System.currentTimeMillis());
        this.screenName = screenName;
        this.width = width;
        this.height = height;
        this.cardName = cardName;
        this.location = location;
    }

    public long getId() {
        return this.id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getScreenName() {
        return this.screenName;
    }
    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }
    public int getWidth() {
        return this.width;
    }
    public void setWidth(int width) {
        this.width = width;
    }
    public int getHeight() {
        return this.height;
    }
    public void setHeight(int height) {
        this.height = height;
    }
    public String getCardName() {
        return this.cardName;
    }
    public void setCardName(String cardName) {
        this.cardName = cardName;
    }
    public String getLocation() {
        return this.location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 703030351)
    public List<Program> getProgramList() {
        if (programList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ProgramDao targetDao = daoSession.getProgramDao();
            List<Program> programListNew = targetDao
                    ._queryLedScreen_ProgramList(id);
            synchronized (this) {
                if (programList == null) {
                    programList = programListNew;
                }
            }
        }
        return programList;
    }
    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 249987504)
    public synchronized void resetProgramList() {
        programList = null;
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 147295029)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getLedScreenDao() : null;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.screenName);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeString(this.cardName);
        dest.writeString(this.location);
    }

    protected LedScreen(Parcel in) {
        this.id = in.readLong();
        this.screenName = in.readString();
        this.width = in.readInt();
        this.height = in.readInt();
        this.cardName = in.readString();
        this.location = in.readString();
        this.programList = new ArrayList<Program>();
        in.readList(this.programList, Program.class.getClassLoader());
    }

    public static final Parcelable.Creator<LedScreen> CREATOR = new Parcelable.Creator<LedScreen>() {
        @Override
        public LedScreen createFromParcel(Parcel source) {
            return new LedScreen(source);
        }

        @Override
        public LedScreen[] newArray(int size) {
            return new LedScreen[size];
        }
    };
}
