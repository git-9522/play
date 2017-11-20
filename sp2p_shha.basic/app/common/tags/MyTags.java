package common.tags;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import com.shove.Convert;

import common.enums.Area;
import common.enums.Province;
import common.utils.number.Arith;
import groovy.lang.Closure;
import play.templates.FastTags;
import play.templates.GroovyTemplate;
import play.templates.GroovyTemplate.ExecutableTemplate;

/**
 * 自定义标签
 *
 * @description
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月4日
 */
public class MyTags extends FastTags {

	/**
	 * 短格式的金额格式化标签
	 * 
	 * @description 用法 #{formatShortNumMoney money:successFinance /}
	 * 
	 * @param params
	 *            标签名称money
	 * @param body
	 * @param out
	 * @param template
	 * @param fromLine
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月4日
	 */
	public static void _formatShortNumMoney(Map<String, Object> params, Closure body, PrintWriter out,
			ExecutableTemplate template, int fromLine) {
		if (params.get("money") == null) {

			return;
		}

		String result = "";

		double money = Convert.strToDouble(params.get("money").toString(), 0);

		if (money < 10000) {
			result = String.format("%.2f", money);
		} else if (10000 <= money && money < 100000000) { // 万
			result = String.format("%.2f", Arith.round(money / 10000, 2));
			;
		} else if (100000000 <= money && money < 1000000000000.00) { // 亿
			result = String.format("%.2f", Arith.round(money / 100000000, 2));
		} else { // 万亿
			result = String.format("%.2f", Arith.round(money / 1000000000000.00, 2));
		}

		out.println(result);
	}
	
	
	/**
	 * 短格式的金额格式化标签
	 * 
	 * @description 用法 #{formatShortMoney money:successFinance /}
	 * 
	 * @param params
	 *            标签名称money
	 * @param body
	 * @param out
	 * @param template
	 * @param fromLine
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月4日
	 */
	public static void _formatShortMoney(Map<String, Object> params, Closure body, PrintWriter out,
			ExecutableTemplate template, int fromLine) {
		if (params.get("money") == null) {

			return;
		}

		String result = "";

		double money = Convert.strToDouble(params.get("money").toString(), 0);

		if (money < 10000) {
			result = String.format("%.2f", money);
		} else if (10000 <= money && money < 100000000) { // 万
			result = String.format("%.2f", Arith.round(money / 10000, 2))+"万";
			;
		} else if (100000000 <= money && money < 1000000000000.00) { // 亿
			result = String.format("%.2f", Arith.round(money / 100000000, 2))+"亿";
		} else { // 万亿
			result = String.format("%.2f", Arith.round(money / 1000000000000.00, 2))+"万亿";
		}

		out.println(result);
	}

	/**
	 * 短格式的金额格式化标签
	 * 
	 * @description 用法 #{formatHTMLShortMoney money:successFinance /}
	 * 
	 * @param params
	 *            标签名称money
	 * @param body
	 * @param out
	 * @param template
	 * @param fromLine
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月4日
	 */
	public static void _formatHTMLShortMoney(Map<String, Object> params, Closure body, PrintWriter out,
			ExecutableTemplate template, int fromLine) {
		if (params.get("money") == null) {

			return;
		}

		String result = "";

		double money = Convert.strToDouble(params.get("money").toString(), 0);

		if (money < 10000) {
			result = String.format("%.0f", money);
		} else if (10000 <= money && money < 100000000) {
			String result_temp = String.format("%.0f", Arith.round(money / 10000, 0));
			for (char t : result_temp.toCharArray()) {
				result += "<span>" + t + "</span>";
			}
			result += "万";
		} else if (100000000 <= money && money < 1000000000000.00) {
			String result_temp =((money / 100000000.00)+"").split("\\.")[0];
			for (char t : result_temp.toCharArray()) {
				result += "<span>" + t + "</span>";
			}
			result += "亿";
			String result_temp1 = String.format("%.0f", Arith.round((money-(Double.parseDouble(result_temp)*100000000)) / 10000, 0));
			for (char t : result_temp1.toCharArray()) {
				result += "<span>" + t + "</span>";
			}
			result += "万";
		} else {
			String result_temp = ((money / 1000000000000.00)+"").split("\\.")[0];
			for (char t : result_temp.toCharArray()) {
				result += "<span>" + t + "</span>";
			}
			result += "万亿";
			String result_temp1 = (((money-(Double.parseDouble(result_temp)*1000000000000.00)) / 100000000.00)+"").split("\\.")[0];
			for (char t : result_temp1.toCharArray()) {
				result += "<span>" + t + "</span>";
			}
			result += "亿";
			String result_temp2 = String.format("%.0f", Arith.round((money-(Double.parseDouble(result_temp)*1000000000000.00)-(Double.parseDouble(result_temp1)*100000000.00)) / 10000, 0));
			for (char t : result_temp2.toCharArray()) {
				result += "<span>" + t + "</span>";
			}
			result += "万";
		}

		out.println(result);
	}

	/**
	 * 整型金额格式化标签 <br>
	 * demo:#{formatIntMoney money:invest_amount}#{/formatIntMoney}
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月4日
	 */
	public static void _formatIntMoney(Map<String, Object> params, Closure body, PrintWriter out,
			ExecutableTemplate template, int fromLine) {
		if (params.get("money") == null) {

			return;
		}

		double money = Convert.strToDouble(params.get("money").toString(), 0);

		if (money % 1 == 0) {
			NumberFormat formater = new DecimalFormat("#");
			out.println(formater.format(money));

			return;
		}

		out.println(money + "");
	}

