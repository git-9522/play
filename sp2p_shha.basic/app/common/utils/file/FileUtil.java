package common.utils.file;

import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang.StringUtils;

import common.utils.DateUtil;
import common.utils.LoggerUtil;
import common.utils.ResultInfo;
import common.utils.number.Arith;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;
import play.Logger;
import play.Play;
import play.db.jpa.Blob;

/**
 * 文件操作
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月4日
 */
public class FileUtil {

	/**
	 * 图片上传(支持gif,jpg,jpeg,png,bmp)
	 *
	 * @param file 会根据上传文件的实际mime类型来判断是否是图片类型,也会判断后缀名是否合法
	 * @param error
	 * @return 失败后返回null，且error实体的code值为负<br>
	 * 成功后返回Map(<br>
	 * 		<b>fileSuffix</b>:[文件后缀名],<br>
	 * 		<b>size</b>:[上传后文件大小,Kb为单位],<br>
	 * 		<b>fileName</b>:[上传后文件名称，也即上传后data/attachments目录下的UUID值],<br>
	 * 		<b>staticFileName</b>:[上传后静态资源文件路径:/data/attachements/uuid],<br>
	 * 		<b>imageResolution</b>:[文件分辨率，xx*xx]<br>
	 * )
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月4日
	 */
	public static ResultInfo uploadImgags(File file){
		ResultInfo result = new ResultInfo();
		
		if (file == null) {
			result.code = -1;
			result.msg = "上传的文件为空";

			return result;
		}
	
		if(Arith.div(file.length(), 1024*1024, 2) > 2){
			result.code = -1;
			result.msg = "上传的文件过大!";

			return result;
		}
		
		common.enums.FileType imageType = common.enums.FileType.FYPE_IMG;
		
		String fileName = file.getName();
		String fileExt = fileName.substring(fileName.lastIndexOf(".")+1);
		boolean flag = false;
		String[] suffexs = {"jpg","jpeg","png","gif"};
		for (String imgext : suffexs) {
			if (StringUtils.isNotBlank(fileExt) && fileExt.equalsIgnoreCase(imgext)) {
				flag = true;

				break;
			}
		}
		if (!flag) {
			result.code = -2;
			result.msg = "文件后缀不正确，请选择正确的图片文件!";

			return result;
		}
		
		//使用JMimeMagic查找该文件的mime类型
		String fileMime = getMIME(file);
		if (fileMime == null) {
			result.code = -3;
			result.msg = "未知的文件类型";
			
			return result;
		} else {
			
			common.enums.FileType fileType = common.enums.FileType.getEnumByMime(fileMime);
			if (fileType == null || (!fileType.equals(imageType))) {
				result.code = -4;
				result.msg = "请检查您的图片是否合法!";

				return result;
			} else{
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(file);
				} catch (FileNotFoundException e) {
					Logger.error(e, "找不到对应的图片");
					result.code = -5;
					result.msg = "找不到文件"+file.getName();
					
					return result;
				}
					
				
				Blob blob = new Blob();
				blob.set(fis, "");
				String filePre = "/images?uuid=";
				
				
				Map<String, Object> fileInfo = new HashMap<String, Object>();
				
				//文件名后缀
				fileInfo.put("fileSuffix", fileExt);//后缀
				
				//压缩后的大小
				fileInfo.put("size", Arith.div(file.length(), 1024, 2));
				
				//使用images方式的文件名
				fileInfo.put("fileName", filePre + blob.getUUID() + "." + fileExt);//
				
				//以静态资源保存的文件名
				fileInfo.put("staticFileName", "/"+Play.configuration.getProperty("attachments.path", "attachments")+"/"+blob.getUUID());//
				
				/* 获取图片分辨率 */
				try {
					Image src = javax.imageio.ImageIO.read(file);
					/* 图片高度 */
					fileInfo.put("height", src.getHeight(null));
					/* 图片宽度 */
					fileInfo.put("width", src.getWidth(null));
					fileInfo.put("imageResolution", src.getWidth(null)+"*"+src.getHeight(null));
				} catch (IOException e) {
					LoggerUtil.info(false, "获取图片分辨率失败");
					result.code = -5;
					result.msg = "找不到文件"+file.getName();
					
					return result;
				}
				
				result.code = 1;
				result.msg = "上传成功";
				result.obj = fileInfo;
				
				return result;
			}
		}
		
	}
	
	
	/**
	 * COPY文件
	 * 
	 * @param srcFile
	 *            源文件路径
	 * @param desFile
	 *            复制后的目标文件路径
	 * @return boolean
	 */
	public static boolean copyToFile(String srcFile, String desFile) {
		File scrfile = new File(srcFile);
		if (scrfile.isFile() == true) {
			int length;
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(scrfile);
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
			}
			File desfile = new File(desFile);
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(desfile, false);
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
			}
			desfile = null;
			length = (int) scrfile.length();
			byte[] b = new byte[length];
			try {
				fis.read(b);
				fis.close();
				fos.write(b);
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			scrfile = null;
			return false;
		}
		scrfile = null;
		return true;
	}
	
	
	
	
	public static File zipImages(String[] images, String targetZipFile) {
		Blob blob = new Blob();
		File targetFile = new File(targetZipFile);
		FileOutputStream target = null;
		
		try {
			target = new FileOutputStream(targetFile);
		} catch (FileNotFoundException e1) {
		}
			
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(target));
		int BUFFER_SIZE = 1024;
		int count;
		byte buff[] = new byte[BUFFER_SIZE];
		File file = null;
		
		for (int i = 0; i < images.length; i++) {
			file = new File(blob.getStore(), images[i].split("\\.")[0]);
			FileInputStream fi = null;
			
			try {
				fi = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				continue;
			}
			
			BufferedInputStream origin = new BufferedInputStream(fi);
			ZipEntry entry = new ZipEntry(file.getName() + ".png");
			
			try {
				out.putNextEntry(entry);
			} catch (IOException e) {
				continue;
			}
			
			
			try {
				while ((count = origin.read(buff)) != -1) {
					out.write(buff, 0, count);
				}
			} catch (IOException e) {
				continue;
			}
			
			try {
				origin.close();
			} catch (IOException e) {
				continue;
			}
		}
		
		try {
			out.close();
		} catch (IOException e) {
		}

		return targetFile;
	}
	
	public static File getStore(String path) {
        String name = path;
        File store = null;
        if(new File(name).isAbsolute()) {
            store = new File(name);
        } else {
            store = Play.getFile(name);
        }
        if(!store.exists()) {
            store.mkdirs();
        }
        return store;
    }
	
    public static byte[] getFile(String path) throws Exception {  
        byte[] b = null;  
        File file = new File(path);  
  
        FileInputStream fis = null;  
        ByteArrayOutputStream ops = null;  
        try {  
  
            if (!file.exists()) {  
                System.out.println("文件不存在！");  
            }  
            if (file.isDirectory()) {  
                System.out.println("不能上传目录！");  
            }  
  
            byte[] temp = new byte[2048];  
  
            fis = new FileInputStream(file);  
            ops = new ByteArrayOutputStream(2048);  
  
            int n;  
            while ((n = fis.read(temp)) != -1) {  
                ops.write(temp, 0, n);  
            }  
            b = ops.toByteArray();  
        } catch (Exception e) {  
            throw new Exception();  
        } finally {  
            if (ops != null) {  
                ops.close();  
            }  
            if (fis != null) {  
                fis.close();  
            }  
        }  
        return b;  
    }
    
    public static File strToFile(byte[] b, String path)  {  
    	File file = new File(path);  
    	FileOutputStream fis = null;  
    	BufferedOutputStream bos = null;  
    	try {  
    		fis = new FileOutputStream(file);  
    		bos = new BufferedOutputStream(fis);  
    		bos.write(b);  
    	} catch (Exception e) {  
    		e.printStackTrace();  
    	} finally {  
    		if (bos != null) {  
    			try {
					bos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
    		}  
    
    		if (fis != null) {  
    			try {
    				fis.close();
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}  
    		}
    	}
    	
    	return file;
    	
    }
	
	/**
	 * 创建文件夹
	 * @param path
	 */
	public static void mkDir(String path){
		File file = new File(path);
		if(!file.exists()){
			file.mkdirs();
		}
	}
	
	/**
	 * 根据当前日期获取文件夹路径 eg:2015\1\3
	 * @return
	 */
	public static String getPathByCurrentDate(){
		String path = File.separator + DateUtil.getCurrentYear()+ File.separator + DateUtil.getCurrentMonth() + File.separator + DateUtil.getCurrentDay();
		return path;
	}
	
	
	/**
	 * 获取文件的MIME码(该方法不是以后缀作为判断标准，准确性比较高)
	 * @param file
	 * @return 如果有该mime类型则返回，否则返回null
	 */
	public static String getMIME(File file){
		MagicMatch match;
		Magic parser = new Magic() ;  
		String mime=null;
			try {
				match = parser.getMagicMatch(file,false);
				mime = match.getMimeType();
			} catch (Exception e) {
				e.printStackTrace();
			}
		return mime;
	}
}
