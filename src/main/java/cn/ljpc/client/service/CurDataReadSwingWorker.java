package cn.ljpc.client.service;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import cn.hutool.core.util.StrUtil;
import cn.ljpc.client.entity.Data;

/**
 * 还原数据
 * 
 * @author Jacker
 */
public class CurDataReadSwingWorker extends SwingWorker<Object, Object> {

	Logger logger = Logger.getLogger(CurDataReadSwingWorker.class.getCanonicalName());

	Component parentComponent;

	JTextArea txta_rec_data;

	JTextArea txta_send_data;

	public CurDataReadSwingWorker(Component parentComponent, JTextArea txta_rec_data, JTextArea txta_send_data) {
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
	}

	@Override
	protected Object doInBackground() throws Exception {
		File file = new File(System.getProperty("user.dir") + File.separator + "conf" + File.separator + "data.dat");
		if (!file.exists()) {
			logger.log(Level.WARNING, "指定文件不存在!");
			publish(new String("指定文件不存在!"));
			return null;
		}
		try (ObjectInputStream ojInputStream = new ObjectInputStream(new FileInputStream(file))) {
			Data data = (Data) ojInputStream.readObject();
			publish(data, new String("还原成功!"));
		} catch (IOException e1) {
			logger.log(Level.WARNING, StrUtil.format("{} {}", e1.getCause(), e1.getMessage()));
			publish(StrUtil.format("读取数据失败 {} {}", e1.getCause(), e1.getMessage()));
			return null;
		}
		return null;
	}

	@Override
	protected void process(List<Object> chunks) {
		Object object = chunks.get(0);
		if (object instanceof String) {
			String message = (String) object;
			JOptionPane.showMessageDialog(this.parentComponent, message);
		} else if (object instanceof Data) {
			Data data = (Data) object;
			String message = (String) chunks.get(1);
			this.txta_rec_data.setText(data.rec);
			this.txta_send_data.setText(data.send);
			JOptionPane.showMessageDialog(this.parentComponent, message);
		}
	}

	@Override
	protected void done() {
		super.done();
	}
}
