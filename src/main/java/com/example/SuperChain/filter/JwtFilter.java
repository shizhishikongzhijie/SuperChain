package com.example.SuperChain.filter;

import com.example.SuperChain.util.JwtUtil;
import com.example.SuperChain.util.resUtils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * JWT过滤器，拦截 /secure的请求
 *
 * @author shizhishi
 */
@Slf4j
//@WebFilter(filterName = "JwtFilter", urlPatterns = "/*")
@Component
public class JwtFilter implements Filter {

    @Value("${whiteList}")
    private String whiteList;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("JwtFilter init");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        response.setCharacterEncoding("UTF-8");
        //打印header
        // 将 Enumeration 转换为 List
//        List<String> headerNamesList = Collections.list(request.getHeaderNames());
//        for (String headerName : headerNamesList) {
//            String headerValue = request.getHeader(headerName);
//            // 处理 headerName 和 headerValue
//            System.out.println("Header Name: " + headerName + ", Header Value: " + headerValue);
//        }
        //获取 header里的token
        String token = request.getHeader("authorization");
//        System.out.println("token:" + token);
        //获取 header里的linkId
        String linkId = request.getHeader("linkid");
//        System.out.println("LinkId:" + linkId);
        //1.获取资源请求路径
        String uri = request.getRequestURI();
        System.out.println("uri:" + uri);

        String origin = request.getHeader("Origin");
        if ("OPTIONS".equals(request.getMethod())) {
            if(whiteList.equals(origin)){
                response.setHeader("Access-Control-Allow-Origin", origin);
            }
//            response.setHeader("Access-Control-Allow-Origin", "http://localhost:8000");
//            response.setHeader("Access-Control-Allow-Origin", "http://kongzhijie.cn:8000");
            response.setHeader("Access-Control-Allow-Methods", "*");
            response.setHeader("Access-Control-Allow-Headers", "*");
            response.setStatus(HttpServletResponse.SC_OK);
            chain.doFilter(request, response);//必须放行，否则Authorization获取不到
        } else {
            //2.判断是否包含登录相关资源路径,要注意排除掉 css/js/图片等资源
            if (uri.contains("/link")||uri.contains("/open")) {
                log.info("不需要加密的link: " + uri);
            } else {
                log.info("其他请求: " + uri);
                if(!uri.contains("/login")&&!uri.contains("/sendCaptcha")&&!uri.contains("/register")&&!uri.contains("/sendCaptcha")){
                    //去除Bearer
                    if (token == null) {
                        log.info("token 为 null");
                        setCorsHeaders(response, origin);
                        setErrorResponse(response, R.tokenNotFound(),origin);
//                        response.getWriter().write(R.tokenNotFound().toJSONString());
                        return;
                    }else{
                        Map<String, String> userData = JwtUtil.verifyToken(token);
//                        log.info("userData:" + userData);
                        //如果userData里有key="error"，说明token过期了，返回错误信息
                        if (userData.containsKey("error")) {
                            if("TokenExpiredException".equals(userData.get("error"))){
                                log.info("token过期了");
                                setCorsHeaders(response, origin);
                                setErrorResponse(response,R.tokenNotFound(), origin);
//                                response.getWriter().write(R.tokenExpired().toJSONString());
                                //设置重定向，有定向但没传值，会报错
//                                response.sendRedirect("/login");
                            }else{
                                log.info("token解密失败");
                                setCorsHeaders(response, origin);
                                setErrorResponse(response,R.tokenDecodeError(), origin);
//                                response.getWriter().write(R.tokenDecodeError().toJSONString());
                            }
                            return;
                        }
                    }
                }
                String linkKey = (String) redisTemplate.opsForValue().get(linkId);
                //拦截器 拿到用户信息，放到request中
                request.setAttribute("linkKey", linkKey);
                //对请求数据进行解密
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        log.info("JwtFilter destroy");
    }

    private void setErrorResponse(HttpServletResponse response, R errorResponse, String origin) throws IOException {
        response.setContentType("application/x-www-form-urlencoded;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(errorResponse.toJSONString());
        setCorsHeaders(response, origin);
    }
    private void setCorsHeaders(HttpServletResponse response, String origin) {
        if(whiteList.equals(origin)){
            response.setHeader("Access-Control-Allow-Origin", origin);
        }
//        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8000");
//        response.setHeader("Access-Control-Allow-Origin", "http://kongzhijie.cn:8000");
        response.setHeader("Access-Control-Allow-Methods", "*");
        response.setHeader("Access-Control-Allow-Headers", "*");
    }
}