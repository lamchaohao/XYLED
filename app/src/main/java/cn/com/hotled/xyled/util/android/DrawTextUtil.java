package cn.com.hotled.xyled.util.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.widget.Toast;

import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.bean.Program;
import cn.com.hotled.xyled.bean.TextContent;
import cn.com.hotled.xyled.global.Global;
import cn.com.hotled.xyled.view.photoview.PhotoView;

/**
 * 用于在activity编辑页面绘图
 * Created by Lam on 2017/3/22.
 */

public class DrawTextUtil {

    private TextContent mTextContent;
    private Paint paint;
    private Canvas canvas;
    private Program mProgram;
    private int mBaseX;
    private int mBaseY;
    private Bitmap mFlowBitmap;
    private Bitmap targetBitmap;
    private PhotoView mPhotoView;
    private int mScreenWidth;
    private int mScreenHeight;
    private boolean isWarnShowed;
    private Context mContext;
    private int mOrientation;

    public DrawTextUtil(int orientation) {
        mOrientation=orientation;
    }

    public DrawTextUtil(TextContent textContent, TextPaint paint, Canvas canvas, Program program, int baseX, int baseY, Bitmap flowBitmap, Bitmap targetBitmap, PhotoView photoView, int screenWidth, int screenHeight, boolean isWarnShowed, Context context) {
        mTextContent = textContent;
        this.paint = paint;
        this.canvas = canvas;
        mProgram = program;
        mBaseX = baseX;
        mBaseY = baseY;
        mFlowBitmap = flowBitmap;
        this.targetBitmap = targetBitmap;
        mPhotoView = photoView;
        mScreenWidth = screenWidth;
        mScreenHeight = screenHeight;
        this.isWarnShowed = isWarnShowed;
        mContext = context;
    }

