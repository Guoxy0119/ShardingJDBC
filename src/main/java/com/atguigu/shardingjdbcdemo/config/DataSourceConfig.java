package com.atguigu.shardingjdbcdemo.config;

import lombok.Data;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.shardingsphere.driver.api.ShardingSphereDataSourceFactory;
import org.apache.shardingsphere.infra.config.algorithm.AlgorithmConfiguration;
import org.apache.shardingsphere.sharding.api.config.ShardingRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.rule.ShardingTableRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.strategy.keygen.KeyGenerateStrategyConfiguration;
import org.apache.shardingsphere.sharding.api.config.strategy.sharding.StandardShardingStrategyConfiguration;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;
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

    /**
     * 在spring的bean的生命周期中，实例化->生成对象->属性填充后会进行afterPropertiesSet方法，这个方法可以用在一些特殊情况中，
     * 也就是某个对象的某个属性需要经过外界得到，比如说查询数据库等方式，这时候可以用到spring的该特性，只需要实现InitializingBean即可
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        dsMap = ds.stream().collect(Collectors.toConcurrentMap(DynamicDatasourceProperty::getKey, x -> x));
    }

    @Primary
    @Bean("dataSource")
    public DataSource dataSource() throws SQLException {
        for (DynamicDatasourceProperty dp : ds) {
            DynamicDatasourceUtil.createDatasource(dp);
        }
//        DynamicDatasource dynamicDatasource = new DynamicDatasource();
//        dynamicDatasource.setDefaultTargetDataSource(DynamicDatasourceUtil.DATASOURCES.get(DynamicDatasourceUtil.DB1));
//        dynamicDatasource.setTargetDataSources(new HashMap<>());

        //打印sql语句  也可能写为 sql.show
        Properties properties = new Properties();
        properties.setProperty("sql-show", Boolean.TRUE.toString());

        DataSource dataSource = ShardingSphereDataSourceFactory.createDataSource(DynamicDatasourceUtil.DATASOURCES, Arrays.asList(createShardingRuleConfiguration()), properties);

        return dataSource;
    }

    /**
     * 创建分片规则
     * 1. 指定逻辑表
     * 2. 配置实际节点
     * 3. 指定主键生成策略
     * 4. 数据表分片策略
     * 5. 数据库分片策略
     */
    private ShardingRuleConfiguration createShardingRuleConfiguration() {

        /*ShardingRuleConfiguration result = new ShardingRuleConfiguration();

        //表分片规则
        result.getTables().add(shardingJdbcTableRuleConfig());
//        result.getTables().add(getOrderItemTableRuleConfiguration());
        //绑定表
//        result.getBindingTableGroups().add(new ShardingTableReferenceRuleConfiguration("foo", "t_order, t_order_item"));
        //广播表
        result.setBroadcastTables(Collections.singletonList("t_udict"));


        //默认分库策略
        result.setDefaultDatabaseShardingStrategy(new StandardShardingStrategyConfiguration("user_id", "inline"));
        //默认分表策略
        result.setDefaultTableShardingStrategy(new StandardShardingStrategyConfiguration("user_id", "inline"));

        //数据分片策略
        Properties dbProps = new Properties();
        dbProps.setProperty("algorithm-expression", "db$->{user_id % 2}");
        result.getShardingAlgorithms().put("dbShardingAlgorithm", new AlgorithmConfiguration("INLINE", dbProps));

        Properties tbProps = new Properties();
        tbProps.setProperty("algorithm-expression", "t_user$->{user_id % 2}");
        result.getShardingAlgorithms().put("tableShardingAlgorithm", new AlgorithmConfiguration("INLINE", tbProps));
        //自增列生成算法名称和配置

        Properties keyProps = new Properties();
        keyProps.setProperty("SNOWFLAKE", "SNOWFLAKE");
        result.getKeyGenerators().put("SNOWFLAKE", new AlgorithmConfiguration("SNOWFLAKE", keyProps));
        result.setDefaultKeyGenerateStrategy(new KeyGenerateStrategyConfiguration("user_id", "SNOWFLAKE"));
*/

        ShardingRuleConfiguration result = new ShardingRuleConfiguration();
        //表规则
        result.getTables().add(getUserTableRuleConfiguration());
        result.getTables().add(getHistoryTableRuleConfiguration());

        //绑定表
//        result.getBindingTableGroups().add("t_order, t_order_item");

        //广播表
        result.getBroadcastTables().add("t_order");

        //默认策略
        Properties dbProps = new Properties();
        dbProps.setProperty("algorithm-expression", "db${user_id % 2}");
        result.getShardingAlgorithms().put("defaultDb", new AlgorithmConfiguration("INLINE", dbProps));
        result.setDefaultDatabaseShardingStrategy(new StandardShardingStrategyConfiguration("user_id", "defaultDb"));

        Properties tbProps = new Properties();
        tbProps.setProperty("algorithm-expression", "t_user_${user_id % 2}");
        result.getShardingAlgorithms().put("defaultTable", new AlgorithmConfiguration("INLINE", tbProps));
        result.setDefaultTableShardingStrategy(new StandardShardingStrategyConfiguration("user_id", "defaultTable"));


        //自定义分片策略
        Properties hisProps = new Properties();
        hisProps.setProperty("auto-create-table-lower", "20220725");
        result.getShardingAlgorithms().put("spi_his_data", new AlgorithmConfiguration("SPI_HIS_DATA", hisProps));


        //主键生成策略
        Properties keyProps = new Properties();
        keyProps.setProperty("key-generator.type","SNOWFLAKE");
        result.getKeyGenerators().put("snowflake", new AlgorithmConfiguration("SNOWFLAKE", keyProps));

        return result;
    }

    private ShardingTableRuleConfiguration getUserTableRuleConfiguration() {
        ShardingTableRuleConfiguration result = new ShardingTableRuleConfiguration("t_user", "db${0..1}.t_user_${[0, 1]}");
        result.setKeyGenerateStrategy(new KeyGenerateStrategyConfiguration("user_id", "snowflake"));
//        result.setDatabaseShardingStrategy();
//        result.setTableShardingStrategy();
        return result;
    }

    private ShardingTableRuleConfiguration getHistoryTableRuleConfiguration() {
        ShardingTableRuleConfiguration result = new ShardingTableRuleConfiguration("t_history", "db${0..1}.t_history_202307${[23, 24]}");
        result.setKeyGenerateStrategy(new KeyGenerateStrategyConfiguration("id", "snowflake"));
        result.setTableShardingStrategy(new StandardShardingStrategyConfiguration("crt_time", "spi_his_data"));
        return result;
    }


    /**
     * 读写分离
     */
