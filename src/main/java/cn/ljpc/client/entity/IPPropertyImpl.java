package cn.ljpc.client.entity;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

public class IPPropertyImpl {
	
	public String remoteIP;

	public int remotePort;

	public String localPort;

	public int fontSize;

	public String fontName;

	public int fontStyle;
	
	public List<String> localStorePath = new ArrayList<String>();

	public IPPropertyImpl(IPProperty ipProperty) {
		this.remoteIP = ipProperty.getRemoteIP();
		this.remotePort = ipProperty.getRemotePort();
		this.localPort = ipProperty.getLocalPort();
		this.fontSize = ipProperty.getFontSize();
		this.fontName = ipProperty.getFontName();
		this.fontStyle = ipProperty.getFontStyle();
		//对字符串进行分割
		String local = ipProperty.getLocalStorePath();
		if(local!=null && !local.equals("")) {
			//默认的分割符是, 
			String substring = local.substring(local.indexOf("[")+1, local.indexOf("]"));
			String[] split = substring.split(",");
			for (int i = 0; i < split.length; i++) {
				localStorePath.add(split[i].trim());
			}
		}
	}

	public void setFont(Font font) {
		this.fontName = font.getName();
		this.fontSize = font.getSize();
		this.fontStyle = font.getStyle();
	}

	public Font getFont() {
		return new Font(fontName, fontStyle, fontSize);
	}
}
