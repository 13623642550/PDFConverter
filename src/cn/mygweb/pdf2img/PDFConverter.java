package cn.mygweb.pdf2img;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import cn.mygweb.filter.PDFFileFilter;

/**
 * PDf转换的桌面端小工具
 * 
 * 	转换导出参考：https://www.cnblogs.com/ssh2/p/3463199.html
 * 
 */
public class PDFConverter implements ActionListener {

	public static void main(String[] args) {
		new PDFConverter();
	}

	private static JFrame jf = new JFrame("PDF转换器");// 创建窗口

	private static JPanel panel1 = new JPanel();// 选择文件panel
	private static JPanel panel2 = new JPanel();// 输出目录panel
	private static JPanel panel3 = new JPanel();// 转换按钮panel
	private static JPanel panel4 = new JPanel();// 日志信息panel

	private static final JTextField textField1 = new JTextField(30);// 选择文件文本框
	private static final JTextField textField2 = new JTextField(30);// 输出目录文本框

	private static JButton btn1 = new JButton("选择文件");// 选择文件按钮
	private static JButton btn2 = new JButton("输出目录");// 输出目录按钮
	private static JButton btn3 = new JButton("开始转换");// 开始转换按钮

	private static JTextArea textArea = new JTextArea();// 日志信息文本框
	private static JScrollPane scrollPane;// 滚动窗口

	private static JFileChooser fileChooser = new JFileChooser();// 文件选择组件

	private static Image ICON = Toolkit.getDefaultToolkit().getImage("images/favicon.jpg");// logo图片

	public static int DEFAULT_TEXT_FONT_SIZE = 14;// 默认字体大小
	public static int DEFAULT_BTN_FONT_SIZE = 12;// 默认按钮字体大小

	/**
	 * 创建好窗体对象
	 */
	public PDFConverter() {

		// 1. 创建一个顶层容器（窗口）
		jf.setIconImage(ICON);
		jf.setResizable(false);
		jf.setSize(600, 400);// 设置窗口大小
		jf.setLocationRelativeTo(null);// 把窗口位置设置到屏幕中心
		jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);// 当点击窗口的关闭按钮时退出程序（没有这一句，程序不会退出）

