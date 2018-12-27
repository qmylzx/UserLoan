//package base.intercept;
//import base.pojo.UserInfo;
//import org.springframework.web.servlet.HandlerInterceptor;
//import org.springframework.web.servlet.ModelAndView;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//public class LTIntercept  implements HandlerInterceptor{
//    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
//        //1 获取请求的地址
//        String url = httpServletRequest.getRequestURI();
//        //2 对特殊地址放行
//        if (url.indexOf("login") >= 0 || url.indexOf("register") >= 0 || url.indexOf("showDeptInfo") >= 0) {
//            return true;
//        }
//        //3 判定session ,存在可以登陆后台
//        UserInfo userInfo = (UserInfo) httpServletRequest.getSession().getAttribute("userInfo");
//        if (userInfo != null) {
//            return true;//身份验证，放行
//        }
//        //4 执行到这里表示用户需要身份验证跳转到登陆页面
//        httpServletRequest.getRequestDispatcher("/WEB-INF/page/admin/login.jsp").forward(httpServletRequest, httpServletResponse);
//        return false;
//    }
//
//    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
//
//    }
//
//    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
//
//    }
//}
