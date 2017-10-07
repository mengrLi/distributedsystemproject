package domain;

import com.sun.istack.internal.Nullable;

/**
 * super sample string encoder.. and decoder
 */
public class RandomString{

    @Nullable
    private static String decode(String code) {
        char[] chars = code.toCharArray();
        for(int i = 0; i < chars.length; ++i) chars[i] -= i;
        return new String(chars);
    }

}