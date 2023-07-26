package com.atguigu.shardingjdbcdemo.entity;


import lombok.Data;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

@Data
@Table(name = "t_history")
public class History {

    private Long id;
    private String memo;
    private Date crtTime;

    @Transient
    private List<Long> ids;

    @Transient
    private Date startTime;
    @Transient
    private Date endTime;

}
