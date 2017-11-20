//投标进度
    function setProgress(o, data) {
        var i = 0;
        setInterval(function () {
            i++
            if (i > data) {
                i = data
            }
            var imgLeft = -(i * 54 + (i * 9)) + 'px'
            o.style.backgroundPosition = imgLeft + '\t' + '0px'
            if (i == 100) {
			    o.innerHTML = "满";
                o.style.color = "#F8951E";
            }/*else if(i==0){
                o.innerHTML = "新";
                o.style.color = "#F8951E";				
			} */else {
          	o.innerHTML = i + '%';
            }
        }, 20)
    }
    $(".progress_circle").each(function () {
        var _t = $(this),
            progress = _t.data("progress");
        setProgress(_t.get(0), parseInt(progress));
    });
<span class="progress">
                        	<i class="progress_circle" data-progress="48.99" style="background-position: -3024px 0px;">48%</i>
                        </span>
.old_prolist span {
display: inline-block;
float: left;
color: #999;
font-size: 14px;
text-align: center;
}
.progress {
    width: 14%;
    text-align: center;
    padding-top: 4px;
}
.progress_circle {
    display: inline-block;
    width: 54px;
    height: 54px;
    line-height: 54px;
    text-align: center;
    background: url(../images/pc_xin/investjd00.png);
    font-style: normal;
    color: #F8951E;
}