package cn.com.hotled.xyled.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lam on 2016/12/1.
 */

public class LedScreen implements Parcelable{

    private String screenName;
    private int width;
    private int height;
    private String cardName;
    private List<Program> mProgramList;

    public LedScreen(String screenName, int width, int height, String cardName, List<Program> programList) {
        this.screenName = screenName;
        this.width = width;
        this.height = height;
        this.cardName = cardName;
        mProgramList = programList;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public List<Program> getProgramList() {
        return mProgramList;
    }

    public void setProgramList(List<Program> programList) {
        mProgramList = programList;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    @Override
    public String toString() {
        return "LedScreen{" +
                "screenName='" + screenName + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", cardName='" + cardName + '\'' +
                ", mProgramList=" + mProgramList +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.screenName);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeString(this.cardName);
        dest.writeList(this.mProgramList);
    }

    protected LedScreen(Parcel in) {
        this.screenName = in.readString();
        this.width = in.readInt();
        this.height = in.readInt();
        this.cardName = in.readString();
        this.mProgramList = new ArrayList<Program>();
        in.readList(this.mProgramList, Program.class.getClassLoader());
    }

    public static final Creator<LedScreen> CREATOR = new Creator<LedScreen>() {
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
