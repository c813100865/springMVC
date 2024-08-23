package javase.新学生管理系统.终极项目.controller;

import javase.新学生管理系统.终极项目.annotation.Controller;
import javase.新学生管理系统.终极项目.annotation.RequestMapping;
import javase.新学生管理系统.终极项目.bean.Emp;
import javase.新学生管理系统.终极项目.tools.DBTools;

@Controller
@RequestMapping("/emp")
public class EMPController {

    //http://localhost:8080/emp/select
    @RequestMapping("/select")
    public Object select(){
        return DBTools.getMapBySql("select * from emp");
    }

    //http://localhost:8080/emp/insert
    @RequestMapping("/insert")
    public Object insert(Emp emp){
        DBTools.insert(emp);
        return DBTools.getMapBySql("select * from emp");
    }

    //http://localhost:8080/emp/insert
    @RequestMapping("/update")
    public Object update(Emp emp){
        DBTools.update(emp);
        return DBTools.getMapBySql("select * from emp");
    }

    //http://localhost:8080/emp/insert
    @RequestMapping("/delete")
    public Object delete(int empno){
        DBTools.delete(Emp.class,empno);
        return DBTools.getMapBySql("select * from emp");
    }
}
