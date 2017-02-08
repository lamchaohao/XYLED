package cn.com.hotled.xyled.bean;

import java.util.List;

/**
 * Created by Lam on 2016/12/14.
 */
public class LedScreen {

    private long id;
    private String screenName;
    private int width;
    private int height;
    private String cardName;
    private String location;
    private List<Program> programList;
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

}
