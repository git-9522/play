define(function(){
	window.weakDialog = function(msg){
		var newMsg = $('<li><span class="weak-type"><i class="weak-true"></i></span><span class="weak-msg">'+msg+'</span><i class="weak-close">×</i></li>'),
			weakDialog = $('<ul id="weak-dialog"></ul>');
		var ifWeak = $('#weak-dialog');
		if(ifWeak.length > 0){
			newMsg.prependTo(ifWeak);
			if(ifWeak.children('li').length > 10){
				ifWeak.children('li').last().remove();
			}
		}else{
			newMsg.appendTo(weakDialog);
			weakDialog.appendTo('body');
		}
		setTimeout(function(){
			newMsg.remove();
		},5000);
		$('.weak-close').click(function(){
			$(this).parent().remove();
		});
	}
});