require(["jquery","easyui"],function($){
	easyloader.locale = "zh_CN";
	var nowDate = new Date(),
		nowYear = nowDate.getFullYear(),
		nowMonth = nowDate.getMonth()+1,
		nowDay = nowDate.getDate(),
		nowHours = nowDate.getHours(),
		nowMinutes = nowDate.getMinutes();
		nowSeconds = nowDate.getSeconds(); 
	var nowTime = nowYear+"/"+ (nowMonth>10? nowMonth : ("0"+nowMonth))+"/"+(nowDay>10? nowDay : ("0"+nowDay))+" "+nowHours+":"+nowMinutes+":"+nowSeconds;
	using("datetimebox",function(){
		$('.easyui-datetimebox2').each(function(){
			$.parser.parse();
			var defaultVal = $(this).val();
			
			$(this).datetimebox({
				width: 181.778,
				height: 29.22,
				value:function(){
					return defaultVal ;
				}(),
			    showSeconds: true,
			    panelWidth: 181.778,
			    editable: false,
			    onShowPanel: function(){
			    	$(this).next().css({
			    		"border-color": "#5A9CCB",
						"box-shadow": "0 0 5px #5A9CCB"
			    	});
			    },
			    onHidePanel: function(){
			    	$(this).next().css({
			    		"border-color": "#ccc",
						"box-shadow": "none"
			    	});
			    }
			});
		});
	});
});