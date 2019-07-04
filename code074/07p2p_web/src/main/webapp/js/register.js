


//错误提示
function showError(id,msg) {
	$("#"+id+"Ok").hide();
	$("#"+id+"Err").html("<i></i><p>"+msg+"</p>");
	$("#"+id+"Err").show();
	$("#"+id).addClass("input-red");
}
//错误隐藏
function hideError(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id).removeClass("input-red");
}
//显示成功
function showSuccess(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id+"Ok").show();
	$("#"+id).removeClass("input-red");
}

//注册协议确认
$(function() {
	$("#agree").click(function(){
		var ischeck = document.getElementById("agree").checked;
		if (ischeck) {
			$("#btnRegist").attr("disabled", false);
			$("#btnRegist").removeClass("fail");
		} else {
			$("#btnRegist").attr("disabled","disabled");
			$("#btnRegist").addClass("fail");
		}
	});
});

//打开注册协议弹层
function alertBox(maskid,bosid){
	$("#"+maskid).show();
	$("#"+bosid).show();
}
//关闭注册协议弹层
function closeBox(maskid,bosid){
	$("#"+maskid).hide();
	$("#"+bosid).hide();
}


function checkPhone() {
    //获取到手机号码
    var phone=$.trim($("#phone").val())
    var flag=true

    if ("" == phone) {
        showError("phone","请输入")
        return false
    }else if (!/^1[1-9]\d{9}$/.test(phone)) {
        showError("phone","输入错了")
        return false
    }else {
        $.ajax({
            url:"loan/checkPhone",
            type:"get",
            data:{
                "phone":phone
            },
            async:false,
            success:function (data) {
                if (data.showMessage=="OK") {
                    showSuccess("phone");
                    flag=true
                } else {
                    showError("phone",data.showMessage)
                    flag=false
                }
            },
            error:function () {
                showError("phone","系统繁忙")
            }

        })
        return flag
    }
}


function passwordCheck() {
    var password=$.trim($("#loginPassword").val())
    var replayPassword=$.trim($("#replayPassword").val())

    if ("" == password) {
        showError("loginPassword","密码不能为空")
        return false
    }else if (!/^[0-9a-zA-Z]+$/.test(password)) {
        showError("loginPassword","密码只能使用字母和数字")
        return false
    }else if (!/^(([a-zA-Z]+[0-9]+)|([0-9]+[a-zA-Z]+))[a-zA-Z0-9]*/.test(password)) {
        showError("loginPassword", "密码必须同时包含密码和数字");
        return false;
    } else if (password.length<6 || password.length>23) {
        showError("loginPassword","输入密码位数错误")
    } else {
        if (password != replayPassword) {
            showError("replayLoginPassword","密码输入不一致")
        }
        showSuccess("loginPassword")
        return true
    }

}

function replayPasswordCheck() {
    var password=$.trim($("#loginPassword").val())
    var replayPassword=$.trim($("#replayLoginPassword").val())

    if ("" == password) {
        showError("loginPassword", "密码不能为空");
        return false;
    }else if (password != replayPassword) {
        showError("replayLoginPassword","两次不一致")
        return false
    }else {
        showSuccess("replayLoginPassword")
        return true
    }
    
}

//验证图片验证码
function captchaCheck() {
    var captcha=$.trim($("#captcha").val())
    var flag=true

    if ("" == captcha) {
        showError("captcha", "请输入验证码");
        return false;
    } else {
        //发送ajax
        $.ajax({
            url:"loan/checkCaptcha",
            type:"get",
            data:{
                "captcha":captcha
            },
            async:false,
            success:function (data) {
                if (data.showMessage == "OK") {
                    //成功了
                    flag = true;
                    showSuccess("captcha")
                } else {
                    showError("captcha",data.showMessage)
                    flag=false
                }
            },
            error:function () {
                showError("captcha","系统繁忙")
            }

        })
        return flag
    }
}

function register() {
    if (checkPhone() && passwordCheck() && replayPasswordCheck() && captchaCheck()) {
        //不可以发送明文,使用js自带的md5进行加密
        var phone=$.trim($("#phone").val())
        var password=$.trim($("#loginPassword").val())
        var replaypassword=$.trim($("#replayLoginPassword").val())

        $("#loginPassword").val($.md5(password))
        $("#replayLoginPassword").val($.md5(replaypassword))

        //向后台发送请求进行数据的添加
        $.ajax({
            url:"loan/register",
            type:"post",
            data:{
                "phone":phone,
                "password":$.md5($("#loginPassword").val())
            },
            success:function (data) {
                if (data.showMessage == "OK") {
                    //跳转到实名认证
                    window.location.href = "realName.jsp";
                } else {
                    showError("captcha",data.showMessage)
                }
            },
            error:function () {
                showError("captcha",'系统异常,稍后重试')
            }


        })


    }

}

































