package com.zxj.utils;

/**
 * regular expression patterns
 */
public abstract class RegexPatterns {
    /**
     * phone number regular expression
     */
    public static final String PHONE_REGEX = "^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\\d{8}$";
    /**
     * email regular expression
     */
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
    /**
     * password regular expression, 4-32 characters of letters, numbers, and underline
     */
    public static final String PASSWORD_REGEX = "^\\w{4,32}$";
    /**
     * verification code regular expression, 6 characters of letters and numbers
     */
    public static final String VERIFY_CODE_REGEX = "^[a-zA-Z\\d]{6}$";

}
