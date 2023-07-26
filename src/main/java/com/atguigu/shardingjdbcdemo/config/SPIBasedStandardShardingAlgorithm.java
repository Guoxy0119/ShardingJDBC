package com.atguigu.shardingjdbcdemo.config;

import com.google.common.collect.Range;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 这里类的泛型是 Long 的原因就是官方文档中的分表列是订单号，订单号类型就是Long，
 * 就是说这个算法你要应用到数据表的哪一个字段，那么这个泛型的类型就是这个字段的类型。
 * <p>
 * 该算法不仅仅是可以用在路由表，也可以用在路由数据库。
 * 当它作为数据库分片算法的时候，方法中的第一个参数是可用的数据源名称列表。
 * 当它作为数据表分表算法的时候第一个参数是可用的数据表列表。
 */
@Slf4j
@Getter
public class SPIBasedStandardShardingAlgorithm implements StandardShardingAlgorithm<Date> {

    //线程共用
    private final ThreadLocal<SimpleDateFormat> formatThreadLocal = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyyMMdd"));


    private Properties props;

    //假设这是查询时 获取数据的id最小值
    private Date tableLowerDate;

    /**
     * 精确路由算法
     *
     * @param availableTargetNames 可用的表列表（配置文件中配置的 actual-data-nodes会被解析成 列表被传递过来）
     * @param shardingValue        精确的值
     * @return 结果表
     */
    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Date> shardingValue) {
        Date value = shardingValue.getValue();
        //根据精确值获取路由表
        String actuallyTableName = shardingValue.getLogicTableName() + shardingSuffix(value);
        if (availableTargetNames.contains(actuallyTableName)) {
            return actuallyTableName;
        }
        return null;
    }


    /**
     * 范围路由算法
     *
     * @param availableTargetNames 可用的表列表（配置文件中配置的 actual-data-nodes会被解析成 列表被传递过来）
     * @param shardingValue        值范围
     * @return 路由后的结果表
     */
    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, RangeShardingValue<Date> shardingValue) {
        Date rangeLowerDate;
        Date rangeUpperDate;
        Range<Date> valueRange = shardingValue.getValueRange();

        //获取范围查询的最小值，如果条件中没有最小值，设置为tableLowerDate
        if (valueRange.hasLowerBound()) {
            rangeLowerDate = valueRange.lowerEndpoint();
        } else {
            rangeLowerDate = tableLowerDate;
        }

        //获取范围查询的最大值，如果没有设置，默认取当前时间。
        if (valueRange.hasUpperBound()) {
            rangeUpperDate = valueRange.upperEndpoint();
        } else {
            rangeUpperDate = new Date();
        }


        List<String> tableNames = new ArrayList<>();
        while (rangeLowerDate.before(rangeUpperDate)) {
            String actuallyTableName = shardingValue.getLogicTableName() + shardingSuffix(rangeLowerDate);
            if (availableTargetNames.contains(actuallyTableName)) {
                tableNames.add(actuallyTableName);
            }
            rangeLowerDate = DateUtils.addDays(rangeLowerDate, 1);
        }

        return tableNames;
    }

    /**
     * SPI方式的 SPI名称，配置文件中配置的时候需要用到
     */
    @Override
    public String getType() {
        return "SPI_HIS_DATA";
    }


    /**
     * 在配置文件中配置算法的时候会配置 props 参数，框架会将props中的配置放在 properties 参数中，并且初始化算法的时候被调用
     *
     * @param properties
     */
    @Override
    public void init(Properties properties) {
        this.props = properties;

        String propValue = properties.getProperty("auto-create-table-lower");
        try {
            this.tableLowerDate = formatThreadLocal.get().parse(propValue);
        } catch (Exception e) {
            log.error("parse auto-create table lower date failed: {}, use default date 2023-07-25", e.getMessage());
            try {
                this.tableLowerDate = formatThreadLocal.get().parse("20230725");
            } catch (ParseException ignored) {
            }
        }

    }

    /**
     * 获取分片相关属性
     */
    @Override
    public Properties getProps() {
        return props;
    }

    /**
     * sharding 表后缀
     */
    private String shardingSuffix(Date shardingValue) {
        return "_" + formatThreadLocal.get().format(shardingValue);
    }

}
