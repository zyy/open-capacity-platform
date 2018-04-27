package com.open.capacity.client.oauth2.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

  
/** 
* @author 作者 owen E-mail: 624191343@qq.com
* @version 创建时间：2018年4月5日 下午7:52:21 
* 类说明 
*/
@Component
@Configuration
public class SecurityHandlerConfig {

	@Resource 
	private ObjectMapper objectMapper ; //springmvc启动时自动装配json处理类
 
    
	/**
	 * 未登录，返回401
	 * 
	 * @return
	 */
	@Bean
	public AuthenticationEntryPoint authenticationEntryPoint() {
		return new AuthenticationEntryPoint() {

			@Override
			public void commence(HttpServletRequest request, HttpServletResponse response,
					AuthenticationException authException) throws IOException, ServletException {
				
				Map<String ,String > rsp =new HashMap<>();  
				rsp.put("resp_code", HttpStatus.UNAUTHORIZED.value() + "") ;
                rsp.put("resp_msg", "请先登录") ;
                
                response.setContentType("application/json;charset=UTF-8");
    			response.getWriter().write(objectMapper.writeValueAsString(rsp));
    			response.getWriter().flush();
    			response.getWriter().close();
                
			}
		};
	}
	
	 

}