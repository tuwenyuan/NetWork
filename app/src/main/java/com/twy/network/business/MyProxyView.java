package com.twy.network.business;

import com.twy.network.interfaces.GET;
import com.twy.network.interfaces.Headers;
import com.twy.network.interfaces.Multipart;
import com.twy.network.interfaces.POST;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Author by twy, Email 499216359@qq.com, Date on 2019/1/11.
 * PS: Not easy to write code, please indicate.
 */
public class MyProxyView  implements InvocationHandler {

    public static Object newInstance(Class[] interfaces) {
        return Proxy.newProxyInstance(MyProxyView.class.getClassLoader(),
                interfaces, new MyProxyView());
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        String methodName = method.getName();
        //System.out.println("调用的方法名称为:"+methodName);
        Class<?> returnType = method.getReturnType();
        //System.out.println("返回的类型为"+returnType.getName());
        Observable<?> returnObject = (Observable<?>)returnType.newInstance();

        //获取到方法的参数列表
        /*Type[] parameterTypes = method.getGenericParameterTypes();
        for (Type type : parameterTypes) {
            System.out.println(type);
            //只有带泛型的参数才是这种Type，所以得判断一下
            if(type instanceof ParameterizedType){
                ParameterizedType parameterizedType = (ParameterizedType) type;
                //获取参数的类型
                System.out.println(parameterizedType.getRawType());
                //获取参数的泛型列表
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                for (Type type2 : actualTypeArguments) {
                    System.out.println(type2);
                }
            }
        }*/

        //获取返回值的类型，此处不是数组，请注意智商，返回值只能是一个
        Type genericReturnType = method.getGenericReturnType();
        if(genericReturnType instanceof ParameterizedType){
            Type[] actualTypeArguments = ((ParameterizedType)genericReturnType).getActualTypeArguments();
            for (Type type : actualTypeArguments) {
                returnObject.type = type;
            }
        }

        for (Annotation a : method.getAnnotations()){
            if(Headers.class.equals(a.annotationType())){
                returnObject.headers = ((Headers)a).value();
            }else if(GET.class.equals(a.annotationType())){
                returnObject.get = (GET) a;
            }else if(POST.class.equals(a.annotationType())){
                returnObject.post = (POST) a;
            }else if(Multipart.class.equals(a.annotationType())){
                returnObject.isMultipart = true;
            }
        }
        returnObject.paramValues = args;
        List<Object> qs = new ArrayList<>();
        //通过注解去取注解上的value
        Annotation parameterAnnotations[][] = method.getParameterAnnotations();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                /*if(Query.class.equals(annotation.annotationType())){
                    Query q = (Query) annotation;
                    qs.add(q);
                }*/
                /*else if(FileType.class.equals(annotation.annotationType())){
                    qs.add(annotation);
                }*/
                qs.add(annotation);
            }
        }
        returnObject.paramNames = qs;
        return returnObject;
    }
}
