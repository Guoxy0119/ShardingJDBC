package com.atguigu.shardingjdbcdemo.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName DynamicDatasourceUtil
 * @Description TODO
 * @Author YangJingBo
 * @Date 2023/3/24 14:46
 */
public class DynamicDatasourceUtil {
    public static final String DB1 = "db1";
    public static final Map<String, DataSource> DATASOURCES = new ConcurrentHashMap<>();
    public static final Logger logger = LoggerFactory.getLogger(DynamicDatasourceUtil.class);
    public static void createDatasource(DynamicDatasourceProperty property) {
        if (DynamicDatasourceUtil.DATASOURCES.containsKey(property.getKey())) {
            return;
        }
        DataSource dataSource = null;
        if (Objects.nonNull(property.getType()) && ("com.zaxxer.hikari.HikariDataSource".equals(property.getType()) || "hikari".equals(property.getType()))) {
            dataSource = createHikariDataSource(property);
        } else if (Objects.nonNull(property.getType()) && ("com.alibaba.druid.pool.DruidDataSource".equals(property.getType()) || "druid".equals(property.getType()))) {
            dataSource = createDruidDataSource(property);
        } else {
            dataSource = createDefaultDataSource(property);
        }
        logger.info("创建数据源: key: {}, type: {}", property.getKey(), property.getType());
        DynamicDatasourceUtil.DATASOURCES.putIfAbsent(property.getKey(), dataSource);
    }

    private static DataSource createDruidDataSource(DynamicDatasourceProperty property) {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(property.getDriverClassName());
        dataSource.setUrl(property.getUrl());
        dataSource.setUsername(property.getUsername());
        dataSource.setPassword(property.getPassword());
        if (Objects.nonNull(property.getMaxActive())) {
            dataSource.setMaxActive(property.getMaxActive());
        }
        if (Objects.nonNull(property.getInitSize())) {
            dataSource.setInitialSize(property.getInitSize());
        }
        if (Objects.nonNull(property.getMinIdle())) {
            dataSource.setMinIdle(property.getMinIdle());
        }
        if (Objects.nonNull(property.getPoolName())) {
            dataSource.setName(property.getPoolName());
        }
        if (Objects.nonNull(property.getMaxWait())) {
            dataSource.setMaxWait(property.getMaxWait());
        }
        if (Objects.nonNull(property.getValidationQuery())) {
            dataSource.setValidationQuery(property.getValidationQuery());
        }
        if (Objects.nonNull(property.getTestOnBorrow())) {
            dataSource.setTestOnBorrow(property.getTestOnBorrow());
        }
        if (Objects.nonNull(property.getRemoveAbandoned())) {
            dataSource.setRemoveAbandoned(property.getRemoveAbandoned());
        }
        if (Objects.nonNull(property.getRemoveAbandonedTimeout())) {
            dataSource.setRemoveAbandonedTimeout(property.getRemoveAbandonedTimeout());
        }

        if (Objects.nonNull(property.getMinEvictableIdleTimeMillis())) {
            dataSource.setMinEvictableIdleTimeMillis(property.getMinEvictableIdleTimeMillis());
        }

        return dataSource;
    }

    private static DataSource createDefaultDataSource(DynamicDatasourceProperty property) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(property.getDriverClassName());
        dataSource.setUrl(property.getUrl());
        dataSource.setUsername(property.getUsername());
        dataSource.setPassword(property.getPassword());
        return dataSource;
    }

    private static HikariDataSource createHikariDataSource(DynamicDatasourceProperty property) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(property.getDriverClassName());
        dataSource.setJdbcUrl(property.getUrl());
        dataSource.setUsername(property.getUsername());
        dataSource.setPassword(property.getPassword());
        if (Objects.nonNull(property.getMaxActive())) {
            dataSource.setMaximumPoolSize(property.getMaxActive());
        }
        if (Objects.nonNull(property.getMinIdle())) {
            dataSource.setMinimumIdle(property.getMinIdle());
        }
        if (Objects.nonNull(property.getPoolName())) {
            dataSource.setPoolName(property.getPoolName());
        }
        if (Objects.nonNull(property.getIdleTimeout())) {
            dataSource.setIdleTimeout(property.getIdleTimeout());
        }
        if (Objects.nonNull(property.getMaxLifetime())) {
            dataSource.setMaxLifetime(property.getMaxLifetime());
        }
        if (Objects.nonNull(property.getConnectionTimeout())) {
            dataSource.setConnectionTimeout(property.getConnectionTimeout());
        }
        return dataSource;
    }
}
