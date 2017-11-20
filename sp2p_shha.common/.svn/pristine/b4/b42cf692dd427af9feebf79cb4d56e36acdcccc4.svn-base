$(function(){
    var oH = $(window).height();
    var oIndex=0;
    var onOff = true;
    var $oS1_img = $('.s1_com');
    var $oS1_icon = $('.s_center .s1_icon li');
    var $oS1_zh = $('.s_center .s1_zh li');
    var $oWave = $('.wave li');
    var $oS2_icon = $('.swiper2 .swiper2_icon li');
    var oS5_time;
    var oNumb=1;
    window.onload = function(){
        $('html,body').animate({
            scrollTop: 0
        },100)
    }; 
    $(window).resize(function(){
        var $windowH = $(window).height();
        $(document).scrollTop($windowH*oIndex);
    })
    /*鼠标滚动动画*/
    $(document).mousewheel(function(){
        var m = arguments[1];
        if(onOff){
            onOff = false;
            oClear1();
            if(m>0){
                oIndex--;
                oMove();
            }else{
                oIndex++;
                oMove();
            }
            oMate();
            oSwitch();
        }
     })
    /*鼠标滚动动画*/
    $('.nav li').not('.active').mouseenter(function(){
        $('.nav li').not('.active').find('a').css('color','#000');
        $(this).find('a').css('color','#ff6600');
    });
     /*圆点点击动画*/
     $('.choose_quan li').click(function(){
         if(onOff){
            onOff = false;
            oClear1();
            oIndex = $(this).index();
            oMove();
            oMate();
            oSwitch();
         }
     })
     /*圆点点击动画*/
     /*圆点控制封装*/
     function oMate(){
        if(oIndex==0){
                $('.header').fadeIn(500);
            }else{
                $('.header').fadeOut(500);
            }
         $('.choose_quan li').eq(oIndex).addClass('choose_active').siblings().removeClass('choose_active');
     }
     /*圆点控制封装*/
     /*翻页封装*/
     function oMove(){
        var $oSs = $('.swiper_wrapper>li');
        if(oIndex<0)oIndex=0;
        $('html,body').stop(true).animate({
            scrollTop: oIndex*oH
        },1000,function(){
            setTimeout(function(){
                onOff=true;
            },1000)
        })
        if(oIndex>$oSs.length-1)oIndex=$oSs.length-1;
        $('html,body').stop(true).animate({
            scrollTop: oIndex*oH
        },1000,function(){
            setTimeout(function(){
                onOff=true;
            },1000)
        })
    }
    /*翻页封装*/
    /*swiper1位置*/
    oSwiper1();
    function oSwiper1(){
        var arr1 = ['0','80','80','150','150','150'],arr2 = ['385','430','335','470','400','310'],arr3 = ['0','160','320'],arr4 = ['300','200','100'];
        for(var i = 0; i<$oS1_icon.length; i++){
            $oS1_icon.eq(i).css({
                backgroundImage: 'url(/public/front/images/swiper_min'+(i+1)+'.png)',
            })
            $oS1_icon.eq(i).animate({
                left: arr1[i] + 'px',
                bottom: arr2[i] + 'px',
                opacity: 1
            },1000)
        }
        for(var s=0; s<$oS1_zh.length; s++){
            $oS1_zh.eq(s).css({
                backgroundImage: 'url(/public/front/images/swiper_zh'+(s+1)+'.png)'
            })
            $oS1_zh.eq(s).animate({
               right: arr3[s] + 'px',
                opacity: 1
            },1000)
        }
        for(var j=0; j<$oWave.length; j++){
            $oWave.eq(j).css({
                backgroundImage: 'url(/public/front/images/lang'+(j+1)+'.png)',
                bottom: j*35 + 'px',
                zIndex: arr4[j]
            })
        }
        var oTimer = setTimeout(function(){
             $oS1_img.animate({
               bottom: 220 +'px',
               opacity: 1
            },1000)
        },500)
    }
    /*swiper1位置*/
    /*swiper2动画*/
    function oSwiper2(){
        var oL2 = ['-20','95','230','340','450','600','720'];
        var oS2_time = setTimeout(function(){
            $('.swiper2 .swiper_title').animate({
                opacity: 1
            },500,function(){
                $('.swiper2 h1').animate({
                    opacity: 1
                },800,function(){
                    $('.swiper2 .swiper2_font').animate({
                        opacity: 1
                    },800,function(){
                        $('.swiper2 .swiper2_img').animate({
                            opacity:1
                        },500)
                        for(var i=0; i<$oS2_icon.length; i++){
                            $oS2_icon.eq(i).animate({
                                left: oL2[i] + 'px',
                                opacity: 1
                            },200*i)
                        }
                    })
                })
            })
        },800)
    }
    /*swiper2动画*/
    /*swiper3动画*/
    function oSwiper3(){
        var oS3_time = setTimeout(function(){
            $('.swiper3 .swiper_title').animate({
                opacity: 1
            },500,function(){
                $('.swiper3 h1').animate({
                    opacity: 1
                },800,function(){
                    $('.swiper3 .swiper3_btn').animate({
                        opacity: 1
                    },800)
                })
            })
        },800)
    }
    /*swiper3动画*/
    /*swiper4动画*/
    function oSwiper4(){
        var oS4_time = setTimeout(function(){
            $('.swiper4 .swiper_title').animate({
                opacity: 1
            },500,function(){
                $('.swiper4 h1').animate({
                    opacity: 1
                },800,function(){
                    $('.swiper4 .s4_main1').animate({
                        opacity: 1,
                        marginLeft:'-393px'
                    },800)
                    $('.swiper4 .s4_main2').animate({
                        opacity: 1,
                        marginLeft:'0px'
                    },800)
                })
            })
        },800)
    }
    /*swiper4动画*/
    /*swiper5动画*/
    oSwiper5();
    function oSwiper5(){
        var $oS5_btn = $('.swiper5 .s5_btn li');
        var $oS5_lab = $('.swiper5 .s5_lab li');
        var oS5_ot = ['一、注册成为会员','二、实名认证','三、充值','四、开始投资'];
        var oS5_l = ['3','195','383','571'];
        var oS5_index = 0;
        var oNoff = true;
        $oS5_lab.click(function(){
            if(oNoff){
                oNoff = false;
                if($(this).index()==0){
                    oS5_index--;
                    oS5_img();
                    console.log(oNoff);
                }else{
                    oS5_index++;
                    oS5_img();
                }
            }
        })
        $('.swiper5 .s5_bor').mouseenter(function(){
            clearInterval(S5_time);
        }).mouseleave(function(){
            clearInterval(S5_time);
            S5_t();
        })
        $oS5_btn.click(function(){
            if(oNoff){
                oNoff = false;
                oS5_index = $(this).index();
                oS5_img();
            }
        })
        $('.swiper5 .s5_btn').mouseenter(function(){
            clearInterval(S5_time);
        }).mouseleave(function(){
            clearInterval(S5_time);
            S5_t();
        })
        for(var i=0; i<$oS5_btn.length; i++){
            $oS5_btn.eq(i).css('left',oS5_l[i]+'px');
        }
        function oS5_img(){
            $('.s5_bor img').css('opacity','0');
            if(oS5_index>$oS5_btn.length-1){
                oS5_index=0;
                oAni();
            }else if(oS5_index<0){
                oS5_index=$oS5_btn.length-1;
                oAni();
            }else{
                oAni();
            }
        }
        S5_t();
        function S5_t(){
            S5_time = setInterval(function(){
                oS5_index++;
                oS5_img();
            },4000)
        }
        function oAni(){
            $('.swiper5 .s5_title').html(oS5_ot[oS5_index]);
            $('.swiper5 .s5_font li').eq(oS5_index).addClass('h5_active').siblings().removeClass('h5_active');
            $('.swiper5 .s5_btn').css('background','url(/public/front/images/swiper5_btn'+(oS5_index+1)+'.png)');
            $('.s5_bor img').attr('src','/public/front/images/swiper5_img'+(oS5_index+1)+'.jpg');
            $('.s5_bor img').stop(true).animate({
                'opacity':1
            },500);
            setTimeout(function(){
                oNoff = true;
            },1000)
        }
        var oS5_t = setTimeout(function(){
            $('.swiper5 .swiper_title').animate({
                opacity: 1
            },500,function(){
                $('.swiper5 h1').animate({
                    opacity: 1
                },800)
            })
        },800)
    }
    /*swiper5动画*/
    /*swiper6动画*/
    function oAi(oai,onm){
        oai.animate({
            width:onm + 'px'
        },500)
    }
    function oAb(){
        var arr=['270','83','15'];
        for(var s=0;s<$('.swiper6 .s6_lucre li').length; s++){
             oAi($('.swiper6 .s6_lucre li .s6_line').eq(s),arr[s]);                   
        }
    }
    function oSwiper6(){
        var oS6_time = setTimeout(function(){
            $('.swiper6 .swiper_title').animate({
                opacity: 1
            },500,function(){
                $('.swiper6 h1').animate({
                    opacity: 1
                },800,function(){
                    $('.swiper6 .f6_font').animate({
                        opacity: 1
                    },800,function(){
                        $('.swiper6 .s6_main').animate({
                        opacity: 1
                    },800,function(){
                            oAb();
                            $('.swiper6 .s6_btn').animate({
                                opacity: 1
                            },500) 
                        })
                    })
                })
            })
        },800)
    }
    /*swiper6动画*/
    /*还原动画*/
    oClear1();
    function oClear1(){
        var oW2 = ['56','77','57','76','131','76','70'],oH2 = ['59','58','58','57','41','57','57'],oT2 = ['175','190','175','190','190','190','175'];
        clearInterval(S5_time);
        $oS1_icon.css({
            left: 0,
            bottom: 385 + 'px',
            opacity: 0
        })
        $oS1_zh.css({
            bottom: 425 + 'px',
            right: 0,
            opacity: 0
        })
        $oS1_img.css({
            bottom:'400px',
            opacity: 0
        })
        for(var i=0; i<$oS2_icon.length; i++){
            $oS2_icon.eq(i).css({
                backgroundImage: 'url(/public/front/images/swiper2_icon'+(i+1)+'.png)',
                width: oW2[i] + 'px',
                height: oH2[i] + 'px',
                left: -20 + 'px',
                top: oT2[i] + 'px',
                opacity: 0
            })
        }
        aOpacity($('.swiper2 .swiper_title'));
        aOpacity($('.swiper2 h1'));
        aOpacity($('.swiper2 .swiper2_font'));
        aOpacity($('.swiper2 .swiper2_img'));
        aOpacity($('.swiper3 .swiper_title'));
        aOpacity($('.swiper3 h1'));
        aOpacity($('.swiper3 .swiper3_btn'));
        aOpacity($('.swiper4 .swiper_title'));
        aOpacity($('.swiper4 h1'));
        aOpacity($('.swiper5 .swiper_title'));
        aOpacity($('.swiper5 h1'));
        aOpacity($('.swiper6 .swiper_title'));
        aOpacity($('.swiper6 h1'));
        aOpacity($('.swiper6 .f6_font'));
        aOpacity($('.swiper6 .s6_main'));
        aOpacity($('.swiper6 .s6_btn'));
        $('.swiper6 .s6_lucre li .s6_line').css('width','0px');
        $('.swiper4 .s4_main1').css({
            opacity: 0,
            marginLeft:'-800px'
        })
        $('.swiper4 .s4_main2').css({
            opacity: 0,
            marginLeft:'430px'
        })
    }
    function aOpacity(aOp){
        aOp.css('opacity','0');
    }
    /*还原动画*/
    /*判断动画*/
    function oSwitch(){
        switch (oIndex){
            case 0 : oSwiper1();
            break;
            case 1 : oSwiper2();
            break;
            case 2 : oSwiper3();
            break;
            case 3 : oSwiper4();
            break;
            case 4 : oSwiper5();
            break;
            case 5 : oSwiper6();
            break;
        }
    }
    /*判断动画*/
    /*计算类*/
    $('.swiper6 .s6_add li').eq(1).find('.s6_se').click(function(){
        $('.swiper6 .s6_add .s6_lab2').fadeIn(500);
    })
    $('.swiper6 .s6_add img').click(function(){
        $('.swiper6 .s6_add .s6_lab2').fadeIn(500);
    })
    $('.swiper6 .s6_add .s6_lab2 p').click(function(){
        oNumb = parseInt($(this).html());
        $('.swiper6 .s6_add .s6_lab2').fadeOut(500);
        $('.swiper6 .s6_add .s6_se').html($(this).html());
    })
    $('.swiper6 .s6_btn').click(function(){
        var oN = /^[0-9]+.?[0-9]*$/;
        var $oMoney = $('.swiper6 .s6_add li').eq(0).find('input').val();
        var $oPi = $('.swiper6 .s6_add li').eq(2).find('input').val();
        if($oMoney==''||$oPi==''){
            alert('值不能为空！')
        }else{
            if($oMoney.search(oN)||$oPi.search(oN)){
                alert('请输入数字！');
            }else{
                if($oPi>=100){
                    alert('您输入的利率太大！')
                }else{
                    $('.swiper6 .s6_lucre li .s6_line').css('width','0px');
                    oAb();
                    formatNum(String(($oPi/100)*$oMoney*oNumb/12),0);
                    formatNum(String(0.0245*$oMoney*oNumb/12),1);
                    formatNum(String(0.0035*$oMoney*oNumb/12),2);
                    
                } 
            }
        }
    })
    /*计算类*/
    /*添加*/
    function formatNum(str,song){
        var newStr = "";
        var count = 0;
        if(str.indexOf(".")==-1){
           for(var i=str.length-1;i>=0;i--){
         if(count % 3 == 0 && count != 0){
           newStr = str.charAt(i) + "," + newStr;
         }else{
           newStr = str.charAt(i) + newStr;
         }
         count++;
           }
           str = newStr + ".00"; //自动补小数点后两位
        }
        else
        {
           for(var i = str.indexOf(".")-1;i>=0;i--){
         if(count % 3 == 0 && count != 0){
           newStr = str.charAt(i) + "," + newStr;
         }else{
           newStr = str.charAt(i) + newStr; //逐个字符相接起来
         }
         count++;
           }
           str = newStr + (str + "00").substr((str + "00").indexOf("."),3);
         }
        $('.swiper6 .s6_lucre li').eq(song).find('.s6_num').html('约'+str+'元');
    }
    /*添加*/
})




















