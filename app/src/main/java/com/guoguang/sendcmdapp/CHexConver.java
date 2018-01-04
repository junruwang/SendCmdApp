package com.guoguang.sendcmdapp;

/**
 * 16进制值与String/Byte之间的转换
 *
 * @author tongy
 * @email tongy@guoguang.com.cn
 * @data 2011-10-16
 */
public class CHexConver {
    /**
     * 16进制字符串转十进制数
     *
     * @param str
     * @return
     */
    public static int hexStr2Int(String str) {
        return Integer.parseInt(str, 16);
    }

    /**
     * 字符串转换成十六进制字符串
     *
     * @param String str 待转换的ASCII字符串
     * @return String 每个Byte之间无空格分隔，如: [61 6C 6B]
     */
    public static String str2HexStr(String str) {

        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;

        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            //sb.append('');
        }
        return sb.toString().trim();
    }

    /**
     * 十六进制转换字符串
     *
     * @param String str Byte字符串(Byte之间无分隔符 如:[616C6B])
     * @return String 对应的字符串
     */
    public static String hexStr2Str(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;

        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }

    /**
     * bytes转换成十六进制字符串
     *
     * @param byte[] b byte数组
     * @return String 每个Byte值之间无空格分隔
     */
    public static String byte2HexStr(byte[] b) {
        String stmp = "";
        StringBuilder sb = new StringBuilder("");
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            sb.append((stmp.length() == 1) ? "0" + stmp : stmp);
            sb.append("");
        }
        return sb.toString().toUpperCase().trim();
    }

    /**
     * bytes字符串转换为Byte值
     *
     * @param String src Byte字符串，每个Byte之间没有分隔符
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * String的字符串转换成unicode的String
     *
     * @param String strText 全角字符串
     * @return String 每个unicode之间无分隔符
     * @throws Exception
     */
    public static String strToUnicode(String strText)
            throws Exception {
        char c;
        StringBuilder str = new StringBuilder();
        int intAsc;
        String strHex;
        for (int i = 0; i < strText.length(); i++) {
            c = strText.charAt(i);
            intAsc = (int) c;
            strHex = Integer.toHexString(intAsc);
            if (intAsc > 128)
                str.append("\\u" + strHex);
            else // 低位在前面补00
                str.append("\\u00" + strHex);
        }
        return str.toString();
    }

    /**
     * unicode的String转换成String的字符串
     *
     * @param String hex 16进制值字符串 （一个unicode为2byte）
     * @return String 全角字符串
     */
    public static String unicodeToString(String hex) {
        int t = hex.length() / 6;
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < t; i++) {
            String s = hex.substring(i * 6, (i + 1) * 6);
            // 高位需要补上00再转
            String s1 = s.substring(2, 4) + "00";
            // 低位直接转
            String s2 = s.substring(4);
            // 将16进制的string转为int
            int n = Integer.valueOf(s1, 16) + Integer.valueOf(s2, 16);
            // 将int转换为字符
            char[] chars = Character.toChars(n);
            str.append(new String(chars));
        }
        return str.toString();
    }

    /**
     * @param c
     * @return
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * 为16进制字符串，单字节拆解两个字节，高4，低4字节；并加上0x30，形成新的16进制字符串
     *
     * @param hexstr
     * @return
     */
    public static String addHex30(String hexstr) {
        String hexstr30 = "";
        if (hexstr != null && hexstr.length() > 0) {
            char[] array = hexstr.toCharArray();
            for (int i = 0; i < array.length; i++) {
                hexstr30 += "3" + array[i];
            }
            return hexstr30;
        } else return hexstr;
    }

    /**
     * 将16进制字符串减掉0x30并将两字节合并为1个字节
     *
     * @param hexstr30
     * @return
     */
    public static String removeHex30(String hexstr30) {
        String hexstr = "";
        if (hexstr30 != null && hexstr30.length() > 0) {
            char[] array = hexstr30.toCharArray();
            for (int i = 0; i < array.length / 2; i++) {
                hexstr += array[i * 2 + 1];
            }
            return hexstr;
        } else return hexstr30;
    }

    /**
     * 将16进制字符串从头到位进行异或，并将结果转换成16进制字符串返回；
     *
     * @param hexStr
     * @return 16进制字符串
     */
    public static String hexStrXOR(String hexStr) {
        byte[] b = hexStringToBytes(hexStr);
        byte[] r = new byte[1];
        for (int i = 0; i < b.length; i++) {
            if (i == 0) r[0] = b[i];
            else r[0] = (byte) (b[i - 1] ^ b[i]);
        }
        return byte2HexStr(r);
    }

    /**
     * byte[]进行异或,byte返回；
     *
     * @param hexStr
     * @return 16进制字符串
     */
    public static byte[] byteXOR(byte[] b) {
        byte[] r = new byte[1];
        for (int i = 0; i < b.length; i++) {
            if (i == 0) r[0] = b[i];
            else r[0] = (byte) (r[0] ^ b[i]);
        }
        return r;
    }

    /**
     * 判断是否为数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        if (str == null || "".equals(str)) {
            return false;
        } else if (str.matches("\\d*")) {
            return true;
        } else {
            return false;
        }
    }

    public static byte[] int2bytes(int value, int size) {
        byte buf[] = new byte[size];
        for (int i = 0; i < buf.length; i++) {
            int s = buf.length - i - 1;
            buf[i] = (byte) (value >> (8 * s));
        }
        return buf;
    }


    //---------------------qt pad-----------------------------

    /**
     * byte[] 转Int byte中是asc字符串
     *
     * @param blen
     * @return
     */
    public static int byte2int(byte[] blen) {
        String slen = null;
        if (blen != null) slen = new String(blen);//CHexConver.byte2HexStr(blen);
        if (slen == null || slen.length() != 4) return -1;
        for (int i = 0; i < slen.length(); i++) {
            if (!"0".equals(slen.substring(i, i + 1))) {
                slen = slen.substring(i);
                break;
            }
        }
        int len = 0;
        try {
            len = Integer.parseInt(slen);
        } catch (Exception e) {

        }
        return len;
    }

    /**
     * 补位
     *
     * @param 数据data 位数dlen 补位方向 direct -1 左补 1 右补
     * @return
     */
    public static String fill(String data, int size, int direct) {
        if (data == null || data.equals("")) {
            data = "";
            for (int i = 0; i < size; i++) {
                data += "0";
            }
        } else {
            int len = data.length() % size;
            if (len == 0) return data;
            for (int i = len; i < size; i++) {
                if (direct == -1) {
                    data = "0" + data;
                } else if (direct == 1) {
                    data += "0";
                }

            }
        }
        return data;
    }
}