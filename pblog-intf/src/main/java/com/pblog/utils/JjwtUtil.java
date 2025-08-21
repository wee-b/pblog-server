package com.pblog.utils;

import com.alibaba.excel.util.StringUtils;
import com.pblog.constant.SignKeyConstant;
import com.pblog.utils.security.Base64;
import com.pblog.utils.security.MD5Signature;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;


import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class JjwtUtil {


    public static int verifyLoginToken(String token) {
        if (StringUtils.isEmpty(token)) {
            return -1;
        }
        try {
            String decodeToken = new String(Base64.decode(token));
            log.info("decode token =:" + decodeToken);
            String[] timeSplit = decodeToken.split("\\^");
            if (timeSplit.length != 3 ||
                    !MD5Signature.verify(timeSplit[1] + timeSplit[0], timeSplit[2], SignKeyConstant.LOGIN_TIME_KEY)) {
                if (timeSplit.length != 3) {
                    log.warn("user coookie value length != 3");
                } else if (!MD5Signature.verify(timeSplit[1] + timeSplit[0],
                        timeSplit[2], SignKeyConstant.LOGIN_TIME_KEY)) {
                    log.warn("user coookie md5 verify error,token: " + token);
                }
                return -1;
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = format.parse(timeSplit[1]);
            //30天      60*60 *24 * 30 * 1000
            if (date == null || ((new Date()).getTime() - date.getTime()) > 2592000000L) {
                return 0;
            }
            return NumberUtils.toInt(timeSplit[0]);
        } catch (Exception e) {
            log.error("user cookie md5 verify error,token: " + token);
            return -1;
        }finally {
        }

    }

    public static String getLoginToken(int userId) throws Exception {
        String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String token = userId + "^" + date + "^" + MD5Signature.sign(date + userId, SignKeyConstant.LOGIN_TIME_KEY);
        String encodeToken = Base64.encode(token.getBytes());
        System.out.println("encode token =:" + encodeToken);
        System.out.println("token =:" + token);
        return encodeToken;
    }
    public static String getLoginToken(int userId,String date) throws Exception {
        String token = userId + "^" + date + "^" + MD5Signature.sign(date + userId, SignKeyConstant.LOGIN_TIME_KEY);
        String encodeToken = Base64.encode(token.getBytes());
        System.out.println("encode token =:" + encodeToken);
        System.out.println("token =:" + token);
        return encodeToken;
    }


    public static void main(String[] args) throws Exception {

        String token = getLoginToken(8263);

        int userId = verifyLoginToken(token);
        System.out.println(userId);


    }

}
