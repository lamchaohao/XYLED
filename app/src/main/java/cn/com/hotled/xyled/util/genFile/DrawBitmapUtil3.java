package cn.com.hotled.xyled.util.genFile;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import java.io.File;

import cn.com.hotled.xyled.bean.Program;
import cn.com.hotled.xyled.bean.TextContent;
import cn.com.hotled.xyled.global.Global;

/**
 * Created by Lam on 2017/2/24.
 */

public class DrawBitmapUtil3 {
    private Program mProgram;
    private TextContent mTextContent;
    private int mTextSize;
    private int mTextColor = Color.RED;
    private boolean isBold;
    private boolean isItalic;
    private boolean isUnderLine;
    private File mTypeFile;
    private int mWidth;
    private int mHeight;
    private int mBaseX = 0;
    private int mBaseY = 25;
    private int mTextEffect;
    private int mOrientation;

    public DrawBitmapUtil3(Program program, TextContent textContent,int width, int height,int orientation) {
        mProgram = program;
        mTextContent = textContent;
        mWidth = width;
        mHeight = height;
        mOrientation=orientation;
    }



    public Bitmap drawBitmap() {
        mTextColor = mTextContent.getTextColor();
        mTextSize = mTextContent.getTextSize();
        isUnderLine = mTextContent.getIsUnderline();
        isItalic = mTextContent.getIsIlatic();
        isBold = mTextContent.getIsbold();
        mTextEffect = mTextContent.getTextEffect();
        mTypeFile = mTextContent.getTypeface();
        mBaseX = mProgram.getBaseX();
        mBaseY = mProgram.getBaseY();
        if (mOrientation==3) {//如果屏幕是竖直的
            return drawVerticalText();
        }else {//正常
            return drawText();
        }
    }

