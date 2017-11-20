define(function(){    
    $(function(){
        // 支付密码弹窗 
        var parent = $(".p_tcinbox"),
            input = parent.children(".hiddentext"),
            li = parent.children("ul.flex").children("li");
        parent.click(function(){
            input.trigger("focus").addClass("focus");
            input.get(0).oninput=function(){
                li.text(" ");
                for(var i = 0; i < input.val().length; i++){
                    var vals=input.val();
                    var txt=vals.substr(i,1);
                    li.eq(i).text(txt);
                }
                if (input.val().length==6) {
                    $(".xf-yzmbtn").addClass('btn');
                } else{
                    $(".xf-yzmbtn").removeClass('btn');
                };
            } 
        });
    })
})