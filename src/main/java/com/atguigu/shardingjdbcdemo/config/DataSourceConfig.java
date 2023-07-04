package com.atguigu.shardingjdbcdemo.config;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName DataSourceConfig
 * @Description TODO
 * @Author YangJingBo
 * @Date 2023/3/24 16:07
 */
@Data
@Component
@ConditionalOnProperty(name = "spring.dynamicdatasource.enabled", havingValue = "true")
@ConfigurationProperties(prefix = "spring.dynamicdatasource")
public class DataSourceConfig implements InitializingBean {
    private List<DynamicDatasourceProperty> ds = new ArrayList<>();

    private Map<String, DynamicDatasourceProperty> dsMap;

    @Override
    public void afterPropertiesSet() throws Exception {
        dsMap = ds.stream().collect(Collectors.toConcurrentMap(DynamicDatasourceProperty::getKey, x -> x));
    }

    @Primary
    @Bean("dataSource")
    public DataSource dataSource() {
        for (DynamicDatasourceProperty dp : ds) {
            DynamicDatasourceUtil.createDatasource(dp);
        }
        DynamicDatasource dynamicDatasource = new DynamicDatasource();
        dynamicDatasource.setDefaultTargetDataSource(DynamicDatasourceUtil.DATASOURCES.get(DynamicDatasourceUtil.DB1));
        dynamicDatasource.setTargetDataSources(new HashMap<>());
        return dynamicDatasource;
    }

    @ConfigurationProperties(prefix = "mybatis.configuration")
    public org.apache.ibatis.session.Configuration mybatisConfiguration() {
        return new org.apache.ibatis.session.Configuration();
    }
//    @Bean("SqlSessionFactory")
//    public SqlSessionFactory db1SqlSessionFactory(@Qualifier("dataSource") DataSource dataSource) throws Exception {
//        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
//        bean.setDataSource(dataSource);
//        // mapper的xml形式文件位置必须要配置，不然将报错：no statement （这种错误也可能是mapper的xml中，namespace与项目的路径不一致导致）
////        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:com/goocidata/supplyx/supplyxlowcode/mapper/**/*.xml"));
//        // 手动设置配置,mybatis会自动根据StringJSONTypeHandler进行类型转换，改为这种方式需手动添加
//        org.apache.ibatis.session.Configuration configuration = mybatisConfiguration();
//        configuration.getTypeHandlerRegistry().register(StringJSONTypeHandler.class);
//        configuration.getTypeHandlerRegistry().register(StringListTypeHandler.class);
//        //可能多数据源导致转换驼峰不生效
//        configuration.setMapUnderscoreToCamelCase(true);
//        bean.setConfiguration(configuration);
//        return bean.getObject();
//    }
//    @Bean("dbSqlSessionTemplate")
//    public SqlSessionTemplate db1SqlSessionTemplate(@Qualifier("SqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
//        return new SqlSessionTemplate(sqlSessionFactory);
//    }


}
