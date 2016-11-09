package cn.com.hotled.xyled.bean;

import java.io.File;

/**
 * Created by Lam on 2016/11/9.
 */

public class TypefaceFile {
    private File file;
    private boolean isSelected;

    public TypefaceFile(File file, boolean isSelected) {
        this.file = file;
        this.isSelected = isSelected;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
