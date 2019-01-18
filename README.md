# NetWork



#### 框架使用说明
该项目框架比较灵活 
对请求相关信息进行了封装 
用户自己实现数据类型转换 
用户可以自己实现请求 默认android api HttpURLConnection请求方式
只需要实现HttpService 实现excuteGetRequest及excutePostRequest及cancelRequest 在初始化 setHttpService(HttpService) 下面OKHttpService是自己实现的 但注意的是该所有阿执行都是在子线程执行 自己必须保证同步执行

    Net net = new Net.Builder()
                    .setConverterFactory(new ResponseConvertFactory(new Gson()))
                    .baseUrl("http://192.168.1.75:8080/test/")
                    .setHttpService(new OkHttpService())
                    .build();
                    
Jar包下载

                    
#### 请求数据封装

    public interface ITestServices {
        @POST("user2")
        Observable<User> getUserPost(@Query(value = "userName",encoded = true) String userName, @Query("password") String password);
        @GET("user2")
        Observable<User> getUserGet(@Query(value = "userName",encoded = true) String userName, @Query("password") String password);
    }

#### 初始化信息

    Net net = new Net.Builder()
                .setConverterFactory(new ResponseConvertFactory(new Gson()))
                .baseUrl("http://192.168.1.75:8080/test/")
                .build();
    ITestServices services = net.create(ITestServices.class);
   
#### 一次请求 

    Net.startRequestData(this, services.getUserPost("涂文远", "123456"), new OnRecvDataListener<User>() {
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
        public void onRecvData(User data) {
            Log.i("twy",Thread.currentThread().getName()+"***onRecvData"+"***"+data.userName+"::::"+data.password+"");
        }
    }); 

![pinned_selection_listview](https://github.com/tuwenyuan/NetWork/blob/master/app/src/main/res/mipmap-xxxhdpi/net.png)
