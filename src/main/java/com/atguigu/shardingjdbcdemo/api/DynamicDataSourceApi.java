package com.atguigu.shardingjdbcdemo.api;

import com.atguigu.shardingjdbcdemo.entity.History;
import com.atguigu.shardingjdbcdemo.entity.Udict;
import com.atguigu.shardingjdbcdemo.entity.User;
import com.atguigu.shardingjdbcdemo.mapper.HistoryMapper;
import com.atguigu.shardingjdbcdemo.mapper.UdictMapper;
import com.atguigu.shardingjdbcdemo.mapper.UserMapper;
import org.apache.shardingsphere.transaction.annotation.ShardingSphereTransactionType;
import org.apache.shardingsphere.transaction.core.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 多数据源测试
 */

@RestController
@RequestMapping("dynamicDataSource")
public class DynamicDataSourceApi {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UdictMapper udictMapper;
    @Autowired
    private HistoryMapper historyMapper;


    @GetMapping("/query")
    public User query(Long userId) {

        User user1 = userMapper.queryById(userId);

//        DynamicDatasourceHandler.setDatasource("db4");
//
//        User user2 = userMapper.queryById(userId);
//
//        DynamicDatasourceHandler.setDatasource("db1");
//
//        User user3 = userMapper.queryById(userId);

        System.out.println("结果：" + user1);
//        System.out.println("结果：" + user2);
//        System.out.println("结果：" + user3);

        return user1;

    }

    @PostMapping("/insert")
    @Transactional(rollbackFor = Exception.class)
    @ShardingSphereTransactionType(TransactionType.XA)
    public int insert(@RequestBody User user) {

        int i = userMapper.insert(user);

        int j = 1/0;

        User user6 = new User();
        user6.setUserId(6L);
        user6.setUsername("test6");
        userMapper.insert(user6);

        System.out.println("结束");

        return i;

    }


    @GetMapping("/queryAll")
    public List<User> queryAll() {

        List<User> users = userMapper.selectAll();
        System.out.println("结果：" + users);

        return users;
    }


    @GetMapping("/queryCommon")
    public List<Udict> queryCommon() {

        List<Udict> udicts = udictMapper.selectAll();
        System.out.println("结果：" + udicts);

        return udicts;
    }


    @GetMapping("/associatedQuery")
    public User associatedQuery(Long userId) {

        User user = userMapper.selectByOrder(userId);

        System.out.println("结果：" + user);

        return user;
    }


    @PostMapping("/history/query")
    public List<History> historyQuery(@RequestBody History data) {

        List<Long> ids = data.getIds();
        List<History> histories = historyMapper.queryByIds(ids);
        System.out.println("结果1：" + histories);

        List<History> page = historyMapper.page(data);
        System.out.println("结果2：" + page);

        return histories;
    }

    @GetMapping("/history/preciseQuery")
    public History hitsoryPreciseQuery(Long id) {

        History history = historyMapper.queryById(id);

        return history;

    }


}
