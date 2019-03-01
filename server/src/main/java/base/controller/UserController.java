package base.controller;

import base.exception.MyException;
import base.pojo.*;
import base.service.IUserInfoService;
import base.service.IUserPaymentService;
import base.service.IUserService;
import base.utils.Thrift;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;


@Controller
@RequestMapping("user")
public class UserController {
    @Autowired
    IUserInfoService iUserInfoService;

    @Autowired
    IUserPaymentService iUserPaymentService;

    @Autowired
    IUserService iUserService;

    @RequestMapping("login") //ajax异步验证登陆
    @ResponseBody
    public Result login2(HttpServletRequest request) throws MyException {
        //1 获取参数
        String loginName = request.getParameter("loginName");
        String passWord = request.getParameter("passWord");
        //2 校验参数
        if (StringUtils.isEmpty(loginName) || StringUtils.isEmpty(passWord)) {
            throw new MyException("用户名或密码不能为空");
        }
        UserInfo userInfo = iUserInfoService.selectUser(loginName, passWord);
        if (userInfo == null) {
            //用户名密码不正确
            throw new MyException("用户名或密码错误");
        }
        //3设置session
        request.getSession().setAttribute("userInfo", userInfo);
        return Result.success();
    }

    @RequestMapping("index") //跳转至登陆页面  需要做分页
    public String index(Model map,
                        @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                        HttpServletRequest request) {
        PageHelper.startPage(pageNum, 10);
        List<User> list = iUserService.showAll();
        PageInfo<User> pageInfo = new PageInfo<User>(list);
        map.addAttribute("pageInfo", pageInfo);
        UserInfo userInfo =  (UserInfo)request.getSession().getAttribute("userInfo");
        String role =  userInfo.getRole();
        if(role.equals("1")){
            return "/index";
        }else {
            return "/adminIndex";
        }
    }

    @RequestMapping("modifyByAdmin") //修改帐号密码
    @ResponseBody
    public Result modifyByAdmin(@RequestParam("loginName") String loginName, @RequestParam("passWord") String passWord,
                         @RequestParam("id") String id) throws MyException{
        try {
            iUserInfoService.updateUser(loginName,passWord,id);
        } catch (Exception e) {
            throw new MyException("修改失败！");
        }
        return Result.success();
    }


    /*
     * 收入、支出、浏览行为
     * */
    @RequestMapping("show/{id}")
    public String show(@PathVariable("id") int id, Model map, HttpServletRequest request) {
        //behavior
        List<Behavior> beHaviorList = iUserPaymentService.getBehavior(String.valueOf(id));
        User user = iUserService.getUser(String.valueOf(id));
        double result =  Thrift.getResult(id);
        map.addAttribute("beHaviorList", beHaviorList);
        HttpSession session = request.getSession();
        session.setAttribute("userId", id);
        session.setAttribute("user",user);
        session.setAttribute("result",(double)Math.round(result*100)/100);
        return "/userInfo";
    }

    @RequestMapping("show/income")
    public String showIn(Model map, HttpServletRequest request) {
        int id = (int) request.getSession().getAttribute("userId");

            List<MoneyAndTime> list = iUserPaymentService.getIncome(String.valueOf(id));
            MoneyAndTime moneyAndTime;
            for (int i = 0; i < list.size(); i++) {
                moneyAndTime = list.get(i);
                moneyAndTime.setIndex(String.valueOf(i));
            }
            map.addAttribute("list", list);

            List<MoneyAndTime> list1 = iUserPaymentService.getPay(String.valueOf(id));
            for (int i = 0; i < list1.size(); i++) {
                moneyAndTime = list1.get(i);
                moneyAndTime.setIndex(String.valueOf(i));
            }
            map.addAttribute("list1", list1);

        return "/echart";
    }


    @RequestMapping("addWhite")
    @ResponseBody
    public Result add(HttpServletRequest request)throws MyException {
        String id =  request.getParameter("id");
        UserInfo userInfo = (UserInfo)request.getSession().getAttribute("userInfo");
        try {
            iUserService.addWhite(userInfo.getId(),id);
        } catch (Exception e) {
            throw new MyException("已经存在于白名单中！");
        }
        return Result.success();
    }

    @RequestMapping("whiteList")
    public String showWhiteList(Model map, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                HttpServletRequest request) { // 需要分页
        PageHelper.startPage(pageNum, 10);
        UserInfo userInfo = (UserInfo)request.getSession().getAttribute("userInfo");
        List<User> list = iUserService.getWhite(userInfo.getId());
        PageInfo<User> pageInfo = new PageInfo<User>(list);
        map.addAttribute("pageInfo", pageInfo);
        return "/whiteList";
    }

    @RequestMapping("remove")
    @ResponseBody
    public Result remove(HttpServletRequest request) throws MyException { // 需要分页
        String id = request.getParameter("id");
        try {
            iUserService.removeWhite(id);
        } catch (Exception e) {
            throw new MyException("请稍候重试");
        }
        return Result.success();
    }


    @RequestMapping("repay")
    public String repay(Model map, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum) {//还款预警  分页
        PageHelper.startPage(pageNum, 10);
        List<User> list = iUserService.showAll();
        PageInfo<User> pageInfo = new PageInfo<User>(list);
        map.addAttribute("pageInfo", pageInfo);
        return "/repay";
    }

    @RequestMapping("infoManager")  //修改帐号密码
    public String infoManager() {
        return "infoManager";
    }

    @RequestMapping("information")  //修改帐号密码  admin
    public String information() {
        return "information";
    }

    @RequestMapping("register")  //修改帐号密码
    public String register() {
        return "register";
    }

    @RequestMapping("doRegister") //修改帐号密码
    @ResponseBody
    public Result register1(@RequestParam("loginName") String loginName, @RequestParam("passWord") String passWord,
                            @RequestParam("passWord1")String verrify) throws MyException{
        System.out.println(loginName);
        System.out.println(passWord);
        System.out.println(verrify);
        if(StringUtils.isEmpty(loginName)||StringUtils.isEmpty(passWord)||StringUtils.isEmpty(verrify)){
            throw new MyException("表单为空！");
        }
        if(!passWord.equals(verrify)){
            throw new MyException("两次密码不同！");
        }
        try {
          iUserInfoService.registerUser(loginName,passWord);
        } catch (Exception e) {
            throw new MyException("注册失败！");
        }

        return Result.success();
    }



    @RequestMapping("modify") //修改帐号密码
    @ResponseBody
    public Result modify(@RequestParam("loginName") String loginName, @RequestParam("passWord") String passWord,
                         HttpServletRequest request) throws MyException{
        UserInfo userInfo = (UserInfo)request.getSession().getAttribute("userInfo");
        try {
            iUserInfoService.updateUser(loginName,passWord,userInfo.getId());
        } catch (Exception e) {
            throw new MyException("修改失败！");
        }
        return Result.success();
    }

}
