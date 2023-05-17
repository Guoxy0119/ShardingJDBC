package com.atguigu.shardingjdbcdemo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "t_user")  //指定对应表，不然找不到 但是要对t_user水平分表呢？
public class User {

    private Long userId;
    private String username;
    private String ustatus;

}
