package com.atguigu.shardingjdbcdemo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.lang.reflect.Method;

/**
 * @ClassName DynamicDatasourceHandler
 * @Description TODO
 * @Author YangJingBo
 * @Date 2023/3/24 14:27
 */
public class DynamicDatasourceHandler {
    private static ThreadLocal<String> threadDatasource = new InheritableThreadLocal<>();
    public static final Logger logger = LoggerFactory.getLogger(DynamicDatasourceHandler.class);
    public static void setDatasource(String dataSource) {
        logger.info("切换数据源:"+dataSource);
        threadDatasource.set(dataSource);
    }
    static {
        setDatasource("db1");
    }
    public static String getDatasource() {
        return threadDatasource.get();
    }

    public static void remove() {
        threadDatasource.remove();
    }

    public static void close(DataSource dataSource) {
        Class<? extends DataSource> clazz = dataSource.getClass();
        try {
            Method close = clazz.getDeclaredMethod("close");
            close.invoke(clazz);
            logger.debug("该数据源已关闭");
        } catch (Exception e) {
            logger.warn("动态数据源关闭错误, 数据源名称 [{}]", dataSource, e);
        }
    }

    public static void switchDataSource(String dataSource){
       // remove();
        setDatasource(dataSource);

    }
}
