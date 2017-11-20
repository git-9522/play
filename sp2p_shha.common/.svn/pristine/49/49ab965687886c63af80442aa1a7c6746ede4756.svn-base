define(['dialog'],function(dialog){
	//开户弹窗
	$(".trusteeship-openbtn").click(function(){
		$('.open-dialog').dialog();
	});
	//下拉账单
	$('.account-billbtn').click(function(){
		var bill = $(this).closest('tr').next('.account-bill').find('.account-billbox');
		if(bill.is(':hidden')){
			bill.slideDown(300);
		}else{
			bill.slideUp(300);
		}
	});
	//账户资产状况收缩
	$('.userasset-control').click(function(){
		var userasset = $('.userasset-box');
		if(userasset.is(':hidden')){
			userasset.stop(true,true).slideDown(300);
			$(this).removeClass('selected');
		}else{
			userasset.stop(true,true).slideUp(300);
			$(this).addClass('selected');
		}
	});
	
	// 表格隔行换色
	$('.account-table').each(function(){
		$(this).children('tbody').children('tr:not(.account-bill):even').addClass('even');
	});

	//转让申请
	$('.account-manange').each(function(){
		var debtBtn= $(this).find('.account-debtbtn'),
			debtForm = $(this).find('.account-debt'),
			mainTable = $(this).find('.account-main'),
			backBtn = debtForm.find('.backpage'),
			bill = $(this).find('.account-billbox');
		debtBtn.click(function(){
			mainTable.addClass('none');
			debtForm.removeClass('none');
		});
		backBtn.click(manageInit);
		$('.account-menu').children().click(manageInit);
		function manageInit(){
			mainTable.removeClass('none');
			debtForm.addClass('none');
			bill.hide();
		}
	});
	// 自动投标开启关闭效果
	$('.account-autoclose').click(function(){
		$('.account-autostatus').addClass('close');
	});
	$('.account-autoopen').click(function(){
		$('.account-autostatus').removeClass('close');
	});
	
	// 会员信息保存设置
	$('.account-inforbox').each(function(){
		var btn = $(this).find('.account-inforedit'),
			input = $(this).find('input,select'),
			enable = false;
		btn.click(function(){
			input.prop('disabled',enable);
			$(this).text(enable ? '编辑' : '保存');
			enable = !enable;
		});
	});

	//修改手机
	$('.change-mobile').click(function(){
		$('.account-safetable').hide();
		$('.account-resetbox').eq(0).show();
	});
	$('.account-resetbox').each(function(){
		var self = $(this),
			returnBtn = $(this).find('.backpage');
		returnBtn.click(function(){
			$('.account-resetbox').hide();
			$('.account-safetable').show();
		});
		var nextBtn = $(this).find('.condition-btn');
		nextBtn.click(function(){
			//验证判断
			self.hide().next('.account-resetbox').show();
		});
	});
	//修改邮箱
	$('.change-email').click(function(){
		$('.account-safetable').hide();
		$('.account-setemail').eq(0).show();
	});
	$('.account-setemail').each(function(){
		var self = $(this),
			returnBtn = $(this).find('.backpage');
		returnBtn.click(function(){
			$('.account-setemail').hide();
			$('.account-safetable').show();
		});
		var nextBtn = $(this).find('.condition-btn');
		nextBtn.click(function(){
			//验证判断
			self.hide().next('.account-setemail').show();
		});
	});
	//修改银行卡
	$('.change-bankcard').click(function(){
		$('.account-safetable').hide();
		$('.account-setbank').show();
	});
	$('.account-setbank').each(function(){
		var self = $(this),
			returnBtn = $(this).find('.backpage');
		returnBtn.click(function(){
			$('.account-setbank').hide();
			$('.account-safetable').show();
		});
	});
	//修改登录密码
	$('.change-password').click(function(){
		$('.account-safetable').hide();
		$('.account-setpassword').show();
	});
	$('.account-setpassword').each(function(){
		var self = $(this),
			returnBtn = $(this).find('.backpage');
		returnBtn.click(function(){
			$('.account-setpassword').hide();
			$('.account-safetable').show();
		});
	});
	//消息详情下拉
	$('.message-title').click(function(){
		var box = $(this).parent().next().find('.message-box');
		if(box.is(":hidden")){
			box.slideDown(300);
		}else{
			box.slideUp(300);
		}
	});

	//银行选择
	$('.bank-list li').click(function(){
		$(this).addClass('selected').siblings().removeClass('selected');
	});
});