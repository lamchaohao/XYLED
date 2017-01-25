package cn.com.hotled.xyled.bean;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;

import java.io.File;

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
    @Convert(converter = FileConverter.class,columnType = String.class)
    private File flowBoundFile;

    private int flowEffect;
    private int flowSpeed;
    private boolean useFlowBound;
    @Convert(converter = ProgramTypeConverter.class,columnType = String.class)
    private ProgramType programType;

    @Convert(converter = FileConverter.class,columnType = String.class)
    private File picFile;
    @ToOne
    private TextButton mTextButton;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1978875243)
    private transient ProgramDao myDao;
    @Generated(hash = 2090265522)
    public Program(long id, int sortNumber, long screenId, String programName,
            int baseX, int baseY, float frameTime, float stayTime,
            File flowBoundFile, int flowEffect, int flowSpeed, boolean useFlowBound,
            ProgramType programType, File picFile) {
        this.id = id;
        this.sortNumber = sortNumber;
        this.screenId = screenId;
        this.programName = programName;
        this.baseX = baseX;
        this.baseY = baseY;
        this.frameTime = frameTime;
        this.stayTime = stayTime;
        this.flowBoundFile = flowBoundFile;
        this.flowEffect = flowEffect;
        this.flowSpeed = flowSpeed;
        this.useFlowBound = useFlowBound;
        this.programType = programType;
        this.picFile = picFile;
    }
    @Generated(hash = 775603163)
    public Program() {
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
    public File getFlowBoundFile() {
        return this.flowBoundFile;
    }
    public void setFlowBoundFile(File flowBoundFile) {
        this.flowBoundFile = flowBoundFile;
    }
    public int getFlowEffect() {
        return this.flowEffect;
    }
    public void setFlowEffect(int flowEffect) {
        this.flowEffect = flowEffect;
    }
    public int getFlowSpeed() {
        return this.flowSpeed;
    }
    public void setFlowSpeed(int flowSpeed) {
        this.flowSpeed = flowSpeed;
    }
    public boolean getUseFlowBound() {
        return this.useFlowBound;
    }
    public void setUseFlowBound(boolean useFlowBound) {
        this.useFlowBound = useFlowBound;
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
    @Generated(hash = 831346144)
    private transient boolean mTextButton__refreshed;
    /** To-one relationship, resolved on first access. */
    @Generated(hash = 962665120)
    public TextButton getMTextButton() {
        if (mTextButton != null || !mTextButton__refreshed) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            TextButtonDao targetDao = daoSession.getTextButtonDao();
            targetDao.refresh(mTextButton);
            mTextButton__refreshed = true;
        }
        return mTextButton;
    }
    /** To-one relationship, returned entity is not refreshed and may carry only the PK property. */
    @Generated(hash = 515791678)
    public TextButton peakMTextButton() {
        return mTextButton;
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 727491815)
    public void setMTextButton(TextButton mTextButton) {
        synchronized (this) {
            this.mTextButton = mTextButton;
            mTextButton__refreshed = true;
        }
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



}
