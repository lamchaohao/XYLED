package cn.com.hotled.xyled.bean;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.File;

/**
 * Created by Lam on 2017/3/8.
 */

@Entity
public class TraceFile {
    @Id(autoincrement = true)
    private long id;
    private String traceLineFile_en;
    private String traceLineFile_zh;
    @Convert(converter = FileConverter.class,columnType = String.class)
    private File filePath;
    private int pixel;
    private int scan;
    private int size;
    private int hub;
    private int scanCount;
    private int foldCount;
    private int moduleWidth;
    private int moduleHeight;
    private int RGBCount;
    private int dotCount;
    @Generated(hash = 1885271922)
    public TraceFile(long id, String traceLineFile_en, String traceLineFile_zh,
            File filePath, int pixel, int scan, int size, int hub, int scanCount,
            int foldCount, int moduleWidth, int moduleHeight, int RGBCount,
            int dotCount) {
        this.id = id;
        this.traceLineFile_en = traceLineFile_en;
        this.traceLineFile_zh = traceLineFile_zh;
        this.filePath = filePath;
        this.pixel = pixel;
        this.scan = scan;
        this.size = size;
        this.hub = hub;
        this.scanCount = scanCount;
        this.foldCount = foldCount;
        this.moduleWidth = moduleWidth;
        this.moduleHeight = moduleHeight;
        this.RGBCount = RGBCount;
        this.dotCount = dotCount;
    }
    @Generated(hash = 1553351687)
    public TraceFile() {
    }
    public long getId() {
        return this.id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getTraceLineFile_en() {
        return this.traceLineFile_en;
    }
    public void setTraceLineFile_en(String traceLineFile_en) {
        this.traceLineFile_en = traceLineFile_en;
    }
    public String getTraceLineFile_zh() {
        return this.traceLineFile_zh;
    }
    public void setTraceLineFile_zh(String traceLineFile_zh) {
        this.traceLineFile_zh = traceLineFile_zh;
    }
    public File getFilePath() {
        return this.filePath;
    }
    public void setFilePath(File filePath) {
        this.filePath = filePath;
    }
    public int getPixel() {
        return this.pixel;
    }
    public void setPixel(int pixel) {
        this.pixel = pixel;
    }
    public int getScan() {
        return this.scan;
    }
    public void setScan(int scan) {
        this.scan = scan;
    }
    public int getSize() {
        return this.size;
    }
    public void setSize(int size) {
        this.size = size;
    }
    public int getHub() {
        return this.hub;
    }
    public void setHub(int hub) {
        this.hub = hub;
    }
    public int getScanCount() {
        return this.scanCount;
    }
    public void setScanCount(int scanCount) {
        this.scanCount = scanCount;
    }
    public int getFoldCount() {
        return this.foldCount;
    }
    public void setFoldCount(int foldCount) {
        this.foldCount = foldCount;
    }
    public int getModuleWidth() {
        return this.moduleWidth;
    }
    public void setModuleWidth(int moduleWidth) {
        this.moduleWidth = moduleWidth;
    }
    public int getModuleHeight() {
        return this.moduleHeight;
    }
    public void setModuleHeight(int moduleHeight) {
        this.moduleHeight = moduleHeight;
    }
    public int getRGBCount() {
        return this.RGBCount;
    }
    public void setRGBCount(int RGBCount) {
        this.RGBCount = RGBCount;
    }
    public int getDotCount() {
        return this.dotCount;
    }
    public void setDotCount(int dotCount) {
        this.dotCount = dotCount;
    }

    
}