		// 2. 创建中间容器（面板容器）
		panel1.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));// 设置边距
		panel4.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

		// 3. 创建组件，并添加到面板容器中
		textField1.setPreferredSize(new Dimension(400, 30));
		textField1.setFont(new Font(null, Font.PLAIN, DEFAULT_TEXT_FONT_SIZE));

		btn1.setPreferredSize(new Dimension(90, 30));
		btn1.setFont(new Font(null, Font.BOLD, DEFAULT_BTN_FONT_SIZE));// 字体
		btn1.setBackground(new Color(37, 165, 247));// 背景色
		btn1.setForeground(Color.WHITE);// 按钮内部字体颜色
		btn1.setFocusPainted(false);// 去掉字体边框
		btn1.setBorderPainted(false);// 去掉按钮边框
		btn1.setCursor(new Cursor(Cursor.HAND_CURSOR));
		panel1.add(btn1);
		panel1.add(textField1);

		//文本框
		textField2.setPreferredSize(new Dimension(400, 30));
		textField2.setFont(new Font(null, Font.PLAIN, DEFAULT_TEXT_FONT_SIZE));

		btn2.setPreferredSize(new Dimension(90, 30));
		btn2.setFont(new Font(null, Font.BOLD, DEFAULT_BTN_FONT_SIZE));
		btn2.setBackground(new Color(37, 165, 247));
		btn2.setForeground(Color.WHITE);
		btn2.setFocusPainted(false);
		btn2.setBorderPainted(false);
		btn2.setCursor(new Cursor(Cursor.HAND_CURSOR));
		panel2.add(btn2);
		panel2.add(textField2);

		//转换按钮
		btn3.setFont(new Font(null, Font.BOLD, 18));
		btn3.setBackground(new Color(37, 165, 247));
		btn3.setForeground(Color.WHITE);
		btn3.setFocusPainted(false);
		btn3.setBorderPainted(false);
		btn3.setCursor(new Cursor(Cursor.HAND_CURSOR));
		panel3.add(btn3);

		//日志框
		textArea.setLineWrap(true);// 自动换行
		textArea.setFont(new Font(null, Font.PLAIN, DEFAULT_TEXT_FONT_SIZE));
		textArea.setAutoscrolls(true);
		// 创建滚动面板
		scrollPane = new JScrollPane(textArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setPreferredSize(new Dimension(500, 220));
		scrollPane.setAutoscrolls(true);
		panel4.add(scrollPane);

		// 4. 把 面板容器 作为窗口的内容面板 设置到 窗口
		Box vBox = Box.createVerticalBox();
		vBox.add(panel1);
		vBox.add(panel2);
		vBox.add(panel3);
		vBox.add(panel4);
		jf.setContentPane(vBox);

		btn1.addActionListener(this);
		btn2.addActionListener(this);
		btn3.addActionListener(this);

		// 5. 显示窗口，前面创建的信息都在内存中，通过 jf.setVisible(true) 把内存中的窗口显示在屏幕上。
		jf.pack();
		jf.setVisible(true);
	}

	/**
	 * 事件监听，进行相应的操作
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn1) {
			setChoosenFilePath(0, textField1);
		} else if (e.getSource() == btn2) {
			setChoosenFilePath(1, textField2);
		} else if (e.getSource() == btn3) {
			String filePath = textField1.getText().trim();
			String resDir = textField2.getText().trim();
			if ("".equals(filePath) || "".equals(resDir)) {
				JOptionPane.showMessageDialog(jf, "请填充完整信息", "警告", JOptionPane.WARNING_MESSAGE);
				return;
			} else {
				pdf2png(textField1.getText().trim(), textField2.getText().trim(), textArea);
			}
		}
	}

	/**
	 * 	通过文件选择器设置选择的路径
	 * @param mode
	 * @param jt
	 */
	public void setChoosenFilePath(int mode, JTextField jt) {
		// 将文件选择风格设置为windows风格
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		SwingUtilities.updateComponentTreeUI(fileChooser);

		// 设置文件选择器只能选择0（文件），1（文件夹）
		fileChooser.setFileSelectionMode(mode);
		if(mode == 0) {//设置默认只显示pdf文件
			FileFilter pdfFilter = new PDFFileFilter();
			fileChooser.setFileFilter(pdfFilter);
		}
		// 打开文件浏览器，点击取消则返回1
		int status = fileChooser.showOpenDialog(jf);// 打开对话框，并显示默认的logo，设置为null则显示java的logo
		if (status == 1) {
			return;
		} else {
			File file = fileChooser.getSelectedFile();
			jt.setText(file.getAbsolutePath());
		}
	}
	
	/**
	 * 将pdf转为图片，并写入日志信息
	 * 
	 * @param fileAddress
	 * @param dirAddress
	 * @param textArea
	 */
	public static void pdf2png(String fileAddress, String dirAddress, JTextArea textArea) {
		File file = new File(fileAddress);
		try {
			PDDocument document = PDDocument.load(file);
			PDFRenderer renderer = new PDFRenderer(document);
			int pages = document.getNumberOfPages();
			for (int i = 0; i < pages; i++) {
				BufferedImage image = renderer.renderImageWithDPI(i, 144);
				File dir = new File(dirAddress);

				if (!dir.exists())
					dir.mkdir();

				String resultName = dir + "\\" + (i + 1) + ".png";
				ImageIO.write(image, "PNG", new File(resultName));
				flushTextArea("生成图片： " + resultName + "\r\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
			flushTextArea(e.getMessage());
		}
		flushTextArea("\r\n转换完成!");
	}

	/**
	 * 	刷新滚动区域的文本信息
	 * @param msg
	 */
	public static void flushTextArea(String msg) {
		textArea.append(msg);
		textArea.paintImmediately(textArea.getBounds());// 实时输出

		//自动实时向下滚动
		JScrollBar vBar = scrollPane.getVerticalScrollBar();
		vBar.setSize(5, vBar.getHeight());
		vBar.setValue(vBar.getMaximum());
		vBar.paint(vBar.getGraphics());
		textArea.scrollRectToVisible(textArea.getVisibleRect());
	}

}
