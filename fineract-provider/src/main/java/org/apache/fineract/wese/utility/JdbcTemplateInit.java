package org.apache.fineract.wese.utility ;

import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcTemplateInit {


    public static JdbcTemplate getJdbcTemplate(String tenantIdentifier){

    	String database = "jdbc:mysql://localhost:3306/";
        StringBuilder stringBuilder = new StringBuilder(database);
        stringBuilder.append(tenantIdentifier);
        String url = stringBuilder.toString();

        JdbcConnectionProperties jdbcConnectionProperties = new JdbcConnectionProperties(url);
        JdbcInit jdbcInit = new JdbcInit(jdbcConnectionProperties);
        JdbcTemplate jdbcTemplate = jdbcInit.getJdbcTemplate();

        return jdbcTemplate ;

    }


}