    public void drawText() {
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
        //先设置好画笔，才进行计算
        paint.setColor(mTextContent.getTextColor());
        paint.setTextSize(mTextContent.getTextSize());

        if (mTextContent.getTypeface()!=null) {
            Typeface typeface = Typeface.createFromFile(mTextContent.getTypeface());
            paint.setTypeface(typeface);
            if (mTextContent.getIsbold()) {//粗体
                paint.setTypeface(Typeface.create(typeface,Typeface.BOLD));
            }
            if (mTextContent.getIsIlatic()) {//斜体
                paint.setTypeface(Typeface.create(typeface,Typeface.ITALIC));
            }
            if (mTextContent.getIsbold()&& mTextContent.getIsIlatic()){//粗斜体
                paint.setTypeface(Typeface.create(typeface,Typeface.BOLD_ITALIC));
            }
        }
        else {
            paint.setTypeface(Typeface.DEFAULT);

            if (mTextContent.getIsbold()) {//粗体
                paint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
            }
            if (mTextContent.getIsIlatic()) {//斜体
                paint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.ITALIC));
            }
            if (mTextContent.getIsbold()&& mTextContent.getIsIlatic()){//粗斜体
                paint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD_ITALIC));
            }


        }
        if (mTextContent.getIsUnderline()) {//下划线
            paint.setUnderlineText(true);
        }else {
            paint.setUnderlineText(false);
        }
        paint.setTextAlign(Paint.Align.LEFT);

        //设置好画笔，开始计算

        if (mOrientation==3){
           drawVerticalText(text);
        }else {
            drawHorizontal(text);
        }

        if (mFlowBitmap!=null&&mProgram.getUseFlowBound()){
            //流水边框
            drawFlowBound();
        }


    }

    private void drawHorizontal(String text) {

        float drawWidth = computeWidth(text);
        //需要加上流水边框的宽度,左右都需要加上
        int tempBaseX=mBaseX;
        int tempBaseY=mBaseY;
        int translateCount=0;
        if (mFlowBitmap!=null&&mProgram.getUseFlowBound()){
            drawWidth+=mFlowBitmap.getHeight()*2;
            //加上流水边框后，文字需要偏移流水边框的一个宽度
            tempBaseX+=mFlowBitmap.getHeight();
            tempBaseY+=mFlowBitmap.getHeight();
            translateCount=mFlowBitmap.getHeight();
        }

        //画图时，分两种情况
        if (mTextContent.getTextEffect()<= Global.TEXT_EFFECT_STATIC){
            //固定
            if (mTextContent.getTextEffect()==Global.TEXT_EFFECT_STATIC){
                targetBitmap = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.ARGB_4444);
                if (targetBitmap != null)
                    canvas.setBitmap(targetBitmap);
                mPhotoView.setImageBitmap(targetBitmap);
                //背景
                drawBgColor(mScreenWidth,mScreenHeight);
                //文本
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText(text, mScreenWidth/2, mBaseY, paint);
            }else {//左移右移
                if (drawWidth>mScreenWidth) {
                    int width = (int) drawWidth;
                    targetBitmap = Bitmap.createBitmap(width<mScreenWidth?mScreenWidth:width, mScreenHeight, Bitmap.Config.ARGB_4444);
                    if (targetBitmap != null)
                        canvas.setBitmap(targetBitmap);
                    mPhotoView.setImageBitmap(targetBitmap);
                }
                //背景
                drawBgColor(drawWidth<mScreenWidth?mScreenWidth:drawWidth,mScreenHeight);
                //文本
                if (mTextContent.getText()!=null&& !TextUtils.isEmpty(mTextContent.getText())) {
                    canvas.drawText(text,tempBaseX,tempBaseY,paint);
                }
            }

        }else {
            //上下移动
            //文本
            if (mTextContent.getText()!=null){
                TextPaint textPaint =new TextPaint();
                textPaint.set(paint);
                //Layout.Alignment.ALIGN_CENTER 表明居中
                StaticLayout currentLayout = null;
                currentLayout = new StaticLayout(text, textPaint, mScreenWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0f, false);

                int height = currentLayout.getHeight();
                if (height>=3072) {
                    if (!isWarnShowed) {
                        Toast.makeText(mContext, R.string.tos_warn_long, Toast.LENGTH_LONG).show();
                        isWarnShowed=true;
                    }
                    height=3072;
                }
                height+=translateCount*2;//加上流水边的宽度
                targetBitmap = Bitmap.createBitmap(mScreenWidth, height<mScreenHeight?mScreenHeight:height, Bitmap.Config.ARGB_4444);
                if (targetBitmap != null)
                    canvas.setBitmap(targetBitmap);
                mPhotoView.setImageBitmap(targetBitmap);
                //背景
                drawBgColor(mScreenWidth,height<mScreenHeight?mScreenHeight:height);
                currentLayout.draw(canvas);
            }
        }
    }

    /**
     * 此方法是用于竖直的屏幕绘画文字
     * @return bitmap 竖直写的文字
     */
    private void drawVerticalText(String text){
        if (text==null) {
            text="none";
        }
        TextPaint textPaint =new TextPaint();
        textPaint.set(paint);
        StaticLayout currentLayout = null;
        currentLayout = new StaticLayout(text, textPaint, mScreenHeight, Layout.Alignment.ALIGN_CENTER, 1.0f, 0f, false);
        int temHeight =currentLayout.getHeight();//得到竖直方向画的所需高度
        if (temHeight<=0)
            temHeight=mScreenWidth;
        if (mTextContent.getTextEffect()==Global.TEXT_EFFECT_STATIC){
            targetBitmap = Bitmap.createBitmap(mScreenHeight, mScreenWidth, Bitmap.Config.ARGB_4444);
        }else {
            targetBitmap = Bitmap.createBitmap(mScreenHeight, temHeight<mScreenHeight?mScreenHeight:temHeight, Bitmap.Config.ARGB_4444);
        }
        if (targetBitmap != null)
            canvas.setBitmap(targetBitmap);
        //背景
        drawBgColor(mScreenWidth,temHeight<mScreenHeight?mScreenHeight:temHeight);
        //文本
        switch (mTextContent.getTextEffect()){
            case Global.TEXT_EFFECT_MOVE_LEFT:
                canvas.translate(0,0);//从(x,y)开始画
                currentLayout.draw(canvas);//文本
                break;
            case Global.TEXT_EFFECT_MOVE_RIGHT:
                canvas.translate(0,0);//从(x,y)开始画
                currentLayout.draw(canvas);//文本
                break;
            case Global.TEXT_EFFECT_APPEAR_MOVE_LEFT:
                canvas.translate(0,0);//从(x,y)开始画
                currentLayout.draw(canvas);//文本
                break;
            case Global.TEXT_EFFECT_APPEAR_MOVE_RIGHT:
                canvas.translate(0,0);//从(x,y)开始画
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
        Bitmap bitmap = resizeBitmap(targetBitmap, -90);
        canvas.setBitmap(bitmap);
        mPhotoView.setImageBitmap(bitmap);
    }

    private float computeWidth(String text) {
        float drawWidth=mBaseX;
        if(text!=null){
            float[] widths=new float[text.length()];
            paint.getTextWidths(text,widths);
            for (int i = 0; i < widths.length; i++) {
                drawWidth+=widths[i];
            }
        }

        //为了避免OOM，bitmap的最大宽度设置为2048,bitmap的最大尺寸为4096*4096
        if (drawWidth>=2048){
            if (!isWarnShowed) {
                Toast.makeText(mContext, R.string.tos_warn_long, Toast.LENGTH_LONG).show();
                isWarnShowed=true;
            }
            return 2048;
        }
        return drawWidth;
    }


    private void drawBgColor(float drawWidth,int drawHeight) {
        Paint bgPaint=new Paint();
        bgPaint.setColor(mTextContent.getTextBackgroudColor());
        canvas.drawRect(mBaseX,0,drawWidth,drawHeight,bgPaint);
    }
    private void drawFlowBound() {

        int[] flowbound = new int[mFlowBitmap.getWidth() * mFlowBitmap.getHeight()];
        for (int x1 = 0, i = 0; x1 < mFlowBitmap.getWidth(); x1++) {
            for (int y1 = 0; y1 < mFlowBitmap.getHeight(); y1++) {
                int pixel = mFlowBitmap.getPixel(x1, y1);
                flowbound[i] = pixel;
                i++;
            }
        }

        int screenHeight = targetBitmap.getHeight();
        int screenWidth = targetBitmap.getWidth();
        Paint flowPaint=new Paint();

        //up part1
        for (int k = 0; k < screenWidth; k++) {
            int colorIndex = (k) * mFlowBitmap.getHeight();//this is right
            while (colorIndex >= flowbound.length) {
                colorIndex -= flowbound.length;
            }
            for (int q = 0; q < mFlowBitmap.getHeight(); q++) {
                flowPaint.setColor(flowbound[colorIndex + q]);
                int i = (screenWidth - k - 1) * screenHeight + q;
                float x=i/screenHeight;
                float y=i%screenHeight;
                canvas.drawPoint(x,y,flowPaint);
            }
        }
        //right part
        for (int j = 0; j < screenHeight; j++) {
            int colorIndex = j * mFlowBitmap.getHeight();
            while (colorIndex>=flowbound.length){
                colorIndex-=flowbound.length;
            }
            for (int q = 0; q < mFlowBitmap.getHeight(); q++) {
                flowPaint.setColor(flowbound[colorIndex + q]);
                int i = targetBitmap.getHeight()*targetBitmap.getWidth() - screenHeight * (q + 1) + j ;
                float x=i/screenHeight;
                float y=i%screenHeight;
                canvas.drawPoint(x,y,flowPaint);
            }
        }

        //down part
        for (int k = screenWidth; k >= 0; k--) {
            int colorIndex = k * mFlowBitmap.getHeight();
            while (colorIndex>=flowbound.length){
                colorIndex-=flowbound.length;
            }

            for (int q = mFlowBitmap.getHeight(); q > 0; q--){
                flowPaint.setColor(flowbound[colorIndex + q - 1]);
                int i = (k - 1) * screenHeight - q ;
                float x=i/screenHeight;
                float y=i%screenHeight;
                canvas.drawPoint(x,y,flowPaint);
            }
        }

        //left part
        for (int j = 0; j < screenHeight; j++) {
            int colorIndex = (j) * mFlowBitmap.getHeight();
            while (colorIndex>=flowbound.length){
                colorIndex-=flowbound.length;
            }
            for (int q = 0; q < mFlowBitmap.getHeight(); q++) {
                flowPaint.setColor(flowbound[colorIndex + q]);
                int i = screenHeight * q + j ;
                float x=i/screenHeight;
                float y=i%screenHeight;
                canvas.drawPoint(x,y,flowPaint);
            }
        }
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
            targetBitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
            return targetBitmap;
        } catch (OutOfMemoryError ex) {
        }
        return bm;
    }


    public DrawTextUtil setTextContent(TextContent textContent) {
        mTextContent = textContent;
        return this;
    }

    public DrawTextUtil setPaint(Paint paint) {
        this.paint = paint;
        return this;
    }

    public DrawTextUtil setCanvas(Canvas canvas) {
        this.canvas = canvas;
        return this;
    }

    public DrawTextUtil setProgram(Program program) {
        mProgram = program;
        return this;
    }

    public DrawTextUtil setBaseX(int baseX) {
        mBaseX = baseX;
        return this;
    }

    public DrawTextUtil setBaseY(int baseY) {
        mBaseY = baseY;
        return this;
    }

    public DrawTextUtil setFlowBitmap(Bitmap flowBitmap) {
        mFlowBitmap = flowBitmap;
        return this;
    }

    public DrawTextUtil setTargetBitmap(Bitmap targetBitmap) {
        this.targetBitmap = targetBitmap;
        return this;
    }

    public DrawTextUtil setPhotoView(PhotoView photoView) {
        mPhotoView = photoView;
        return this;
    }

    public DrawTextUtil setScreenWidth(int screenWidth) {
        mScreenWidth = screenWidth;
        return this;
    }

    public DrawTextUtil setScreenHeight(int screenHeight) {
        mScreenHeight = screenHeight;
        return this;
    }

    public DrawTextUtil setWarnShowed(boolean warnShowed) {
        isWarnShowed = warnShowed;
        return this;
    }

    public DrawTextUtil setContext(Context context) {
        mContext = context;
        return this;
    }

    public boolean isWarnShowed() {
        return isWarnShowed;
    }

    public Bitmap getTargetBitmap() {

        return targetBitmap;
    }
}
