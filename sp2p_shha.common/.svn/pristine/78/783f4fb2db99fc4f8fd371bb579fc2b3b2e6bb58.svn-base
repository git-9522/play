$(document).ready(function(){
    var a = $('.issue').length;
    
	$(".cnt li").click(function(){
        $(this).addClass("sel").siblings().removeClass('sel');
        $(this).parents('.cnt').attr('sine','true');
    })
	.hover(
		function(){$(this).addClass("hover")},
		function(){$(this).removeClass("hover")}
		)	
    $('.check_btn').click(function(){
        var s='';
        $('.issue').each(function(i){
            if($(this).find('.cnt').attr('sine')=='false'){
                s+= (i+1)+',';
            }
        })
        if(s == ''){
            // console.log('做完了')
            $('.pass').html('');
            var temp = '';
            $('.issue').each(function(i){
            	// console.info($(this).find(".sel label span").attr("value"));
            	if(i == 0) {
            		temp += "[" + $(this).find(".sel label").attr("value") + ",";
            	} else if(i == a - 1) {
            		temp += $(this).find(".sel label").attr("value") + "]";
            	} else {
            		temp += $(this).find(".sel label").attr("value") + ",";
            	}
            });
            $('#form_params').val(temp);
            $('#form_signin').submit();
        }else{
            $('.pass').html('您第'+s+'题还没做哦!');
        }
    })
})