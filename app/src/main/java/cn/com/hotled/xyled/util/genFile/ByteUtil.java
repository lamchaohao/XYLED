package cn.com.hotled.xyled.util.genFile;

/**
 * Created by Lam on 2017/2/24.
 */

public class ByteUtil {

    /**
     * @param source          源数值
     * @param byteArrayLength 要转变成的byte数组长度
     * @return
     */
    public static byte[] intToByteArray(int source, int byteArrayLength) {
        byte[] result = new byte[byteArrayLength];
        for (int length = byteArrayLength, index = 0; length > 0; length--, index++) {
            int bitCount = (length - 1) * 8;
            int temp = source;
            temp = temp >> bitCount; //移位
            result[index] = (byte) (temp & 0xff);
        }
        return result;
    }

    /**
     * @param targetStart 要赋值的目标数组的开始序列,从0开始
     * @param source      源数组
     * @param target      目标数组
     */
    public static void setInbyteArray(int targetStart, byte[] source, byte[] target) {
        for (int i = 0; i < source.length; i++) {
            target[targetStart + i] = source[i];
        }
    }
}
