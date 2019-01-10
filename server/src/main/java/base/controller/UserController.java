package base.controller;

import base.exception.MyException;
import base.pojo.Result;
import base.pojo.User;
import base.pojo.UserInfo;
import base.service.IUserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.LinkedList;
import java.util.List;

@Controller
@RequestMapping("user")
public class UserController {
    @Autowired
    IUserInfoService IUserInfoService;

    @RequestMapping("login") //ajax异步验证登陆
    @ResponseBody
    public Result login2(HttpServletRequest request) throws MyException {
        System.out.println("login.json"+request.getParameter("loginName"));
        //1 获取参数
        String loginName = request.getParameter("loginName");
        String passWord = request.getParameter("passWord");
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

    @RequestMapping("index") //跳转至登陆页面
    public String index(Model map) {
        List<User> list = new LinkedList<>();
        User user= null;
        for (int i = 0; i <10 ; i++) {
            user  =new User();
            user.setId(i);
            user.setSex("das"+i);
            user.setEducation("dasd");
            user.setJob("job");
            user.setPhoneNumber("156489489");
            list.add(user);
        }
        map.addAttribute("list",list);
        return "/index";
    }
    @RequestMapping("show/{id}")
    public String show(@PathVariable("id") int id,Model map){
        System.out.println(id);
        List<User> list = new LinkedList<>();
        User user= null;
        for (int i = 0; i <10 ; i++) {
            user  =new User();
            user.setId(i*10);
            user.setSex("das"+i);
            user.setEducation("dasd");
            user.setJob("job");;
            user.setPhoneNumber("156489489");
            list.add(user);
        }
        map.addAttribute("list",list);
       // return "/echart";
        return "/userInfo";
    }
    @RequestMapping("whiteList")
    public String showWhiteList(Model map){
        List<User> list = new LinkedList<>();
        User user= null;
        for (int i = 0; i <10 ; i++) {
            user  =new User();
            user.setId(i*10);
            user.setSex("das"+i);;
            user.setEducation("dasd");;
            user.setJob("job");
            user.setPhoneNumber("156489489");
            list.add(user);
        }
        map.addAttribute("list",list);
        return "/whiteList";
    }

    @RequestMapping("repay")
    public String repay(Model map){
        List<User> list = new LinkedList<>();
        User user= null;
        for (int i = 0; i <10 ; i++) {
            user  =new User();
            user.setId(i*10);
            user.setSex("das"+i);
            user.setEducation("dasd");;
            user.setJob("job");
            user.setPhoneNumber("156489489");
            list.add(user);
        }
        map.addAttribute("list",list);
        return "/repay";
    }

    @RequestMapping("infoManager")
    public String infoManager(){
        return "infoManager";
    }
    @RequestMapping("modify")
    @ResponseBody
    public Result modify(@RequestParam("loginName")String loginName,@RequestParam("passWord")String passWord){
        System.out.println(loginName);
        System.out.println(passWord);
        return Result.success();
    }
}
