package cn.ljpc.client.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;

import com.sun.glass.events.KeyEvent;

import cn.hutool.core.util.StrUtil;
import cn.ljpc.client.component.IPBoard;
import cn.ljpc.client.component.JFontChooser;
import cn.ljpc.client.entity.IPPropertyImpl;
import cn.ljpc.client.service.CurDataReadSwingWorker;
import cn.ljpc.client.service.CurDataSaveSwingWorker;
import cn.ljpc.client.service.SendSocketSwingWorker;
import cn.ljpc.client.service.TcpServer;
import cn.ljpc.client.util.ConfigFileUtil;
import javax.swing.AbstractAction;
import javax.swing.Action;

public class DataTransportClient extends JFrame {

	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(DataTransportClient.class.getCanonicalName());
	private IPPropertyImpl config;
	private JTextField txt_remote_ip;
	private JLabel lbl_remote_port;
	private JTextField txt_remote_port;
	private JLabel lbl_local_port;
	public JTextField txt_local_port;
	public JButton btn_server_start;
	public JLabel lbl_local_server_status;
	private JTextArea txta_rec_data;
	private JButton btn_rec_data_clear;
	private JTextArea txta_send_data;
	private JLabel label;
	public JComboBox<String> combox_rec_file;
	private JMenu mnNewMenu;
	private JMenu menu_2;
	private JMenuItem menuItem;
	private JMenuItem mntmNewMenuItem_3;
	private JMenuItem menuItem_1;
	private JMenuItem menuItem_2;
	public JButton btn_dir_select;
	private TcpServer server;
	private JMenuItem menuItem_4;
	private final Action action = new SwingAction();
	private JMenuItem mntmIp;

