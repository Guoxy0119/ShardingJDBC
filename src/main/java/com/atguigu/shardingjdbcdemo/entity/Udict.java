package com.atguigu.shardingjdbcdemo.entity;

import lombok.Data;

import javax.persistence.Table;

@Data
@Table(name = "t_udict")
public class Udict {

    private Long dictid;
    private String ustatus;
    private String uvalue;

}
