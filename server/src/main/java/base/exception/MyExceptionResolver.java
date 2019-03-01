package base.exception;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

import com.google.gson.Gson;

public class MyExceptionResolver implements HandlerExceptionResolver {
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler, Exception e) {
        //1 打印异常信息
        e.printStackTrace();
        //定义一个错误信息
        String message = "系统繁忙，请稍候再试！";
        //判断该错误是否是预期的错误
        if (e instanceof MyException || StringUtils.isEmpty(e.getMessage())) {
            message = ((MyException) e).getMessage();
        }
        /* 2 判断请求类型 */
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        ResponseBody responseBody = handlerMethod.getMethod().getAnnotation(ResponseBody.class);
        if (responseBody != null) {
            //2.1 如果是json请求，则返回json数据
            Map<String, Object> responseMap = new HashMap<String, Object>();
            responseMap.put("code", "999999");
            responseMap.put("message", message);
            String json = new Gson().toJson(responseMap);
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setContentType("application/json; charset=utf-8");
            try {
                httpServletResponse.getWriter().write(json);
                httpServletResponse.getWriter().flush();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            // 返回一个空的ModelAndView表示已经手动生成响应
            return new ModelAndView();
        }
        //2.2如果是action请求 则跳转到错误页面
        // 页面转发（跳转至错误页面）
        ModelAndView modelAndView = new ModelAndView();
        //将错误信息传到页面
        modelAndView.addObject("message", message);
        //指向错误页面
        modelAndView.setViewName("error");

        return modelAndView;


    }
}