package cn.ljpc.client.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

public class PropertiesTest {
	
	public static void savePropertiesFile() {
		String filepath = "C:\\Users\\Jacker\\Desktop\\1.properties";
		try(FileOutputStream fileOutputStream = new FileOutputStream(filepath)) {
			Properties properties = new Properties();
			properties.put("name", "lisa");
			properties.put("age", String.valueOf(20));
			
//			String[] strings = new String[] {"string1", "string2"};
//			properties.put("strings", strings);
			
//			properties.put("list", Arrays.asList("list1","list2"));
			properties.put("path", "C:\\Users\\Jacker\\Desktop\\1.properties-C:\\java");
			properties.store(fileOutputStream, "save test");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void readPropertiesFile() {
		String filepath = "C:\\Users\\Jacker\\Desktop\\1.properties";
		try(FileInputStream fileOutputStream = new FileInputStream(filepath)) {
			Properties properties = new Properties();
			properties.load(fileOutputStream);
			String paths = (String)properties.get("path");
			String[] split = paths.split("-");
			for (int i = 0; i < split.length; i++) {
				System.out.println(split[i]);
				File file = new File(split[i]);
				System.out.println(file.isFile());
			}
//			Set<Entry<Object,Object>> entrySet = properties.entrySet();
//			Iterator<Entry<Object, Object>> iterator = entrySet.iterator();
//			while (iterator.hasNext()) {
//				Entry<Object, Object> next = iterator.next();
//				System.out.println(next.getKey()+"===="+next.getValue());
//			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		readPropertiesFile();
	}
}
