package com.airing.spring.cloud.base.filter;

import com.airing.spring.cloud.base.Constant;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * HTTP请求包装类
 *
 * @author GEYI
 * @date 2021年03月31日 10:05
 */
public class RequestWrapper extends HttpServletRequestWrapper {

    /**
     * 存储请求体数据的容器
     */
    private byte[] body;

    public RequestWrapper(HttpServletRequest request) throws IOException {
        super(request);

        String bodyStr = inputStream2String(request.getInputStream());
        this.body = bodyStr.getBytes(Constant.DEFAULT_CHARSET);
    }

    /**
     * 获取请求Body
     *
     * @return java.lang.String
     * @author GEYI
     * @date 2021年03月31日 10:06
     */
    public String getBodyString() {
        try {
            return new String(body, Constant.DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setBody(String body) {
        try {
            this.body = body.getBytes(Constant.DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将inputStream里的数据读取出来并转换成字符串
     *
     * @param inputStream
     * @return java.lang.String
     * @author GEYI
     * @date 2021年03月31日 10:06
     */
    private String inputStream2String(InputStream inputStream) {
        if (inputStream == null) {
            return null;
        }

        ByteArrayOutputStream output = null;
        String requestBody = null;

        try {
            output = new ByteArrayOutputStream();
            byte[] buff = new byte[512];
            int index;
            while ((index = inputStream.read(buff)) != -1) {
                output.write(buff, 0, index);
            }
            requestBody = new String(output.toByteArray(), Constant.DEFAULT_CHARSET);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return requestBody;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {

        final ByteArrayInputStream inputStream = new ByteArrayInputStream(body);

        return new ServletInputStream() {
            @Override
            public int read() throws IOException {
                return inputStream.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }
        };
    }
}
