package javase.新学生管理系统.终极项目.ui;


import com.google.gson.Gson;
import javase.新学生管理系统.终极项目.annotation.*;

import javase.新学生管理系统.终极项目.Demo;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.sql.SQLOutput;
import java.util.*;

public class MyServer {


    //默认扫描项目全源码
    static String scanpacke=System.getProperty("user.dir")+"\\src";

    //所有controller类
    static ArrayList<String> controllerNames=new ArrayList<>();

    //项目名
    static String projectName=new File(System.getProperty("user.dir")).getName();

    //所有的Mapping方法
    static Map<String, MethodObject> map=new HashMap();

    static Gson gson=new Gson();


    public static class MethodObject{
        public Method method;
        public Object obj;
    }

    //根据key找地址栏参数的值
    //id=1001&name=aaa&pwd=1&sex=1&tel=0101001
    public static String getValueByKey(String pars,String key){
        String value="";
        if (pars.indexOf(key+"=")<0)
            return value;
        int begin=pars.indexOf(key+"=")+key.length()+1;
        int end=pars.indexOf("&",begin);
        value=pars.substring(begin,end);
        return  value;
    }

    //递归加载包下的所有类
    public static void loadControllerNames(File file){
        //使用listFiles()方法获取该文件夹下的所有文件和子文件夹，并将它们存储在files数组中
        File[] files=file.listFiles();
        //遍历
        for (File finfo:files){
            //检查当前文件是否为目录。如果是目录，则进行递归调用
            if(finfo.isDirectory())
                loadControllerNames(finfo);
            else {
                //获取当前文件的绝对路径并存储在fileName变量中
                String fileName=finfo.getAbsolutePath();
                if(fileName.indexOf(".java")>=0){
                    //从文件路径中找到与项目名称后的\\src\\位置，作为起始位置
                    int begin=fileName.indexOf(projectName+"\\src\\")+projectName.length()+5;
                    int end=fileName.lastIndexOf(".");
                    //把路径里面的\\转换成文件的.
                    // 即 a\\b\\c\\d  -  a.b.c.d
                    controllerNames.add(fileName.substring(begin,end).replace("\\","."));
                }
            }
        }
    }

    public static class Service extends Thread{
        private Socket socket=null;
        public Service(Socket socket){
            this.socket=socket;
        }

        @Override
        public void run() {
            try {
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();
                BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));
                PrintWriter writer=new PrintWriter(outputStream);

                //开始接受用户的信息
                //解析http协议行
                String line = reader.readLine();
                String head=line.split(" ")[1];
                String action=head.split("\\?")[0];
                String parsPath="";
                if (head.split("\\?").length>1)
                    parsPath= URLDecoder.decode(head.split("\\?")[1]+"&");

                String resultData="";

                //分发给不同的controller
                MethodObject methodObject = map.get(action);
                if (methodObject!=null) {
                    Parameter[] parameters = methodObject.method.getParameters();
                    Object[] pars = new Object[parameters.length];
                    for (int i = 0; i < parameters.length; i++) {
                        if (parameters[i].getType() == String.class)
                            pars[i] = getValueByKey(parsPath, parameters[i].getName());
                        else if (parameters[i].getType() == Integer.class || parameters[i].getType() == int.class) {
                            pars[i] = Integer.parseInt(getValueByKey(parsPath, parameters[i].getName()));
                        } else {
                            //一律当作对象处理
                            Class pinfo = parameters[i].getType();
                            Object o = pinfo.newInstance();
                            Field[] fields = pinfo.getDeclaredFields();
                            for (Field field : fields) {
                                if (!"".equals(getValueByKey(parsPath, field.getName()))) {
                                    field.setAccessible(true);
                                    if (field.getType() == Integer.class || field.getType() == int.class)
                                        field.set(o, Integer.parseInt(getValueByKey(parsPath, field.getName())));
                                    else if (field.getType() == String.class)
                                        field.set(o, getValueByKey(parsPath, field.getName()));
                                }
                            }
                            pars[i] = o;
                        }
                    }
                    System.out.println(pars);
                    resultData = gson.toJson(methodObject.method.invoke(methodObject.obj, pars));
                }else{
                    resultData="您请求的资源不存在!";
                }



                //返回给浏览器
                writer.println("HTTP/1.1 200 OK");
                writer.println("content-type:application/json;charset=utf-8");
                writer.println();
                writer.println(resultData);
                writer.flush();
                socket.shutdownOutput();
                System.out.println(resultData);

            }catch (Exception e){
                e.printStackTrace();
            }finally {
                try {
                    socket.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    //加载包扫描.所有控制器
    public static void loadController()throws Exception {
        Properties properties=new Properties();
        //遇到编码问题,用这个方法
        InputStream input = Demo.class.getResourceAsStream("/application.properties");
        properties.load(new InputStreamReader(input,"utf-8"));
        //这个包的系统路径scanpack
        //.换成\
        scanpacke = System.getProperty("user.dir") + "\\src\\" + "" +
        properties.getProperty("scanpacke").replace(".", "\\");
        File file=new File(URLDecoder.decode(scanpacke));

        loadControllerNames(file);

        //判断是否有Controller注解
        String tempMethodName="";
        for (String controllerClass : controllerNames) {
            //获得controller类
            Class<?> cinfo = Class.forName(controllerClass);
            //把每个controller类new出来
            Object obj = Class.forName(controllerClass).newInstance();
            //cinfo是类,obj是对象
            //判断是否有controller注解
            if (cinfo.getAnnotation(Controller.class) != null) {
                if (cinfo.getAnnotation(RequestMapping.class) != null) {
                    //把类的@RequestMapping注解信息存下来
                    tempMethodName = cinfo.getAnnotation(RequestMapping.class).value();
                } else {
                    tempMethodName = "";
                }
                //遍历有@Controller注解的类里面的所有方法
                Method[] methods = cinfo.getDeclaredMethods();
                for (Method method: methods){
                    //找到有@RequestMapping注解的方法
                    if(method.getAnnotation(RequestMapping.class)!=null){
                        //在类的注解的地址后面加上方法的地址
                        //  /student +/insert
                        tempMethodName+="/"+method.getAnnotation(RequestMapping.class).value();
                        //把地址的所有//换成网址的/
                        tempMethodName=tempMethodName.replace("//","/");
                        //把方法和对应的类对应起来装到MethodObject容器里面
                        //System.out.println(tempMethodName);
                        MethodObject info=new MethodObject();
                        info.method=method;
                        info.obj=obj;
                        //把所有的RequestMapping注解的方法和对应的类放到容器里面
                        map.put(tempMethodName,info);
                        if(cinfo.getAnnotation(RequestMapping.class)!=null){
                            //更新tempMethodName保证每个方法路径独立
                            tempMethodName=cinfo.getAnnotation(RequestMapping.class).value();
                        }else {
                            tempMethodName="";
                        }
                    }

                }
            }
        }
    }

    //控制器的启动
    public static void start()throws Exception{
        ServerSocket server=new ServerSocket(8080);
        //加载所有的controller
        loadController();
        System.out.println("服务器已启动...");
        while (true){
            Socket socket = server.accept();
            System.out.println(socket+":连接我了");
            new Service(socket).start();
        }
    }

    public static void main(String[] args) throws Exception {
        loadController();
        System.out.println(map);
    }

}











