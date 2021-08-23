package cn.ljpc.client.service;

import java.awt.Component;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import cn.hutool.core.util.StrUtil;
import cn.ljpc.client.component.SendFileProgressor;

public class SendSocketSwingWorker extends SwingWorker<Object, Object> {

	/**
	 * 日志
	 */
	Logger logger = Logger.getLogger(SendSocketSwingWorker.class.getCanonicalName());

	/**
	 * 待传输的文件
	 */
	File file;

	/**
	 * 待传输的数据
	 */
	String data;

	/**
	 * 服务器的ip地址
	 */
	String ipaddr;

	/**
	 * 服务器的端口号
	 */
	int port;

	/**
	 * 显示文件传输进度
	 */
	SendFileProgressor fileProgress;

	/**
	 * parentComponent
	 */
	Component parentComponent;

	/**
	 * 传输数据显示位置
	 */
	JTextArea txta_send_data;

	/**
	 * 指定传输的对象是文件
	 * 
	 * @param file
	 * @param ipaddr
	 * @param port
	 */
	public SendSocketSwingWorker(Component parentComponent, File file, String ipaddr, int port) {
		if (file == null) {
			throw new IllegalArgumentException("file不能为空");
		}
		if (!file.exists() || !file.isFile()) {
			throw new IllegalArgumentException("file指定文件不存在");
		}
		if (ipaddr == null) {
			throw new IllegalArgumentException("ipaddr不能为空");
		}
		if (port <= 0 || port > 65535) {
			throw new IllegalArgumentException("port指定错误");
		}
		if (parentComponent == null) {
			throw new IllegalArgumentException("parentComponent不能为空");
		}
		this.file = file;
		this.ipaddr = ipaddr;
		this.port = port;
		this.parentComponent = parentComponent;
	}

	/**
	 * 指定传输的数据
	 * 
	 * @param data
	 * @param ipaddr
	 * @param port
	 */
	public SendSocketSwingWorker(Component parentComponent, JTextArea txta_send_data, String data, String ipaddr,
			int port) {
		if (data == null) {
			throw new IllegalArgumentException("data不能为空");
		}
		if (ipaddr == null) {
			throw new IllegalArgumentException("ipaddr不能为空");
		}
		if (port <= 0 || port > 65535) {
			throw new IllegalArgumentException("port指定错误");
		}
		if (parentComponent == null) {
			throw new IllegalArgumentException("parentComponent不能为空");
		}
		if (txta_send_data == null) {
			throw new IllegalArgumentException("txta_send_data不能为空");
		}
		this.data = data;
		this.ipaddr = ipaddr;
		this.port = port;
		this.parentComponent = parentComponent;
		this.txta_send_data = txta_send_data;
	}

