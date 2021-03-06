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

		JLabel lbl_remote_ip = new JLabel("??????IP");
		springLayout.putConstraint(SpringLayout.NORTH, lbl_remote_ip, 10, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, lbl_remote_ip, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(lbl_remote_ip);

		txt_remote_ip = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, txt_remote_ip, -3, SpringLayout.NORTH, lbl_remote_ip);
		springLayout.putConstraint(SpringLayout.WEST, txt_remote_ip, 21, SpringLayout.EAST, lbl_remote_ip);
		springLayout.putConstraint(SpringLayout.EAST, txt_remote_ip, 179, SpringLayout.EAST, lbl_remote_ip);
		getContentPane().add(txt_remote_ip);
		txt_remote_ip.setColumns(10);

		lbl_remote_port = new JLabel("??????port");
		springLayout.putConstraint(SpringLayout.NORTH, lbl_remote_port, 0, SpringLayout.NORTH, lbl_remote_ip);
		springLayout.putConstraint(SpringLayout.WEST, lbl_remote_port, 39, SpringLayout.EAST, txt_remote_ip);
		getContentPane().add(lbl_remote_port);

		txt_remote_port = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, txt_remote_port, -3, SpringLayout.NORTH, lbl_remote_ip);
		springLayout.putConstraint(SpringLayout.WEST, txt_remote_port, 6, SpringLayout.EAST, lbl_remote_port);
		getContentPane().add(txt_remote_port);
		txt_remote_port.setColumns(10);

		lbl_local_port = new JLabel("??????port");
		springLayout.putConstraint(SpringLayout.NORTH, lbl_local_port, 25, SpringLayout.SOUTH, lbl_remote_ip);
		springLayout.putConstraint(SpringLayout.WEST, lbl_local_port, 0, SpringLayout.WEST, lbl_remote_ip);
		getContentPane().add(lbl_local_port);

		txt_local_port = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, txt_local_port, -3, SpringLayout.NORTH, lbl_local_port);
		springLayout.putConstraint(SpringLayout.WEST, txt_local_port, 0, SpringLayout.WEST, txt_remote_ip);
		springLayout.putConstraint(SpringLayout.EAST, txt_local_port, 74, SpringLayout.EAST, lbl_local_port);
		getContentPane().add(txt_local_port);
		txt_local_port.setColumns(10);

		btn_server_start = new JButton("??????");
		btn_server_start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String localPort = txt_local_port.getText();
				Integer port = checkPort(DataTransportClient.this, localPort);
				if (port == null) {
					return;
				}
				String text = btn_server_start.getText();
				if (text.equals("??????")) {
					btn_server_start.setText("??????");
					lbl_local_server_status.setText(StrUtil.format("??????????????????????????????{}????????????...", port));
					lbl_local_server_status.setForeground(Color.RED);
					txt_local_port.setEnabled(false);
					combox_rec_file.setEnabled(false);
					btn_dir_select.setEnabled(false);
					// ??????tcp?????????
					server.setPort(port);
					server.start();
				} else if (text.equals("??????")) {
					btn_server_start.setText("??????");
					lbl_local_server_status.setText("???????????????????????????????????????...");
					lbl_local_server_status.setForeground(Color.BLACK);
					txt_local_port.setEnabled(true);
					combox_rec_file.setEnabled(true);
					btn_dir_select.setEnabled(true);
					// ??????tcp?????????
					server.toStop();
				}
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, btn_server_start, -4, SpringLayout.NORTH, lbl_local_port);
		springLayout.putConstraint(SpringLayout.WEST, btn_server_start, 21, SpringLayout.EAST, txt_local_port);
		getContentPane().add(btn_server_start);

		lbl_local_server_status = new JLabel("???????????????????????????????????????...");
		// ?????????????????????????????????????????????????????????????????????????????????
		lbl_local_server_status.setOpaque(true);
		springLayout.putConstraint(SpringLayout.NORTH, lbl_local_server_status, 0, SpringLayout.NORTH, lbl_local_port);
		springLayout.putConstraint(SpringLayout.WEST, lbl_local_server_status, 0, SpringLayout.WEST, lbl_remote_port);
		springLayout.putConstraint(SpringLayout.EAST, lbl_local_server_status, -116, SpringLayout.EAST,
				getContentPane());
		getContentPane().add(lbl_local_server_status);

		txta_rec_data = new JTextArea();
		txta_rec_data.setToolTipText("???????????????");
		txta_rec_data.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(txta_rec_data);
		springLayout.putConstraint(SpringLayout.WEST, scrollPane, 0, SpringLayout.WEST, lbl_remote_ip);
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane, -321, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(scrollPane);

		btn_rec_data_clear = new JButton("??????");
		btn_rec_data_clear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				txta_rec_data.setText("");
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, btn_rec_data_clear, 6, SpringLayout.SOUTH, scrollPane);
		springLayout.putConstraint(SpringLayout.WEST, btn_rec_data_clear, 0, SpringLayout.WEST, lbl_remote_ip);
		getContentPane().add(btn_rec_data_clear);

		txta_send_data = new JTextArea();
		txta_send_data.setToolTipText("???????????????");
		// ???????????????????????????
		DropTargetListener listener = new DropTargetListenerImpl();
		// ??? textArea ??????????????????????????????
		DropTarget dropTarget = new DropTarget(txta_send_data, DnDConstants.ACTION_COPY_OR_MOVE, listener, true);
		JScrollPane scrollPane2 = new JScrollPane(txta_send_data);
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane2, 6, SpringLayout.SOUTH, btn_rec_data_clear);
		springLayout.putConstraint(SpringLayout.WEST, scrollPane2, 0, SpringLayout.WEST, lbl_remote_ip);
		springLayout.putConstraint(SpringLayout.EAST, scrollPane2, 0, SpringLayout.EAST, scrollPane);
		getContentPane().add(scrollPane2);

		JButton btn_send_data_clear = new JButton("??????");
		btn_send_data_clear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// ????????????
				txta_send_data.setText("");
			}
		});
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane2, -6, SpringLayout.NORTH, btn_send_data_clear);
		springLayout.putConstraint(SpringLayout.WEST, btn_send_data_clear, 0, SpringLayout.WEST, lbl_remote_ip);
		springLayout.putConstraint(SpringLayout.SOUTH, btn_send_data_clear, -10, SpringLayout.SOUTH, getContentPane());
		getContentPane().add(btn_send_data_clear);

		final JButton btn_select_file = new JButton("????????????");
		btn_select_file.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.showDialog(btn_select_file.getParent(), "????????????");
				File file = fileChooser.getSelectedFile();
				if (file != null) {
					if (file.isFile()) {
						// todo ????????????????????????socket????????????
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

		JButton btn_send_data = new JButton("??????");
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
					JOptionPane.showMessageDialog(DataTransportClient.this, "????????????????????????!");
					return;
				}

				// ?????????????????????????????????
				SendSocketSwingWorker sendSocketSwingWorker = new SendSocketSwingWorker(DataTransportClient.this,
						txta_send_data, data, ipaddr, portInteger);
				sendSocketSwingWorker.execute();
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, btn_send_data, 0, SpringLayout.NORTH, btn_send_data_clear);
		springLayout.putConstraint(SpringLayout.EAST, btn_send_data, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(btn_send_data);

		label = new JLabel("????????????????????????");
		springLayout.putConstraint(SpringLayout.NORTH, label, 19, SpringLayout.SOUTH, lbl_local_port);
		springLayout.putConstraint(SpringLayout.WEST, label, 0, SpringLayout.WEST, lbl_remote_ip);
		getContentPane().add(label);

		// ?????????????????????
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
						logger.log(Level.INFO, "server ???????????????");
					}
					logger.log(Level.INFO, "??????-->" + event.getItem());
					break;
				case ItemEvent.DESELECTED:
					logger.log(Level.INFO, "????????????-->" + event.getItem());
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
				jFileChooser.showDialog(DataTransportClient.this, "??????");
				File file = jFileChooser.getSelectedFile();
				if (file != null) {
					if (file.isDirectory()) {
						// ???combox??????????????????????????????????????????????????????
						DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) combox_rec_file.getModel();
						String newItem = file.getAbsolutePath();
						for (int i = 0; i < model.getSize(); i++) {
							String s = (String) model.getElementAt(i);
							if (s.equals(newItem)) {
								return;
							}
						}

						// ??????config
						DataTransportClient.this.config.localStorePath.add(newItem);
						// ????????????
						ComboboxToolTipRenderer cellRenderer = (ComboboxToolTipRenderer) combox_rec_file.getRenderer();
						cellRenderer.add(newItem);
						model.addElement(newItem);
						// ???????????????????????????????????????
						combox_rec_file.setSelectedIndex(model.getIndexOf(newItem));
						// ??????tcpserver????????????????????????
						server.setFilepath(newItem);
						logger.log(Level.INFO, "combobox???????????????" + newItem);
					}
				} else {
					logger.log(Level.INFO, "???????????????????????????");
				}
			}
		});
		getContentPane().add(btn_dir_select);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		mnNewMenu = new JMenu("??????(F)");
		mnNewMenu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(mnNewMenu);

		menuItem_2 = new JMenuItem("??????????????????");
		menuItem_2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// ??????????????????????????????????????????
				CurDataSaveSwingWorker saveSwingWorker = new CurDataSaveSwingWorker(DataTransportClient.this,
						txta_rec_data, txta_send_data, false);
				saveSwingWorker.execute();
			}
		});
		mnNewMenu.add(menuItem_2);

		JMenuItem menuItem_3 = new JMenuItem("??????????????????");
		menuItem_3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CurDataReadSwingWorker dataReadSwingWorker = new CurDataReadSwingWorker(DataTransportClient.this,
						txta_rec_data, txta_send_data);
				dataReadSwingWorker.execute();
			}
		});

		menuItem_4 = new JMenuItem("??????????????????-????????????");
		menuItem_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// ??????????????????????????????????????????
				CurDataSaveSwingWorker saveSwingWorker = new CurDataSaveSwingWorker(DataTransportClient.this,
						txta_rec_data, txta_send_data, true);
				saveSwingWorker.execute();
			}
		});
		mnNewMenu.add(menuItem_4);
		mnNewMenu.add(menuItem_3);

		mnNewMenu.addSeparator();
		JMenuItem mntmNewMenuItem = new JMenuItem("??????");
		mntmNewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeWindow();
			}
		});
		mnNewMenu.add(mntmNewMenuItem);

		JMenu mnw = new JMenu("??????(W)");
		mnw.setMnemonic(KeyEvent.VK_E);
		menuBar.add(mnw);

		JMenuItem mntmNewMenuItem_1 = new JMenuItem("??????");
		mnw.add(mntmNewMenuItem_1);
		
		mntmIp = mnw.add(action);
		mntmIp.setText("IP??????");
		mntmIp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				IPBoard ipboard = new IPBoard();
			}
		});

		menu_2 = new JMenu("??????(O)");
		menu_2.setMnemonic(KeyEvent.VK_O);
		menuBar.add(menu_2);

		mntmNewMenuItem_3 = new JMenuItem("??????");
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

		JMenu menu_1 = new JMenu("??????(H)");
		menu_1.setMnemonic(KeyEvent.VK_H);
		menuBar.add(menu_1);

		menuItem = new JMenuItem("????????????(H)");
		menu_1.add(menuItem);
		menu_1.addSeparator();

		menuItem_1 = new JMenuItem("??????");
		menuItem_1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// todo ?????????about??????

			}
		});
		menu_1.add(menuItem_1);

		// ?????????tcp?????????
		server = new TcpServer(DataTransportClient.this, (String) combox_rec_file.getSelectedItem(), txta_rec_data);

		// ?????????????????????
		txt_local_port.setText(config.localPort);
		txt_remote_ip.setText(config.remoteIP);
		txt_remote_port.setText(String.valueOf(config.remotePort));
	}

	/**
	 * ??????????????????????????????
	 */
	private void closeWindow() {
		int i = JOptionPane.showConfirmDialog(DataTransportClient.this, "????????????????");
		if (i == JOptionPane.OK_OPTION) {
			if (server != null) {
				// ??????tcp?????????
				server.toStop();
			}
			// ??????????????????
			ConfigFileUtil.saveConfigFile(this.config);
			// ????????????
			DataTransportClient.this.dispose();
		}
	}

	/**
	 * ???????????????????????????
	 *
	 * @param parentComponent
	 * @param localPort
	 * @return
	 */
	static Integer checkPort(Component parentComponent, String localPort) {
		if (!Pattern.matches("[0-9]{1,5}", localPort)) {
			JOptionPane.showMessageDialog(parentComponent, "???????????????????????????!");
			return null;
		}
		Integer port = Integer.valueOf(localPort);
		if (port <= 0 || port > 65535) {
			JOptionPane.showMessageDialog(parentComponent, "??????????????????[1-65535]!");
			return null;
		}
		return port;
	}

	/**
	 * ??????ip??????????????????
	 *
	 * @param parentComponent
	 * @param ipaddr
	 * @return
	 */
	static String checkIpaddr(Component parentComponent, String ipaddr) {
		if (!Pattern.matches("((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)", ipaddr)) {
			JOptionPane.showMessageDialog(parentComponent, "??????????????????ip??????!");
			return null;
		}
		return ipaddr;
	}

	/**
	 * ???????????????????????????????????????jtextarea???
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
		 * ????????????
		 * 
		 * @param files
		 */
		private void traverseFile(Collection<File> files) {
			for (File file : files) {
				if (file != null) {
					if (file.isFile()) {// ??????
						SendSocketSwingWorker socketSwingWorker = new SendSocketSwingWorker(DataTransportClient.this,
								file, ipaddr, portInteger);
						socketSwingWorker.execute();
					} else if (file.isDirectory()) {// ????????????????????????
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
				 * 1. ??????: ??????????????????????????????????????????????????????????????????????????????????????????, ???????????????????????????
				 */
				if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
					// ????????????????????????
					dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
					isAccept = true;

					// ????????????????????????????????????
					@SuppressWarnings("unchecked")
					List<File> files = (List<File>) dtde.getTransferable()
							.getTransferData(DataFlavor.javaFileListFlavor);
					if (files != null) {
						// ???????????????ip???port????????????
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
				 * 2. ??????: ??????????????????????????????????????????????????????????????????????????????, ?????????????????????????????????????????????
				 */
				if (dtde.isDataFlavorSupported(DataFlavor.stringFlavor)) {
					// ????????????????????????
					dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
					isAccept = true;

					// ??????????????????????????????
					String text = dtde.getTransferable().getTransferData(DataFlavor.stringFlavor).toString();

					// ?????????????????????
					txta_send_data.append(text);
				}

//				/*
//				 * 3. ??????: ???????????????????????????????????????????????????: ?????????????????????????????????????????????????????????,
//				 * ???????????????????????????????????????????????????????????????????????????????????????????????????????????????
//				 */
//				if (dtde.isDataFlavorSupported(DataFlavor.imageFlavor)) {
//					// ????????????????????????
//					dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
//					isAccept = true;
				//
//					// ??????????????????????????????
//					Image image = (Image) dtde.getTransferable().getTransferData(DataFlavor.imageFlavor);
				//
//					// ????????? image ?????????, ????????????????????????????????????????????????: ??????????????????????????????????????????????????????,
//					// ????????????????????????????????????????????????
//					txta_send_data.append("??????: " + image.getWidth(null) + " * " + image.getHeight(null) + "\n");
//				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			// ??????????????????????????????????????????, ??????????????????????????????????????????????????????????????????????????????, ?????????????????????????????????????????????????????????
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
