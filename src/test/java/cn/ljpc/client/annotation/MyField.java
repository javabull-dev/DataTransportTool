package cn.ljpc.client.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD) // 注解用于字段上
@Retention(RetentionPolicy.RUNTIME) // 保留到运行时，可通过注解获取
public @interface MyField {
	String description();

	int length();
}