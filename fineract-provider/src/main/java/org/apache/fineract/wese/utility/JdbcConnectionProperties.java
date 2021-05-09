package org.apache.fineract.wese.utility;

import org.apache.fineract.wese.helper.Constants;

import java.util.Properties ;

import java.sql.Driver;

public class JdbcConnectionProperties{

    private String driverClassName ;
    private String url ;
    private String password ;
    private String username ;
    private Properties properties ;


    public Properties getProperties() {
        return properties;
    }

    public JdbcConnectionProperties(String url){

        this.username = Constants.defaultUsername ;
        this.password = Constants.defaultPassword ;
        this.driverClassName = Constants.defaultDriverClassName ;
        this.url = url ;

        init();
    }

    public JdbcConnectionProperties(String driverClassName , String url , String username , String password){

        this.driverClassName = driverClassName ;
        this.url = url ;
        this.username = username ;
        this.password = password ;

        init();
        
    }
    public void init(){

        this.properties = new Properties();
        this.properties.setProperty("url" ,url);
        this.properties.setProperty("driverClassName" ,driverClassName);
        this.properties.setProperty("username" ,username);
        this.properties.setProperty("password" ,password);


    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }
}
