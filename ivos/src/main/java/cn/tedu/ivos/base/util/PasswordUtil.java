package cn.tedu.ivos.base.util;

import java.security.SecureRandom;

/* 密码工具类，提供生成指定长度随机密码的功能 */
public class PasswordUtil {
    // 定义允许出现在密码中的字符集合 大小写字母 数字 特殊字符
    private static final String ALLOWED_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%&*.-_=+";
    // 创建一个安全的随机数生成器，用于生成随机密码
    private static final SecureRandom random = new SecureRandom();

    /* 创建一个生成指定长度的随机密码的方法 */
    public static String generateRandomPassword(int length) {
        if(length < 5) throw new RuntimeException("密码长度不能小于5");
        //创建一个字符串构建器，用于拼接生成的密码
        StringBuilder password = new StringBuilder();
        for(int i = 0; i < length; i++){
            //在随机可选串的长度之内生成一个随机下标
            int index = random.nextInt(ALLOWED_CHARS.length());
            //根据生成的随机下标获取随机字符
            char randomChar = ALLOWED_CHARS.charAt(index);
            //添加随机字符到密码中
            password.append(randomChar);
        }
        //返回生成的密码
        return password.toString();
    }


}
