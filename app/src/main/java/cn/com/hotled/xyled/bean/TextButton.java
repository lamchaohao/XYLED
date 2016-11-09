package cn.com.hotled.xyled.bean;

import android.graphics.Typeface;
import android.widget.Button;

import java.io.File;

/**
 * Created by Lam on 2016/11/1.
 */

public class TextButton {
    private Button button;
    private String text;
    private File typeface;
    private int textSize;
    private int textColor;
    private int textBackgroudColor;
    private boolean isbold;
    private boolean isIlatic;
    private boolean isUnderline;
    private int transX;
    private int transY;
    private boolean isSelected;


    public TextButton(String text, int textSize, int textColor, int textBackgroudColor, boolean isbold, boolean isIlatic, boolean isUnderline) {
        this.text = text;
        this.textSize = textSize;
        this.textColor = textColor;
        this.textBackgroudColor = textBackgroudColor;
        this.isbold = isbold;
        this.isIlatic = isIlatic;
        this.isUnderline = isUnderline;
    }

    public Button getButton() {
        return button;
    }

    public void setButton(Button button) {
        this.button = button;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getTextBackgroudColor() {
        return textBackgroudColor;
    }

    public void setTextBackgroudColor(int textBackgroudColor) {
        this.textBackgroudColor = textBackgroudColor;
    }

    public boolean isbold() {
        return isbold;
    }

    public void setIsbold(boolean isbold) {
        this.isbold = isbold;
    }

    public boolean isIlatic() {
        return isIlatic;
    }

    public void setIlatic(boolean ilatic) {
        isIlatic = ilatic;
    }

    public boolean isUnderline() {
        return isUnderline;
    }

    public void setUnderline(boolean underline) {
        isUnderline = underline;
    }

    public int getTransX() {
        return transX;
    }

    public void setTransX(int transX) {
        this.transX = transX;
    }

    public int getTransY() {
        return transY;
    }

    public void setTransY(int transY) {
        this.transY = transY;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public File getTypeface() {
        return typeface;
    }

    public void setTypeface(File typeface) {
        this.typeface = typeface;
    }
}
