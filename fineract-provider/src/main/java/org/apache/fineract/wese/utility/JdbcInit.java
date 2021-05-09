package org.apache.fineract.wese.utility;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.util.Properties;


public class JdbcInit {

    private DriverManagerDataSource driverManagerDataSource ;
    private JdbcConnectionProperties jdbcConnectionProperties ;
    private JdbcTemplate jdbcTemplate ;

    public JdbcInit(JdbcConnectionProperties jdbcConnectionProperties){
        this.jdbcConnectionProperties = jdbcConnectionProperties ;
        init();
    }

    public void init(){

        Properties properties = jdbcConnectionProperties.getProperties();
        this.driverManagerDataSource = new DriverManagerDataSource();
        driverManagerDataSource.setConnectionProperties(properties);
        driverManagerDataSource.setUrl(properties.getProperty("url"));
        driverManagerDataSource.setPassword(properties.getProperty("password"));
        driverManagerDataSource.setUsername(properties.getProperty("username"));
        driverManagerDataSource.setDriverClassName(properties.getProperty("driverClassName"));
        this.jdbcTemplate = new JdbcTemplate(driverManagerDataSource);

    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
}
