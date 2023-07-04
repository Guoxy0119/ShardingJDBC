package com.atguigu.shardingjdbcdemo.mapper;

import com.atguigu.shardingjdbcdemo.entity.Course;
import com.atguigu.shardingjdbcdemo.util.MyMapper;

public interface CourseMapper extends MyMapper<Course> {

    Course queryById(String id);
}