//    private ReadwriteSplittingRuleConfiguration createReadwriteSplittingRuleConfiguration() {
//
//        /*
//         * 读写分离规则配置
//         * name 读写分离数据源名称
//         * autoAwareDataSourceName 自动发现数据源名称(与数据库发现配合使用)
//         * writeDataSourceName  写库数据源名称
//         * readDataSourceNames  读库数据源名称列表
//         * loadBalancerName  读库负载均衡算法名称
//         * */
//        List<String> readDataSourceNames1 = new ArrayList<>();
//        readDataSourceNames1.add("write_ds0_read0");
//        readDataSourceNames1.add("write_ds0_read1");
//        ReadwriteSplittingDataSourceRuleConfiguration dataSourceConfiguration1 = new ReadwriteSplittingDataSourceRuleConfiguration(
//                "ds_0", new StaticReadwriteSplittingStrategyConfiguration("write_ds1_read0",
//                Arrays.asList("write_ds1_read1")),null,"roundRobin");
//        List<String> readDataSourceNames2 = new ArrayList<>();
//        readDataSourceNames1.add("write_ds1_read0");
//        readDataSourceNames1.add("write_ds1_read1");
//        ReadwriteSplittingDataSourceRuleConfiguration dataSourceConfiguration2 = new ReadwriteSplittingDataSourceRuleConfiguration(
//                "ds_1", null,null, "roundRobin");
//
//        // 负载均衡算法  轮询算法: ROUND_ROBIN   随机访问算法: RANDOM   权重访问算法: WEIGHT
//        Map<String, AlgorithmConfiguration> loadBalanceMaps = new HashMap<>(1);
//        loadBalanceMaps.put("roundRobin", new AlgorithmConfiguration("ROUND_ROBIN", new Properties()));
//
//        ReadwriteSplittingRuleConfiguration readWriteSplittingyRuleConfiguration = new ReadwriteSplittingRuleConfiguration(Arrays.asList(dataSourceConfiguration1, dataSourceConfiguration2), loadBalanceMaps);
//
//        return readWriteSplittingyRuleConfiguration;
//    }

    /**
     * 表分片规则配置   5.x
     */
    public ShardingTableRuleConfiguration shardingJdbcTableRuleConfig() {
        //1. 逻辑表
        String logicTable = "t_user";

        //2. 物理表
        String actualTable = "db$->{1..2}.t_user_$->{1..2}";
        ShardingTableRuleConfiguration tableRuleConfiguration = new ShardingTableRuleConfiguration(logicTable, actualTable);

        //3. 指定主键生成策略
        tableRuleConfiguration.setKeyGenerateStrategy(new KeyGenerateStrategyConfiguration("user_id", "SNOWFLAKE"));

        //4. 数据表分片策略
        tableRuleConfiguration.setTableShardingStrategy(new StandardShardingStrategyConfiguration("user_id", "tbShardingAlgorithm"));

        //5. 数据库分片策略
        tableRuleConfiguration.setDatabaseShardingStrategy(new StandardShardingStrategyConfiguration("user_id", "dbShardingAlgorithm"));

        return tableRuleConfiguration;
    }


    /**
     * 表分片规则配置   4.x
     * 1. 指定逻辑表
     * 2. 配置实际节点
     * 3. 指定主键生成策略
     * 4. 数据表分片策略
     * 5. 数据库分片策略
     */
