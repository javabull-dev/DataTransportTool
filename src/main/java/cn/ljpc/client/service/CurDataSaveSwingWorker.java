package cn.ljpc.client.service;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import cn.hutool.core.util.StrUtil;
import cn.ljpc.client.entity.Data;

/**
 * 将当前数据保存至配置文件夹中
 * 
 * @author Jacker
 *
 */
public class CurDataSaveSwingWorker extends SwingWorker<Object, Object> {

	/**
	 * 日志
	 */
	Logger logger = Logger.getLogger(CurDataSaveSwingWorker.class.getCanonicalName());

	/**
	 * 
	 */
	Component parentComponent;

	/**
	 * 
	 */
	JTextArea txta_rec_data;

	/**
	 * 
	 */
	JTextArea txta_send_data;

	/**
	 * 
	 */
	Data data;
	
	/**
	 * 数据是否追加
	 */
	boolean append;

	public CurDataSaveSwingWorker(Component parentComponent, JTextArea txta_rec_data, JTextArea txta_send_data, boolean append) {
		if (parentComponent == null) {
			throw new IllegalArgumentException("parentComponent不能为空");
		}
		if (txta_rec_data == null) {
			throw new IllegalArgumentException("txta_rec_data不能为空");
		}
		if (txta_send_data == null) {
			throw new IllegalArgumentException("txta_send_data不能为空");
		}
		this.parentComponent = parentComponent;
		this.txta_rec_data = txta_rec_data;
		this.txta_send_data = txta_send_data;
		String rec = txta_rec_data.getText();
		String send = txta_send_data.getText();
		data = new Data();
		data.rec = rec;
		data.send = send;
		this.append = append;
	}

	@Override
	protected Object doInBackground() throws Exception {
		File baseDir = new File(System.getProperty("user.dir") + File.separator + "conf");
		// 如果不存在conf目录，就创建
		if (!baseDir.exists()) {
			if (!baseDir.mkdir()) {
				logger.log(Level.WARNING, "无法创建conf目录");
				publish(new String("无法创建conf目录"));
				return null;
			}
		}

		if(append) {// 上一次的数据
			Data appendData = null;
			try (ObjectInputStream objectInputStream = new ObjectInputStream(
					new FileInputStream(baseDir.getAbsolutePath() + File.separator + "data.dat"));) {
				appendData = (Data) objectInputStream.readObject();
			} catch (Exception e) {
				e.printStackTrace();
				logger.log(Level.WARNING, StrUtil.format("{} {}", e.getCause(), e.getMessage()));
			} finally {
				if (appendData != null) {
					data.rec = appendData.rec + "\n" + data.rec;
					data.send = appendData.send + "\n" + data.send;
				}
			}
		}

		// 将内容写入conf/data.dat中
		// todo 写入成功后没有对应的提示
		try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(
				new FileOutputStream(baseDir.getAbsolutePath() + File.separator + "data.dat"));) {
			objectOutputStream.writeObject(data);
			objectOutputStream.flush();
		} catch (IOException e1) {
			logger.log(Level.WARNING, StrUtil.format("{} {}", e1.getCause(), e1.getMessage()));
			publish(StrUtil.format("写入数据失败 {} {}", e1.getCause(), e1.getMessage()));
			return null;
		}
		publish(new String("写入成功!"));
		return null;
	}

	@Override
	protected void process(List<Object> chunks) {
		String message = (String) chunks.get(0);
		JOptionPane.showMessageDialog(parentComponent, message);
	}

	@Override
	protected void done() {
		super.done();
	}
}
