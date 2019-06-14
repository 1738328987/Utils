package cn.gy.utils;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;

/**
 * 文件上传的工具类
 */
public class FileUtils {

	/**
	 * 图片的上传功能
	 * @param file
	 * @param httpSession
	 * @return
	 */
    	public String upload(MultipartFile file, HttpSession httpSession){
    		String filename = file.getOriginalFilename();//获取文件的名称
    		String filepath = httpSession.getServletContext().getRealPath("static/img");//获取当前工作路径下的虚拟路径
    		System.out.println(filename);
    		System.out.println(filepath);
    		String suffix = FilenameUtils.getExtension(filename);//获取文件后缀
    		System.out.println(suffix);
    		if(suffix.equals("jpg")){
    			File files = new File(filepath,System.currentTimeMillis()+"."+suffix);
    			if(!files.exists()){
    				files.mkdirs();//建立子目录
    			}
    			try {
    				file.transferTo(files);//保存文件
				} catch (IllegalStateException e) {
    				e.printStackTrace();
    			} catch (IOException e) {
					e.printStackTrace();
				}
			}
    		return "fileUpload";
    	}
}
