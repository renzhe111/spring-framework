/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.beans.factory.xml;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.testfixture.beans.ITestBean;
import org.springframework.beans.testfixture.beans.TestBean;
import org.springframework.core.io.ClassPathResource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * spring2.5以后，为了简化setter方法属性注入，引用p名称空间的概念，可以将<property> 子元素，简化为<bean>元素属性配置
 * 使用p命名空间之前需要通过xmlns:c=”http://www.springframework.org/schema/p”进行声明。
 * 此测试用例就是用来测试p名称空间的
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Arjen Poutsma
 */
public class SimplePropertyNamespaceHandlerTests {

	@Test
	public void simpleBeanConfigured() throws Exception {
		DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		new XmlBeanDefinitionReader(beanFactory).loadBeanDefinitions(
				new ClassPathResource("simplePropertyNamespaceHandlerTests.xml", getClass()));
		ITestBean rob = (TestBean) beanFactory.getBean("rob");
		ITestBean sally = (TestBean) beanFactory.getBean("sally");
		assertThat(rob.getName()).isEqualTo("Rob Harrop");
		assertThat(rob.getAge()).isEqualTo(24);
		assertThat(sally).isEqualTo(rob.getSpouse());
	}

	@Test
	public void innerBeanConfigured() throws Exception {
		DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		new XmlBeanDefinitionReader(beanFactory).loadBeanDefinitions(
				new ClassPathResource("simplePropertyNamespaceHandlerTests.xml", getClass()));
		TestBean sally = (TestBean) beanFactory.getBean("sally2");
		ITestBean rob = sally.getSpouse();
		assertThat(rob.getName()).isEqualTo("Rob Harrop");
		assertThat(rob.getAge()).isEqualTo(24);
		assertThat(sally).isEqualTo(rob.getSpouse());
	}

	/**
	 * 属性定义了两次，抛异常
	 * @throws Exception
	 */
	@Test
	public void withPropertyDefinedTwice() throws Exception {
		DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		assertThatExceptionOfType(BeanDefinitionStoreException.class).isThrownBy(() ->
				new XmlBeanDefinitionReader(beanFactory).loadBeanDefinitions(
							new ClassPathResource("simplePropertyNamespaceHandlerTestsWithErrors.xml", getClass())));
	}

	@Test
	public void propertyWithNameEndingInRef() throws Exception {
		DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		new XmlBeanDefinitionReader(beanFactory).loadBeanDefinitions(
				new ClassPathResource("simplePropertyNamespaceHandlerTests.xml", getClass()));
		ITestBean sally = (TestBean) beanFactory.getBean("derivedSally");
		/**
		 * TODO 不明白为什么sally.getSpouse().getName()).isEqualTo("r")，p:spouseRef="r"是什么意思？
		 */
		assertThat(sally.getSpouse().getName()).isEqualTo("r");
	}

}
