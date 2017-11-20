package common.utils;

import java.util.LinkedList;
import java.util.List;

/**
 * 数组工具类
 * 
 * @author yanpengfei
 */
public class ArrayUtil {

	/**
	 * 去除数组中重复的记录
	 * 
	 * @param a
	 * 
	 * @return
	 */
	public static String[] arrayUnique(String[] strArray) {
	    List<String> list = new LinkedList<String>();
	    
	    for (int i = 0; i < strArray.length; i++) {
	        if (!list.contains(strArray[i])) {
	            list.add(strArray[i]);
	        }
	    }
	    
	    return (String[])list.toArray(new String[list.size()]);
	}
	
}
