define(function(){
	$('.w-news-list>li').each(function(){
		var that = $(this),
			head = that.children('.help-question-a'),
			cont = that.children('.help-answer');
		head.on('click',function(){
			if(that.hasClass('selected')){
				cont.stop(true,true).slideUp(500,function(){
					that.removeClass('selected');
				});
			}else{
				that.addClass('selected').css("height","auto").siblings('.selected').children('.help-question-a').trigger('click');
				cont.stop(true,true).slideDown(500);
			}
		})
	});
});