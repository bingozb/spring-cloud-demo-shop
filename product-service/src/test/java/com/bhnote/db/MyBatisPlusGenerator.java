package com.bhnote.db;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

/**
 * @author bingo
 * @date 2022/1/7
 */
public class MyBatisPlusGenerator {

    public static void main(String[] args) {
        //1. 全局配置
        GlobalConfig config = new GlobalConfig();
        config.setAuthor("Bingo")
                // TODO 生成路径，最好使用绝对路径，window路径是不一样的
                .setOutputDir("E:\\MyBatisPlusGenerator\\springcloud-demo-shop\\product-service\\src\\main\\java")
                // 文件覆盖
                .setFileOverride(true)
                // 主键策略 雪花算法需要自己输入
                .setIdType(IdType.INPUT)
                .setDateType(DateType.ONLY_DATE)
                // 设置生成的service接口名字，默认Service是以I开头的
                .setServiceName("%sService")
                // 实体类结尾名称
                .setEntityName("%sDO")
                // 生成基本的resultMap
                .setBaseResultMap(true)
                // 不使用AR模式
                .setActiveRecord(false)
                // 生成基本的SQL片段
                .setBaseColumnList(true);

        //2. 数据源配置
        DataSourceConfig dsConfig = new DataSourceConfig();
        // 设置数据库类型
        dsConfig.setDbType(DbType.MYSQL)
                .setDriverName("com.mysql.cj.jdbc.Driver")
                // TODO 数据库配置
                .setUrl("jdbc:mysql://192.168.2.188:3306/shop_product?useSSL=false")
                .setUsername("root")
                .setPassword("123456");

        //3. 策略配置globalConfiguration中
        StrategyConfig stConfig = new StrategyConfig();
        //全局大写命名
        stConfig.setCapitalMode(true)
                // 数据库表映射到实体的命名策略
                .setNaming(NamingStrategy.underline_to_camel)
                //使用lombok
                .setEntityLombokModel(true)
                //使用restController注解
                .setRestControllerStyle(true)
                // TODO 生成的表, 支持多表一起生成，以数组形式填写
                .setInclude("product", "product_lock");

        //4. 包名策略配置
        PackageConfig pkConfig = new PackageConfig();
        pkConfig.setParent("com.bhnote")
                .setMapper("mapper")
                .setService("service")
                .setController("controller")
                .setEntity("model")
                .setXml("mapper");

        //5. 整合配置
        AutoGenerator ag = new AutoGenerator();
        ag.setGlobalConfig(config)
                .setDataSource(dsConfig)
                .setStrategy(stConfig)
                .setPackageInfo(pkConfig);

        //6. 执行操作
        ag.execute();
    }
}