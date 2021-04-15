package com.airing.spring.cloud.base.utils.http;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HttpRequestUtils {

    private static final Logger log = LoggerFactory.getLogger(HttpRequestUtils.class);

    private static final HttpClient HTTP_CLIENT = new HttpClient();
    private static final String DEFAULT_CHARSET = "UTF-8";

    public static String get(String url, int requestTimeout, int socketTimeout) {
        CloseableHttpResponse response = null;
        try {
            HttpGet httpRequest = new HttpGet(url);
            CloseableHttpClient client = HTTP_CLIENT.getHttpClient(requestTimeout, socketTimeout);
            response = client.execute(httpRequest);
            int stateCode = response.getStatusLine().getStatusCode();
            byte[] resultByte = EntityUtils.toByteArray(response.getEntity());
            String resultStr = new String(resultByte, DEFAULT_CHARSET);
            log.debug("url:{}, stateCode:{}, response:{}", url, stateCode, resultStr);
            return resultStr;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException();
        } finally {
            if (response != null) {
                try {
                    response.getEntity().getContent().close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    public static String get(String url) {
        return get(url, 5000, 5000);
    }

    public static String post(String url, String jsonParams) {
        CloseableHttpResponse response = null;
        try {
            HttpPost httpRequest = new HttpPost(url);
            CloseableHttpClient client = HTTP_CLIENT.getHttpClient(10000, 10000);
            // 设置请求头参数
            httpRequest.addHeader("Content-type", "application/json; charset=utf-8");
            httpRequest.setHeader("Accept", "application/json");
            if (jsonParams != null) {
                httpRequest.setEntity(new StringEntity(jsonParams, Charset.forName(DEFAULT_CHARSET)));
            }
            response = client.execute(httpRequest);
            int stateCode = response.getStatusLine().getStatusCode();
            byte[] resultByte = EntityUtils.toByteArray(response.getEntity());
            String resultStr = new String(resultByte, DEFAULT_CHARSET);
            log.debug("url:{}, jsonParams:{}, stateCode:{}, response:{}", url, jsonParams, stateCode, resultStr);
            return resultStr;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException();
        } finally {
            if (response != null) {
                try {
                    response.getEntity().getContent().close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    public static String postForm(String url, Map<String, String> headerParams, Map<String, String> params) {
        CloseableHttpResponse response = null;
        try {
            HttpPost httpRequest = new HttpPost(url);
            CloseableHttpClient client = HTTP_CLIENT.getHttpClient(10000, 10000);
            if (headerParams != null && headerParams.size() > 0) {
                for (Map.Entry<String, String> key : headerParams.entrySet()) {
                    String headerKey = key.getKey();
                    httpRequest.setHeader(headerKey, headerParams.get(headerKey));
                }
            }
            List<BasicNameValuePair> pairList = new ArrayList<>();
            Set<Map.Entry<String, String>> paramsSet = params.entrySet();
            for (Map.Entry<String, String> param : paramsSet) {
                pairList.add(new BasicNameValuePair(param.getKey(), param.getValue()));
            }
            httpRequest.setEntity(new UrlEncodedFormEntity(pairList, DEFAULT_CHARSET));
            response = client.execute(httpRequest);
            int stateCode = response.getStatusLine().getStatusCode();
            byte[] resultByte = EntityUtils.toByteArray(response.getEntity());
            String resultStr = new String(resultByte, DEFAULT_CHARSET);
            log.debug("url:{}, headerParams:{}, params:{}, stateCode:{}, response:{}", url, headerParams, params,
                    stateCode,
                    resultStr);
            return resultStr;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException();
        } finally {
            if (response != null) {
                try {
                    response.getEntity().getContent().close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

}
