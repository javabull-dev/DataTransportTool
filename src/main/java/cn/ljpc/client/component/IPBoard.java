package cn.ljpc.client.component;

import java.awt.GridLayout;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import cn.ljpc.client.util.InetAddressUtil;

public class IPBoard extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextArea textField;

	/**
	 * Create the frame.
	 */
	public IPBoard() {
		setTitle("IP地址");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(1, 0, 0, 0));
			
		//获取ip列表
		StringBuilder stringBuilder = new StringBuilder();
		List<String> ips = InetAddressUtil.getHostIp();
		if (ips != null) {
			for (int i = 0; i < ips.size(); i++) {
				stringBuilder.append(ips.get(i)).append("\n");
			}
		}
		
		textField = new JTextArea();
		textField.setText(stringBuilder.toString());
		contentPane.add(textField);
		textField.setColumns(10);
		
		setVisible(true);
	}
}
