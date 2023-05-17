package com.atguigu.shardingjdbcdemo;

import com.atguigu.shardingjdbcdemo.entity.Course;
import com.atguigu.shardingjdbcdemo.entity.User;
import com.atguigu.shardingjdbcdemo.mapper.CourseMapper;
import com.atguigu.shardingjdbcdemo.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ApplicationTests {

    //注入mapper
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private UserMapper userMapper;

    //================测试垂直分库========================

    //新增操作
    @Test
    public void addUserDb() {
        User user = new User();
        user.setUsername("lucy");
        user.setUstatus("a");
        userMapper.insert(user);
    }

    //查询操作
    @Test
    public void findUserDb(){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id",865282859332534273L);
        User user = userMapper.selectOne(wrapper);
        System.out.println("查询出的用户为："+user);
    }

    //=================测试水平分库=========================

    //新增操作
    @Test
    public void addCourseDb(){
        for (int i = 0; i <= 10; i++) {
            Course course = new Course();
            course.setCname("java"+i);
            course.setUserId(100L+i);
            course.setCstatus("Normal"+i);
            courseMapper.insert(course);
        }
    }

    //查询操作
    @Test
    public void findCourseDb(){
        QueryWrapper<Course> wrapper = new QueryWrapper<>();
        wrapper.eq("cid",865266836072038400L);
        wrapper.eq("user_id",101L);
        Course course = courseMapper.selectOne(wrapper);
        System.out.println("查询出的课程为："+course);
    }

    //=================测试水平分表=========================
    //添加课程的方法
    @Test
    public void addCourse() {
        for (int i = 0; i <=10; i++) {
            Course course = new Course();
            course.setCname("java"+i);
            course.setUserId(100L);
            course.setCstatus("Normal"+i);
            courseMapper.insert(course);
        }
    }

    //查询课程的方法
    @Test
    public void findCourse(){
        QueryWrapper<Course> wrapper = new QueryWrapper<>();
        wrapper.eq("cid",865258802432180224L);
        Course course = courseMapper.selectOne(wrapper);
        System.out.println("查询出的课程为："+course);
    }

}
