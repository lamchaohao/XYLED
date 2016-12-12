package cn.com.hotled.xyled.bean;

/**
 * Created by Lam on 2016/12/1.
 */

public class Program {
    private String programName;
    private ProgramType mProgramType;
    public enum ProgramType{
        Text,Pic,Video
    }
    private int sortNumber;

    public Program(String programName, ProgramType programType) {
        this.programName = programName;
        mProgramType = programType;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public ProgramType getProgramType() {
        return mProgramType;
    }

    public void setProgramType(ProgramType programType) {
        mProgramType = programType;
    }

    @Override
    public String toString() {
        return "Program{" +
                "programName='" + programName + '\'' +
                ", mProgramType=" + mProgramType +
                '}';
    }
}
