package com.yun.hello.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Spring Security 配置类.
 * 
 * 想要开启Spring方法级安全，你需要在已经添加了@Configuration注解的类上再添加@EnableGlobalMethodSecurity注解
 * @PreAuthorize 注解适合进入方法前的权限验证
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // 启用方法安全设置
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	/**
	 * remember me key
	 */
	private static final String KEY = "yubuyun";
	
	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
    private PasswordEncoder passwordEncoder;

	/**
	 * 加密bean
	 */
	@Bean  
    public PasswordEncoder passwordEncoder() {  
        return new BCryptPasswordEncoder();   // 使用 BCrypt 加密
    }  
	
	@Bean
    public AuthenticationProvider authenticationProvider() {  
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder); // 设置密码加密方式
        return authenticationProvider;  
    }  
 
	/**
	 * 自定义配置
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/css/**", "/js/**", "/fonts/**", "/index","/weather").permitAll() // 都可以访问
				.antMatchers("/admins/**").hasRole("ADMIN") // 需要相应的角色才能访问
				.and()
				.formLogin()   //基于 Form 表单登录验证
				.loginPage("/login").failureUrl("/login-error") // 自定义登录界面
				.and().rememberMe().key(KEY) // 启用 remember me
				.and().exceptionHandling().accessDeniedPage("/403");  // 处理异常，拒绝访问就重定向到 403 页面
	}
	
	/**
	 * 认证信息管理
	 * @param auth
	 * @throws Exception
	 */
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService);
		auth.authenticationProvider(authenticationProvider());
	}
}
