package cn.ljpc.client.component;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;

import cn.hutool.core.util.StrUtil;

public class FileProgressor extends JFrame {

	Logger logger = Logger.getLogger(FileProgressor.class.getCanonicalName());
	private JPanel contentPane;
	public JProgressBar progressBar;
	public JLabel lbl_proc;
	public JLabel lbl_filename;
	public JLabel lbl_send_byte;
	public JLabel lbl_filedir;
	public JButton btn_opendir;
	public String filepath;

	public FileProgressor() {
		setTitle("文件传输");
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 547, 294);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);

		JLabel label = new JLabel("文件传输");
		sl_contentPane.putConstraint(SpringLayout.NORTH, label, 10, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, label, 10, SpringLayout.WEST, contentPane);
		contentPane.add(label);

		progressBar = new JProgressBar(0, 100);
		sl_contentPane.putConstraint(SpringLayout.WEST, progressBar, 6, SpringLayout.EAST, label);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, progressBar, 0, SpringLayout.SOUTH, label);
		sl_contentPane.putConstraint(SpringLayout.EAST, progressBar, 314, SpringLayout.EAST, label);
		contentPane.add(progressBar);

		lbl_proc = new JLabel("进度");
		sl_contentPane.putConstraint(SpringLayout.WEST, lbl_proc, 13, SpringLayout.EAST, progressBar);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lbl_proc, 0, SpringLayout.SOUTH, label);
		contentPane.add(lbl_proc);

		lbl_filename = new JLabel("文件名");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lbl_filename, 10, SpringLayout.SOUTH, label);
		sl_contentPane.putConstraint(SpringLayout.WEST, lbl_filename, 0, SpringLayout.WEST, label);
		contentPane.add(lbl_filename);

		lbl_send_byte = new JLabel("传输文件字节数");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lbl_send_byte, 12, SpringLayout.SOUTH, lbl_filename);
		sl_contentPane.putConstraint(SpringLayout.WEST, lbl_send_byte, 0, SpringLayout.WEST, label);
		contentPane.add(lbl_send_byte);

		lbl_filedir = new JLabel("文件夹");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lbl_filedir, 14, SpringLayout.SOUTH, lbl_send_byte);
		sl_contentPane.putConstraint(SpringLayout.WEST, lbl_filedir, 0, SpringLayout.WEST, label);
		contentPane.add(lbl_filedir);

		btn_opendir = new JButton("打开文件夹");
		btn_opendir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 打开文件夹
				try {
					Desktop.getDesktop().open(new File(filepath));
				} catch (IOException e1) {
					logger.log(Level.WARNING, StrUtil.format("{} {}", e1.getCause(), e1.getMessage()));
				}
			}
		});
		sl_contentPane.putConstraint(SpringLayout.NORTH, btn_opendir, 26, SpringLayout.SOUTH, lbl_filedir);
		sl_contentPane.putConstraint(SpringLayout.WEST, btn_opendir, 0, SpringLayout.WEST, label);
		contentPane.add(btn_opendir);
	}
}
