package cn.ljpc.client.service;

import java.awt.Color;
import java.awt.Component;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import cn.hutool.core.util.StrUtil;
import cn.ljpc.client.ui.DataTransportClient;

/**
 * 多线程同步阻塞式
 *
 * @author Jacker
 */
public class TcpServer {

	/**
	 * 日志
	 */
	private static Logger logger = Logger.getLogger(TcpServer.class.getCanonicalName());

	/**
	 * 监听端口号
	 */
	private int port;

	/**
	 * 线程池内管理的最大线程数量
	 */
	private int THREAD = 80;

	/**
	 * 保存文件的位置
	 */
	private String filepath;

	/**
	 * 数据显示的位置
	 */
	private JTextArea txta_rec_data;

	/**
	 * socket
	 */
	private ServerSocketChannel serverSocketChannel;

	/**
	 * 线程池
	 */
	ExecutorService pool;

	/**
	 *
	 */
	Component parentComponent;

	/**
	 *
	 */
	Thread thread;

	/**
	 * 
	 */
	Runnable runnable;

	/**
	 * @param filepath
	 * @param txta_rec_data
	 * @throws IOException
	 */
	public TcpServer(Component parentComponent, String filepath, JTextArea txta_rec_data) {
		this(parentComponent, 9999, filepath, txta_rec_data);
	}

	/**
	 * @param port
	 * @param filepath
	 * @param txta_rec_data
	 * @throws IOException
	 */
	public TcpServer(Component parentComponent, int port, String filepath, JTextArea txta_rec_data) {
		this.port = port;
		this.filepath = filepath;
		this.txta_rec_data = txta_rec_data;
		this.parentComponent = parentComponent;
		this.runnable = new TaskRunnable();
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getPort() {
		return port;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public String getFilepath() {
		return filepath;
	}

	/**
	 * 关闭服务器
	 */
	public void toStop() {
		try {
			if (this.serverSocketChannel != null && this.serverSocketChannel.isOpen()) {
				this.serverSocketChannel.close();
			}
			if (pool != null && pool.isShutdown()) {
				pool.shutdownNow();
			}
		} catch (IOException e) {
			logger.log(Level.INFO, StrUtil.format("{} {}", e.getCause(), e.getMessage()));
		}
	}

	/**
	 * 启动服务器
	 */
//	public void start() {
//		try (ServerSocket serverSocket = new ServerSocket()) {
//			server = serverSocket;
//			serverSocket.bind(new InetSocketAddress(port));
//			ExecutorService executorService = Executors.newFixedThreadPool(THREAD);
//
//			while (true) {
//				Socket client = serverSocket.accept();
//				AcceptSocketSwingWorker acceptSocketSwingWorker = new AcceptSocketSwingWorker(client, filepath,
//						txta_rec_data);
//				executorService.submit(acceptSocketSwingWorker);
//			}
//		} catch (Exception e) {
//			logger.log(Level.INFO, StrUtil.format("{} {}", e.getCause(), e.getMessage()));
//		}
//	}

	/**
	 * 启动服务器 ServerSocketChannel为可中断server
	 *
	 */
	public void start() {
		new Thread(this.runnable).start();
	}

	class TaskRunnable implements Runnable {
		@Override
		public void run() {
			try {
				serverSocketChannel = ServerSocketChannel.open();
				serverSocketChannel.bind(new InetSocketAddress(port));
//				pool = Executors.newFixedThreadPool(THREAD);

				while (true) {
					SocketChannel accept = serverSocketChannel.accept();
					Socket client = accept.socket();

					AcceptSocketSwingWorker acceptSocketSwingWorker = new AcceptSocketSwingWorker(client, filepath,
							txta_rec_data);
					acceptSocketSwingWorker.execute();
//					pool.submit(acceptSocketSwingWorker);
				}
			} catch (IOException e) {
				final String message = StrUtil.format("{} {}", e.getCause(), e.getMessage());
				logger.log(Level.INFO, message);
				// 在DET线程上执行
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						JOptionPane.showMessageDialog(parentComponent, message);
						DataTransportClient dataTransportClient = (DataTransportClient) parentComponent;
						dataTransportClient.btn_server_start.setText("启动");
						dataTransportClient.lbl_local_server_status.setText("服务器状态：服务器尚未启动...");
						dataTransportClient.lbl_local_server_status.setForeground(Color.BLACK);
						dataTransportClient.txt_local_port.setEnabled(true);
						dataTransportClient.combox_rec_file.setEnabled(true);
						dataTransportClient.btn_dir_select.setEnabled(true);
					}
				});
			} finally {
				if (serverSocketChannel != null) {
					try {
						serverSocketChannel.close();
					} catch (IOException e) {
						logger.log(Level.INFO, StrUtil.format("{} {}", e.getCause(), e.getMessage()));
					}
				}
			}
		}

	}

}
