package com.atguigu.shardingjdbcdemo.api;

import com.atguigu.shardingjdbcdemo.config.DynamicDatasourceHandler;
import com.atguigu.shardingjdbcdemo.entity.User;
import com.atguigu.shardingjdbcdemo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 多数据源测试
 */

@RestController
@RequestMapping("dynamicDataSource")
public class DynamicDataSourceApi {

    @Autowired
    private UserMapper userMapper;


    @GetMapping("/query")
    public void query(Long userId) {

        User user1 = userMapper.queryById(userId);

        DynamicDatasourceHandler.setDatasource("db4");

        User user2 = userMapper.queryById(userId);

        DynamicDatasourceHandler.setDatasource("db1");

        User user3 = userMapper.queryById(userId);

        System.out.println("结果：" + user1);
        System.out.println("结果：" + user2);
        System.out.println("结果：" + user3);

    }


}