	/**
	 * 图片。目前支持的可配置的img属性：src, data-original, width, height, class, style, alt
	 * <br>
	 * demo： <br>
	 * 图片懒加载：#{img data_original:tp?.image_url, class:"lazy", width:"1920",
	 * height:"400" /} <br>
	 * 图片加载：#{img src:tp?.image_url, width:"1920", height:"400"/}
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月25日
	 */
	public static void _img(Map args, Closure body, PrintWriter out, GroovyTemplate.ExecutableTemplate template,
			int fromLine) {
		StringBuffer result = new StringBuffer("<img");

		// 必选参数图片路径：src（图片及时加载）或者data_original（图片懒加载）。
		boolean isSrc = args.containsKey("src");
		boolean isDataOriginal = args.containsKey("data_original");

		if (isSrc) {
			result.append(" src=\"").append(args.get("src")).append("\"");
		} else {
			result.append(" src=\"/public/common/imgloading.gif\""); // 图片懒加载
		}

		if (isDataOriginal) {
			result.append(" data-original=\"").append(args.get("data_original")).append("\"");
		}

		result.append(" onerror=\"this.src='/public/common/default.jpg'\""); // 默认图片

		if (args.containsKey("width")) { // 图片一定要给宽度和高度
			result.append(" width=\"").append(args.get("width")).append("\"");
		}

		if (args.containsKey("height")) {
			result.append(" height=\"").append(args.get("height")).append("\"");
		}

		if (args.containsKey("class")) {
			result.append(" class=\"").append(args.get("class")).append("\"");
		}

		if (args.containsKey("style")) {
			result.append(" style=\"").append(args.get("style")).append("\"");
		}

		if (args.containsKey("alt")) {
			result.append(" alt=\"").append(args.get("alt")).append("\"");
		}

		result.append("/>");

		out.print(result.toString());
	}

	/**
	 * 敏感信息星号处理 <br>
	 * demo: <br>
	 * 用户名：#{asterisk str:user?.name, start:2, end:2, count:4/} <br>
	 * 姓名：#{asterisk str:loanUserInfo?.reality_name, start:1, end:0, count:2/}
	 * <br>
	 * 身份证：#{asterisk str:loanUserInfo?.idNumber, start:6, end:4, count:8/} <br>
	 * 手机号：#{asterisk str:loanUserInfo?.mobile, start:3, end:4, count:4/} <br>
	 * 银行卡号：#{asterisk str:loanUserInfo?.card, start:6, end:4, count:8/}
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月26日
	 */
	public static void _asterisk(Map<String, Object> params, Closure body, PrintWriter out, ExecutableTemplate template,
			int fromLine) {
		if (params.get("str") == null) {

			return;
		}

		String str = params.get("str").toString();
		int length = str.length();
		if (length == 0) {

			return;
		}

		StringBuffer result = new StringBuffer();

		int start = Convert.strToInt(params.get("start").toString(), 0);
		if (start > 0) {
			if (start <= length) {
				result.append(str.substring(0, start));
			} else {
				result.append(str.substring(0, length));
			}
		}

		int count = Convert.strToInt(params.get("count").toString(), 3);
		for (int i = 0; i < count; i++) {
			result.append("*");
		}

		int end = Convert.strToInt(params.get("end").toString(), 0);
		if (end > 0) {
			if (end <= length) {
				result.append(str.substring(length - end, length));
			} else {
				result.append(str.substring(0, length));
			}
		}

		out.println(result.toString());
	}

	public static void _index(Map<?, ?> args, Closure body, PrintWriter out, ExecutableTemplate template,
			int fromLine) {
		if (args.get("n") == null) {
			return;
		}
		int index = 0;
		try {
			Object temp = args.get("index");
			index = Integer.parseInt(temp.toString());
		} catch (Exception e) {
			return;
		}
		if (index <= 0) {
			return;
		}
		char c = 'A';

		out.println((char) ((int) c + index - 1));
	}

	public static void _bidName(Map<?, ?> args, Closure body, PrintWriter out, ExecutableTemplate template,
			int fromLine) {
		if (args.get("name") == null) {
			return;
		}
		String name = "";
		try {
			name = args.get("name").toString();
		} catch (Exception e) {
			return;
		}
		if (name.length() == 0) {
			return;
		}
		name = name.substring(0, 1);
		if ("房".equals(name)) {
			out.print("房产质押");
		} else if ("车".equals(name)) {
			out.print("车辆质押");
		} else if ("企".equals(name)) {
			out.print("企业质押");
		} else {
			return;
		}
	}

	/**
	 * <h3>13</h3>
	 * <p>
	 * 10月
	 * </p>
	 * <span>Oct</span>
	 * 
	 * @param params
	 * @param body
	 * @param out
	 * @param template
	 * @param fromLine
	 */
	public static void _printHTMLDate(Map<String, Object> params, Closure body, PrintWriter out,
			ExecutableTemplate template, int fromLine) {
		Date data = (Date) params.get("data");
		StringBuffer sb = new StringBuffer();
		sb.append("<h3>" + new SimpleDateFormat("d").format(data) + "</h3>");
		sb.append("<p>" + new SimpleDateFormat("M").format(data) + "月</p>");
		sb.append("<span>" + new SimpleDateFormat("MMM", Locale.ENGLISH).format(data) + "</span>");
		out.println(sb.toString());
	}

	public static void areas(Map<?, ?> args, Closure body, PrintWriter out, ExecutableTemplate template, int fromLine) {
		if (args.get("code") == null) {
			return;
		}
		String code = "";
		try {
			code = args.get("code").toString();
		} catch (Exception e) {
			return;
		}
		Area[] areas = Province.getAreasByCode(code);
		if (areas == null) {
			return;
		}
		String result = "";
		for (Area area : areas) {
			result += "<option value='" + area.code + "'>" + area.name + "</option>";
		}
		out.print(result);
	}

}