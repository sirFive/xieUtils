package com.xie.xieutils.util;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * httpClient工具类
 */
public class HttpClientUtil {

    private static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    /**
     * 请求超时时间
     */
    private static final int CONNECT_TIMEOUT = 3 * 1000;
    private static final int SOCKET_TIMEOUT = 3 * 1000;

    /**
     * 最大路由数
     */
    private static final int MAX_COON = 300;
    private static final int MAX_ROUTE = 150;

    /**
     * 线程检查间隔
     */
    private static final int CHECK_TIME = 5 * 1000;
    private static final int SLEEP_TIME = 5 * 1000;

    /**
     * 连接池
     */
    private static PoolingHttpClientConnectionManager connManager;

    /**
     * 初始化连接池
     */
    static {
        ConnectionSocketFactory plainSocketFactory = PlainConnectionSocketFactory.getSocketFactory();
        LayeredConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactory.getSocketFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create().register("http", plainSocketFactory)
                .register("https", sslSocketFactory).build();
        connManager = new PoolingHttpClientConnectionManager(registry);
        // 设置最大连接数
        connManager.setMaxTotal(MAX_COON);
        // 设置每个连接的路由数
        connManager.setDefaultMaxPerRoute(MAX_ROUTE);
        //开启线程监控
        IdleConnectionMonitorThread idleConnectionMonitorThread = new IdleConnectionMonitorThread(connManager);
        idleConnectionMonitorThread.start();
    }

    /**
     * 获取httpClient
     *
     * @return
     */
    public static CloseableHttpClient getHttpClient() {
        //请求失败时,进行请求重试
        HttpRequestRetryHandler retryHandler = new HttpRequestRetryHandler() {
            @Override
            public boolean retryRequest(IOException e, int i, HttpContext httpContext) {
                if (i > 3) {
                    //重试超过3次,放弃请求
                    logger.error("retry has more than 3 time, give up request");
                    return false;
                }
                if (e instanceof NoHttpResponseException) {
                    //服务器没有响应,可能是服务器断开了连接,应该重试
                    logger.error("receive no response from server, retry");
                    return true;
                }
                if (e instanceof SSLHandshakeException) {
                    // SSL握手异常
                    logger.error("SSL hand shake exception");
                    return false;
                }
                if (e instanceof InterruptedIOException) {
                    //超时
                    logger.error("InterruptedIOException");
                    return false;
                }
                if (e instanceof UnknownHostException) {
                    // 服务器不可达
                    logger.error("server host unknown");
                    return false;
                }
                if (e instanceof ConnectTimeoutException) {
                    // 连接超时
                    logger.error("Connection Time out");
                    return false;
                }
                if (e instanceof SSLException) {
                    logger.error("SSLException");
                    return false;
                }
                return false;
            }
        };
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connManager)
                .setRetryHandler(retryHandler)
                .build();
        return httpClient;
    }

    /**
     * 对http请求进行基本设置
     * @param httpRequestBase http请求
     */
    private static void setRequestConfig(HttpRequestBase httpRequestBase){
        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(CONNECT_TIMEOUT)
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setSocketTimeout(SOCKET_TIMEOUT).build();

        httpRequestBase.setConfig(requestConfig);
    }

    /**
     * 开启线程监控，关闭无效连接
     */
    public static class IdleConnectionMonitorThread extends Thread {

        private final HttpClientConnectionManager connMgr;
        private volatile boolean shutdown;

        public IdleConnectionMonitorThread(HttpClientConnectionManager connMgr) {
            super();
            this.connMgr = connMgr;
        }

        @Override
        public void run() {
            try {
                while (!shutdown) {
                    synchronized (this) {
                        wait(SLEEP_TIME);
                        // 关闭失效连接
                        connMgr.closeExpiredConnections();
                        // 关闭用完后超过5秒的连接
                        connMgr.closeIdleConnections(CHECK_TIME, TimeUnit.MILLISECONDS);
                    }
                }
            } catch (InterruptedException ex) {
                // terminate
            }
        }

        public void shutdown() {
            shutdown = true;
            synchronized (this) {
                notifyAll();
            }
        }

    }

    /**
     * 发送post请求
     * @param url
     * @return
     */
    public static String sendPost(String url, Map<String,Object> paramMap){
        String result = null;
        CloseableHttpClient httpClient = getHttpClient();
        CloseableHttpResponse response = null;
        InputStream in = null;
        HttpPost httpPost = new HttpPost(url);
        setRequestConfig(httpPost);
        //封装参数
        List<NameValuePair> nameValuePairList = new ArrayList<>();
        if(paramMap != null){
            for(Map.Entry<String,Object> entry : paramMap.entrySet()){
                nameValuePairList.add(new BasicNameValuePair(entry.getKey(),entry.getValue().toString()));
            }
        }
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairList,"utf-8"));
            response = httpClient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            if (httpClient !=null){
                in = httpEntity.getContent();
               result = IOUtils.toString(in,"utf-8");
            }
        }catch (Exception e){

        }finally {
            //关闭连接
            try {
                if(in !=null){
                    in.close();
                }
                if (response != null){
                    response.close();
                }
            }catch (IOException io){
                io.printStackTrace();
            }

        }

        return result;
    }

    /**
     * 发送get请求
     * @param url
     * @return
     */
    public static String sendGet(String url){
        String result = null;
        CloseableHttpClient httpClient = getHttpClient();
        CloseableHttpResponse response = null;
        InputStream in = null;
        HttpGet httpGet = new HttpGet(url);
        setRequestConfig(httpGet);
        try {
            response = httpClient.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            if (httpClient !=null){
                in = httpEntity.getContent();
                result = IOUtils.toString(in,"utf-8");
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //关闭连接
            try {
                if(in !=null){
                    in.close();
                }
                if (response != null){
                    response.close();
                }
            }catch (IOException io){
                io.printStackTrace();
            }

        }
        return  result;
    }
}
