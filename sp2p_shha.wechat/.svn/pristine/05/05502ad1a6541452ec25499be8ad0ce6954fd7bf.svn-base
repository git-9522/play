define(function(){
    /*菜单按钮*/
    $(".xf-menu").on("touchend",menu_switch);
    $(".xf-mask").on("touchend",munu_close);
    function menu_switch(){
        var inquiry_content =$(".xf-menulist");      
        if(inquiry_content.is(":hidden")){
            $(".xf-mask").show();
            inquiry_content.slideDown();
            $(".xf-menu").addClass("click");
        }
        else{
            inquiry_content.slideUp();
            $(".xf-mask").hide();
            $(".xf-menu").removeClass("click");
        } 
    }
    function munu_close(){
        $(".xf-menulist").hide();
        $(".xf-mask").hide();
        $(".xf-menu").removeClass("click");
    }
	    /*底部居底*/
	$(function() {
		var win_hei = $(window).outerHeight(true);
		var bghei = $(".center").outerHeight(true);
		var bot_hei = $(".xf-footer").outerHeight(true);
		$('.center').wrap('<div class="y-wrap"></div>');
		$('.y-wrap').css('min-height', win_hei - bot_hei + 3);
		$(".xf-footer").css("position", "relative").show();
	})
   
})


