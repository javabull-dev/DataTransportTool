package cn.ljpc.client.service;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import cn.hutool.core.util.StrUtil;
import cn.ljpc.client.component.FileProgressor;

/**
 * doInBackground():执行后台的任务并返回这一工作的结果 process( List< V > data )
 * :处理事件分配线程中的中间进度数据 void publish(V... data)
 * :传递中间进度数据到事件分配线程。从doInBackground调用这一方法 void execute() :为工作器线程的执行预定这个工作器。
 * SwingWorker.StateValue getState() :得到这个工作器线程的状态，值为PENDING、STARTED或DONE之一
 * <p>
 * 接收数据swingworker
 *
 * @author Jacker
 */
public class AcceptSocketSwingWorker extends SwingWorker<Object, Object> {

	/**
	 * 日志
	 */
	private Logger logger = Logger.getLogger(AcceptSocketSwingWorker.class.getCanonicalName());

	/**
	 * 客户端socket连接
	 */
	private Socket client;

	/**
	 * 接收文件后所要存放文件的路径
	 */
	private String filepath;

	/**
	 * 接收数据存放显示的位置
	 */
	private JTextArea txta_rec_data;

	/**
	 * 文件接收的进度条
	 */
	private FileProgressor fileProgressor;

	/**
	 * 所要接收的文件名
	 */
	private String filename = "";

	/**
	 * @param client
	 * @param filepath
	 * @param txta_rec_data
	 */
	public AcceptSocketSwingWorker(Socket client, String filepath, JTextArea txta_rec_data) {
		if (client == null) {
			throw new IllegalArgumentException("client不能为空");
		}
		if (filepath == null) {
			throw new IllegalArgumentException("filepath不能空");
		}
		if (txta_rec_data == null) {
			throw new IllegalArgumentException("txta_rec_data不能为空");
		}
		this.client = client;
		this.filepath = filepath;
		this.txta_rec_data = txta_rec_data;
		this.fileProgressor = new FileProgressor();
		this.fileProgressor.filepath = this.filepath;
	}

	@Override
	protected Object doInBackground() throws Exception {
		if (client.isConnected()) {
			try (BufferedInputStream fis = new BufferedInputStream(client.getInputStream());
					BufferedOutputStream bos = new BufferedOutputStream(client.getOutputStream())) {
				// 1.获取传输数据的类型 file（文件名，编码格式），数据文本（编码格式）
				// 1.1 "FILE:hello.jar:12380123" --- "DATA:UTF8"
				// 2.返回收到信息的响应，比如 class OK, class NO
				// 2.1 成功，"class OK\t"，失败，"class NO\tmessage:charset unsupport"
				// 2.2 当失败时，传输停止，跳到第五步
				// 3.得到数据
				// 3.1 将得到的数据进行保存
				// 4.返回收到信息的响应，比如 data OK, data NO
				// 5.关闭连接
				byte[] buffer = new byte[1024];
				int len;
				String encoding = "";
				Long filesize = 0L;
				// 使用while循环会出现死锁
				if (-1 != (len = fis.read(buffer, 0, buffer.length))) {
					// 对buffer中的数据处理
					StringBuffer stringBuffer = new StringBuffer(4);
					for (int i = 0; i < 4; i++) {
						char ch = (char) buffer[i];
						stringBuffer.append(ch);
					}
					String content = stringBuffer.toString();
					if (content.equals("DATA")) {
						// 字符串文本数据
						filename = "";

					} else if (content.equals("FILE")) {
						// 文件
					} else {
						// 未知的数据类型
						throw new Exception("未知数据类型");
					}
				}
				String c = new String(buffer, 0, len, StandardCharsets.UTF_8);
				String substring = c.substring(c.indexOf(":") + 1);
				String[] split = substring.split(":");
				if (split.length == 1) {// 接送文本数据
					encoding = substring;
					bos.write("class OK".getBytes(Charset.forName("utf8")));
					bos.flush();
				} else if (split.length == 2) {// 接收文件数据
					this.filename = split[0];
					filesize = Long.parseLong(split[1]);
					bos.write("class OK".getBytes(Charset.forName("utf8")));
					bos.flush();
				} else {// 其他数据
					bos.write("class NO".getBytes(Charset.forName("utf8")));
					bos.flush();
					// 关闭socket连接
					client.close();
					return null;
				}

				if (this.filename.length() != 0) {
					publish(fileProgressor);
					// 保存文件
					byte[] buf = new byte[1024];
					try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
							new FileOutputStream(this.filepath + File.separator + this.filename))) {
						int length;
						long summary = 0L;
						while (-1 != (length = fis.read(buf, 0, buf.length))) {
							bufferedOutputStream.write(buf, 0, length);
							bufferedOutputStream.flush();
							// 显示文件传输速率
							// @Param filesize 文件的总大小
							// @Param summary 文件的已保存的大小
							summary += length;
							publish(filesize, summary);
						}
						Thread.sleep(500);
						publish(filesize, summary);
					} catch (Exception e) {
						logger.log(Level.INFO, e.getCause() + " " + e.getMessage());
					}
				} else {
					// 显示文本数据
					InetAddress inetAddress = client.getInetAddress();
					publish(txta_rec_data, StrUtil.format("接收来自{}:{}的数据\n{}", inetAddress.getHostAddress(),
							client.getPort(), getSeparate()));
					String readData = readData(fis);
					// publish不能够太紧密
					Thread.sleep(200);
					publish(txta_rec_data, StrUtil.format("{}\n{}", readData, getSeparate()));
				}
			} catch (Exception e) {
				logger.log(Level.INFO, e.getMessage());
			}
		}
		return null;
	}

	@Override
	protected void process(List<Object> chunks) {
		Object object = chunks.get(0);
		if (object instanceof JTextArea) {
			txta_rec_data.append((String) chunks.get(1));
		} else if (object instanceof Long) {
			// 显示动态的数据
			Long filelength = (Long) object;
			Long summary = (Long) chunks.get(1);
			fileProgressor.progressBar.setValue((int) (summary * 100 / filelength));
			fileProgressor.lbl_proc.setText(StrUtil.format("{}{}%", "进度", (int) (summary * 100 / filelength)));
			fileProgressor.lbl_filename.setText("文件名 " + filename);
			fileProgressor.lbl_filedir.setText("文件路径 " + filepath);
			fileProgressor.lbl_send_byte.setText(StrUtil.format("{} {}B/{}B", "字节数", summary, filelength));
		} else if (object instanceof FileProgressor) {
			// 接收文件，显示接收文件的进度条
			this.fileProgressor.setVisible(true);
		}
	}

	private String readData(BufferedInputStream fis) throws IOException {
		byte[] buffer = new byte[1024];
		int len;
		StringBuffer stringBuffer = new StringBuffer();
		while (-1 != (len = fis.read(buffer, 0, buffer.length))) {
			stringBuffer.append(new String(buffer, 0, len, StandardCharsets.UTF_8));
		}
		return stringBuffer.toString();
	}

	@Override
	protected void done() {
		if (this.fileProgressor != null) {
			this.fileProgressor.progressBar.setIndeterminate(true);
		}
		super.done();
	}

	public String getSeparate() {
		return "--------------------------\n";
	}
}
