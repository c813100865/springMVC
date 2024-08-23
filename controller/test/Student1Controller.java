package javase.新学生管理系统.终极项目.controller.test;


import javase.新学生管理系统.终极项目.annotation.Controller;
import javase.新学生管理系统.终极项目.annotation.RequestMapping;
import javase.新学生管理系统.终极项目.tools.DBTools;

@Controller
@RequestMapping("/student1")
public class Student1Controller {
    @RequestMapping("/select")
    public Object select(){
        return DBTools.getMapBySql("select * from student");
    }
}
