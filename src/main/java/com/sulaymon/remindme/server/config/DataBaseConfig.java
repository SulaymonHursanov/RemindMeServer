package com.sulaymon.remindme.server.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Sulaymon on 10.07.2017.
 */
@Configuration
@EnableJpaRepositories("com.sulaymon.remindme.server.repository")
@EnableTransactionManagement
@PropertySource("classpath:db.properties")
@ComponentScan("com.sulaymon.remindme.server")
public class DataBaseConfig {

    @Resource
    private Environment env;

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(){
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan(env.getRequiredProperty("db.entity.package"));
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        em.setJpaProperties(getHibernateProperties());
        return em;
    }

    public Properties getHibernateProperties(){
        try {
            Properties pr = new Properties();
            InputStream is = getClass().getClassLoader().getResourceAsStream("hibernate.properties");
            pr.load(is);
            return pr;
        } catch (IOException e) {
            throw new IllegalArgumentException("Can't find 'hibernate.properties' in classpath");
        }
    }

    @Bean
    public PlatformTransactionManager transactionManager(){
        JpaTransactionManager manager = new JpaTransactionManager();
        manager.setEntityManagerFactory(entityManagerFactory().getObject());
        return manager;
    }

    @Bean
    public DataSource dataSource (){
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(env.getRequiredProperty("db.url"));
        ds.setDriverClassName(env.getRequiredProperty("db.driver"));
        ds.setUsername(env.getRequiredProperty("db.username"));
        ds.setPassword(env.getRequiredProperty("db.password"));

        ds.setInitialSize(Integer.parseInt(env.getRequiredProperty("db.initialSize")));
        ds.setMinIdle(Integer.parseInt(env.getRequiredProperty("db.minIdle")));
        ds.setMaxIdle(Integer.parseInt(env.getRequiredProperty("db.maxIdle")));
        ds.setTimeBetweenEvictionRunsMillis(Long.parseLong(env.getRequiredProperty("db.timeBetweenEvictionRunsMillis")));
        ds.setTestOnBorrow(Boolean.parseBoolean(env.getRequiredProperty("db.testOnBorrow")));
        ds.setMinEvictableIdleTimeMillis(Long.parseLong(env.getRequiredProperty("db.minEvictableIdleTimeMillis")));
        ds.setValidationQuery(env.getRequiredProperty("db.validationQuery"));
        return ds;
    }
}
