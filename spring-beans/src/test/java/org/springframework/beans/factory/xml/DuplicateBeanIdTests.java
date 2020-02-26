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

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.testfixture.beans.TestBean;
import org.springframework.core.io.ClassPathResource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;


/**
 * 在Spring 3.1中，bean id属性（以及核心模式中的所有其他id属性）不再是xsd:id类型，而是xsd:string类型。
 * 这允许在嵌套的 beans  元素中使用相同的bean id。
 * 相同嵌套级别*内的重复id*仍将被视为ProblemReporter中的错误，因为这永远不会是预期/有效的情况。
 * 自： 3.1款 另见：
 *
 * With Spring 3.1, bean id attributes (and all other id attributes across the
 * core schemas) are no longer typed as xsd:id, but as xsd:string.  This allows
 * for using the same bean id within nested &lt;beans&gt; elements.
 *
 * Duplicate ids *within the same level of nesting* will still be treated as an
 * error through the ProblemReporter, as this could never be an intended/valid
 * situation.
 *
 * @author Chris Beams
 * @since 3.1
 * @see org.springframework.beans.factory.xml.XmlBeanFactoryTests#testWithDuplicateName
 * @see org.springframework.beans.factory.xml.XmlBeanFactoryTests#testWithDuplicateNameInAlias
 */
public class DuplicateBeanIdTests {

	/**
	 * 同一个beans标签下面，bean的id不能重复
	 */
	@Test
	public void duplicateBeanIdsWithinSameNestingLevelRaisesError() {
		DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(bf);
		assertThatExceptionOfType(Exception.class).as("duplicate ids in same nesting level").isThrownBy(() ->
			reader.loadBeanDefinitions(new ClassPathResource("DuplicateBeanIdTests-sameLevel-context.xml", this.getClass())));
	}

	/**
	 * 不同的beans标签下面，bean的id可以重复，但是重复的bean只会存在一个，TODO 是后面的配置覆盖了前面的配置吗？
	 */
	@Test
	public void duplicateBeanIdsAcrossNestingLevels() {
		DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(bf);
		reader.loadBeanDefinitions(new ClassPathResource("DuplicateBeanIdTests-multiLevel-context.xml", this.getClass()));
		TestBean testBean = bf.getBean(TestBean.class); // there should be only one
		assertThat(testBean.getName()).isEqualTo("nested");
	}
}
