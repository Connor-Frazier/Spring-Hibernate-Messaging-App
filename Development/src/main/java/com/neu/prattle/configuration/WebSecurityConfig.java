package com.neu.prattle.configuration;

import com.neu.prattle.model.JWTAuthorizationFilter;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration of endpoints requiring JWT tokens
 */
@EnableWebSecurity
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable()
            .addFilterAfter(new JWTAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
            .authorizeRequests().antMatchers("/rest/user/securelogin",
            "/rest/government/login", "/rest/user/create", "/chat/*", "/", "/*.js", "/*.html")
            .permitAll()
            .anyRequest().authenticated();
    http.cors();
  }
}
