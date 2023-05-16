package com.atguigu.shardingjdbcdemo;

import com.atguigu.shardingjdbcdemo.mapper.CourseMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
class ApplicationTests {

    //注入mapper
    @Autowired
    private CourseMapper courseMapper;

    @Test
    void contextLoads() {

    }

}
