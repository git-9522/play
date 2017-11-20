       // *根据屏幕尺寸改变根元素字体
// 翻页效果
$(function(){

	var ruler=slideruler({
		ID:".relative",
		minValue:0,
		maxValue:20000,
		interval:1000,
		tabwin:200
	})
});
function slideruler(obj){
	var _this=document.querySelector(obj.ID),
	    innerHtml="",
	    hbox=obj.tabwin,
	 	len=obj.maxValue/obj.interval+1,
	 	win=$(obj.ID).width(),
	 	wobj=win/2,
	 	startX,endX,speed=0,moveX,leftX=0,
	 	row,sum,slow,pay,val;
		$(obj.ID).append("<ul class='line'></ul>").append("<span class='bigline'></span>");
		for(i=0;i<len;i++)
		{
			innerHtml+="<li><span>"+i*obj.interval+"</span></li>"
		}
		innerHtml+="<li><span>更多</span></li>"
		$(".line").append(innerHtml);
		$(".line").width(hbox*(len+1));
		document.addEventListener("touchstart", function (e){
			startX = e.touches[0].pageX;
		});
		document.addEventListener("touchend", function(e){
			endX=leftX;
		});
		_this.addEventListener('touchmove', function (event) {
				moveX=event.touches[0].pageX;
				speed=moveX-startX;
				sum=-parseInt($(".line").css("margin-left"))+wobj;
				row=parseInt(sum/hbox),
				slow=parseInt(sum%hbox/(hbox/1))*obj.interval/1;
				pay=row*obj.interval+slow;
				if(endX!=null)
				{
					leftX=speed+endX;
				}else{
					leftX=speed;
				}
				if(leftX>=wobj)
				{
					pay=0;
					leftX=wobj;
				}
				if(leftX<=win-hbox*(len-1)-wobj)
				{
					pay=obj.maxValue;
					leftX=win-hbox*(len-1)-wobj;
				}
				
				$(".txt").css({"margin-left":leftX+"px"})
				$(".line").css({"margin-left":leftX+"px"})
				$(".l-text").val(pay);
				$(".l-text02").val(pay/1000);
		})
				$(".l-text02").blur(function(){
					val=$(this).val()*1000;
					$(".l-text").val(val);
					if(val>obj.maxValue)
					{
						leftX=win-hbox*len-wobj;
					}else if(val<0)
					{
						pay=0;
						leftX=obj;
						$(".l-text02").val(0);
					}
					else{

						leftX=wobj-(val/obj.interval*hbox+val%obj.interval*(hbox/10)/obj.interval/10);
					}
					endX=leftX;
					$(".txt").animate({"margin-left":leftX+"px"})
					$(".line").animate({"margin-left":leftX+"px"})
				})
				$(".l-text").blur(function(){
					val=$(this).val();
					val02=parseInt($(this).val()/1000);
					$(".l-text02").val(val02);
					if(val>obj.maxValue)
					{
						leftX=win-hbox*len-wobj;
					}else if(val<0)
					{
						pay=0;
						leftX=obj;
						$(".l-text").val(0);
					}
					else{

						leftX=wobj-(val/obj.interval*hbox+val%obj.interval*(hbox/10)/obj.interval/10);
					}
					endX=leftX;
					$(".txt").animate({"margin-left":leftX+"px"})
					$(".line").animate({"margin-left":leftX+"px"})
				})
				$(".q-alltou").click(function(){
					$(".l-text").val(20000);
					leftX=win-hbox*len-wobj+hbox;
					endX=leftX;
					$(".txt").animate({"margin-left":leftX+"px"})
					$(".line").animate({"margin-left":leftX+"px"})
				});
}


