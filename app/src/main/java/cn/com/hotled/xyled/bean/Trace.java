package cn.com.hotled.xyled.bean;

/**
 * Created by Lam on 2017/3/9.
 */

public class Trace {
    private boolean isSelected;
    private TraceFile mTraceFile;

    public Trace() {
    }

    public Trace(boolean isSelected, TraceFile traceFile) {
        this.isSelected = isSelected;
        mTraceFile = traceFile;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public TraceFile getTraceFile() {
        return mTraceFile;
    }

    public void setTraceFile(TraceFile traceFile) {
        mTraceFile = traceFile;
    }
}
