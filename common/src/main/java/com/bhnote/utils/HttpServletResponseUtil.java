package com.bhnote.utils;

import com.bhnote.config.JacksonConfig;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @author bingo
 * @date 2022/1/6
 */
public class HttpServletResponseUtil {

    /**
     * 响应Json
     */
    public static void responseJson(HttpServletResponse response, Object obj) {
        response.setStatus(200);
        response.setContentType("application/json; charset=utf-8");
        try (PrintWriter printWriter = response.getWriter()) {
            printWriter.print(JacksonConfig.getObjectMapper().writeValueAsString(obj));
            response.flushBuffer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
