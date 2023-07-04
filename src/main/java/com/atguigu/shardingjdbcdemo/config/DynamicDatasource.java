package com.atguigu.shardingjdbcdemo.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Objects;

/**
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
