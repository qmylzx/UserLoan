package base.controller;

import base.exception.MyException;
import base.pojo.Result;
import base.pojo.UserInfo;
import base.service.IUserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("admin")
public class AdminController {
    @Autowired
    IUserInfoService IUserInfoService;

    @RequestMapping("login.action") //跳转至登陆页面
    public String login() {
        return "/login";
    }

    @RequestMapping("login.json") //ajax异步验证登陆
    @ResponseBody
    public Result login2(HttpServletRequest request) throws MyException {
        //1 获取参数
        String loginName = request.getParameter("login_name");
        String passWord = request.getParameter("pass_word");
        //2 校验参数
        if (StringUtils.isEmpty(loginName) || StringUtils.isEmpty(passWord)) {
            throw new MyException("用户名或密码不能为空");
        }
        UserInfo userInfo = IUserInfoService.selectUser(loginName, passWord);
        if (userInfo == null) {
            //用户名密码不正确
            throw new MyException("用户名或密码错误");
        }
        //3设置session
        request.getSession().setAttribute("userInfo", userInfo);
        return Result.success();
    }

}
