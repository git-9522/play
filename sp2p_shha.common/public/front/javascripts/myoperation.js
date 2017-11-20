/**
 * Created by Administrator on 2017-06-29.
 */
$(function(){
    var onOff = true,
        oIndex = 0;
    $('.btn-next').click(function(){
        var box=$('.content'),
        obj=$('.content-show'),
        len=box.find('li').length;
        if(onOff == true){
            onOff = false;
            setTimeout(function(){
                onOff = true;
            },1000);
            if(oIndex==len-1){
                /*console.log(oIndex);*/
                return false;
            }
            oIndex++;
            yun();
            onOff = false;
            
            box.find('li').eq(oIndex-1).addClass('zi2').next('li').addClass('zi1');
            box.find('li').eq(oIndex-1).css({
                'transform':'rotateX(200deg)',
                '-webkit-transform':'rotateX(200deg)'
            });
            
            setTimeout(function(){
                box.find('li').eq(oIndex-1).css({
                    'opacity':0
                });
                box.find('li').removeClass('zi2');
                box.find('li').removeClass('zi1');
                box.find('li').eq(oIndex-1).next('li').addClass('zi1');
            },1000);
        }
    });
    $('.btn-prev').click(function(){
        var box=$('.content'),
        obj=$('.content-show'),
        len=box.find('li').length;
        if(onOff == true){
            onOff = false;
            box.find('li').eq(oIndex).addClass('zi1').prev('li').addClass('zi2');
            setTimeout(function(){
                onOff = true;
                box.find('li').removeClass('zi2');
                box.find('li').removeClass('zi1');
                box.find('li').eq(oIndex).addClass('zi2');
                box.find('li').eq(oIndex).next('li').addClass('zi1');
            },1000);
            if(oIndex==0){
                /*box.eq(len-1).removeClass('zi2');*/
                return false;
            }
            oIndex--;
            setTimeout(function(){
                yun();
            },500);
            box.find('li').eq(oIndex).css({
                'opacity': 1,
                'transform':'rotateX(0deg)',
                '-webkit-transform':'rotateX(0deg)'
            });
        }
    });
    function yun(){
        switch(oIndex){
                case 0:
                $('.content li').removeClass('yun_move');
                $('.yun_see').hide();
                break;
            case 1:
                $('.content li').eq(oIndex).addClass('yun_move').siblings().removeClass('yun_move');
                $('.yun_see').show();
                $('.yun_see .yun_btn').eq(0).attr("src","/public/front/images/yun_btn_big1.png");
                $('.yun_see .yun_btn').eq(0).addClass('zi5').siblings().removeClass('zi5');
                break;
                case 2:
                $('.content li').eq(oIndex).addClass('yun_move').siblings().removeClass('yun_move');
                $('.yun_see').show();
                $('.yun_see .yun_btn').eq(0).attr("src","/public/front/images/yun_btn_big1.png");
                $('.yun_see .yun_btn').eq(1).attr("src","/public/front/images/yun_btn2.png");
                $('.yun_see .yun_btn').eq(0).addClass('zi5').siblings().removeClass('zi5');
                break;
                case 3:
                $('.content li').eq(oIndex).addClass('yun_move').siblings().removeClass('yun_move');
                $('.yun_see').show();
                $('.yun_see .yun_btn').eq(0).attr("src","/public/front/images/yun_btn1.png");
                $('.yun_see .yun_btn').eq(1).attr("src","/public/front/images/yun_btn_big2.png");
                
                $('.yun_see .yun_btn').eq(1).addClass('zi5').siblings().removeClass('zi5');
                break;
                case 4:
                $('.content li').eq(oIndex).addClass('yun_move').siblings().removeClass('yun_move');
                $('.yun_see').show();
                $('.yun_see .yun_btn').eq(1).addClass('zi5').siblings().removeClass('zi5');
                break;
                case 5:
                $('.content li').eq(oIndex).addClass('yun_move').siblings().removeClass('yun_move');
                $('.yun_see').show();
                $('.yun_see .yun_btn').eq(1).attr("src","/public/front/images/yun_btn_big2.png");
                $('.yun_see .yun_btn').eq(2).attr("src","/public/front/images/yun_btn3.png");
                $('.yun_see .yun_btn').eq(1).addClass('zi5').siblings().removeClass('zi5');
                break;
                case 6:
                $('.content li').eq(oIndex).addClass('yun_move').siblings().removeClass('yun_move');
                $('.yun_see').show();
                 $('.yun_see .yun_btn').eq(1).attr("src","/public/front/images/yun_btn2.png");
                $('.yun_see .yun_btn').eq(2).attr("src","/public/front/images/yun_btn_big3.png");
                $('.yun_see .yun_btn').eq(3).attr("src","/public/front/images/yun_btn4.png");
                $('.yun_see .yun_btn').eq(2).addClass('zi5').siblings().removeClass('zi5');
                break;
                case 7:
                $('.content li').eq(oIndex).addClass('yun_move').siblings().removeClass('yun_move');
                $('.yun_see').show();
                $('.yun_see .yun_btn').eq(3).addClass('zi5').siblings().removeClass('zi5');
                $('.yun_see .yun_btn').eq(2).attr("src","/public/front/images//yun_btn3.png");
                $('.yun_see .yun_btn').eq(3).attr("src","/public/front/images//yun_btn_big4.png");
                break;
                case 8:
                $('.yun_see').hide();
                $('.content li').removeClass('yun_move');
        }
    }
            
});
















