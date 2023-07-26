package com.atguigu.shardingjdbcdemo.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

@Data
//@TableName(value = "t_user")  //指定对应表，不然找不到 但是要对t_user水平分表呢？
@Table(name = "t_user")
public class User {

    @Column(name = "user_id")
//    @Id
    private Long userId;

    @Column(name = "name")
    private String username;
//    private String ustatus;

    @Column(name = "crt_time")
    private Date crtTime;

}
