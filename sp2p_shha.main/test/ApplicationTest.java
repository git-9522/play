import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.junit.*;

import com.shove.security.Encrypt;

import play.test.*;
import play.mvc.*;
import play.mvc.Http.*;

public class ApplicationTest extends FunctionalTest {

    @Test
    public void testThatIndexPageWorks() {
    	System.out.println(Encrypt.MD5("123456mW1rVrFYv2jz41TH"));
    }
    
    public String doGet()  
    {  
        String uriAPI = "http://localhost:9000/payment/mmm/repaymentSyn";  
        String result= "";  
//      HttpGet httpRequst = new HttpGet(URI uri);  
//      HttpGet httpRequst = new HttpGet(String uri);  
//      创建HttpGet或HttpPost对象，将要请求的URL通过构造方法传入HttpGet或HttpPost对象。  
        HttpGet httpRequst = new HttpGet(uriAPI);  
  
//      new DefaultHttpClient().execute(HttpUriRequst requst);  
        try {  
   //使用DefaultHttpClient类的execute方法发送HTTP GET请求，并返回HttpResponse对象。  
            HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequst);//其中HttpGet是HttpUriRequst的子类  
            if(httpResponse.getStatusLine().getStatusCode() == 200)  
            {  
                HttpEntity httpEntity = httpResponse.getEntity();  
                result = EntityUtils.toString(httpEntity);//取出应答字符串  
            // 一般来说都要删除多余的字符   
                result.replaceAll("\r", "");//去掉返回结果中的"\r"字符，否则会在结果字符串后面显示一个小方格    
            }  
                   else   
                        httpRequst.abort();  
           } catch (ClientProtocolException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
            result = e.getMessage().toString();  
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
            result = e.getMessage().toString();  
        }  
        return result;  
    }  
    
    public String doPost()  
    {  
        String uriAPI = "http://www.my1698.com/payment/mmm/repaymentSyn";//Post方式没有参数在这里  
        String result = "";  
        HttpPost httpRequst = new HttpPost(uriAPI);//创建HttpPost对象  
          
        try {  
            HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequst);
            System.out.println(httpResponse.getStatusLine().getStatusCode());
            if(httpResponse.getStatusLine().getStatusCode() == 200)  
            {
                HttpEntity httpEntity = httpResponse.getEntity();  
                result = EntityUtils.toString(httpEntity);//取出应答字符串  
            }  
        } catch (UnsupportedEncodingException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
            result = e.getMessage().toString();  
        }  
        catch (ClientProtocolException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
            result = e.getMessage().toString();  
        }  
        catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
            result = e.getMessage().toString();  
        }  
        return result;  
    } 
}