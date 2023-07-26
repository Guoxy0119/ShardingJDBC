package com.atguigu.shardingjdbcdemo.mapper;

import com.atguigu.shardingjdbcdemo.entity.History;
import com.atguigu.shardingjdbcdemo.util.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HistoryMapper extends MyMapper<History> {

    History queryById(Long id);
    List<History> queryByIds(@Param("ids") List<Long> id);


    List<History> page(@Param("data") History data);


}
