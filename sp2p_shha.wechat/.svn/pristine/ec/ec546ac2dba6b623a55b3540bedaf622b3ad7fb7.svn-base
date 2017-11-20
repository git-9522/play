require.config({
	paths: {
		"jquery": "lib/jquery", //jquery包
		"validate": "validation/jquery.validate", /*表单验证插件*/
		"messages": "validation/messages_zh", /*表单验证插件错误提示信息*/
	},
	shim: {
	}
});
define(['domReady!','jquery','validate','dialog','messages'],function(doc,$){
	
	//选项卡

	
	/**
	 * 是否为空(不为空返回null)
	 * 
	 * @returns
	 */
	String.prototype.isNotBlank = function(){
		var blank = /^\s*$/;
		return  (!blank.test(this));
	}
	/**
	 * 字符串长度是否符合要求
	 * 
	 * @returns
	 */
	String.prototype.isLess = function(minNum){
		var flag = (this.length < minNum);
		return flag;
	}
	String.prototype.isMore = function(maxNum){
		var flag = (this.length > maxNum);
		return flag;
	}
	
	
	String.prototype.isBetween = function(min,max){
	
		return (this.isNotBlank() && (!this.isLess(min)) && (!this.isMore(max)));
	}
	
	/**
	 * 是否有效的手机号码
	 * 
	 * @returns
	 */
	String.prototype.isMobileNum = function() {
		
		return (new RegExp(/^((13[0-9])|(14[4,7])|(15[^4,\D])|(17[0-9])|(18[0-9]))(\d{8})$/).test(this));
	}
	
	/**
	 * 是否有效的密码
	 * 
	 */
	String.prototype.isPassWord = function() {
		
		return (new RegExp(/^([0-9a-zA-Z]{6,15})$/).test(this));
	}
	
	/**
	 * 是否有效的密码
	 * 
	 */
	String.prototype.isNumber = function() {
		
		return (new RegExp(/^([0-9]{1,2})$/).test(this));
	}
	
	/**
	 * 是否是整数倍
	 * 
	 */
	String.prototype.isMultiple = function(num){
		return ((this.isBlank()) && (this%num == 0));
	}
	
	String.prototype.isPositInt = function(){
		var res = /^[1-9]+[0-9]*]*$/;
		var vl = this;
		var flag = res.test(vl);
		return flag;
	}
	
	String.prototype.isNumLess = function(min){
		if(!this.isNotBlank()){
			return false;
		}
		var fl = parseFloat(this);
		if(fl < min ){
			return true;
		}
		return false;
	}
	String.prototype.isNumMore = function(max){
		if(!this.isNotBlank()){
			return false;
		}
		var fl = parseFloat(this);
		if(fl > max ){
			return true;
		}
		return false;
	}
	
	/** 字符串是否是属于该区间的数字【包含两个区间】 */
	String.prototype.isNumBetween = function(min,max){
		return (this.isNotBlank() && (!this.isNumLess(min)) && (!this.isNumMore(max)))
	}
	
	/** 一位小数或者非负整数 */
	String.prototype.isFloat = function() {
		var res = new RegExp(/^(\d+\.\d{1,1}|\d+)$/);
		return res.test(this);
	}
	/**
	 * 是否为汉字
	 * @returns
	 */
	String.prototype.isChinese = function() {
		return (new RegExp("[\\u4E00-\\u9FFF]+","g")
				.test(this));
	}
	/**
	 * 是否有效的邮箱
	 * 
	 * @returns
	 */
	String.prototype.isEmail = function() {
		return (
				new RegExp(/^([a-zA-Z0-9])+([a-zA-Z0-9_.-])+@([a-zA-Z0-9_-])+((\.([a-zA-Z0-9_-]){2,3}){1,2})$/).test(this)
			   );
	}
	/**
	 * 是否是QQ邮箱
	 */
	String.prototype.isQQEmail = function(){
		return new RegExp(/^([\s\S]*@qq.com)$/).test(this);
	}
	
	String.prototype.isQQ = function(){
		return new RegExp(/^\d{6,10}$/).test(this);
	}
	
	//判断元素值是是否是日期类型
	String.prototype.isDate = function() {
		return (new RegExp(
				/^([1-2]\d{3})[\/|\-](0?[1-9]|10|11|12)[\/|\-]([1-2]?[0-9]|0[1-9]|30|31)$/ig)
				.test(this));
	}
	
	//判断两个表单元素的值是否相等
	$.fn.isEqual = function(another){
		return ($(this).val() === another.val());
	}
	
	//截取字数：
	$.fn.limit=function(){   
		var self = $("[limit]");
		  self.each(function(){
		       var objString = $(this).text();
		       var objLength = $(this).text().length;
		       var num = $(this).attr("limit");
		   if(objLength > num){           
			   $(this).text(objString.substring(0,num) + "...");
		       }else{
		    	   $(this).text(objString);
		       }
		  })
	}
	
	//文本域换行显示
	$.fn.newline=function(){   
		var self = $(".newline");
		self.each(function(){
			var element = $(this);
			var temp = element.text().trim().replace(/\n/g, '<br>');
			$(this).html(temp);
		})
	}
	
	//处理ajax请求被拦截的情况
	window.interceptorCheck = function(result) {
		if(result.code == -100){//没有登录
			top.location.href="/wx/loginAndRegiste/login.html";
			return false;
		}
		if(result.code == -102){//未开户
			top.location.href="/wx/account/security/createAccount.html";
			return false;
		}
		if(result.code == -103){//未实名认证
			top.location.href="/wx/account/security/createAccount.html";
			return false;
		}
		return true;
	}

	//正则范围：0到9的正整数,字母大小写。
	$.fn.integerAndLetters=function(){
		  var reg = new RegExp('^([0-9a-zA-Z]{0,20}).*');
		  $(this).val($(this).val().replace(/[^0-9a-zA-Z]/g,'').replace(reg,'$1'));
	}
	//正则范围：0.0-99.9或者100，可输入一位小数
	$.fn.proportionRange=function(){
		$(this).val($(this).val().replace(/[^\d\.]/g, '').replace(/^\.+/, '').replace(/^(100|\d{1,2}(\.\d{0,1})?).*/,'$1'));
	}
	//正则范围：0.0-9.9或者10，可输入一位小数，10的时候不可在输入
	$.fn.discountRange=function(){
		$(this).val($(this).val().replace(/[^\d\.]/g, '').replace(/^\.+/, '').replace(/^(10|\d{0,1}(\.\d{0,1})?).*/,'$1'));
	}
	//正则范围：1-12，正整数
	$.fn.oneToTwelfth=function(){
		$(this).val($(this).val().replace(/[^\d]/g,'').replace(/(^12|11|10|(\d{0,1})).*/, '$1'));
	}
	//正则范围：0-10，正整数
	$.fn.zeroToTen=function(){
		$(this).val($(this).val().replace(/[^\d]/g,'').replace(/(^10|(\d{0,1})).*/, '$1'));
	}
	//正则范围：0.00-9999999.99,可输入2位小数，7位整数位
	$.fn.withdrawRange=function(){
	    $(this).val($(this).val().replace(/[^\d\.]/g,'').replace(/^\.+/,'').replace(/^(\d{1,7}(\.\d{0,2})?).*/,'$1'));
	}
	//正则范围：0.00-20位整数,可输入2位小数，
	$.fn.maxRange=function(){
		$(this).val($(this).val().replace(/[^\d\.]/g,'').replace(/^\.+/,'').replace(/^(\d{1,18}(\.\d{0,2})?).*/,'$1'));
	}
	//正则范围：0.00-99.99或者100, 可输入2位小数，100的时候不可以输入小数位
	$.fn.percentage=function(){
		$(this).val($(this).val().replace(/[^\d\.]/g,'').replace(/^\.+/,'').replace(/^(100|\d{1,2}(\.\d{0,2})?).*/,'$1'));
	}
	//正则范围：0到n位的最大值，正整数。n表示最大位数
	$.fn.ndigitInteger=function(n){
		  var reg = new RegExp('^(\\d\{0,'+n+'\}).*');
		  $(this).val($(this).val().replace(/[^\d]/g,'').replace(reg,'$1'));
	}

});