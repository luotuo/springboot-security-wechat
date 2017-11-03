package com.jwcq.config;

import com.sun.istack.NotNull;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Created by luotuo on 17-9-26.
 */
public class MyPasswordEncoder implements PasswordEncoder {
    @NotNull
    private final String encodingAlgorithm;

    public MyPasswordEncoder(final String encodingAlgorithm) {
        this.encodingAlgorithm = encodingAlgorithm;
    }

    public MyPasswordEncoder() {this("2");}

    public String encode(CharSequence rawPassword) {
        String password = rawPassword.toString();
        if (encodingAlgorithm.equals("0")) {
            // MD5
            return md5Alg(password);
        } else if (encodingAlgorithm.equals("1")) {
            // MD5 with salt
            return md5WithSaltAlg(password);
        } else {
            // clear text
            return clearText(password);
        }
    }

    private String clearText(final String password) {
        return password;
    }

    private String md5Alg(final String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(password.getBytes("UTF-8"));
            // digest()最后确定返回md5 hash值，返回值为8位字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            return password;
        }
    }

    private String md5WithSaltAlg(final String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(password.getBytes("UTF-8"));
            // digest()最后确定返回md5 hash值，返回值为8位字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            return password;
        }
    }

    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if(encodedPassword != null && encodedPassword.length() != 0) {
            return encode(rawPassword).equals(encodedPassword);
        } else {
            return false;
        }
    }
}
