package com.open.capacity.common.interceptor;

import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.open.capacity.common.constant.TraceConstant;
import com.open.capacity.common.constant.UaaConstant;
import com.open.capacity.common.util.StringUtils;
import com.open.capacity.common.util.TokenUtil;

import cn.hutool.core.util.StrUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class RestTemplateInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
        HttpServletRequest httpRequest = attributes.getRequest();
        String header = httpRequest.getHeader("Authorization");

        
		String token = StringUtils.isBlank(StringUtils.substringAfter(header, OAuth2AccessToken.BEARER_TYPE+" ")) ? httpRequest.getParameter(OAuth2AccessToken.ACCESS_TOKEN) :  StringUtils.substringAfter(header, OAuth2AccessToken.BEARER_TYPE +" ");
		
		token = StringUtils.isBlank(httpRequest.getHeader(UaaConstant.TOKEN_HEADER)) ? token : httpRequest.getHeader(UaaConstant.TOKEN_HEADER) ;
		
		
		//传递token
        HttpHeaders headers = request.getHeaders();
        headers.add(UaaConstant.TOKEN_HEADER,  token);

       //传递traceId
        String traceId = StrUtil.isNotEmpty(MDC.get(TraceConstant.LOG_TRACE_ID))  ?  MDC.get(TraceConstant.LOG_TRACE_ID) :  MDC.get(TraceConstant.LOG_TRACE_ID) ;
        if (StrUtil.isNotEmpty(traceId)) {
            headers.add(TraceConstant.HTTP_HEADER_TRACE_ID,  traceId);
        }
        
        // 保证请求继续执行
        return execution.execute(request, body);
    }
}