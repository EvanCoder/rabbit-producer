package com.rabbit.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * @author Evan
 * @create 2021/3/4 14:58
 */
@Slf4j
public class HttpReq {

    private final String baseUrl;
    private String req;
    private StringBuilder params = new StringBuilder();


    public HttpReq(String baseUrl){
        this.baseUrl = baseUrl;
    }

    public static HttpReq get(String baseUrl){
        return new HttpReq(baseUrl);
    }

    public HttpReq req(String req){
        this.req = req;
        return this;
    }

    public HttpReq param(String name, String value){
        if (params.length() > 0){
            params.append("&");
        }
        try {
            params.append(name).append("=").append(URLEncoder.encode(value, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public String exec(){
        HttpURLConnection http = null;
        try {
            http = (HttpURLConnection) new URL(baseUrl + (req == null ? "" : req)
                    + (params.length() > 0 ? ("?" + params) : "")).openConnection();
            http.setRequestProperty("Accept-Charset", "UTF-8");
            HttpURLConnection.setFollowRedirects(false);
            http.setConnectTimeout(5 * 1000);
            http.setReadTimeout(5 * 1000);
            http.connect();

            int status = http.getResponseCode();
            String charset = getCharset(http.getHeaderField("Content-Type"));

            if (status == 200){
                return readResponseBody(http, charset);
            } else {
                log.warn("Non 200 response :" + readErrorResponseBody(http, charset, status));
                return null;
            }
        } catch (Exception e) {
            log.error("Exec error {}", e.getMessage());
            return null;
        } finally {
            if (http != null){
                http.disconnect();
            }
        }
    }

    private static String getCharset(String contentType){
        if (contentType == null){
            return "UTF-8";
        }

        String charset = null;
        for (String param : contentType.replace(" ", "").split(";")){
            if (param.startsWith("charset=")){
                charset = param.split("=", 2)[1];
                break;
            }
        }
        return charset == null ? "UTF-8" : charset;
    }

    private static String readResponseBody(HttpURLConnection http, String charset) throws IOException {
        InputStream inputStream = http.getInputStream();
        return toString(inputStream, charset);
    }

    private static String readErrorResponseBody(HttpURLConnection http, String charset,
                                                int status) throws IOException {
        InputStream errorStream = http.getInputStream();
        if (errorStream != null){
            String error = toString(errorStream, charset);
            return ("STATUS CODE =" + status + "\n\n" + error);
        } else {
            return ("STATUS CODE =" + status);
        }
    }

    private static String toString(InputStream inputStream, String charset) throws IOException {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];

        int length;
        while ((length = inputStream.read(buffer)) != -1){
            bao.write(buffer, 0, length);
        }
        return new String(bao.toByteArray(), charset);
    }

}