	/**
	 * @return
	 * @throws Exception
	 */
	@Override
	protected Object doInBackground() throws Exception {
		// 1.获取传输数据的类型 file（文件名，编码格式），数据文本（编码格式）
		// 1.1 "FILE:hello.jar:12380123" --- "DATA:UTF8"
		// 2.返回收到信息的响应，比如 class OK, class NO
		// 2.1 成功，"class OK\t"，失败，"class NO\tmessage:charset unsupport"
		// 2.2 当失败时，传输停止，跳到第五步
		// 3.得到数据
		// 3.1 将得到的数据进行保存
		// 4.返回收到信息的响应，比如 data OK, data NO
		// 5.关闭连接

		// socket Stream关闭，socket即关闭
		try (Socket socket = new Socket(this.ipaddr, this.port)) {
			try (OutputStream os = new BufferedOutputStream(socket.getOutputStream());
					InputStream is = new BufferedInputStream(socket.getInputStream())) {
				if (this.file != null) {// 传输文件
					long filelength = file.length();
					// 写数据
					os.write(
							StrUtil.format("FILE:{}:{}", file.getName(), filelength).getBytes(Charset.forName("utf8")));
					os.flush();
					byte[] buf = new byte[1024];
					// 读数据
					int read = is.read(buf);
					String data = new String(buf, 0, read, Charset.forName("utf8"));
					if (data.contains("class OK")) {
						// 传输文件数据
						try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
							fileProgress = new SendFileProgressor();
							publish(fileProgress);
							int length;
							long progress = 0;
							while (-1 != (length = inputStream.read(buf, 0, buf.length))) {
								os.write(buf, 0, length);
								os.flush();
								progress += length;
								publish(Long.valueOf(progress), Long.valueOf(filelength));
							}
							Thread.sleep(500);
							publish(Long.valueOf(progress), Long.valueOf(filelength));
						} catch (Exception e) {
							logger.log(Level.WARNING, e.getCause() + " " + e.getMessage());
							publish(new String(e.getCause() + " " + e.getMessage()));
						}

					} else if (data.contains("class NO")) {
						publish(null, new String("对方无法识别发送的数据类型!"));
					} else {
						publish(null, new String("无法识别对方发送类型的应答!"));
					}
				} else if (this.data != null) {// 传输数据
					// 发送数据
					// 通过socket发送数据
					// 数据在txta_send_data中的格式如下
					//
					// 今天天气很好！
					// --------------------------
					// 数据正在发送给 192.168.19.1 80
					// 数据发送成功
					// --------------------------
					// \n
					os.write(StrUtil.format("DATA:{}", "utf8").getBytes(Charset.forName("utf8")));
					os.flush();
					byte[] buf = new byte[80];
					int read = is.read(buf);
					String data = new String(buf, 0, read, Charset.forName("utf8"));
					if (data.contains("class OK")) {
						// 在发送数据的Jtextarea中显示 "数据正在发送给 192.168.19.1 80"
						publish(txta_send_data, new String(getSeparate() + "数据正在发送给"
								+ socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + "\n"));
						os.write(this.data.getBytes("utf8"));
						os.flush();
						Thread.sleep(200);
						publish(txta_send_data, new String("数据发送成功" + getSeparate()));
					} else if (data.contains("class NO")) {
						publish(txta_send_data, new String(getSeparate() + "对方无法识别发送的数据类型!" + getSeparate()));
					} else {
						publish(txta_send_data, new String(getSeparate() + "无法识别对方发送类型的应答!" + getSeparate()));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.log(Level.WARNING, e.getCause() + " " + e.getMessage());
				publish(txta_send_data,
						new String(getSeparate() + e.getCause() + " " + e.getMessage() + getSeparate()));
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.WARNING, e.getCause() + " " + e.getMessage());
			publish(txta_send_data, new String(getSeparate() + e.getCause() + " " + e.getMessage() + getSeparate()));
		}

		return null;
	}

	public String getSeparate() {
		return "\n--------------------------\n";
	}

	/**
	 * 处理中间数据 处理异常
	 */
	@Override
	protected void process(List<Object> chunks) {
		Object object = chunks.get(0);
		if (object instanceof SendFileProgressor) {
			// 显示SendFileProgressor窗口
			SendFileProgressor fileProgressor = (SendFileProgressor) object;
			fileProgressor.setVisible(true);
		} else if (object instanceof Long) {
			if (fileProgress != null) {
				// 显示动态的数据
				Long filelength = (Long) chunks.get(1);
				Long progress = (Long) object;
				fileProgress.progressBar.setValue((int) (progress * 100 / filelength));
				fileProgress.lbl_filename.setText("文件名 " + file.getName());
				fileProgress.lbl_proc.setText(StrUtil.format("{}{}%", "进度", (int) (progress * 100 / filelength)));
				fileProgress.lbl_send_byte.setText(StrUtil.format("{} {}B/{}B", "字节数", progress, filelength));
				fileProgress.lbl_filedir.setText("文件路径 " + file.getParent());
			}
		} else if (object instanceof String) {
			if (fileProgress != null) {
				// fileProgress显示传输的状态
				JOptionPane.showMessageDialog(this.fileProgress, (String) object);
			}
		} else if (object instanceof JTextArea) {
			String message = (String) chunks.get(1);
			txta_send_data.append(message);
		}

		if (object == null) {
			// 需要传输的是文件，但是还没有显示fileProgress，出现异常情况
			JOptionPane.showMessageDialog(parentComponent, (String) chunks.get(1));
		}
	}

	@Override
	protected void done() {
		if (fileProgress == null) {
		} else {
//			fileProgress.progressBar.setValue(100);
			fileProgress.progressBar.setIndeterminate(true);
		}

	}
}
