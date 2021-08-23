package cn.ljpc.client;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadFactory;

import javax.swing.SwingWorker;

import org.junit.Test;

import cn.ljpc.client.annotation.MyField;
import cn.ljpc.client.entity.Apple;
import sun.awt.AppContext;

public class Test2 {

	/**
	 * ExecutorService
	 */
	@Test
	public void test1() {
		ExecutorService pool = Executors.newFixedThreadPool(50);
		Callable<Void> callable = new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				Thread.sleep(4000);
				return null;
			}
		};
		Future<Void> result = pool.submit(callable);
		pool.shutdown();

		try {
			System.out.println("获取返回值开始");
			System.out.println(result.get());
			System.out.println("获取返回值结束");
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

	}

	static class IntegerCallable<T> implements Callable<T> {

		@Override
		public T call() throws Exception {
			Thread.sleep(4000);
			return null;
		}
	}

	/**
	 * 使用Callable+FutureTask的方式实现调用 get方法一直在阻塞
	 */
	@Test
	public void test2() {
		Callable<Integer> callable = new IntegerCallable<Integer>();
		FutureTask<Integer> futureTask = new FutureTask<Integer>(callable);
		Thread thread = new Thread(futureTask);
		thread.start();

		try {
			System.out.println("获取返回值开始");
			futureTask.get();
			System.out.println("获取返回值结束");
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 线程池的控制由Swing框架处理
	 */
	@Test
	public void test3() {
		ThreadFactory defaultThreadFactory = Executors.defaultThreadFactory();
		System.out.println(defaultThreadFactory);
		ThreadFactory defaultThreadFactory2 = Executors.defaultThreadFactory();
		System.out.println(defaultThreadFactory2);

		System.out.println();
		final AppContext appContext = AppContext.getAppContext();
		ExecutorService executorService = (ExecutorService) appContext.get(SwingWorker.class);
		System.out.println(executorService);
		ExecutorService executorService2 = (ExecutorService) appContext.get(SwingWorker.class);
		System.out.println(executorService2);
		System.out.println();
	}

	/**
	 * 自定义注解
	 */
	@Test
	public void test4() {
		Field[] declaredFields = Apple.class.getDeclaredFields();
		for (int i = 0; i < declaredFields.length; i++) {
			Field field = declaredFields[i];
			if (field.isAnnotationPresent(MyField.class)) {
				MyField annotation = field.getAnnotation(MyField.class);
				System.out.println(annotation.description() + ":" + annotation.length());
			}
		}
	}

	@Test
	public void test5() throws IllegalArgumentException, IllegalAccessException {
		Apple apple = new Apple();
		apple.setId(10);
		apple.setName("name");
		Field[] declaredFields = apple.getClass().getDeclaredFields();
		for (int i = 0; i < declaredFields.length; i++) {
			Field field = declaredFields[i];
			if (field.isAccessible()) {
				Object object = field.get(apple);
				System.out.println(object);
			}
		}
	}

	private void multiArgs(String... strs) {
		for (int i = 0; i < strs.length; i++) {
			System.out.println(strs[i]);
		}
	}

	@Test
	public void test6() {
		multiArgs("hello", "world");
	}

	enum type {
		hello, world
	};

	private void name(Object... objects) {
		type t = (type) objects[0];
		switch (t) {
		case hello:
			System.out.println("hello");
			break;
		default:
			break;
		}
	}

	@Test
	public void test7() {
		name(type.hello);
	}
	
	@Test
	public void test8() {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String format = dateFormat.format(date);
		System.out.println(format);
	}
}
