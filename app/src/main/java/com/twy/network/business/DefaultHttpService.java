package com.twy.network.business;


import com.twy.network.Exception.HttpException;
import com.twy.network.interfaces.DataListener;
import com.twy.network.interfaces.HttpService;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Author by twy, Email 499216359@qq.com, Date on 2019/1/8.
 * PS: Not easy to write code, please indicate.
 */
public class DefaultHttpService extends HttpService {
    Map<String,List<HttpURLConnection>> map = new HashMap<>();

    //HttpURLConnection urlConn = null;

    @Override
    public void excuteGetRequest(Map<String, String> headers, String params, final DataListener listener) {
        HttpURLConnection urlConn = null;
        try{
            StringBuilder builder = new StringBuilder(requestInfo.getUrl());
            if (params.length() > 0) {
                if(requestInfo.getUrl().contains("?")){
                    builder.append("&").append(params);
                }else{
                    builder.append("?").append(params);
                }
            }
            String url = builder.toString();
            URL getUrl = new URL(url);
            urlConn = (HttpURLConnection) getUrl.openConnection();

            if(fragmentToString!=null){
                if(map.get(fragmentToString)==null){
                    List<HttpURLConnection> list = new ArrayList<>();
                    list.add(urlConn);
                    map.put(fragmentToString,list);
                }else {
                    map.get(fragmentToString).add(urlConn);
                }
            }


            urlConn.setDoInput(true);
            urlConn.setUseCaches(false);
            urlConn.setConnectTimeout(10000);
            urlConn.setRequestMethod("GET");
            //添加请求头
            if(headers!=null) {
                for (String key : headers.keySet()) {
                    urlConn.setRequestProperty(key, requestInfo.getHeads().get(key));
                }
            }
            urlConn.connect();

            int responseCode = urlConn.getResponseCode();
            if (responseCode < 200 || responseCode >= 300) {
                urlConn.disconnect();
                throw new HttpException(responseCode,urlConn.getResponseMessage());
            }

            BufferedReader bis = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "utf-8"));

