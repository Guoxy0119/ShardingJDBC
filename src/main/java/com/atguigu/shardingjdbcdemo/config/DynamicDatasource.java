package com.atguigu.shardingjdbcdemo.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Objects;

/**
 * Spring boot提供了AbstractRoutingDataSource 根据用户定义的规则选择当前的数据源，
 * 这样我们可以在执行查询之前，设置使用的数据源。
 * 实现可动态路由的数据源，在每次数据库查询操作前执行
 * 抽象方法 determineCurrentLookupKey() 决定使用哪个数据源
 * @ClassName DynamicDatasource
 * @Description TODO
 * @Author YangJingBo
 * @Date 2023/3/24 14:26
 */
public class DynamicDatasource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return DynamicDatasourceHandler.getDatasource();
    }
    @Override
    protected DataSource determineTargetDataSource() {
        Object o = determineCurrentLookupKey();
        if (Objects.nonNull(o) && DynamicDatasourceUtil.DATASOURCES.containsKey(o)) {
            return DynamicDatasourceUtil.DATASOURCES.get(o);
        }
        throw new RuntimeException("数据源不存在");
    }
}
