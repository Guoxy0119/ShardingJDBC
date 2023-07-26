package com.atguigu.shardingjdbcdemo.mapper;

import com.atguigu.shardingjdbcdemo.entity.User;
import com.atguigu.shardingjdbcdemo.util.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper extends MyMapper<User> {
    User queryById(Long id);

    List<User> queryByIds(@Param("ids") List<Long> ids);


    User selectByOrder(Long id);

}
