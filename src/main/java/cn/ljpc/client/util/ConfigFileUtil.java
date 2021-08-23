package cn.ljpc.client.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import cn.ljpc.client.entity.IPPropertyImpl;

public class ConfigFileUtil {

	public static void saveConfigFile(IPPropertyImpl data) {
		String baseDir = System.getProperty("user.dir");
		File configFile = new File(baseDir, "conf/conf.properties");
		try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(configFile),
				Charset.forName("utf8"))) {
			Properties properties = new Properties();
			Map<String, Object> map = new HashMap<>();
			Field[] declaredFields = data.getClass().getDeclaredFields();
			for (int i = 0; i < declaredFields.length; i++) {
				Field field = declaredFields[i];
				// 通过属性获取属性值，前提是修饰符必须为public
				Object o = "";
				try {
					o = String.valueOf(field.get(data));
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} finally {
					map.put(field.getName(), o);
				}
			}
			properties.putAll(map);
			properties.store(osw, "");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
