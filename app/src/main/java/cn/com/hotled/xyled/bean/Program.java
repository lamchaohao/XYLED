package cn.com.hotled.xyled.bean;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;

import java.io.File;
import java.util.List;

import cn.com.hotled.xyled.dao.DaoSession;
import cn.com.hotled.xyled.dao.ProgramDao;
import cn.com.hotled.xyled.dao.TextButtonDao;

/**
 * Created by Lam on 2016/12/13.
 */
@Entity
public class Program {
    @Id(autoincrement = true)
    private long id;

    private int sortNumber;

    private long screenId;
    private String programName;
    private int baseX;
    private int baseY;
    private float frameTime;
    private float stayTime;
    @Convert(converter = ProgramTypeConverter.class,columnType = String.class)
    private ProgramType programType;

    @Convert(converter = FileConverter.class,columnType = String.class)
    private File picFile;

    @ToMany(referencedJoinProperty = "programId")
    private List<TextButton> mTextButtons;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1978875243)
    private transient ProgramDao myDao;

    @Generated(hash = 1291497007)
    public Program(long id, int sortNumber, long screenId, String programName, int baseX, int baseY,
            float frameTime, float stayTime, ProgramType programType, File picFile) {
        this.id = id;
        this.sortNumber = sortNumber;
        this.screenId = screenId;
        this.programName = programName;
        this.baseX = baseX;
        this.baseY = baseY;
        this.frameTime = frameTime;
        this.stayTime = stayTime;
        this.programType = programType;
        this.picFile = picFile;
    }


    public Program() {
        setId(System.currentTimeMillis());
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getSortNumber() {
        return this.sortNumber;
    }

    public void setSortNumber(int sortNumber) {
        this.sortNumber = sortNumber;
    }

    public long getScreenId() {
        return this.screenId;
    }

    public void setScreenId(long screenId) {
        this.screenId = screenId;
    }

    public String getProgramName() {
        return this.programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public ProgramType getProgramType() {
        return this.programType;
    }

    public void setProgramType(ProgramType programType) {
        this.programType = programType;
    }

    public File getPicFile() {
        return this.picFile;
    }

    public void setPicFile(File picFile) {
        this.picFile = picFile;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1854599080)
    public List<TextButton> getMTextButtons() {
        if (mTextButtons == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            TextButtonDao targetDao = daoSession.getTextButtonDao();
            List<TextButton> mTextButtonsNew = targetDao
                    ._queryProgram_MTextButtons(id);
            synchronized (this) {
                if (mTextButtons == null) {
                    mTextButtons = mTextButtonsNew;
                }
            }
        }
        return mTextButtons;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 2029917038)
    public synchronized void resetMTextButtons() {
        mTextButtons = null;
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
    @Generated(hash = 1211846659)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getProgramDao() : null;
    }


    public int getBaseX() {
        return this.baseX;
    }


    public void setBaseX(int baseX) {
        this.baseX = baseX;
    }


    public int getBaseY() {
        return this.baseY;
    }


    public void setBaseY(int baseY) {
        this.baseY = baseY;
    }


    public float getFrameTime() {
        return this.frameTime;
    }


    public void setFrameTime(float frameTime) {
        this.frameTime = frameTime;
    }


    public float getStayTime() {
        return this.stayTime;
    }


    public void setStayTime(float stayTime) {
        this.stayTime = stayTime;
    }


}