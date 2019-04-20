package scorpio.utils;

import java.util.UUID;

/**
 * @author zhangx
 */

public class UUIDUtils {

    public static String[] chars = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0",
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y",
            "Z"                 };

    /**
     * 生成指定长度的uuid
     */
    public static String getUUID(int length) {
        int groupLength = 32 / length;
        StringBuilder sb = new StringBuilder();
        String id = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < length; i++) {
            String str = id.substring(i * groupLength, i * groupLength + groupLength);
            int x = Integer.parseInt(str, 16);
            sb.append(chars[x % 0x3E]);
        }
        return sb.toString();
    }
    /**
     * 16位UUID
     */
    public static String getUUID16() {
        return getUUID(16);
    }

    public static String get() {
        String s = UUID.randomUUID().toString();
        //去掉“-”符号
        return s.substring(0,8)+s.substring(9,13)+s.substring(14,18)+s.substring(19,23)+s.substring(24);
    }
}
