package com.twy.network;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.twy.network.business.Net;
import com.twy.network.business.OkHttpService;
import com.twy.network.interfaces.ITestServices;
import com.twy.network.interfaces.OnRecvDataListener;
import com.twy.network.model.CommBean;
import com.twy.network.model.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn_get = findViewById(R.id.btn_get);
        Button btn_post = findViewById(R.id.btn_post);
        Button btn_upload = findViewById(R.id.btn_upload);
        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserGet();
            }
        });
        btn_get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserGet();
                getUserPost();
            }
        });
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFile();
            }
        });

        //.setHttpService(new OkHttpService())
        Net net = new Net.Builder()
                .setConverterFactory(new ResponseConvertFactory(new Gson()))
                .baseUrl("http://94.191.92.69:8080/Upload/")
                //.setHttpService(new OkHttpService())
                .build();
        services = net.create(ITestServices.class);
    }

    private void uploadFile(){
        if(getCrashReportFiles()==null){
            saveCrashInfo2File(new Exception("故意发生错误"));
        }
        //存在的文件
        File file = new File(getFilesDir() + "/crash",getCrashReportFiles()[0]);
        Net.startRequestData(this, services.uploadFile("sss", file), new OnRecvDataListener<CommBean>() {
            @Override
            public void onRecvData(CommBean data) {
                Log.i("twy",data.toString());
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

    ITestServices services;
    private void getUserGet(){
        Net.startRequestData(this, services.getIndexPageData(1, 20,640,208), new OnRecvDataListener<String>() {
            @Override
            public void onStart() {
                Log.i("twy",Thread.currentThread().getName()+"***onStart");
            }

            @Override
            public void onComplate() {
                Log.i("twy",Thread.currentThread().getName()+"***onComplate");
            }

            @Override
            public void onError(Exception e) {
                Log.i("twy",Thread.currentThread().getName()+"***onError"+e.getMessage());
            }

            @Override
            public void onRecvData(String data) {
                Log.i("twy",data);
            }
        });
    }

    private void getUserPost(){
        /*Net.startRequestData( services.getUserPost("涂文远", "123456"), new OnRecvDataListener<User1>() {
            @Override
            public void onStart() {
                Log.i("twy",Thread.currentThread().getName()+"***onStart");
            }

            @Override
            public void onComplate() {
                Log.i("twy",Thread.currentThread().getName()+"***onComplate");
            }

            @Override
            public void onError(Exception e) {
                Log.i("twy",Thread.currentThread().getName()+"***onError"+e.getMessage());
            }

            @Override
            public void onRecvData(User1 data) {
                Log.i("twy",Thread.currentThread().getName()+"***onRecvData"+"***"+data.userName+"::::"+data.password+"");
            }
        });*/
        User1 user = new User1();
        user.loginId = "13025417416";
        user.code = "1234";
        Net.startRequestData(services.logon(user), new OnRecvDataListener<String>() {
            @Override
            public void onRecvData(String data) {
                Log.i("twy",data);
            }

            @Override
            public void onError(Exception e) {
                Log.i("twy",Thread.currentThread().getName()+"***onError"+e.getMessage());
            }
        });

    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    private String saveCrashInfo2File(Throwable ex) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Map<String, String> info = new HashMap<String, String>();
        info.put("data","aaaaaaaaaaaaaaaaaaa");
        info.put("data1","bbbbbbbbbbbbbbbb");
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        for (Map.Entry<String, String> entry : info.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append("\""+key+"\":"+"\""+value+"\",");
        }

        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        ex.printStackTrace(pw);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(pw);
            cause = cause.getCause();
        }
        pw.close();
        String result = writer.toString();
        sb.append("\"result\":"+"\""+result+"\"}");
        //sb.append(result);


        try {
            long timestamp = System.currentTimeMillis();
            String time = format.format(new Date());
            String fileName = "crash-" + time + "-" + timestamp + "-test.cr";
            String drp = getFilesDir() + "/crash";
            java.io.File drf = new java.io.File(drp);
            if (!drf.exists()) {
                drf.mkdir();
            }
            java.io.File f = new java.io.File(drf, fileName);
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(sb.toString().getBytes());
            fos.close();
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private String[] getCrashReportFiles() {
        java.io.File filesDir = new java.io.File(getFilesDir() + "/crash");
        // 实现FilenameFilter接口的类实例可用于过滤器文件名
        FilenameFilter filter = new FilenameFilter() {
            // accept(FileType dir, String name)
            // 测试指定文件是否应该包含在某一文件列表中。
            public boolean accept(java.io.File dir, String name) {
                return name.endsWith("-test.cr");
            }
        };
        // 返回一个字符串数组，这些字符串指定此抽象路径名表示的目录中满足指定过滤器的文件和目录
        return filesDir.list(filter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
