package cn.com.hotled.xyled.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import cn.com.hotled.xyled.bean.FileConverter;
import java.io.File;

import cn.com.hotled.xyled.bean.TextContent;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "TEXT_CONTENT".
*/
public class TextContentDao extends AbstractDao<TextContent, Long> {

    public static final String TABLENAME = "TEXT_CONTENT";

    /**
     * Properties of entity TextContent.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, long.class, "id", true, "_id");
        public final static Property Text = new Property(1, String.class, "text", false, "TEXT");
        public final static Property Typeface = new Property(2, String.class, "typeface", false, "TYPEFACE");
        public final static Property TextSize = new Property(3, int.class, "textSize", false, "TEXT_SIZE");
        public final static Property TextColor = new Property(4, int.class, "textColor", false, "TEXT_COLOR");
        public final static Property TextBackgroudColor = new Property(5, int.class, "textBackgroudColor", false, "TEXT_BACKGROUD_COLOR");
        public final static Property Isbold = new Property(6, boolean.class, "isbold", false, "ISBOLD");
        public final static Property IsIlatic = new Property(7, boolean.class, "isIlatic", false, "IS_ILATIC");
        public final static Property IsUnderline = new Property(8, boolean.class, "isUnderline", false, "IS_UNDERLINE");
        public final static Property IsSelected = new Property(9, boolean.class, "isSelected", false, "IS_SELECTED");
        public final static Property IsTextReverse = new Property(10, boolean.class, "isTextReverse", false, "IS_TEXT_REVERSE");
        public final static Property SortNumber = new Property(11, int.class, "sortNumber", false, "SORT_NUMBER");
        public final static Property ProgramId = new Property(12, long.class, "programId", false, "PROGRAM_ID");
        public final static Property TextEffect = new Property(13, int.class, "textEffect", false, "TEXT_EFFECT");
        public final static Property TextSpeed = new Property(14, int.class, "textSpeed", false, "TEXT_SPEED");
    }

    private final FileConverter typefaceConverter = new FileConverter();

    public TextContentDao(DaoConfig config) {
        super(config);
    }
    
    public TextContentDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"TEXT_CONTENT\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ," + // 0: id
                "\"TEXT\" TEXT," + // 1: text
                "\"TYPEFACE\" TEXT," + // 2: typeface
                "\"TEXT_SIZE\" INTEGER NOT NULL ," + // 3: textSize
                "\"TEXT_COLOR\" INTEGER NOT NULL ," + // 4: textColor
                "\"TEXT_BACKGROUD_COLOR\" INTEGER NOT NULL ," + // 5: textBackgroudColor
                "\"ISBOLD\" INTEGER NOT NULL ," + // 6: isbold
                "\"IS_ILATIC\" INTEGER NOT NULL ," + // 7: isIlatic
                "\"IS_UNDERLINE\" INTEGER NOT NULL ," + // 8: isUnderline
                "\"IS_SELECTED\" INTEGER NOT NULL ," + // 9: isSelected
                "\"IS_TEXT_REVERSE\" INTEGER NOT NULL ," + // 10: isTextReverse
                "\"SORT_NUMBER\" INTEGER NOT NULL ," + // 11: sortNumber
                "\"PROGRAM_ID\" INTEGER NOT NULL ," + // 12: programId
                "\"TEXT_EFFECT\" INTEGER NOT NULL ," + // 13: textEffect
                "\"TEXT_SPEED\" INTEGER NOT NULL );"); // 14: textSpeed
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"TEXT_CONTENT\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, TextContent entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getId());
 
        String text = entity.getText();
        if (text != null) {
            stmt.bindString(2, text);
        }
 
        File typeface = entity.getTypeface();
        if (typeface != null) {
            stmt.bindString(3, typefaceConverter.convertToDatabaseValue(typeface));
        }
        stmt.bindLong(4, entity.getTextSize());
        stmt.bindLong(5, entity.getTextColor());
        stmt.bindLong(6, entity.getTextBackgroudColor());
        stmt.bindLong(7, entity.getIsbold() ? 1L: 0L);
        stmt.bindLong(8, entity.getIsIlatic() ? 1L: 0L);
        stmt.bindLong(9, entity.getIsUnderline() ? 1L: 0L);
        stmt.bindLong(10, entity.getIsSelected() ? 1L: 0L);
        stmt.bindLong(11, entity.getIsTextReverse() ? 1L: 0L);
        stmt.bindLong(12, entity.getSortNumber());
        stmt.bindLong(13, entity.getProgramId());
        stmt.bindLong(14, entity.getTextEffect());
        stmt.bindLong(15, entity.getTextSpeed());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, TextContent entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getId());
 
        String text = entity.getText();
        if (text != null) {
            stmt.bindString(2, text);
        }
 
        File typeface = entity.getTypeface();
        if (typeface != null) {
            stmt.bindString(3, typefaceConverter.convertToDatabaseValue(typeface));
        }
        stmt.bindLong(4, entity.getTextSize());
        stmt.bindLong(5, entity.getTextColor());
        stmt.bindLong(6, entity.getTextBackgroudColor());
        stmt.bindLong(7, entity.getIsbold() ? 1L: 0L);
        stmt.bindLong(8, entity.getIsIlatic() ? 1L: 0L);
        stmt.bindLong(9, entity.getIsUnderline() ? 1L: 0L);
        stmt.bindLong(10, entity.getIsSelected() ? 1L: 0L);
        stmt.bindLong(11, entity.getIsTextReverse() ? 1L: 0L);
        stmt.bindLong(12, entity.getSortNumber());
        stmt.bindLong(13, entity.getProgramId());
        stmt.bindLong(14, entity.getTextEffect());
        stmt.bindLong(15, entity.getTextSpeed());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.getLong(offset + 0);
    }    

    @Override
    public TextContent readEntity(Cursor cursor, int offset) {
        TextContent entity = new TextContent( //
            cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // text
            cursor.isNull(offset + 2) ? null : typefaceConverter.convertToEntityProperty(cursor.getString(offset + 2)), // typeface
            cursor.getInt(offset + 3), // textSize
            cursor.getInt(offset + 4), // textColor
            cursor.getInt(offset + 5), // textBackgroudColor
            cursor.getShort(offset + 6) != 0, // isbold
            cursor.getShort(offset + 7) != 0, // isIlatic
            cursor.getShort(offset + 8) != 0, // isUnderline
            cursor.getShort(offset + 9) != 0, // isSelected
            cursor.getShort(offset + 10) != 0, // isTextReverse
            cursor.getInt(offset + 11), // sortNumber
            cursor.getLong(offset + 12), // programId
            cursor.getInt(offset + 13), // textEffect
            cursor.getInt(offset + 14) // textSpeed
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, TextContent entity, int offset) {
        entity.setId(cursor.getLong(offset + 0));
        entity.setText(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setTypeface(cursor.isNull(offset + 2) ? null : typefaceConverter.convertToEntityProperty(cursor.getString(offset + 2)));
        entity.setTextSize(cursor.getInt(offset + 3));
        entity.setTextColor(cursor.getInt(offset + 4));
        entity.setTextBackgroudColor(cursor.getInt(offset + 5));
        entity.setIsbold(cursor.getShort(offset + 6) != 0);
        entity.setIsIlatic(cursor.getShort(offset + 7) != 0);
        entity.setIsUnderline(cursor.getShort(offset + 8) != 0);
        entity.setIsSelected(cursor.getShort(offset + 9) != 0);
        entity.setIsTextReverse(cursor.getShort(offset + 10) != 0);
        entity.setSortNumber(cursor.getInt(offset + 11));
        entity.setProgramId(cursor.getLong(offset + 12));
        entity.setTextEffect(cursor.getInt(offset + 13));
        entity.setTextSpeed(cursor.getInt(offset + 14));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(TextContent entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(TextContent entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(TextContent entity) {
        throw new UnsupportedOperationException("Unsupported for entities with a non-null key");
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
