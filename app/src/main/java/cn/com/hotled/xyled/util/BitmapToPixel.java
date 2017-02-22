package cn.com.hotled.xyled.util;

import android.content.Context;
import android.graphics.Bitmap;

import cn.com.hotled.xyled.global.Global;

/**
 * Created by Lam on 2017/1/16.
 */

public class BitmapToPixel {
    private static final int RGB = 0;
    private static final int RBG = 1;
    private static final int GRB = 2;
    private static final int GBR = 3;
    private static final int BRG = 4;
    private static final int BGR = 5;

    public static byte[] convertBitmapToPixel(Bitmap bitmap, Context context) {
        byte[] bitmapPixels = null;
        int xStart = 0;
        int yStart = 0;
        bitmapPixels = new byte[bitmap.getWidth() * bitmap.getHeight()];
        int rgbOrder = context.getSharedPreferences(Global.SP_SCREEN_CONFIG, context.MODE_PRIVATE).getInt(Global.KEY_RGB_ORDER, 0);
        for (int x1 = 0, i = xStart * yStart; x1 < bitmap.getWidth(); x1++) {
            for (int y1 = 0; y1 < bitmap.getHeight(); y1++) {
                int pixel = bitmap.getPixel(x1, y1);
                int blue = 0;
                int green = 0;
                int red = 0;
                String hexStr = Integer.toHexString(pixel);
                if (pixel == 0 && hexStr.length() < 6) {
//                    Log.i("move","no color,x = "+x1+", y= "+y1);
                } else if (hexStr.length() >= 6) {
                    int length = hexStr.length();
                    hexStr = hexStr.substring(length - 6);//保证要有6位数字
                    blue= Integer.parseInt(hexStr.substring(0, 2), 16);//使用十六进制
                    green = Integer.parseInt(hexStr.substring(2, 4), 16);
                    red = Integer.parseInt(hexStr.substring(4, 6), 16);
                }

                bitmapPixels[i] = caculateColor(red,green,blue,rgbOrder);
                i++;
            }
        }
        return bitmapPixels;
    }

    private static byte caculateColor(int red, int green, int blue, int rgbOrder){
        // 因为rgb，分别用一个int来表示，值为0-255
        blue = blue / 85;
        green = green / 85;
        red = red / 85;
        // 最终要转换成一个byte中的低6位，每个颜色用两位来表示，序列是 bb gg rr，每种颜色最多有4种可能，00,01,10,11
        // 最若全蓝 110000 =48,100000=32,010000=16，
        // 最若全蓝 110000 =48,100000=32,010000=16，
        switch (rgbOrder){
            case RGB:
                red = red * 16;
                green = green * 4;
                blue = blue * 1;
                break;

            case RBG:
                red = red * 16;
                blue = blue * 4;
                green = green * 1;
                break;
            case GRB:
                green = green * 16;
                red = red * 4;
                blue = blue * 1;
                break;
            case GBR:
                green = green * 16;
                blue = blue * 4;
                red = red * 1;
                break;
            case BRG:
                blue = blue * 16;
                red = red * 4;
                green = green * 1;
                break;
            case BGR:
                blue = blue * 16;
                green = green * 4;
                red = red * 1;
                break;

        }

        byte color = (byte) (blue + green + red);
        return color;
    }

}
