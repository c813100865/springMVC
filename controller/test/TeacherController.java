package javase.新学生管理系统.终极项目.controller.test;


import javase.新学生管理系统.终极项目.annotation.Controller;
import javase.新学生管理系统.终极项目.annotation.RequestMapping;
import javase.新学生管理系统.终极项目.bean.Teacher;

@Controller
@RequestMapping("/teacher")
public class TeacherController {
    @RequestMapping("teacher/show2")
    public Object show222(Teacher info){
        System.out.println(info);
        System.out.println("teacher-show22222");
        return "";
    }
}
