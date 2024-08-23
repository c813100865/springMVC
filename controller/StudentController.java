package javase.新学生管理系统.终极项目.controller;

import javase.新学生管理系统.终极项目.annotation.Controller;
import javase.新学生管理系统.终极项目.annotation.RequestMapping;
import javase.新学生管理系统.终极项目.bean.Student001;
import javase.新学生管理系统.终极项目.tools.DBTools;


@Controller
@RequestMapping("/student")
public class StudentController {

    //http://localhost:8080/student/select
    @RequestMapping("/select")
    public Object select(){
        return DBTools.getMapBySql("select * from student");
    }

    //http://localhost:8080/emp/insert
    @RequestMapping("/insert")
    public Object insert(Student001 emp){
        DBTools.insert(emp);
        return DBTools.getMapBySql("select * from student");
    }

    //http://localhost:8080/emp/insert
    @RequestMapping("/update")
    public Object update(Student001 emp){
        DBTools.update(emp);
        return DBTools.getMapBySql("select * from student");
    }

    //http://localhost:8080/emp/insert
    @RequestMapping("/delete")
    public Object delete(int id){
        DBTools.delete(Student001.class,id);
        return DBTools.getMapBySql("select * from student");
    }


}