    private Bitmap drawText() {
        Paint paint = new Paint();
        Canvas canvas = new Canvas();
        String text = mTextContent.getText();
        if (mTextContent.getIsTextReverse()) {
            //倒叙的文字
            StringBuilder reverseText = new StringBuilder();
            for (int i = mTextContent.getText().length()-1; i >=0 ; i--) {
                String substring = mTextContent.getText().substring(i, i + 1);
                reverseText.append(substring);
            }
            text = reverseText.toString();
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
        Bitmap bitmap =null;

        //画图时，分两种情况
        if (mTextContent.getTextEffect()<=Global.TEXT_EFFECT_STATIC){
            //左移右移和固定
            int tempWidth =0;
            float drawWidth = computeWidth(text, paint);
            if (drawWidth > mWidth) {
                tempWidth = (int) drawWidth;
            }else {
                tempWidth=mWidth;
            }
            //取消两边空白的
            if (mTextEffect== Global.TEXT_EFFECT_APPEAR_MOVE_LEFT
                    ||mTextEffect== Global.TEXT_EFFECT_APPEAR_MOVE_RIGHT) {
                tempWidth+=mWidth;//立即出现并左移右移的加上最后一边过渡
            }else if (mTextEffect== Global.TEXT_EFFECT_MOVE_LEFT
                    ||mTextEffect== Global.TEXT_EFFECT_MOVE_RIGHT){
                tempWidth+=mWidth*2;//左移右移的加上两边过渡
            }else if (mTextEffect==Global.TEXT_EFFECT_STATIC){
                //静止显示的
                tempWidth = mWidth;
            } else {
                tempWidth=mWidth;
            }
            bitmap = Bitmap.createBitmap(tempWidth, mHeight, Bitmap.Config.ARGB_4444);
            if (bitmap != null)
                canvas.setBitmap(bitmap);
            //背景
            drawBgColor(tempWidth,mHeight,canvas);
            //文本
            switch (mTextEffect){
                case Global.TEXT_EFFECT_MOVE_LEFT:
                    canvas.drawText(text, mBaseX+mWidth, mBaseY, paint);
                    break;
                case Global.TEXT_EFFECT_MOVE_RIGHT:
                    canvas.drawText(text, mBaseX+mWidth, mBaseY, paint);
                    break;
                case Global.TEXT_EFFECT_APPEAR_MOVE_LEFT:
                    canvas.drawText(text, mBaseX, mBaseY, paint);
                    break;
                case Global.TEXT_EFFECT_APPEAR_MOVE_RIGHT:
                    canvas.drawText(text, mBaseX+mWidth, mBaseY, paint);
                    break;
                case Global.TEXT_EFFECT_STATIC://这里应该居中显示
                    paint.setTextAlign(Paint.Align.CENTER);
                    canvas.drawText(text, tempWidth/2, mBaseY, paint);
                    break;

            }
        }else {
            //上下移动

            if (mTextContent.getText()!=null){
                TextPaint textPaint =new TextPaint();
                textPaint.set(paint);
                StaticLayout currentLayout = null;
                currentLayout = new StaticLayout(text, textPaint, mWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0f, false);//不同的字体之间的间距是不同的
                int height = currentLayout.getHeight();
                if (height<=mHeight){
                    height=mHeight;
                }
                //加上过渡
                height+=mHeight*2;
                bitmap = Bitmap.createBitmap(mWidth, height, Bitmap.Config.ARGB_4444);
                if (bitmap != null)
                    canvas.setBitmap(bitmap);
                //背景
                drawBgColor(mWidth,height,canvas);
                canvas.translate(0,mHeight);//从(x,y)开始画
                //文本
                currentLayout.draw(canvas);
            }
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
        return drawWidth;
    }

    private void drawBgColor(float drawWidth,float drawHeight,Canvas canvas) {
        Paint bgPaint=new Paint();
        bgPaint.setColor(mTextContent.getTextBackgroudColor());
        if (mTextEffect== Global.TEXT_EFFECT_APPEAR_MOVE_LEFT||mTextEffect== Global.TEXT_EFFECT_APPEAR_MOVE_RIGHT) {
            canvas.drawRect(0, 0, drawWidth+mWidth, drawHeight, bgPaint);
        }else if (mTextEffect==Global.TEXT_EFFECT_MOVE_UP||mTextEffect==Global.TEXT_EFFECT_MOVE_DOWN){
            canvas.drawRect(0, 0, drawWidth, drawHeight, bgPaint);
        }else if (mTextEffect==Global.TEXT_EFFECT_MOVE_LEFT||mTextEffect==Global.TEXT_EFFECT_MOVE_RIGHT){
            canvas.drawRect(mBaseX+mWidth, 0, mBaseX+mWidth+drawWidth, drawHeight, bgPaint);
        }else if (mTextEffect==Global.TEXT_EFFECT_STATIC){
            canvas.drawRect(0, 0, drawWidth, drawHeight, bgPaint);
        }
    }

    /**
     * 此方法是用于竖直的屏幕绘画文字
     * @return bitmap 竖直写的文字
     */
    private Bitmap drawVerticalText(){

        Paint paint = new Paint();

        String text = mTextContent.getText();
        if (text==null)
            text="null";
        if (mTextContent.getIsTextReverse()) {
            //倒叙的文字
            StringBuilder reverseText = new StringBuilder();
            for (int i = text.length()-1; i >=0 ; i--) {
                String substring = text.substring(i, i + 1);
                reverseText.append(substring);
            }
            text = reverseText.toString();
        }

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
        Bitmap bitmap =null;

        TextPaint textPaint =new TextPaint();
        textPaint.set(paint);
        StaticLayout currentLayout = null;
        currentLayout = new StaticLayout(text, textPaint, mHeight, Layout.Alignment.ALIGN_CENTER, 1.0f, 0f, false);
        int temWidth =currentLayout.getHeight();//得到竖直方向画的所需高度

        switch (mTextEffect){
            case Global.TEXT_EFFECT_MOVE_LEFT:
                temWidth+=mWidth*2;//左移右移的加上两边过渡
                break;
            case Global.TEXT_EFFECT_MOVE_RIGHT:
                temWidth+=mWidth*2;//左移右移的加上两边过渡
                break;
            case Global.TEXT_EFFECT_APPEAR_MOVE_LEFT:
                temWidth+=mWidth;//立即出现并左移右移的加上最后一边过渡
                break;
            case Global.TEXT_EFFECT_APPEAR_MOVE_RIGHT:
                temWidth+=mWidth;//立即出现并左移右移的加上最后一边过渡
                break;
            case Global.TEXT_EFFECT_STATIC://这里应该居中显示
                temWidth = mWidth;  //静止显示的就屏幕大小
                break;
            case Global.TEXT_EFFECT_MOVE_UP:
                temWidth+=mWidth*2;//左移右移的加上两边过渡
                break;
            case Global.TEXT_EFFECT_MOVE_DOWN:
                temWidth+=mWidth*2;//左移右移的加上两边过渡
                break;

        }

        bitmap = Bitmap.createBitmap(mHeight, temWidth, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        //背景
        drawBgColor(mHeight, temWidth,canvas);

        //文本
        switch (mTextEffect){
            case Global.TEXT_EFFECT_MOVE_LEFT:
                canvas.translate(0,mBaseX+mWidth);//从(x,y)开始画
                currentLayout.draw(canvas);//文本
                break;
            case Global.TEXT_EFFECT_MOVE_RIGHT:
                canvas.translate(0,mBaseX+mWidth);//从(x,y)开始画
                currentLayout.draw(canvas);//文本
                break;
            case Global.TEXT_EFFECT_APPEAR_MOVE_LEFT:
                canvas.translate(0,mBaseX);//从(x,y)开始画
                currentLayout.draw(canvas);//文本
                break;
            case Global.TEXT_EFFECT_APPEAR_MOVE_RIGHT:
                canvas.translate(0,mBaseX);//从(x,y)开始画
                currentLayout.draw(canvas);//文本
                break;
            case Global.TEXT_EFFECT_STATIC://这里应该居中显示
                canvas.translate(0,0);//从(x,y)开始画
                currentLayout.draw(canvas);//文本
                break;
            case Global.TEXT_EFFECT_MOVE_UP:
                canvas.translate(0,0);//从(x,y)开始画
                currentLayout.draw(canvas);//文本
                break;
            case Global.TEXT_EFFECT_MOVE_DOWN:
                canvas.translate(0,0);//从(x,y)开始画
                currentLayout.draw(canvas);//文本
                break;

        }

        return resizeBitmap(bitmap, -90);
    }

    /**
     *
     * @param bm 要旋转的bitmap
     * @param orientationDegree 旋转角度
     * @return 旋转后的bitmap
     */
    private Bitmap resizeBitmap(Bitmap bm, final int orientationDegree){
        Matrix m = new Matrix();
        m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);

        try {
            Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
            return bm1;
        } catch (OutOfMemoryError ex) {
        }
        return bm;
    }

    private Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree) {

        Matrix matrix = new Matrix();
        matrix.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        float targetX, targetY;
        if (orientationDegree == 90) {
            targetX = bm.getHeight();
            targetY = 0;
        } else {
            targetX = bm.getHeight();
            targetY = bm.getWidth();
        }

        final float[] values = new float[9];
        matrix.getValues(values);

        float x1 = values[Matrix.MTRANS_X];
        float y1 = values[Matrix.MTRANS_Y];

        matrix.postTranslate(targetX - x1, targetY - y1);

        Bitmap bm1 = Bitmap.createBitmap(bm.getHeight(), bm.getWidth(), Bitmap.Config.ARGB_4444);
        Paint paint = new Paint();
        Canvas canvas = new Canvas(bm1);
        canvas.drawBitmap(bm, matrix, paint);

        return bm1;
    }

    public void setTextContent(TextContent textContent) {
        mTextContent = textContent;
    }
}
