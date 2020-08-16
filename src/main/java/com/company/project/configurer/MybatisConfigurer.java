package com.company.project.configurer;

import com.github.pagehelper.PageInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import tk.mybatis.spring.annotation.MapperScan;

import javax.sql.DataSource;
import java.util.Properties;

import static com.company.project.common.constant.ProjectConstant.TYPE_ALIASES_PACKAGE;

/**
 * Mybatis & Mapper & PageHelper 配置
 * @author Ray。
 */
@MapperScan("com.company.project.manage.dao")
@Configuration
public class MybatisConfigurer {

    @Bean
    public SqlSessionFactory sqlSessionFactoryBean(DataSource dataSource) throws Exception {
        PackagesSqlSessionFactoryBean sqlSessionFactory = new PackagesSqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSource);
        sqlSessionFactory.setTypeAliasesPackage(TYPE_ALIASES_PACKAGE);

        //配置分页插件，详情请查阅官方文档
        PageInterceptor interceptor = new PageInterceptor();
        Properties properties = new Properties();
        //分页尺寸为0时查询所有纪录不再执行分页
        properties.setProperty("pageSizeZero", "true");
        //页码<=0 查询第一页，页码>=总页数查询最后一页
        properties.setProperty("reasonable", "true");
        //支持通过 Mapper 接口参数来传递分页参数
        properties.setProperty("supportMethodsArguments", "true");
        interceptor.setProperties(properties);

        //添加插件
        //为了防止插件被重复注册，可以在启动类中使用"@SpringBootApplication(exclude = PageHelperAutoConfiguration.class)"排除默认的配置
        sqlSessionFactory.setPlugins(new Interceptor[]{interceptor});

        //添加XML目录
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactory.setMapperLocations(resolver.getResources("classpath:mapper/*.xml"));
        return sqlSessionFactory.getObject();
    }

}

