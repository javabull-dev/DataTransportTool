package cn.ljpc.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.regex.Pattern;

import org.aeonbits.owner.ConfigFactory;

import cn.hutool.core.util.StrUtil;
import cn.ljpc.client.entity.Data;
import cn.ljpc.client.entity.IPProperty;

public class Test {

	@org.junit.Test
	public void test1() {
		byte[] bytes = "hello:world".getBytes();
		String string = new String(bytes, Charset.defaultCharset());
		String string2 = string.substring(string.indexOf(":") + 1);
		System.out.println(string2);
	}

	@org.junit.Test
	public void test2() {
		System.out.println(System.getProperties().getProperty("user.dir"));
	}

	@org.junit.Test
	public void test3() {
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		URL resource = contextClassLoader.getResource(".");
		System.out.println(resource);
	}

	@org.junit.Test
	public void test4() {
		// 获取包内资源
		URL resource = Test.class.getClassLoader().getResource(".");
		// /E:/JavaCode/pure%20java/eclipse/client/target/test-classes/
		System.out.println(resource.getPath());
		String substring = resource.getPath().substring(1);
		System.out.println(substring);
	}

	@org.junit.Test
	public void test5() {
		// 获取项目路径
		System.out.println(System.getProperty("user.dir"));

		File directory = new File("");// 参数为空
		String courseFile = null;
		try {
			courseFile = directory.getCanonicalPath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(courseFile);

		// 获取类路径
		System.out.println(System.getProperty("java.class.path"));
	}

	/**
	 * 测试反射
	 */
	@org.junit.Test
	public void test6() {
		Data data = new Data();
		Class<? extends Data> clazz = data.getClass();
		Type[] genericInterfaces = clazz.getGenericInterfaces();
		for (int i = 0; i < genericInterfaces.length; i++) {
			System.out.println(genericInterfaces[i]);
		}
		System.out.println();
		// 获取属性的名
		Field[] declaredFields = clazz.getDeclaredFields();
		for (int i = 0; i < declaredFields.length; i++) {
			System.out.println(declaredFields[i]);
			Field field = declaredFields[i];
			System.out.println("属性名 " + field.getName());
			Class<?> type = field.getType();
			System.out.println("属性类型 " + type.getName());
		}
	}

	/**
	 * owner工具的测试使用
	 */
	@org.junit.Test
	public void test7() {
		String confPath = System.getProperty("user.dir") + File.separator + "conf/conf.properties";
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(confPath),
					Charset.forName("utf8"));
			Properties properties = new Properties();
			properties.load(inputStreamReader);
			IPProperty config = ConfigFactory.create(IPProperty.class, properties);
			System.out.println(config);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 测试hutool工具的StrUtil.format方法
	 */
	@org.junit.Test
	public void test8() {
		System.out.println(StrUtil.format("{}-{}", 123, "hello"));
	}

	/**
	 * 测试打开文件夹
	 */
	@org.junit.Test
	public void test9() {
//		try {
//			Desktop.getDesktop().open(new File("E:\\AndroidCode"));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	/**
	 * 正则表达式
	 */
	@org.junit.Test
	public void test10() {
		boolean matches = Pattern.matches("[0-9]{1,5}", "0");
		System.out.println(matches);
		boolean matches2 = Pattern.matches("((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)",
				"192.168.191");
		System.out.println(matches2);
	}

	/**
	 * 查看文件大小
	 */
	@org.junit.Test
	public void test11() {
		File file = new File("C:\\Users\\Jacker\\Desktop\\loop_ping.py");
		try (FileInputStream fileInputStream = new FileInputStream(file)) {
			int available = fileInputStream.available();
			System.out.println(available);
			System.out.println(file.getName());
			System.out.println(file.getUsableSpace());
			System.out.println(file.getTotalSpace());
			System.out.println(file.getFreeSpace());
			System.out.println(file.length());
			System.out.println(new File("F:\\系统镜像\\window10-1909-x64.iso").length());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@org.junit.Test
	public void test12() {
		File file = new File("C:\\Users\\Jacker\\Desktop\\loop_ping.py");
		String path = file.getPath();
		System.out.println(path);
		System.out.println(file.getParent());
	}

	@org.junit.Test
	public void test13() {
		File file = null;
		System.out.println(file instanceof File);
	}
}
