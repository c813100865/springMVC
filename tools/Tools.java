package javase.新学生管理系统.终极项目.tools;

import java.util.Scanner;

public class Tools {
    static Scanner input=new Scanner(System.in);

    //输出字符串并获取用户输入的字符串
    public static String getString(String msg){
        System.out.print(msg);
        return input.next();
    }

    //输出字符串并获取用户输入的整形
    public static int getInt(String msg){
        System.out.print(msg);
        return input.nextInt();
    }

    //菜单生成器
    public static int menuFactory(String ... items){
        StringBuffer sb=new StringBuffer("请选择功能菜单<");
        System.out.println("---------------------------------");
        for (int i=0;i<items.length;i++) {
            System.out.println("\t\t"+(i+1)+"、"+items[i]);
            sb.append(i+1+",");
        }
        sb.delete(sb.length()-1,sb.length());
        sb.append(">:");
        System.out.println("---------------------------------");
        return getInt(sb.toString());
    }
}