            StringBuilder sb = new StringBuilder();
            String lines;
            while ((lines = bis.readLine()) != null) {
                sb.append(lines);
            }
            final String finalLines = sb.toString();
            cacel(urlConn);
            listener.converter(finalLines);
            urlConn.disconnect();
        }catch (final Exception e){
            listener.onError(e);
            if(urlConn!=null)
                cacel(urlConn);
        }
    }

    @Override
    public void excutePostRequest(Map<String, String> headers, String params, final DataListener listener,String bodyStr) {
        HttpURLConnection urlConn = null;
        try{
            //urlConn = createPostRequest(params);

            URL getUrl = new URL(bodyStr==null?requestInfo.getUrl():(requestInfo.getUrl().contains("?")?requestInfo.getUrl()+"&"+params:requestInfo.getUrl()+"?"+params));
            urlConn = (HttpURLConnection) getUrl.openConnection();

            if(fragmentToString!=null){
                if(map.get(fragmentToString)==null){
                    List<HttpURLConnection> list = new ArrayList<>();
                    list.add(urlConn);
                    map.put(fragmentToString,list);
                }else {
                    map.get(fragmentToString).add(urlConn);
                }
            }

            urlConn.setDoOutput(true);
            urlConn.setConnectTimeout(10000);
            urlConn.setRequestMethod("POST");

            //添加请求头
            if(headers!=null) {
                for (String key : headers.keySet()) {
                    urlConn.setRequestProperty(key, requestInfo.getHeads().get(key));
                }
            }
            if(bodyStr==null){
                urlConn.getOutputStream().write(params.getBytes("utf-8"));
            }else {
                urlConn.setRequestProperty("Content-Type", " application/json");
                urlConn.getOutputStream().write(bodyStr.getBytes("utf-8"));
            }
            int responseCode = urlConn.getResponseCode();
            if (responseCode < 200 || responseCode >= 300) {
                urlConn.disconnect();
                throw new HttpException(responseCode,urlConn.getResponseMessage());
            }

            BufferedReader bis = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "utf-8"));

            StringBuilder sb = new StringBuilder();
            String lines;
            while ((lines = bis.readLine()) != null) {
                sb.append(lines);
            }
            final String finalLines = sb.toString();
            cacel(urlConn);
            listener.converter(finalLines);
            urlConn.disconnect();
        }catch (final Exception e){
            listener.onError(e);
            if(urlConn!=null)
                cacel(urlConn);
        }
    }

    @Override
    public void excutePutRequest(Map<String, String> headers, String params, DataListener listener, String bodyStr) {
        HttpURLConnection urlConn = null;
        try{
            //urlConn = createPostRequest(params);

            URL getUrl = new URL(bodyStr==null?requestInfo.getUrl():(requestInfo.getUrl().contains("?")?requestInfo.getUrl()+"&"+params:requestInfo.getUrl()+"?"+params));
            urlConn = (HttpURLConnection) getUrl.openConnection();

            if(fragmentToString!=null){
                if(map.get(fragmentToString)==null){
                    List<HttpURLConnection> list = new ArrayList<>();
                    list.add(urlConn);
                    map.put(fragmentToString,list);
                }else {
                    map.get(fragmentToString).add(urlConn);
                }
            }

            urlConn.setDoOutput(true);
            urlConn.setConnectTimeout(10000);
            urlConn.setRequestMethod("PUT");

            //添加请求头
            if(headers!=null) {
                for (String key : headers.keySet()) {
                    urlConn.setRequestProperty(key, requestInfo.getHeads().get(key));
                }
            }
            if(bodyStr==null){
                urlConn.getOutputStream().write(params.getBytes("utf-8"));
            }else {
                urlConn.setRequestProperty("Content-Type", " application/json");
                urlConn.getOutputStream().write(bodyStr.getBytes("utf-8"));
            }
            int responseCode = urlConn.getResponseCode();
            if (responseCode < 200 || responseCode >= 300) {
                urlConn.disconnect();
                throw new HttpException(responseCode,urlConn.getResponseMessage());
            }

            BufferedReader bis = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "utf-8"));

            StringBuilder sb = new StringBuilder();
            String lines;
            while ((lines = bis.readLine()) != null) {
                sb.append(lines);
            }
            final String finalLines = sb.toString();
            cacel(urlConn);
            listener.converter(finalLines);
            urlConn.disconnect();
        }catch (final Exception e){
            listener.onError(e);
            if(urlConn!=null)
                cacel(urlConn);
        }
    }

    @Override
    public void excuteDeleteRequest(Map<String, String> headers, String params, DataListener listener, String bodyStr) {
        HttpURLConnection urlConn = null;
        try{
            //urlConn = createPostRequest(params);

            URL getUrl = new URL(bodyStr==null?requestInfo.getUrl():(requestInfo.getUrl().contains("?")?requestInfo.getUrl()+"&"+params:requestInfo.getUrl()+"?"+params));
            urlConn = (HttpURLConnection) getUrl.openConnection();

            if(fragmentToString!=null){
                if(map.get(fragmentToString)==null){
                    List<HttpURLConnection> list = new ArrayList<>();
                    list.add(urlConn);
                    map.put(fragmentToString,list);
                }else {
                    map.get(fragmentToString).add(urlConn);
                }
            }

            urlConn.setDoOutput(true);
            urlConn.setConnectTimeout(10000);
            urlConn.setRequestMethod("DELETE");

            //添加请求头
            if(headers!=null) {
                for (String key : headers.keySet()) {
                    urlConn.setRequestProperty(key, requestInfo.getHeads().get(key));
                }
            }
            if(bodyStr==null){
                urlConn.getOutputStream().write(params.getBytes("utf-8"));
            }else {
                urlConn.setRequestProperty("Content-Type", " application/json");
                urlConn.getOutputStream().write(bodyStr.getBytes("utf-8"));
            }
            int responseCode = urlConn.getResponseCode();
            if (responseCode < 200 || responseCode >= 300) {
                urlConn.disconnect();
                throw new HttpException(responseCode,urlConn.getResponseMessage());
            }

            BufferedReader bis = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "utf-8"));

            StringBuilder sb = new StringBuilder();
            String lines;
            while ((lines = bis.readLine()) != null) {
                sb.append(lines);
            }
            final String finalLines = sb.toString();
            cacel(urlConn);
            listener.converter(finalLines);
            urlConn.disconnect();
        }catch (final Exception e){
            listener.onError(e);
            if(urlConn!=null)
                cacel(urlConn);
        }
    }


    @Override
    public void excuteUploadFileRequest(Map<String, String> headers, String params, File file, DataListener listener) {
        String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        InputStream is = null;
        DataOutputStream dos = null;
        HttpURLConnection urlConn = null;
        try {
            URL url = new URL(requestInfo.getUrl().contains("?")?requestInfo.getUrl()+"&"+params:requestInfo.getUrl()+"?"+params);
            urlConn = (HttpURLConnection) url.openConnection();
            if(fragmentToString!=null){
                if(map.get(fragmentToString)==null){
                    List<HttpURLConnection> list = new ArrayList<>();
                    list.add(urlConn);
                    map.put(fragmentToString,list);
                }else {
                    map.get(fragmentToString).add(urlConn);
                }
            }
            //添加请求头
            if(headers!=null) {
                for (String key : headers.keySet()) {
                    urlConn.setRequestProperty(key, requestInfo.getHeads().get(key));
                }
            }
            urlConn.setReadTimeout(100000000);
            urlConn.setConnectTimeout(100000000);
            urlConn.setDoInput(true); // 允许输入流
            urlConn.setDoOutput(true); // 允许输出流
            urlConn.setUseCaches(false); // 不允许使用缓存
            urlConn.setRequestMethod("POST"); // 请求方式
            urlConn.setRequestProperty("Charset", "utf-8"); // 设置编码
            urlConn.setRequestProperty("connection", "keep-alive");
            urlConn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+ BOUNDARY);
            urlConn.getOutputStream().write(params.getBytes("utf-8"));
            /**
             * 当文件不为空，把文件包装并且上传
             */
            OutputStream outputSteam = urlConn.getOutputStream();

            dos = new DataOutputStream(outputSteam);
            StringBuffer sb = new StringBuffer();
            sb.append("--"+BOUNDARY+"\r\n");//数据分割线
            /**
             * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
             * filename是文件的名字，包含后缀名的 比如:abc.png
             */
            sb.append("Content-Disposition: form-data; name=\""+params.split("=")[0]+"\"; filename=\""+ file.getName() + "\"\r\n");
            sb.append("Content-Type: application/octet-stream; charset="+ "utf-8\r\n");
            sb.append("\r\n");
            dos.write(sb.toString().getBytes());
            is = new FileInputStream(file);
            byte[] bytes = new byte[1024];
            int len;
            while ((len = is.read(bytes)) != -1) {
                dos.write(bytes, 0, len);
            }

            dos.write(LINE_END.getBytes());
            byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
                    .getBytes();
            dos.write(end_data);
            dos.flush();
            /**
             * 获取响应码 200=成功 当响应成功，获取响应的流
             */
            int res = urlConn.getResponseCode();
            if (res == 200) {
                //return SUCCESS;
                BufferedReader bis = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "utf-8"));

                StringBuilder sb1 = new StringBuilder();
                String lines;
                while ((lines = bis.readLine()) != null) {
                    sb1.append(lines);
                }
                final String finalLines = sb1.toString();
                cacel(urlConn);
                listener.converter(finalLines);
                urlConn.disconnect();
            }else {
                urlConn.disconnect();
                throw new HttpException(res,urlConn.getResponseMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            listener.onError(e);
            cacel(urlConn);
        }finally {
            try {
                dos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void cancelRequest() {
        if(map.get(fragmentToString)!=null){
            for(HttpURLConnection urlConnection : map.get(fragmentToString)){
                urlConnection.disconnect();
                map.get(fragmentToString).remove(urlConnection);
            }
            map.remove(fragmentToString);
        }
    }

    private void cacel(HttpURLConnection urlConn){
        if(fragmentToString!=null && map.get(fragmentToString)!=null){
            if(map.get(fragmentToString).size()==1){
                map.remove(fragmentToString);
            }else{
                map.get(fragmentToString).remove(urlConn);
            }
        }
    }

    /*private HttpURLConnection createPostRequest(String params) throws IOException {
        URL getUrl = new URL(requestInfo.getUrl());
        HttpURLConnection urlConn = (HttpURLConnection) getUrl.openConnection();
        urlConn.setDoOutput(true);
        urlConn.setConnectTimeout(10000);
        urlConn.setRequestMethod("POST");
        urlConn.getOutputStream().write(params.getBytes("utf-8"));
        return urlConn;
    }*/

    /*private HttpURLConnection createGetRequest(String params) throws IOException {
        StringBuilder builder = new StringBuilder(requestInfo.getUrl());
        if (params.length() > 0) {
            if(requestInfo.getUrl().contains("?")){
                builder.append("&").append(params);
            }else{
                builder.append("?").append(params);
            }
        }
        String url = builder.toString();
        URL getUrl = new URL(url);
        HttpURLConnection urlConn = (HttpURLConnection) getUrl.openConnection();
        urlConn.setDoInput(true);
        urlConn.setUseCaches(false);
        urlConn.setConnectTimeout(10000);
        urlConn.setRequestMethod("GET");
        urlConn.connect();
        return urlConn;
    }*/

}