//    public TableRuleConfiguration shardingJdbcTableRuleConfig(){
//
//        //1. 逻辑表
//        String logicTable = "t_user";
//        //2. 物理表
//        String actualTable = "ds$->{0..1}.t_user_$->{0..1}";
//        ShardingTableRuleConfiguration TableRuleConfiguration = new ShardingTableRuleConfiguration(logicTable, actualTable);
//        TableRuleConfiguration tableRuleConfiguration = new TableRuleConfiguration(logicTable, actualTable);
//        //3. 指定主键生成策略
//        tableRuleConfiguration.setKeyGeneratorConfig(new KeyGeneratorConfiguration("SNOWFLAKE","user_id"));
//
//
//        //4. 设置分表策略
//        //inline模式
//        InlineShardingStrategyConfiguration tableStrategy = new InlineShardingStrategyConfiguration("user_id", "t_user_$->{user_id % 2}");
//        tableRuleConfiguration.setTableShardingStrategyConfig(tableStrategy);
//
//        //5. 设置分库策略
//        //inline模式
//        InlineShardingStrategyConfiguration dbStrategy = new InlineShardingStrategyConfiguration("user_id", "ds${user_id % 2}");
//        tableRuleConfiguration.setDatabaseShardingStrategyConfig(dbStrategy);
//
//        //自定义模式
////        PreciseShardingAlgorithm
//
////        StandardShardingStrategyConfiguration shardingStrategy = new StandardShardingStrategyConfiguration("user_id", );
//
//        return tableRuleConfiguration;
//    }
    @ConfigurationProperties(prefix = "mybatis.configuration")
    public org.apache.ibatis.session.Configuration mybatisConfiguration() {
        return new org.apache.ibatis.session.Configuration();
    }

    @Bean("SqlSessionFactory")
    public SqlSessionFactory db1SqlSessionFactory(@Qualifier("dataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        // mapper的xml形式文件位置必须要配置，不然将报错：no statement （这种错误也可能是mapper的xml中，namespace与项目的路径不一致导致）
//        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:com/goocidata/supplyx/supplyxlowcode/mapper/**/*.xml"));
        // 手动设置配置,mybatis会自动根据StringJSONTypeHandler进行类型转换，改为这种方式需手动添加
        org.apache.ibatis.session.Configuration configuration = mybatisConfiguration();
//        configuration.getTypeHandlerRegistry().register(StringJSONTypeHandler.class);
//        configuration.getTypeHandlerRegistry().register(StringListTypeHandler.class);
        //可能多数据源导致转换驼峰不生效
        configuration.setMapUnderscoreToCamelCase(true);
        bean.setConfiguration(configuration);
        return bean.getObject();
    }

    @Bean("dbSqlSessionTemplate")
    public SqlSessionTemplate db1SqlSessionTemplate(@Qualifier("SqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }


}
