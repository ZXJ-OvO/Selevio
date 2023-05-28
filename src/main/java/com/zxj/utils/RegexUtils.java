package com.zxj.utils;

import cn.hutool.core.util.StrUtil;


/**
 * Regular expression tool class: this class verification is reversed
 */
public class RegexUtils {
    /**
     * Verify whether the string is a invalid phone number
     *
     * @param phone phone number to be verified
     * @return true: valid, false: invalid
     */
    public static boolean isPhoneInvalid(String phone) {
        return mismatch(phone, RegexPatterns.PHONE_REGEX);
    }
    /**
     * Verify whether the string is a invalid email
     *
     * @param email email to be verified
     * @return true: valid, false: invalid
     */
    public static boolean isEmailInvalid(String email) {
        return mismatch(email, RegexPatterns.EMAIL_REGEX);
    }
    /**
     * Verify whether the string is a invalid code
     *
     * @param code code to be verified
     * @return true: valid, false: invalid
     */
    public static boolean isCodeInvalid(String code) {
        return mismatch(code, RegexPatterns.VERIFY_CODE_REGEX);
    }
    /**
     * Verify whether the string is mismatch regular expression
     *
     * @param str   string to be verified
     * @param regex regular expression
     * @return true: the string is empty or null; !str.matches(regex): true : mismatch ; false: match
     */
    private static boolean mismatch(String str, String regex) {
        if (StrUtil.isBlank(str)) {
            return true;
        }
        return !str.matches(regex);
    }
}
