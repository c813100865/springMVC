package javase.新学生管理系统.终极项目.bean;


import javase.新学生管理系统.终极项目.annotation.ID;
import javase.新学生管理系统.终极项目.annotation.Table;

@Table("student001")
public class Student001 {
    @ID
    public Integer id;
    public String name;
    public String pwd;
    public String sex;
    public Integer tel;


    public Student001(Integer id, String name, String pwd, String sex, Integer tel) {
        this.id = id;
        this.name = name;
        this.pwd = pwd;
        this.sex = sex;
        this.tel = tel;
    }

    public Student001() {

    }

    @Override
    public String toString() {
        return "Student001{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", pwd='" + pwd + '\'' +
                ", sex='" + sex + '\'' +
                ", tel=" + tel +
                '}';
    }
}
