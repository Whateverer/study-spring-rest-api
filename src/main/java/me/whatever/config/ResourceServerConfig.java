package me.whatever.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    // 리소스서버는 우리가 만든 Oauth서버와 같이 연동이 된다.
    // 리소스에 접근을 할 때 Oauth에서 설정한 토큰 정보를 검증(유효한지 확인)하는 역할
//    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception{
        resources.resourceId("event");
    }

    public void configure(HttpSecurity http) throws Exception {
        http
                .anonymous()
                    .and()
                .authorizeRequests()
                .mvcMatchers(HttpMethod.GET, "/api/**")
                    .permitAll()
                .anyRequest()
                    .authenticated()
                .and()
                    .exceptionHandling()
                .accessDeniedHandler(new OAuth2AccessDeniedHandler()); // 접근권한이 없는 경우 403에러를 내보내는 핸들러
    }
}
