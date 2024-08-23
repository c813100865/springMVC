package javase.新学生管理系统.终极项目.bean;

import javase.新学生管理系统.学生管理系统10.anno.Table;
import javase.新学生管理系统.终极项目.annotation.ID;

@Table("student")
public class Student {
    @ID
    public Integer id;
    public String name;
    public String pwd;
    public String tel;
    public String address;
    public Integer classid;
    public Integer rid;

    public Student() {
    }

    public Student(Integer id, String name, String pwd, String tel, String address, Integer classid, Integer rid) {
        this.id = id;
        this.name = name;
        this.pwd = pwd;
        this.tel = tel;
        this.address = address;
        this.classid = classid;
        this.rid = rid;
    }

    @Override
    public String toString() {
        return "Student{" +
                "address='" + address + '\'' +
                ", classid=" + classid +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", pwd='" + pwd + '\'' +
                ", rid=" + rid +
                ", tel='" + tel + '\'' +
                '}';
    }
}
