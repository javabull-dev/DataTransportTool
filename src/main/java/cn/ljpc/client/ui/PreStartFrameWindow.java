package cn.ljpc.client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;

import org.aeonbits.owner.ConfigFactory;

import cn.ljpc.client.entity.IPProperty;
import cn.ljpc.client.entity.IPPropertyImpl;

public class PreStartFrameWindow extends JWindow implements Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	Logger logger = Logger.getLogger(PreStartFrameWindow.class.getCanonicalName());
	Thread splashThread; // 进度条更新线程
	JProgressBar progress; // 进度条

	public PreStartFrameWindow() {
		Container container = getContentPane(); // 得到容器
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)); // 设置光标
		URL url = getClass().getClassLoader().getResource("loading.jpg"); // 图片的位置
		if (url != null) {
			container.add(new JLabel(new ImageIcon(url)), BorderLayout.CENTER); // 增加图片
		}
		progress = new JProgressBar(1, 100); // 实例化进度条
		progress.setStringPainted(true); // 描绘文字
		progress.setString("程序正在加载,请稍候"); // 设置显示文字
		progress.setBackground(Color.white); // 设置背景色
		container.add(progress, BorderLayout.SOUTH); // 增加进度条到容器上
		Dimension screen = getToolkit().getScreenSize(); // 得到屏幕尺寸
		pack();
		setLocation(screen.width / 2 - getSize().width / 2, screen.height / 2 - getSize().height / 2); // 设置窗口位置
	}

	public void start() {
		this.toFront(); // 窗口前端显示
		splashThread = new Thread(this); // 实例化线程
		splashThread.start(); // 开始运行线程
	}

	public void run() {
		IPPropertyImpl config = null;
		setVisible(true); // 显示窗口
		try {
			for (int i = 0; i < 100; i++) {
				Thread.sleep(100);
				progress.setValue(progress.getValue() + 1);
			}
			String curDir = System.getProperties().getProperty("user.dir");
			File file = new File(curDir + File.separator + "conf");
			// 创建配置文件目录，如果存在，则略过
			if (!file.exists()) {
				if (!file.mkdir()) {
					logger.log(Level.WARNING, "无法创建文件夹");
				}
			}
			// 创建配置文件，如果存在，则读取
			String confPath = file.getAbsolutePath() + File.separator + "conf.properties";
			try {
				InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(confPath),
						Charset.forName("utf8"));
				Properties properties = new Properties();
				properties.load(inputStreamReader);
				IPProperty ipProperty = ConfigFactory.create(IPProperty.class, properties);
				config = new IPPropertyImpl(ipProperty);
			} catch (Exception e) {
				logger.log(Level.WARNING, e.getCause() + " " + e.getMessage());
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getCause() + " " + e.getMessage());
		}
		dispose(); // 释放窗口
		// 启动UI程序
		toFrame(config);
	}

	public void toFrame(IPPropertyImpl config) {
		final DataTransportClient frame = new DataTransportClient(config);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}