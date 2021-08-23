package cn.ljpc.client.entity;

import org.aeonbits.owner.Config;

public interface IPProperty extends Config {

	@Key("remoteIP")
	String getRemoteIP();

	@Key("remotePort")
	int getRemotePort();

	@Key("localPort")
	String getLocalPort();

	@Key("fontSize")
	int getFontSize();

	@Key("fontName")
	String getFontName();

	@Key("fontStyle")
	int getFontStyle();
	
	@Key("localStorePath")
	String getLocalStorePath();
	
}
