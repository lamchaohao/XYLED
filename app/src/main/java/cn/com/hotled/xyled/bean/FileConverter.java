package cn.com.hotled.xyled.bean;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.io.File;

/**
 * Created by Lam on 2016/12/14.
 */

public class FileConverter implements PropertyConverter<File,String> {

    @Override
    public File convertToEntityProperty(String databaseValue) {
        return new File(databaseValue);
    }

    @Override
    public String convertToDatabaseValue(File entityProperty) {
        return entityProperty.getAbsolutePath();
    }
}
