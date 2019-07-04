
//同意实名认证协议
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

function checkRealName() {
    var realName=$.trim($("#realName").val())

    if ("" == realName) {
        showError("realName", "请输入真实姓名");
        return false
    } else if (!/^[\u4e00-\u9fa5]{0,}$/.test(realName)) {
        showError("realName", "姓名只能使用中文");
        return false
    } else {
        showSuccess("realName")
        return true
    }
}

function checkIdCard() {
    var idCard=$.trim($("#idCard").val())
    var replayIdCard=$.trim($("#replayIdCard").val())

    if ("" == idCard) {
        showError("idCard","请输入身份证号")
        return false
    }else if (!/(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(idCard)) {
        showError("idCard", "请输入争取的身份证号");
        return false;
    } else {
        if (idCard != replayIdCard) {
            showError("replayIdCard","两次结果不一致")
        }
        showSuccess("idCard")
        return true
    }
}

function checkReplayIdCard() {
    var idCard=$.trim($("#idCard").val())
    var replayIdCard=$.trim($("#replayIdCard").val())

    if ("" == idCard) {
        showError("idCard","请输入身份证号")
        return false
    }else if ("" == replayIdCard) {
        showError("replayIdCard", "确认身份证号不能为空");
        return false;
    } else if (replayIdCard != idCard) {
        showError("replayIdCard", "两次输入不一致");
        return false
    } else {
        showSuccess("replayIdCard")
        return true
    }

}

function checkCaptcha() {
    var captcha=$.trim($("#captcha").val())
    var flag=true
    if ("" == captcha) {
        showError("captcha", "验证码不能为空");
        return false;
    } else {
        $.ajax({
            url:"loan/checkCaptcha",
            data:{
                "captcha":captcha
            },
            type:"post",
            async:false,
            success:function (data) {
                if (data.showMessage == "OK") {
                    showSuccess("captcha");
                    flag=true
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

function verifyRealName() {
    var realName=$.trim($("#realName").val())
    var idCard=$.trim($("#idCard").val())

    if (checkRealName() && checkIdCard() && checkReplayIdCard() && checkCaptcha()) {
        $.ajax({
            url:"loan/verifyRealName",
            type:"post",
            data:{
                "realName":realName,
                "idCard":idCard
            },
            success:function (data) {
                if (data.showMessage == "OK") {
                    window.location.href="index"
                }else {
                    alert(data.showMessage)
                }

            },
            error:function () {
                showError("captcha",'系统繁忙')
            }
        })
    }
}



