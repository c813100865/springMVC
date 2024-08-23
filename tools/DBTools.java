package javase.新学生管理系统.终极项目.tools;


import javase.新学生管理系统.终极项目.annotation.ID;
import javase.新学生管理系统.终极项目.annotation.Size;
import javase.新学生管理系统.终极项目.annotation.Table;
import javase.新学生管理系统.终极项目.bean.Emp;
import javase.新学生管理系统.终极项目.bean.Student001;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class DBTools {

        static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
        final static String URL="jdbc:mysql://120.26.246.25:3306/test?useSSL=false&useUnicode=true&characterEncoding=utf-8";
        final static String NAME="root";
        final static String PWD="123qwe!@#";
        private static ResultSet rs = null;
        private static PreparedStatement pst = null;
        private static Connection conn = null;

        //万能的增删改方法
        public static int executeUpdate(String sql,Object ... objs) {
        int i=-1;
        Connection conn=null;
        try {
            conn = DriverManager.getConnection(URL, NAME, PWD);
            conn.setAutoCommit(false);
            PreparedStatement pps = conn.prepareStatement(sql);
            for(int index=1;objs!=null&&index<=objs.length;index++){
                pps.setObject(index,objs[index-1]);
            }
            i=pps.executeUpdate();
            pps.close();
            conn.commit();
        }catch (Exception e){
            System.out.println(e.getMessage());
            try {
                conn.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }finally {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return i;
    }

        ////insert into 表名(列名1...列名N) values(值1...值N)
        //添加
        public static <T> int insert(T t){
        int result=-1;
        try {
            Class cinfo=t.getClass();
            //拼接sql语句
            StringBuffer sb=new StringBuffer();
            //获得insert into 表名
            sb.append("insert into "+cinfo.getSimpleName());
            sb.append("(");
            //获得这个类的所有属性 设置为列名
            Field[] fields = cinfo.getDeclaredFields();
            ArrayList plist=new ArrayList();
            //初始化temp 后来拼成values(值1...值N)
            String temp="";
            for (int i=0;i<fields.length;i++){
                //因为主键时自动增长的,不需要添加主键
                //只需要查看属性是否有主键的注解就行
                if(fields[i].getAnnotation(ID.class)==null){
                    sb.append(fields[i].getName()+",");
                    //把属性的值放到plist里,以便等下拼接
                    plist.add(fields[i].get(t));
                    temp+="?,";
                }
            }
            sb.delete(sb.length()-1,sb.length());
            sb.append(") values( "+temp);
            sb.delete(sb.length()-1,sb.length());
            sb.append(")");
            //sb拼接完成insert into 表名(列名1...列名N) values(?,?,?,?) 值1...值N
            //再在后面加上plist转化为数组,就完成sql语句了
            //insert into 表名(列名1...列名N) values(?,?,?,?)

            System.out.println(sb);
            plist.forEach(p->{
                System.out.println(p);
            });
            result=executeUpdate(sb.toString().toLowerCase(),plist.toArray());
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return result;
    }

        //修改
        //UPDATE `test`.`student001` SET `name` = '秦始皇' WHERE (`id` = '1005');
        public static <T> int update(T t){
        int result=-1;
        try {
            Class cinfo=t.getClass();
            StringBuffer sb=new StringBuffer();
            //upadate 表名
            sb.append("update "+cinfo.getSimpleName());
            //upadate 表名 set
            sb.append(" set ");
            //获取属性,对应在sql的列
            Field[] fields = cinfo.getDeclaredFields();
            //参数放在plist里
            ArrayList plist=new ArrayList<>();
            //主键,where条件
            String key="";
            for (int i=0;i< fields.length;i++){
                //先判断是不是主键
                if(fields[i].getAnnotation(ID.class)==null){
                    //不是主键
                    sb.append(fields[i].getName()+"=?,");
                    //存储对应的值
                    plist.add(fields[i].get(t));
                }else {
                    //是主键 存储起来
                    key=fields[i].getName();
                }
            }
            //获取主键的值
            plist.add(cinfo.getDeclaredField(key).get(t));
            sb.delete(sb.length()-1,sb.length());
            sb.append(" where "+key+"=? ");
//            System.out.println(sb);
//            plist.forEach(p->{
//                System.out.println(p);
//            });
            result=executeUpdate(sb.toString().toLowerCase(),plist.toArray());
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return result;
    }

        //删除
        public static int delete(Class cinfo,Integer id){
        int result=-1;
        try {
            StringBuffer sb=new StringBuffer();
            sb.append("delete from "+cinfo.getSimpleName());
            //只需要获取主键的值就行
            Field[] fields = cinfo.getDeclaredFields();
            Arrays.stream(fields).forEach(p->{
                //把每个属性的注解提取出来
                ID idan=p.getAnnotation(ID.class);
                //如果有ID注解
                if(idan!=null){
                    sb.append(" where "+p.getName()+" = ?");
                }
            });

            System.out.println(sb.toString()+"\t"+id);
            result=executeUpdate(sb.toString().toLowerCase(),id);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return result;
    }

        //查询
        public static <T> ArrayList<T> executeQuery(String sql,Class<T> cinfo,Object ... objs) {
        ArrayList<T> list=new ArrayList<>();
        try {
            Connection conn= DriverManager.getConnection(URL,NAME,PWD);
            PreparedStatement pps=conn.prepareStatement(sql);
            for (int index=1;objs!=null&&index<objs.length;index++){
                pps.setObject(index,objs[index-1]);
            }
            ResultSet resultSet=pps.executeQuery();
            while (resultSet.next()){
                T t=cinfo.newInstance();
                Field[] fields = cinfo.getDeclaredFields();
                for (int i=0;i<fields.length;i++){
                    fields[i].setAccessible(true);
                    if(fields[i].getType()==Integer.class)
                        fields[i].set(t,resultSet.getInt(fields[i].getName()));
                    else if(fields[i].getType()==String.class)
                        fields[i].set(t,resultSet.getString(fields[i].getName()));
                    else if(fields[i].getType()==Date.class)
                        fields[i].set(t,resultSet.getDate(fields[i].getName()));
                }
                list.add(t);
            }
            resultSet.close();;
            conn.close();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return list;
    }



        //把java的数据类型转化为mysql的数据类型
        public static String javaTypeToMysqlType(Field field){
        Class cinfo=field.getType();
        //通过变量的注解获取长度,没有注解就取默认值
        Size size=field.getAnnotation(Size.class);
        if(cinfo==Integer.class)
            return "int("+(size!=null?size.value():10)+")";
        if(cinfo== Date.class)
            return "date";

        return "varchar("+(size!=null?size.value():50)+")";
    }

        //把数据库转化为json结构
        public static ArrayList<HashMap<String,String>> getMapBySql(String sql,Object...objs) {
        ArrayList<HashMap<String,String>> list=new ArrayList<>();
        try {
            Connection conn = DriverManager.getConnection(URL, NAME, PWD);
            PreparedStatement pps = conn.prepareStatement(sql);
            for (int index=1;objs!=null&&index<=objs.length;index++){
                pps.setObject(index,objs[index-1]);
            }
            ResultSet resultSet=pps.executeQuery();
            //获取所有列名
            ArrayList<String> colums=new ArrayList<>();
            ResultSetMetaData metaData=resultSet.getMetaData();
            //开始遍历所有列
            for (int i=0;i<metaData.getColumnCount();i++){
                colums.add(metaData.getColumnName(i+1));
            }
            while (resultSet.next()){
                HashMap<String,String> map =new HashMap<>();
                for (int i=0;i<colums.size();i++){
                    if(resultSet.getObject(colums.get(i))!=null)
                        map.put(colums.get(i),resultSet.getObject(colums.get(i)).toString());
                }
                list.add(map);
            }
            resultSet.close();
            conn.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

        //通过实体类生成数据库表(正向工程)
        //create table 表名(
        // 列名1 类型(大小) 约束,
        // 列名N 类型(大小) 约束
        //    create table table001(
        //          id int(4) primary key auto_increment,
        //          name varchar(50)
        //      )
        // )
        public static void createTable(Class cinfo){
        StringBuffer sb=new StringBuffer();
        sb.append("create table ");
        //获取这个类的注解,判断是否有表
        Table tablean= (Table) cinfo.getAnnotation(Table.class);
        //判断这个类有没有表,没有表的话用table注解的名字
        if(tablean!=null){
            sb.append(tablean.value());
        }else {
            //没有注解就用类名当表名
            sb.append(cinfo.getSimpleName());
        }
        sb.append("(");
        //获取类的所有属性当列名
        Field[] fields = cinfo.getDeclaredFields();
        Arrays.stream(fields).forEach(p->{
            //列名 数据类型
            sb.append(p.getName()+" "+javaTypeToMysqlType(p)+" ");
            //再给主键 带有ID注解的属性加上主键 和自动增长
            ID idan =p.getAnnotation(ID.class);
            if(idan!=null)
                sb.append("primary key auto_increment");
            sb.append(",");
        });
        sb.delete(sb.length()-1,sb.length());
        sb.append(")");
            System.out.println(sb);
        //executeUpdate(sb.toString().toLowerCase());
        System.out.println("数据表生成成功");
    }


}
