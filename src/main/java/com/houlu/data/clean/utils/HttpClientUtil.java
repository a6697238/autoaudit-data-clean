package com.houlu.data.clean.utils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Lu Hou
 * @date 2017/10/29
 * @time 上午12:27
 */
public class HttpClientUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientUtil.class);


  //设置超时时间
  private static final int HTTP_SOCKET_TIMEOUT = 100000;
  private static final int HTTP_CONNECT_TIMEOUT = 100000;
  private static final int HTTP_CONNECTION_REQUEST_TIMEOUT = 100000;

  private static final String UPLOAD_FILE = "up_load_file";

  public static final String HTTP_RETURN_ERROR = "http_return_error";


  public static byte[] doGetRequest(String getUrl) throws IOException {
    HttpGet httpGet = new HttpGet(getUrl);
    byte[] bytes = null;
    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
      RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(HTTP_SOCKET_TIMEOUT)
          .setConnectionRequestTimeout(HTTP_CONNECTION_REQUEST_TIMEOUT)
          .setConnectTimeout(HTTP_CONNECT_TIMEOUT).build();
      httpGet.setConfig(requestConfig);
      try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
        ByteArrayOutputStream outstream = new ByteArrayOutputStream(4 * 1024);
        byte[] buffer = new byte[4096];
        int len;
        while ((len = response.getEntity().getContent().read(buffer)) > 0) {
          outstream.write(buffer, 0, len);
        }
        outstream.close();
        bytes = outstream.toByteArray();
      }
    }
    return bytes;
  }

  public static void main(String[] args) throws IOException {
    byte[] buffer = doGetRequest(
        "https://e.uc.cn/material-center/web/mfs/inner/10297317/mcimages/16083179a7bd277b244d5fa4c69e5ac4a2eb38.png");
    FileOutputStream fileOutputStream = new FileOutputStream(new File("test.jpg"));
    fileOutputStream.write(buffer);
  }
}
