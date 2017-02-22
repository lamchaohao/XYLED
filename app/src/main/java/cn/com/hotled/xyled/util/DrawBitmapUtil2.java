package cn.com.hotled.xyled.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.com.hotled.xyled.App;
import cn.com.hotled.xyled.bean.Program;
import cn.com.hotled.xyled.bean.ProgramType;
import cn.com.hotled.xyled.bean.TextContent;
import cn.com.hotled.xyled.dao.TextContentDao;
import cn.com.hotled.xyled.global.Global;

import static android.graphics.Color.BLACK;

/**
 * Created by Lam on 2017/1/5.
 */

public class DrawBitmapUtil2 {
    private Activity mContext;
    private List<Program> mProgramList;
    private List<TextContent> mTextContentList;
    private int mTextSize;
    private int mTextColor = Color.RED;
    private int mTextBgColor = BLACK;
    private boolean isBold;
    private boolean isItalic;
    private boolean isUnderLine;
    private File mTypeFile;
    private List<Integer> mWidthList;
    private List<Integer> mHeightList;
    private int mBaseX = 0;
    private int mBaseY = 25;
    private int mIndex=0;
    private int mTextEffect;

    public DrawBitmapUtil2(Activity context, List<Program> programList, List<Integer> widths, List<Integer> heights) {
        mContext = context;
        mProgramList = programList;
        mWidthList = widths;
        mHeightList = heights;
    }



    public List<Bitmap> drawBitmap() {
        List<Bitmap> bitmaps = new ArrayList<>();
        for (Program program : mProgramList) {
            if (program.getProgramType()== ProgramType.Text){//如果是文本的节目才继续
                mTextContentList = ((App) mContext.getApplication()).getDaoSession().getTextContentDao().queryBuilder().where(TextContentDao.Properties.ProgramId.eq(program.getId())).list();
                if (mTextContentList == null || mTextContentList.size() == 0) {
                    mTextContentList = new ArrayList<>();
                }

                for (int i = 0; i < mTextContentList.size(); i++) {
                    if (i == 0) {
                        TextContent textContent =  mTextContentList.get(0);
                        mTextBgColor = textContent.getTextBackgroudColor();
                        mTextColor = textContent.getTextColor();
                        mTextSize = textContent.getTextSize();
                        isUnderLine = textContent.getIsUnderline();
                        isItalic = textContent.getIsIlatic();
                        isBold = textContent.getIsbold();
                        mTextEffect = textContent.getTextEffect();
                        mTypeFile = textContent.getTypeface();
                        mBaseX = program.getBaseX();
                        mBaseY = program.getBaseY();
                    }
                }
                bitmaps.add(drawText());
                mIndex++;
            }

        }
        return bitmaps;
    }

    private Bitmap drawText() {
        Paint paint = new Paint();
        Canvas canvas = new Canvas();
        StringBuilder sb = new StringBuilder();
        for (TextContent textContent : mTextContentList) {
            sb.append(textContent.getText());
        }

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));

        //如果图片比所设置的宽，则需加长

        //先设置好画笔，才进行计算
        paint.setColor(mTextColor);
        paint.setTextSize(mTextSize);

        if (mTypeFile != null) {
            Typeface typeface = Typeface.createFromFile(mTypeFile);
            paint.setTypeface(typeface);
            if (isBold) {//粗体
                paint.setTypeface(Typeface.create(typeface, Typeface.BOLD));
            }
            if (isItalic) {//斜体
                paint.setTypeface(Typeface.create(typeface, Typeface.ITALIC));
            }
            if (isBold && isItalic) {//粗斜体
                paint.setTypeface(Typeface.create(typeface, Typeface.BOLD_ITALIC));
            }
        } else {
            paint.setTypeface(Typeface.DEFAULT);

            if (isBold) {//粗体
                paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            }
            if (isItalic) {//斜体
                paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
            }
            if (isBold && isItalic) {//粗斜体
                paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC));
            }


        }
        if (isUnderLine) {//下划线
            paint.setUnderlineText(true);
        } else {
            paint.setUnderlineText(false);
        }
        paint.setTextAlign(Paint.Align.LEFT);
        Bitmap bitmap = Bitmap.createBitmap(mWidthList.get(mIndex), mHeightList.get(mIndex), Bitmap.Config.ARGB_4444);
//        Bitmap bitmap =null;
        if (bitmap != null)
            canvas.setBitmap(bitmap);
        //设置好画笔，开始计算
        int mWidth =0;
        float drawWidth = computeWidth(sb.toString(), paint);
        if (drawWidth > mWidthList.get(mIndex)) {
            mWidth = (int) drawWidth;
            //这里在两边都加上了一段空白区域
            if (mTextEffect== Global.TEXT_EFFECT_APPEAR) {
                mWidth+=mWidthList.get(mIndex);
            }else {
                mWidth+=mWidthList.get(mIndex)*2;
            }

            bitmap = Bitmap.createBitmap(mWidth, mHeightList.get(mIndex), Bitmap.Config.ARGB_4444);
            if (bitmap != null)
                canvas.setBitmap(bitmap);
        }
        //背景
        drawBgColor(drawWidth, canvas);
        //文本
        switch (mTextEffect){
            case Global.TEXT_EFFECT_MOVE_LEFT:
                canvas.drawText(sb.toString(), mBaseX+mWidthList.get(mIndex), mBaseY, paint);
                break;
            case Global.TEXT_EFFECT_APPEAR:
                canvas.drawText(sb.toString(), mBaseX, mBaseY, paint);
                break;
            case Global.TEXT_EFFECT_STATIC:
                canvas.drawText(sb.toString(), mBaseX, mBaseY, paint);
                break;
        }

        return bitmap;
    }

    private float computeWidth(String text, Paint paint) {
        float drawWidth = mBaseX;
        float[] widths = new float[text.length()];
        paint.getTextWidths(text, widths);
        for (int i = 0; i < widths.length; i++) {
            drawWidth += widths[i];
        }
        Log.i("drawbitmaputils2", "computeWidth:drawWidth " + drawWidth);
        return drawWidth;
    }

    private void drawBgColor(float drawWidth, Canvas canvas) {
        Paint bgPaint = new Paint();
        bgPaint.setColor(mTextBgColor);
        if (mTextEffect== Global.TEXT_EFFECT_APPEAR) {
            canvas.drawRect(0, 0, drawWidth+mWidthList.get(mIndex), mHeightList.get(mIndex), bgPaint);
        }else {
            canvas.drawRect(mBaseX+mWidthList.get(mIndex), 0, drawWidth+mWidthList.get(mIndex), mHeightList.get(mIndex), bgPaint);
        }

    }

}
