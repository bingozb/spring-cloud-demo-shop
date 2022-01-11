package com.bhnote.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author bingo
 * @date 2022/1/6
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * 分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    /**
     * 批量插入
     */
    @Bean
    public CustomSqlInjector myBatisPlusSqlInjector() {
        return new CustomSqlInjector();
    }

    static class CustomSqlInjector extends DefaultSqlInjector {

        @Override
        public List<AbstractMethod> getMethodList(Class<?> mapperClass) {
            List<AbstractMethod> methodList = super.getMethodList(mapperClass);
            methodList.add(new InsertBatchSomeColumn());
            return methodList;
        }
    }
}
