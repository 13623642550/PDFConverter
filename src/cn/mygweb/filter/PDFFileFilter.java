package cn.mygweb.filter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/** 
 * PDF文件过滤器
*/
public class PDFFileFilter extends FileFilter{

	@Override
	public boolean accept(File f) {
		if(f.getName().contains(".pdf") || f.isDirectory()) {
			return true;
		}
		return false;
	}

	@Override
	public String getDescription() {
		return ".pdf";
	}

}
