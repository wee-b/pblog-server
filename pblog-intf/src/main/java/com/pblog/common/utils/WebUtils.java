package com.pblog.common.utils;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class WebUtils {

    public static String renderString(HttpServletResponse response,String msg){
        try{
            response.setStatus(200);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().print(msg);

        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

}
