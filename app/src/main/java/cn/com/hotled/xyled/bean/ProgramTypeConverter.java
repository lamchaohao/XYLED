package cn.com.hotled.xyled.bean;

import org.greenrobot.greendao.converter.PropertyConverter;

/**
 * Created by Lam on 2016/12/14.
 */

public class ProgramTypeConverter implements PropertyConverter<ProgramType,String> {
    @Override
    public ProgramType convertToEntityProperty(String databaseValue) {
        return ProgramType.valueOf(databaseValue);
    }

    @Override
    public String convertToDatabaseValue(ProgramType entityProperty) {
        return entityProperty.name();
    }
}
