var referrer = "";//登录后返回页面
referrer = document.referrer;
if (!referrer) {
    try {
        if (window.opener) {
            // IE下如果跨域则抛出权限异常，Safari和Chrome下window.opener.location没有任何属性
            referrer = window.opener.location.href;
        }
    } catch (e) {
    }
}

//按键盘Enter键即可登录
$(document).keyup(function (event) {
    if (event.keyCode == 13) {
        login();
    }
});

$(function () {

    loanStage()

    //60秒倒计时
    $("#dateBtn1").on("click", function () {
        var _this = $(this);
        //如果成功发送请求之后才开始进行倒计时
        var _phone=$.trim($("#phone").val())
        if ("" == _phone) {
            $("#showId").html("请输入手机号");
        } else {

            //发送ajax
            $.ajax({
                url:"loan/messageCode",
                type:"post",
                data:{
                    "phone":_phone
                },
                success:function (data) {
                    if (data.showMessage == "OK") {
                        alert(data.messageCode)
                        //只有成功了才开始计时
                        if (!$(this).hasClass("on")) {
                            $.leftTime(60, function (d) {
                                if (d.status) {
                                    _this.addClass("on");
                                    _this.html((d.s == "00" ? "60" : d.s) + "秒后重新获取");
                                } else {
                                    _this.removeClass("on");
                                    _this.html("获取验证码");
                                }
                            });
                        }
                    } else {
                        //失败之后进行提示错误
                        $("#showId").html(data.showMessage)
                    }
                },
                error:function () {
                    $("#showId").html("通信失败");
                }

            })


        }


    });

})

function loanStage() {
    $.ajax({
        url: "loan/stage",
        type: "get",
        data: {},
        success: function (data) {
            $(".historyAvgRate").html(data.historyAvgRate)
            $("#userCount").html(data.allUserCount)
            $("#sumBidMoney").html(data.sumBidMoney)


        },
        error: function () {
            alert("请求失败")
        }

    })

}

function checkPhone() {
    var phone = $.trim($("#phone").val())

    if ("" == phone) {
        $("#showId").html('请输入手机号');
        return false;
    } else if (!/^1[1-9]\d{9}$/.test(phone)) {
        $("#showId").html('请输入正确的手机号');
        return false;
    } else {
        $("#showId").html('');
        return true

    }
}


function checkPassword() {
    var password = $.trim($("#loginPassword").val())

    if ("" == password) {
        $("#showId").html("请输入密码");
        return false
    } else {
        $("#showId").html("");
        return true
    }

}

function checkCaptcha() {
    var captcha = $.trim($("#captcha").val())
    var flag = true

    if ("" == captcha) {
        $("#showId").html("请输入验证码");
        return false;
    } else {
        //发送请求验证是否可用
        $.ajax({
            url: "loan/checkCaptcha",
            type: "get",
            data: {
                "captcha": captcha
            },
            dataType: "json",
            async: false,
            success: function (data) {
                if (data.showMessage == "OK") {
                    $("#showId").html("");
                    flag = true;
                } else {
                    $("#showId").html(data.showMessage);
                    flag = false
                }
            },
            error: function () {
                $("#showId").html("通讯失败")
            }
        })
        return flag
    }
}

function checkMessageCode() {
    var messageCode = $.trim($("#messageCode").val())
    var phone = $.trim($("#phone").val())
    var flag=true

    if ('' == messageCode) {
        $("#showId").html("请输入短信验证码")
        return false
    }else if (isNaN(messageCode)) {
        $("#showId").html("验证码应该是纯数字");
        return false;
    } else {
        //发送ajax进行值的验证
        $.ajax({
            url:"loan/checkMessage",
            type:"post",
            data:{
                "phone":phone,
                "messageCode":messageCode
            },
            async:false,
            success:function (data) {
                if (data.showMessage == "OK") {
                    $("#showId").html("");
                    flag = true;
                } else {
                    $("#showId").html(data.showMessage);
                    flag=false
                }
            },
            error:function () {
                $("#showId").html("请求发送失败");
            }

        })
        return flag;
    }

}

function login() {
    var phone = $.trim($("#phone").val())
    var password = $.trim($("#loginPassword").val())

    if (checkPhone() && checkPassword() && checkMessageCode()) {
        $("#loginPassword").val($.md5(password));
        $.ajax({
            url: "loan/login",
            type: "post",
            data: {
                "phone": phone,
                "password": $.md5(password)
            },
            async: true,
            success: function (data) {
                if (data.showMessage == "OK") {
                    window.location.href = referrer;
                } else {
                    alert(data.showMessage)
                }
            },
            error: function () {
                alert("通信失败")
            }
        });
    } else {
        $("#showId").html("输入的信息有误")
    }

}