	public DataTransportClient(final IPPropertyImpl config) {
		try {
			BeautyEyeLNFHelper.launchBeautyEyeLNF();
		} catch (Exception e) {
		}
		this.config = config;
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				closeWindow();
			}
		});

		setTitle("DataTransportTool");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 616, 790);
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);

		JLabel lbl_remote_ip = new JLabel("远程IP");
		springLayout.putConstraint(SpringLayout.NORTH, lbl_remote_ip, 10, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, lbl_remote_ip, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(lbl_remote_ip);

		txt_remote_ip = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, txt_remote_ip, -3, SpringLayout.NORTH, lbl_remote_ip);
		springLayout.putConstraint(SpringLayout.WEST, txt_remote_ip, 21, SpringLayout.EAST, lbl_remote_ip);
		springLayout.putConstraint(SpringLayout.EAST, txt_remote_ip, 179, SpringLayout.EAST, lbl_remote_ip);
		getContentPane().add(txt_remote_ip);
		txt_remote_ip.setColumns(10);

		lbl_remote_port = new JLabel("远程port");
		springLayout.putConstraint(SpringLayout.NORTH, lbl_remote_port, 0, SpringLayout.NORTH, lbl_remote_ip);
		springLayout.putConstraint(SpringLayout.WEST, lbl_remote_port, 39, SpringLayout.EAST, txt_remote_ip);
		getContentPane().add(lbl_remote_port);

		txt_remote_port = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, txt_remote_port, -3, SpringLayout.NORTH, lbl_remote_ip);
		springLayout.putConstraint(SpringLayout.WEST, txt_remote_port, 6, SpringLayout.EAST, lbl_remote_port);
		getContentPane().add(txt_remote_port);
		txt_remote_port.setColumns(10);

		lbl_local_port = new JLabel("本地port");
		springLayout.putConstraint(SpringLayout.NORTH, lbl_local_port, 25, SpringLayout.SOUTH, lbl_remote_ip);
		springLayout.putConstraint(SpringLayout.WEST, lbl_local_port, 0, SpringLayout.WEST, lbl_remote_ip);
		getContentPane().add(lbl_local_port);

		txt_local_port = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, txt_local_port, -3, SpringLayout.NORTH, lbl_local_port);
		springLayout.putConstraint(SpringLayout.WEST, txt_local_port, 0, SpringLayout.WEST, txt_remote_ip);
		springLayout.putConstraint(SpringLayout.EAST, txt_local_port, 74, SpringLayout.EAST, lbl_local_port);
		getContentPane().add(txt_local_port);
		txt_local_port.setColumns(10);

		btn_server_start = new JButton("启动");
		btn_server_start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String localPort = txt_local_port.getText();
				Integer port = checkPort(DataTransportClient.this, localPort);
				if (port == null) {
					return;
				}
				String text = btn_server_start.getText();
				if (text.equals("启动")) {
					btn_server_start.setText("停止");
					lbl_local_server_status.setText(StrUtil.format("服务器状态：服务器在{}端口启动...", port));
					lbl_local_server_status.setForeground(Color.RED);
					txt_local_port.setEnabled(false);
					combox_rec_file.setEnabled(false);
					btn_dir_select.setEnabled(false);
					// 开启tcp服务器
					server.setPort(port);
					server.start();
				} else if (text.equals("停止")) {
					btn_server_start.setText("启动");
					lbl_local_server_status.setText("服务器状态：服务器尚未启动...");
					lbl_local_server_status.setForeground(Color.BLACK);
					txt_local_port.setEnabled(true);
					combox_rec_file.setEnabled(true);
					btn_dir_select.setEnabled(true);
					// 关闭tcp服务器
					server.toStop();
				}
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, btn_server_start, -4, SpringLayout.NORTH, lbl_local_port);
		springLayout.putConstraint(SpringLayout.WEST, btn_server_start, 21, SpringLayout.EAST, txt_local_port);
		getContentPane().add(btn_server_start);

		lbl_local_server_status = new JLabel("服务器状态：服务器尚未启动...");
		// 设置背景颜色必须先将它设置为不透明的，因为默认是透明的
		lbl_local_server_status.setOpaque(true);
		springLayout.putConstraint(SpringLayout.NORTH, lbl_local_server_status, 0, SpringLayout.NORTH, lbl_local_port);
		springLayout.putConstraint(SpringLayout.WEST, lbl_local_server_status, 0, SpringLayout.WEST, lbl_remote_port);
		springLayout.putConstraint(SpringLayout.EAST, lbl_local_server_status, -116, SpringLayout.EAST,
				getContentPane());
		getContentPane().add(lbl_local_server_status);

		txta_rec_data = new JTextArea();
		txta_rec_data.setToolTipText("接收的内容");
		txta_rec_data.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(txta_rec_data);
		springLayout.putConstraint(SpringLayout.WEST, scrollPane, 0, SpringLayout.WEST, lbl_remote_ip);
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane, -321, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(scrollPane);

		btn_rec_data_clear = new JButton("清空");
		btn_rec_data_clear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				txta_rec_data.setText("");
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, btn_rec_data_clear, 6, SpringLayout.SOUTH, scrollPane);
		springLayout.putConstraint(SpringLayout.WEST, btn_rec_data_clear, 0, SpringLayout.WEST, lbl_remote_ip);
		getContentPane().add(btn_rec_data_clear);

		txta_send_data = new JTextArea();
		txta_send_data.setToolTipText("发送的内容");
		// 创建拖拽目标监听器
		DropTargetListener listener = new DropTargetListenerImpl();
		// 在 textArea 上注册拖拽目标监听器
		DropTarget dropTarget = new DropTarget(txta_send_data, DnDConstants.ACTION_COPY_OR_MOVE, listener, true);
		JScrollPane scrollPane2 = new JScrollPane(txta_send_data);
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane2, 6, SpringLayout.SOUTH, btn_rec_data_clear);
		springLayout.putConstraint(SpringLayout.WEST, scrollPane2, 0, SpringLayout.WEST, lbl_remote_ip);
		springLayout.putConstraint(SpringLayout.EAST, scrollPane2, 0, SpringLayout.EAST, scrollPane);
		getContentPane().add(scrollPane2);

		JButton btn_send_data_clear = new JButton("清空");
		btn_send_data_clear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 清空数据
				txta_send_data.setText("");
			}
		});
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane2, -6, SpringLayout.NORTH, btn_send_data_clear);
		springLayout.putConstraint(SpringLayout.WEST, btn_send_data_clear, 0, SpringLayout.WEST, lbl_remote_ip);
		springLayout.putConstraint(SpringLayout.SOUTH, btn_send_data_clear, -10, SpringLayout.SOUTH, getContentPane());
		getContentPane().add(btn_send_data_clear);

		final JButton btn_select_file = new JButton("选择文件");
		btn_select_file.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.showDialog(btn_select_file.getParent(), "选择文件");
				File file = fileChooser.getSelectedFile();
				if (file != null) {
					if (file.isFile()) {
						// todo 启动子线程，通过socket发送文件
						String port = txt_remote_port.getText();
						String ip = txt_remote_ip.getText();
						String ipaddr = checkIpaddr(DataTransportClient.this, ip);
						if (ipaddr == null) {
							return;
						}
						Integer portInteger = checkPort(DataTransportClient.this, port);
						if (portInteger == null) {
							return;
						}
						SendSocketSwingWorker socketSwingWorker = new SendSocketSwingWorker(DataTransportClient.this,
								file, ipaddr, portInteger);
						socketSwingWorker.execute();
					}
				}
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, btn_select_file, 0, SpringLayout.NORTH, btn_send_data_clear);
		springLayout.putConstraint(SpringLayout.WEST, btn_select_file, 18, SpringLayout.EAST, btn_send_data_clear);
		getContentPane().add(btn_select_file);

		JButton btn_send_data = new JButton("发送");
		btn_send_data.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String port = txt_remote_port.getText();
				String ip = txt_remote_ip.getText();
				String ipaddr = checkIpaddr(DataTransportClient.this, ip);
				if (ipaddr == null) {
					return;
				}
				Integer portInteger = checkPort(DataTransportClient.this, port);
				if (portInteger == null) {
					return;
				}
				String data = txta_send_data.getText();
				if (data.equals("")) {
					JOptionPane.showMessageDialog(DataTransportClient.this, "木有发送的数据泪!");
					return;
				}

				// 开启后台任务，发送数据
				SendSocketSwingWorker sendSocketSwingWorker = new SendSocketSwingWorker(DataTransportClient.this,
						txta_send_data, data, ipaddr, portInteger);
				sendSocketSwingWorker.execute();
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, btn_send_data, 0, SpringLayout.NORTH, btn_send_data_clear);
		springLayout.putConstraint(SpringLayout.EAST, btn_send_data, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(btn_send_data);

		label = new JLabel("本地接收文件目录");
		springLayout.putConstraint(SpringLayout.NORTH, label, 19, SpringLayout.SOUTH, lbl_local_port);
		springLayout.putConstraint(SpringLayout.WEST, label, 0, SpringLayout.WEST, lbl_remote_ip);
		getContentPane().add(label);

		// 保存文件的位置
//		String baseDir = (String) System.getProperties().get("user.home");
		combox_rec_file = new JComboBox<String>();
		combox_rec_file.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				switch (event.getStateChange()) {
				case ItemEvent.SELECTED:
					if(server!=null) {
						server.setFilepath((String)event.getItem());
					}else {
						logger.log(Level.INFO, "server 没有初始化");
					}
					logger.log(Level.INFO, "选中-->" + event.getItem());
					break;
				case ItemEvent.DESELECTED:
					logger.log(Level.INFO, "取消选中-->" + event.getItem());
					break;
				}
			}
		});
		ComboboxToolTipRenderer renderer = new ComboboxToolTipRenderer();
		List<String> list = new ArrayList<String>();
		Iterator<String> iterator = config.localStorePath.iterator();
		while (iterator.hasNext()) {
			String item = iterator.next();
			combox_rec_file.addItem(item);
			list.add(item);
		}
		combox_rec_file.setRenderer(renderer);
		renderer.setTooltips(list);

		springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 17, SpringLayout.SOUTH, combox_rec_file);
		springLayout.putConstraint(SpringLayout.NORTH, combox_rec_file, -4, SpringLayout.NORTH, label);
		springLayout.putConstraint(SpringLayout.WEST, combox_rec_file, 6, SpringLayout.EAST, label);
		springLayout.putConstraint(SpringLayout.EAST, combox_rec_file, 78, SpringLayout.EAST, txt_remote_port);
		getContentPane().add(combox_rec_file);

		btn_dir_select = new JButton("+");
		springLayout.putConstraint(SpringLayout.NORTH, btn_dir_select, -4, SpringLayout.NORTH, label);
		springLayout.putConstraint(SpringLayout.WEST, btn_dir_select, 6, SpringLayout.EAST, combox_rec_file);
		btn_dir_select.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jFileChooser = new JFileChooser();
				jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				jFileChooser.showDialog(DataTransportClient.this, "选择");
				File file = jFileChooser.getSelectedFile();
				if (file != null) {
					if (file.isDirectory()) {
						// 向combox中添加路径项，如果已存在，则不添加。
						DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) combox_rec_file.getModel();
						String newItem = file.getAbsolutePath();
						for (int i = 0; i < model.getSize(); i++) {
							String s = (String) model.getElementAt(i);
							if (s.equals(newItem)) {
								return;
							}
						}

						// 加入config
						DataTransportClient.this.config.localStorePath.add(newItem);
						// 加入提示
						ComboboxToolTipRenderer cellRenderer = (ComboboxToolTipRenderer) combox_rec_file.getRenderer();
						cellRenderer.add(newItem);
						model.addElement(newItem);
						// 将新添加如的项设置被选中项
						combox_rec_file.setSelectedIndex(model.getIndexOf(newItem));
						// 更新tcpserver中保存文件的位置
						server.setFilepath(newItem);
						logger.log(Level.INFO, "combobox加如目录项" + newItem);
					}
				} else {
					logger.log(Level.INFO, "用户取消选择文件夹");
				}
			}
		});
		getContentPane().add(btn_dir_select);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		mnNewMenu = new JMenu("文件(F)");
		mnNewMenu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(mnNewMenu);

		menuItem_2 = new JMenuItem("保存当前数据");
		menuItem_2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 将当前数据保存至配置文件夹中
				CurDataSaveSwingWorker saveSwingWorker = new CurDataSaveSwingWorker(DataTransportClient.this,
						txta_rec_data, txta_send_data, false);
				saveSwingWorker.execute();
			}
		});
		mnNewMenu.add(menuItem_2);

		JMenuItem menuItem_3 = new JMenuItem("还原上次数据");
		menuItem_3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CurDataReadSwingWorker dataReadSwingWorker = new CurDataReadSwingWorker(DataTransportClient.this,
						txta_rec_data, txta_send_data);
				dataReadSwingWorker.execute();
			}
		});

		menuItem_4 = new JMenuItem("保存当前数据-追加模式");
		menuItem_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 将当前数据保存至配置文件夹中
				CurDataSaveSwingWorker saveSwingWorker = new CurDataSaveSwingWorker(DataTransportClient.this,
						txta_rec_data, txta_send_data, true);
				saveSwingWorker.execute();
			}
		});
		mnNewMenu.add(menuItem_4);
		mnNewMenu.add(menuItem_3);

		mnNewMenu.addSeparator();
		JMenuItem mntmNewMenuItem = new JMenuItem("退出");
		mntmNewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeWindow();
			}
		});
		mnNewMenu.add(mntmNewMenuItem);

		JMenu mnw = new JMenu("窗口(W)");
		mnw.setMnemonic(KeyEvent.VK_E);
		menuBar.add(mnw);

		JMenuItem mntmNewMenuItem_1 = new JMenuItem("设置");
		mnw.add(mntmNewMenuItem_1);
		
		mntmIp = mnw.add(action);
		mntmIp.setText("IP列表");
		mntmIp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				IPBoard ipboard = new IPBoard();
			}
		});

		menu_2 = new JMenu("格式(O)");
		menu_2.setMnemonic(KeyEvent.VK_O);
		menuBar.add(menu_2);

		mntmNewMenuItem_3 = new JMenuItem("字体");
		mntmNewMenuItem_3.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFontChooser fontChooser = new JFontChooser();
				fontChooser.setSelectedFont(new Font(config.fontName, config.fontStyle, config.fontSize));
				int result = fontChooser.showDialog(DataTransportClient.this);
				if (result == JFontChooser.OK_OPTION) {
					Font font = fontChooser.getSelectedFont();
					config.setFont(font);
				}
			}
		});
		menu_2.add(mntmNewMenuItem_3);

		JMenu menu_1 = new JMenu("帮助(H)");
		menu_1.setMnemonic(KeyEvent.VK_H);
		menuBar.add(menu_1);

		menuItem = new JMenuItem("查看帮助(H)");
		menu_1.add(menuItem);
		menu_1.addSeparator();

		menuItem_1 = new JMenuItem("关于");
		menuItem_1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// todo 软件的about窗口

			}
		});
		menu_1.add(menuItem_1);

		// 初始化tcp服務器
		server = new TcpServer(DataTransportClient.this, (String) combox_rec_file.getSelectedItem(), txta_rec_data);

		// 初始化界面数据
		txt_local_port.setText(config.localPort);
		txt_remote_ip.setText(config.remoteIP);
		txt_remote_port.setText(String.valueOf(config.remotePort));
	}

	/**
	 * 关闭窗口的询问对话框
	 */
	private void closeWindow() {
		int i = JOptionPane.showConfirmDialog(DataTransportClient.this, "是否要退出?");
		if (i == JOptionPane.OK_OPTION) {
			if (server != null) {
				// 关闭tcp服务器
				server.toStop();
			}
			// 保存配置文件
			ConfigFileUtil.saveConfigFile(this.config);
			// 关闭窗口
			DataTransportClient.this.dispose();
		}
	}

	/**
	 * 检查端口号是否合法
	 *
	 * @param parentComponent
	 * @param localPort
	 * @return
	 */
	static Integer checkPort(Component parentComponent, String localPort) {
		if (!Pattern.matches("[0-9]{1,5}", localPort)) {
			JOptionPane.showMessageDialog(parentComponent, "请填写合法的端口号!");
			return null;
		}
		Integer port = Integer.valueOf(localPort);
		if (port <= 0 || port > 65535) {
			JOptionPane.showMessageDialog(parentComponent, "端口号范围是[1-65535]!");
			return null;
		}
		return port;
	}

	/**
	 * 检查ip地址是否合法
	 *
	 * @param parentComponent
	 * @param ipaddr
	 * @return
	 */
	static String checkIpaddr(Component parentComponent, String ipaddr) {
		if (!Pattern.matches("((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)", ipaddr)) {
			JOptionPane.showMessageDialog(parentComponent, "请填写合法的ip地址!");
			return null;
		}
		return ipaddr;
	}

	/**
	 * 用于支持将文件、文本拖拽如jtextarea中
	 * 
	 * @author Jacker
	 *
	 */
	class DropTargetListenerImpl implements DropTargetListener {

		private Integer portInteger = null;
		private String ipaddr = null;

		@Override
		public void dragEnter(DropTargetDragEvent dtde) {
		}

		@Override
		public void dragOver(DropTargetDragEvent dtde) {

		}

		@Override
		public void dropActionChanged(DropTargetDragEvent dtde) {
		}

		@Override
		public void dragExit(DropTargetEvent dte) {
		}

		/**
		 * 遍历文件
		 * 
		 * @param files
		 */
		private void traverseFile(Collection<File> files) {
			for (File file : files) {
				if (file != null) {
					if (file.isFile()) {// 文件
						SendSocketSwingWorker socketSwingWorker = new SendSocketSwingWorker(DataTransportClient.this,
								file, ipaddr, portInteger);
						socketSwingWorker.execute();
					} else if (file.isDirectory()) {// 文件夹，遍历发送
						List<File> listFiles = Arrays.asList(file.listFiles());
						traverseFile(listFiles);
					}
				}
			}
		}

		@Override
		public void drop(DropTargetDropEvent dtde) {
			boolean isAccept = false;

			try {
				/*
				 * 1. 文件: 判断拖拽目标是否支持文件列表数据（即拖拽的是否是文件或文件夹, 支持同时拖拽多个）
				 */
				if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
					// 接收拖拽目标数据
					dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
					isAccept = true;

					// 以文件集合的形式获取数据
					@SuppressWarnings("unchecked")
					List<File> files = (List<File>) dtde.getTransferable()
							.getTransferData(DataFlavor.javaFileListFlavor);
					if (files != null) {
						// 检查输入的ip和port是否合法
						String port = txt_remote_port.getText();
						String ip = txt_remote_ip.getText();
						String ipaddr = checkIpaddr(DataTransportClient.this, ip);
						if (ipaddr == null) {
							dtde.dropComplete(true);
							return;
						}
						Integer portInteger = checkPort(DataTransportClient.this, port);
						if (portInteger == null) {
							dtde.dropComplete(true);
							return;
						}
						this.portInteger = portInteger;
						this.ipaddr = ipaddr;
						traverseFile(files);
					}
				}

				/*
				 * 2. 文本: 判断拖拽目标是否支持文本数据（即拖拽的是否是文本内容, 或者是否支持以文本的形式获取）
				 */
				if (dtde.isDataFlavorSupported(DataFlavor.stringFlavor)) {
					// 接收拖拽目标数据
					dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
					isAccept = true;

					// 以文本的形式获取数据
					String text = dtde.getTransferable().getTransferData(DataFlavor.stringFlavor).toString();

					// 输出到文本区域
					txta_send_data.append(text);
				}

//				/*
//				 * 3. 图片: 判断拖拽目标是否支持图片数据。注意: 拖拽图片不是指以文件的形式拖拽图片文件,
//				 * 而是指拖拽一个正在屏幕上显示的并且支持拖拽的图片（例如网页上显示的图片）。
//				 */
//				if (dtde.isDataFlavorSupported(DataFlavor.imageFlavor)) {
//					// 接收拖拽目标数据
//					dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
//					isAccept = true;
				//
//					// 以图片的形式获取数据
//					Image image = (Image) dtde.getTransferable().getTransferData(DataFlavor.imageFlavor);
				//
//					// 获取到 image 对象后, 可以对该图片进行相应的操作（例如: 用组件显示、图形变换、保存到本地等）,
//					// 这里只把图片的宽高输出到文本区域
//					txta_send_data.append("图片: " + image.getWidth(null) + " * " + image.getHeight(null) + "\n");
//				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			// 如果此次拖拽的数据是被接受的, 则必须设置拖拽完成（否则可能会看到拖拽目标返回原位置, 造成视觉上以为是不支持拖拽的错误效果）
			if (isAccept) {
				dtde.dropComplete(true);
			}
		}

	}
	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, "SwingAction");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
}
