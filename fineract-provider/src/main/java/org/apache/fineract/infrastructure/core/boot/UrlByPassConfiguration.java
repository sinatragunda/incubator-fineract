/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 21 November 2022 at 09:30
 */
package org.apache.fineract.infrastructure.core.boot;
import org.springframework.security.config.annotation.web.configuration.* ;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UrlByPassConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {

        System.err.println("------------------when is this function requested now ? ");

        http.authorizeRequests()
                .antMatchers("/auth/resetpassword/*").permitAll()
                .anyRequest().authenticated();
    }
}
