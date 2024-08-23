package javase.新学生管理系统.终极项目.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

//有此注解、代表可以处理用户请求
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {
}
