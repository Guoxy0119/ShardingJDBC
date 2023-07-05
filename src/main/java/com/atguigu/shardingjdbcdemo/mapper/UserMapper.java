package com.atguigu.shardingjdbcdemo.mapper;

import com.atguigu.shardingjdbcdemo.entity.User;
import com.atguigu.shardingjdbcdemo.util.MyMapper;

public interface UserMapper extends MyMapper<User> {
    User queryById(Long id);
}
