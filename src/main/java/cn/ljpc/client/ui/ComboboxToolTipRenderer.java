package cn.ljpc.client.ui;

import java.awt.Component;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;

/**
 * combobox内容的提示
 * @author Jacker
 *
 */
public class ComboboxToolTipRenderer extends DefaultListCellRenderer {

	List<String> tooltips;

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {

		JComponent comp = (JComponent) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

		if (-1 < index && null != value && null != tooltips) {
			list.setToolTipText(tooltips.get(index));
		}
		return comp;
	}

	public void setTooltips(List<String> tooltips) {
		this.tooltips = tooltips;
	}

	public String get(int index) {
		return tooltips.get(index);
	}

	public void add(String tooltip) {
		tooltips.add(tooltip);
	}
}
