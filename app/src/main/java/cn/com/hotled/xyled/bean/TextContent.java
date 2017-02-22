package cn.com.hotled.xyled.bean;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.File;

/**
 * Created by Lam on 2016/11/1.
 */
@Entity
public class TextContent {
    @Id(autoincrement = true)
    private long id;
    private String text;
    @Convert(converter = FileConverter.class,columnType = String.class)
    private File typeface;
    private int textSize;
    private int textColor;
    private int textBackgroudColor;
    private boolean isbold;
    private boolean isIlatic;
    private boolean isUnderline;
    private boolean isSelected;
    private int sortNumber;
    private long programId;
    private int textEffect;
    @Generated(hash = 112386340)
    public TextContent(long id, String text, File typeface, int textSize,
            int textColor, int textBackgroudColor, boolean isbold, boolean isIlatic,
            boolean isUnderline, boolean isSelected, int sortNumber, long programId,
            int textEffect) {
        this.id = id;
        this.text = text;
        this.typeface = typeface;
        this.textSize = textSize;
        this.textColor = textColor;
        this.textBackgroudColor = textBackgroudColor;
        this.isbold = isbold;
        this.isIlatic = isIlatic;
        this.isUnderline = isUnderline;
        this.isSelected = isSelected;
        this.sortNumber = sortNumber;
        this.programId = programId;
        this.textEffect = textEffect;
    }
    @Generated(hash = 1675015659)
    public TextContent() {
    }
    public long getId() {
        return this.id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getText() {
        return this.text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public File getTypeface() {
        return this.typeface;
    }
    public void setTypeface(File typeface) {
        this.typeface = typeface;
    }
    public int getTextSize() {
        return this.textSize;
    }
    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }
    public int getTextColor() {
        return this.textColor;
    }
    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }
    public int getTextBackgroudColor() {
        return this.textBackgroudColor;
    }
    public void setTextBackgroudColor(int textBackgroudColor) {
        this.textBackgroudColor = textBackgroudColor;
    }
    public boolean getIsbold() {
        return this.isbold;
    }
    public void setIsbold(boolean isbold) {
        this.isbold = isbold;
    }
    public boolean getIsIlatic() {
        return this.isIlatic;
    }
    public void setIsIlatic(boolean isIlatic) {
        this.isIlatic = isIlatic;
    }
    public boolean getIsUnderline() {
        return this.isUnderline;
    }
    public void setIsUnderline(boolean isUnderline) {
        this.isUnderline = isUnderline;
    }
    public boolean getIsSelected() {
        return this.isSelected;
    }
    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
    public int getSortNumber() {
        return this.sortNumber;
    }
    public void setSortNumber(int sortNumber) {
        this.sortNumber = sortNumber;
    }
    public long getProgramId() {
        return this.programId;
    }
    public void setProgramId(long programId) {
        this.programId = programId;
    }
    public int getTextEffect() {
        return this.textEffect;
    }
    public void setTextEffect(int textEffect) {
        this.textEffect = textEffect;
    }

}
