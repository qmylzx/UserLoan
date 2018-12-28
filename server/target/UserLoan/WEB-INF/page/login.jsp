<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>登录</title>
    <!--javaex-->
    <!--字体图标样式-->
    <link href="/static/javaex/pc/css/icomoon.css" rel="stylesheet"/>
    <!--动画样式-->
    <link href="/static/javaex/pc/css/animate.css" rel="stylesheet"/>
    <!--核心样式-->
    <link href="/static/javaex/pc/css/style.css" rel="stylesheet"/>
    <!--jquery，当前版本不可更改jquery版本-->
    <script src="/static/javaex/pc/lib/jquery-1.7.2.min.js"></script>
    <!--全局动态修改-->
    <script src="/static/javaex/pc/js/common.js"></script>
    <!--核心组件-->
    <script src="/static/javaex/pc/js/javaex.min.js"></script>
    <!--表单验证-->
    <script src="/static/javaex/pc/js/javaex-formVerify.js"></script>
    <style>
        .login-bg {
            width: 1920px;
            min-height: 920px;
            height: calc(100vh - 100px - 100px);
            background-image: url("/static/");
            background-size: auto 100%;
            background-position: 45% center;
            background-repeat: no-repeat;
            position: relative;
        }

        .login-form {
            position: absolute;
            width: 288px;
            right: 260px;
            background: #fff;
            padding: 20px 38px 0;
            top: 25%;
        }

        .login-title {
            font-size: 20px;
            text-align: center;
            margin-bottom: 25px;
        }

        .login-item {
            display: flex;
            margin-bottom: 14px;
        }

        .login-link {
            margin-bottom: 14px;
        }

        .login-link label, .login-link a {
            color: #9B9B9C;
        }

        .login-link a {
            font-size: 14px;
        }

        .login-text {
            min-height: 40px;
            width: 100%;
            color: #B3B3B3;
            font-size: 14px;
            border: 1px solid #EEEFF0;
            border-radius: 3px;
            font-family: Helvetica, "microsoft yahei", sans-serif;
            padding-left: 16px;
            outline: none;
        }

        .button.login {
            background-color: #2D9C8B;
            color: white;
            width: 100%;
            border-radius: 3px;
            font-size: 14px;
            height: 40px;
            line-height: 40px;
        }

        .login-link .line {
            display: inline-block;
            width: 1px;
            height: 12px;
            background-color: #EEEEEE;
            margin: 0 6px;
        }
    </style>

</head>
<body>
<!--中间主体信息-->
<div class="login-bg animated zoomIn">
    <!--表单-->
    <form id="login1">
        <div class="login-form">
            <!--标题-->
            <div class="login-title">登录系统</div>
            <!--用户名-->
            <div class="login-item">
                <input type="text" class="login-text" id="login_name" name="login_name" data-type="必填"
                       placeholder="用户名"/>
            </div>
            <!--密码-->
            <div class="login-item">
                <input type="password" class="login-text" name="pass_word" data-type="必填" placeholder="请输入密码"/>
            </div>
            <!--登录按钮-->
            <div class="login-item">
                <input type="button" id="submit" class="button login" value="登录"/>
            </div>

        </div>
    </form>
</div>
<script>
    $("#submit").click(function () {
        if (javaexVerify()) {
            console.log("sssss");
            $.ajax({
                url: "${pageContext.request.contextPath}/admin/login.json",
                type: "POST",
                dataType: "json",
                data: $("#login1").serialize(),
                success: function (rtn) {
                    if (rtn.code == "000000") {
                        window.location.href = "${pageContext.request.contextPath}/admin/index.action";
                    } else {
                        //  alert("用户名或密码错误");
                        addErrorMsg("login_name", rtn.message)
                    }
                }, error: function (rtn) {
                    console.log(rtn);
                }
            });
        }
    });
</script>
</body>
</html>

