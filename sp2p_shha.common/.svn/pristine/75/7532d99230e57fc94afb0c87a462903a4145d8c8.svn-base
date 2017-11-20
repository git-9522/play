define(['skitter'],function(skitter){
	var newCSS = $("<link type='text/css' rel='stylesheet' href='/public/front/stylesheets/my.skitter.styles.css'/>");
	newCSS.appendTo($("head"));
	$('.box_skitter_large').skitter({
		label: true,
    	numbers: false,
        navigation: false,
    	dots:true
	});
	//在首页初始化 arr
	$('.image_number').each(function(i) {
		$('.image_number').eq(i).html(arr[i])
	})
	$('.image img').css("height","400");
});