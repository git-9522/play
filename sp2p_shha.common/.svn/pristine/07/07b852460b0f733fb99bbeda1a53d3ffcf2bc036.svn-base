package common.utils;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * 
 * 生成水印
 * 
 */
public class ImageMarkUtil {

	/** 水印透明度 */
	private static float alpha = 0.5f;
	/** 水印图片旋转角度 */
	@SuppressWarnings("unused")
	private static double degree = 0f;
	/** 水印图片间隔 */
	private static int interval = 0;

	/**
	 * 设置水印参数，不设置就使用默认值
	 * 
	 * @param alpha
	 *            水印透明度
	 * @param degree
	 *            水印图片旋转角度 *
	 * @param interval
	 *            水印图片间隔
	 */
	public static void setImageMarkOptions(float alpha, int degree, int interval) {
		if (alpha != 0.0f) {
			ImageMarkUtil.alpha = alpha;
		}
		if (degree != 0f) {
			ImageMarkUtil.degree = degree;
		}
		if (interval != 0f) {
			ImageMarkUtil.interval = interval;
		}

	}

	/**
	 * 给图片添加水印图片
	 * 
	 * @param waterImgPath
	 *            水印图片路径
	 * @param srcImgPath
	 *            源图片路径
	 * @param targerPath
	 *            目标图片路径
	 */
	public static void waterMarkByImg(String waterImgPath, String srcImgPath, String targerPath) throws Exception {
		waterMarkByImg(waterImgPath, srcImgPath, targerPath, 0);
	}

	/**
	 * 给图片添加水印图片
	 * 
	 * @param waterImgPath
	 *            水印图片路径
	 * @param srcImgPath
	 *            源图片路径
	 * @param targerPath
	 *            目标图片路径
	 */
	public static void waterMarkByImg(String waterImgPath, String srcImgPath) {
		try {
			waterMarkByImg(waterImgPath, srcImgPath, srcImgPath, 0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 给图片添加水印图片、可设置水印图片旋转角度
	 * 
	 * @param waterImgPath
	 *            水印图片路径
	 * @param srcImgPath
	 *            源图片路径
	 * @param targerPath
	 *            目标图片路径
	 * @param degree
	 *            水印图片旋转角度
	 */
	public static void waterMarkByImg(String waterImgPath, String srcImgPath, String targerPath, double degree)
			throws Exception {
		OutputStream os = null;
		try {

			Image srcImg = ImageIO.read(new File(srcImgPath));

			BufferedImage buffImg = new BufferedImage(srcImg.getWidth(null), srcImg.getHeight(null),
					BufferedImage.TYPE_INT_RGB);

			// 1、得到画笔对象
			Graphics2D g = buffImg.createGraphics();

			// 2、设置对线段的锯齿状边缘处理
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.drawImage(srcImg.getScaledInstance(srcImg.getWidth(null), srcImg.getHeight(null), Image.SCALE_SMOOTH), 0,
					0, null);
			// 3、设置水印旋转
			if (0 != degree) {
				g.rotate(Math.toRadians(degree), (double) buffImg.getWidth() / 2, (double) buffImg.getHeight() / 2);
			}

			// 4、水印图片的路径 水印图片一般为gif或者png的，这样可设置透明度
			ImageIcon imgIcon = new ImageIcon(waterImgPath);

			// 5、得到Image对象。
			Image img = imgIcon.getImage();

			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));

			// 6、水印图片的位置
			for (int height = interval + imgIcon.getIconHeight(); height < buffImg.getHeight(); height = height
					+ interval + imgIcon.getIconHeight()) {
				for (int weight = interval + imgIcon.getIconWidth(); weight < buffImg.getWidth(); weight = weight
						+ interval + imgIcon.getIconWidth()) {
					g.drawImage(img, weight - imgIcon.getIconWidth(), height - imgIcon.getIconHeight(), null);
				}
			}
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
			// 7、释放资源
			g.dispose();

			// 8、生成图片
			os = new FileOutputStream(targerPath);
			ImageIO.write(buffImg, "JPG", os);

			System.out.println("图片完成添加水印图片");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != os)
					os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {

		System.out.println("..添加水印图片开始...");
		// watermarkPath 水印图片地址 uploadPath 上传成功后文件地址
		String watermarkPath = "D://logo.png";
		String uploadPath = "D://card.png";
		// ImageMarkUtil.waterMarkByImg(watermarkPath, uploadPath);
		// 如果你觉得水印间隔太近了，可以通过下面代码设置间隔：
		// 修改默认参数
		ImageMarkUtil.setImageMarkOptions(0.0f, 0, 20);
		ImageMarkUtil.waterMarkByImg(watermarkPath, uploadPath);
		System.out.println("..添加水印图片结束...");
	}

}
