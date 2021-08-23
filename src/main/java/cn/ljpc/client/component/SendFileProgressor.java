package cn.ljpc.client.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Jacker
 *
 */
public class SendFileProgressor extends FileProgressor {

	public SendFileProgressor() {
		super();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		btn_opendir.setText("结束");
		//移除已有的事件监听器
		btn_opendir.removeActionListener(btn_opendir.getActionListeners()[0]);
		btn_opendir.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}

}
