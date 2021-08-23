package cn.ljpc.client.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class InetAddressUtil {

    /** 获取主机地址 */
    public static List<String> getHostIp(){
        List<String> res = null;
        try {
        	res = getInet4Address();
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        return res;
    }

    /** 获取IPV4网络配置 */
    private static List<String> getInet4Address() throws SocketException {
        // 获取所有网卡信息
    	List<String> res = new ArrayList<>();
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface netInterface = (NetworkInterface) networkInterfaces.nextElement();
            Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress ip = (InetAddress) addresses.nextElement();
                if (ip instanceof Inet4Address) {
                    res.add(ip.getHostAddress());
                }
            }
        }
        
        return res;
    }
}
