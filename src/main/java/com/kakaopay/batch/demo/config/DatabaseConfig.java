package com.kakaopay.batch.demo.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:db.properties")
public class DatabaseConfig {

    @Value("${datasource.driver-class-name}")
    private String driverClassName;

    @Value("${datasource.user-name}")
    private String username;

    @Value("${datasource.password}")
    private String password;

    @Value("${datasource.url}")
    private String url;

    @Value("${datasource.maximum-pool-size}")
    private int maximumPoolSize;

    @Value("${datasource.minimum-idle}")
    private int minimumIdle;

    @Bean
    @Qualifier("batchDemoDataSource")
    public HikariDataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setConnectionTestQuery("SELECT 1");
        dataSource.setMaximumPoolSize(maximumPoolSize);
        dataSource.setMinimumIdle(minimumIdle);

        return dataSource;
    }

}
