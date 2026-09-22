package com.b2cmall.order.web.filter;
import com.b2cmall.common.*;
import com.b2cmall.order.bean.CurrentEmployeeBean;
import com.b2cmall.order.component.RequestHolderComponent;
import com.b2cmall.order.feign.EmployeeFeignClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Collections;

@Component
public class TokenFilter extends OncePerRequestFilter {
    private final EmployeeFeignClient employee;
    private final ObjectMapper json;
    public TokenFilter(EmployeeFeignClient employee,ObjectMapper json) {this.employee=employee;this.json=json;}
    @Override protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain chain)
            throws ServletException,IOException {
        RequestHolderComponent.clear();
        try {
            if(("/order/health".equals(request.getRequestURI()) && "GET".equals(request.getMethod())) ||
               ("/order/mock/callback".equals(request.getRequestURI()) && "POST".equals(request.getMethod()))) {
                chain.doFilter(request,response);return;
            }
            try {
                java.util.List<String> headers=Collections.list(request.getHeaders("Authorization"));
                if(headers.size()!=1 || headers.get(0).trim().isEmpty())throw new BizException(401,"请在Authorization请求头中提供token");
                Result<CurrentEmployeeBean> result=employee.current(headers.get(0));
                if(result==null || result.status!=200 || result.data==null ||
                   result.data.id==null || result.data.shopId==null)throw new BizException(401,"登录身份无效");
                RequestHolderComponent.set(result.data);
            } catch(BizException e) {reject(response,e.status,e.getMessage());return;}
              catch(FeignException e) {reject(response,e.status()==401 ? 401 : 503,"登录校验失败或服务暂不可用");return;}
            chain.doFilter(request,response);
        } finally {RequestHolderComponent.clear();}
    }
    private void reject(HttpServletResponse response,int status,String message)throws IOException {
        response.setStatus(status);response.setContentType("application/json;charset=UTF-8");
        json.writeValue(response.getWriter(),new Result<>(status,message,null));
    }
}

