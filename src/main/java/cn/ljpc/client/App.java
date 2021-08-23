package cn.ljpc.client;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

import org.aeonbits.owner.ConfigFactory;

import cn.ljpc.client.entity.IPProperty;
import cn.ljpc.client.entity.IPPropertyImpl;
import cn.ljpc.client.ui.DataTransportClient;
import cn.ljpc.client.ui.PreStartFrameWindow;

public class App {
	public static void main(String[] args) {
//		PreStartFrameWindow splash = new PreStartFrameWindow();
//		splash.start();
		IPPropertyImpl config = null;
		IPProperty ipProperty = null;
		String confPath = System.getProperty("user.dir") + File.separator + "conf/conf.properties";
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(confPath),
					Charset.forName("utf8"));
			Properties properties = new Properties();
			properties.load(inputStreamReader);
			ipProperty = ConfigFactory.create(IPProperty.class, properties);
		} catch (Exception e) {
			e.printStackTrace();
		}
		final IPPropertyImpl fconfig = new IPPropertyImpl(ipProperty);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DataTransportClient frame = new DataTransportClient(fconfig);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
