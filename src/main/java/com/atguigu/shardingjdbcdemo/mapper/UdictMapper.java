package com.atguigu.shardingjdbcdemo.mapper;

import com.atguigu.shardingjdbcdemo.entity.Udict;
import com.atguigu.shardingjdbcdemo.util.MyMapper;

public interface UdictMapper extends MyMapper<Udict> {

    Udict queryById(String id);


}
