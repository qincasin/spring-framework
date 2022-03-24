/*
 * Copyright 2002-2018 the original author or authors.
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

package org.springframework.transaction.config;

import org.w3c.dom.Element;

import org.springframework.aop.config.AopNamespaceUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.lang.Nullable;
import org.springframework.transaction.event.TransactionalEventListenerFactory;
import org.springframework.transaction.interceptor.BeanFactoryTransactionAttributeSourceAdvisor;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.util.ClassUtils;

/**
 * {@link org.springframework.beans.factory.xml.BeanDefinitionParser
 * BeanDefinitionParser} implementation that allows users to easily configure
 * all the infrastructure beans required to enable annotation-driven transaction
 * demarcation.
 *
 * <p>By default, all proxies are created as JDK proxies. This may cause some
 * problems if you are injecting objects as concrete classes rather than
 * interfaces. To overcome this restriction you can set the
 * '{@code proxy-target-class}' attribute to '{@code true}', which
 * will result in class-based proxies being created.
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Chris Beams
 * @author Stephane Nicoll
 * @since 2.0
 */
class AnnotationDrivenBeanDefinitionParser implements BeanDefinitionParser {

	/**
	 * Parses the {@code <tx:annotation-driven/>} tag. Will
	 * {@link AopNamespaceUtils#registerAutoProxyCreatorIfNecessary register an AutoProxyCreator}
	 * with the container as necessary.
	 */
	@Override
	@Nullable
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		//element----->    <tx:annotation-driven transaction-manager="transactionManager"/>

		//向spring容器注册了一个 BD --> 包装的class是 TransactionalEventListenerFactory.class

		registerTransactionalEventListenerFactory(parserContext);
		String mode = element.getAttribute("mode");
		if ("aspectj".equals(mode)) {
			// mode="aspectj"
			registerTransactionAspect(element, parserContext);
			if (ClassUtils.isPresent("javax.transaction.Transactional", getClass().getClassLoader())) {
				registerJtaTransactionAspect(element, parserContext);
			}
		}
		else {
			//我们要分析的源码入口是这里
			// mode="proxy"
			AopAutoProxyConfigurer.configureAutoProxyCreator(element, parserContext);
		}
		return null;
	}

	private void registerTransactionAspect(Element element, ParserContext parserContext) {
		String txAspectBeanName = TransactionManagementConfigUtils.TRANSACTION_ASPECT_BEAN_NAME;
		String txAspectClassName = TransactionManagementConfigUtils.TRANSACTION_ASPECT_CLASS_NAME;
		if (!parserContext.getRegistry().containsBeanDefinition(txAspectBeanName)) {
			RootBeanDefinition def = new RootBeanDefinition();
			def.setBeanClassName(txAspectClassName);
			def.setFactoryMethodName("aspectOf");
			registerTransactionManager(element, def);
			parserContext.registerBeanComponent(new BeanComponentDefinition(def, txAspectBeanName));
		}
	}

	private void registerJtaTransactionAspect(Element element, ParserContext parserContext) {
		String txAspectBeanName = TransactionManagementConfigUtils.JTA_TRANSACTION_ASPECT_BEAN_NAME;
		String txAspectClassName = TransactionManagementConfigUtils.JTA_TRANSACTION_ASPECT_CLASS_NAME;
		if (!parserContext.getRegistry().containsBeanDefinition(txAspectBeanName)) {
			RootBeanDefinition def = new RootBeanDefinition();
			def.setBeanClassName(txAspectClassName);
			def.setFactoryMethodName("aspectOf");
			registerTransactionManager(element, def);
			parserContext.registerBeanComponent(new BeanComponentDefinition(def, txAspectBeanName));
		}
	}

	private static void registerTransactionManager(Element element, BeanDefinition def) {
		//value 就是 如果配置了 transaction-manager，则读取其配置的值，否则默认读取 transactionManager
		def.getPropertyValues().add("transactionManagerBeanName",
				TxNamespaceHandler.getTransactionManagerName(element));
	}

	private void registerTransactionalEventListenerFactory(ParserContext parserContext) {
		RootBeanDefinition def = new RootBeanDefinition();
		def.setBeanClass(TransactionalEventListenerFactory.class);
		parserContext.registerBeanComponent(new BeanComponentDefinition(def,
				TransactionManagementConfigUtils.TRANSACTIONAL_EVENT_LISTENER_FACTORY_BEAN_NAME));
	}


	/**
	 * Inner class to just introduce an AOP framework dependency when actually in proxy mode.
	 */
	private static class AopAutoProxyConfigurer {
		//这个里面注入了 4个BD
		// 1.InfrastructureAdvisorAutoProxyCreator
		// 2.AnnotationTransactionAttributeSource
		// 3.TransactionInterceptor
		// 4.BeanFactoryTransactionAttributeSourceAdvisor

		public static void configureAutoProxyCreator(Element element, ParserContext parserContext) {
			//向spring容器注册BD --> InfrastructureAdvisorAutoProxyCreator.class ,BD的名称：org.springframework.aop.config.internalAutoProxyCreator
			AopNamespaceUtils.registerAutoProxyCreatorIfNecessary(parserContext, element);

			//事务切面名称
			//org.springframework.transaction.config.internalTransactionAdvisor
			String txAdvisorBeanName = TransactionManagementConfigUtils.TRANSACTION_ADVISOR_BEAN_NAME;
			//条件成立    说明 spring容器 内 不存在 事务切面的BD 信息..走if内的逻辑， 注册 事务切面 相关的逻辑
			if (!parserContext.getRegistry().containsBeanDefinition(txAdvisorBeanName)) {
				Object eleSource = parserContext.extractSource(element);


				//创建一个BD  -->  AnnotationTransactionAttributeSource.class,并给BD起了个名称: &sourceName& ,
				//   我们假设这个变量的名字 就叫做 annotationTransactionAttributeSource#1
				// Create the TransactionAttributeSource definition.
				RootBeanDefinition sourceDef = new RootBeanDefinition(
						"org.springframework.transaction.annotation.AnnotationTransactionAttributeSource");
				sourceDef.setSource(eleSource);
				sourceDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
				String sourceName = parserContext.getReaderContext().registerWithGeneratedName(sourceDef);


				// Create the TransactionInterceptor definition.
				//创建 一个BD --> TransactionInterceptor.class(事务增强器)
				RootBeanDefinition interceptorDef = new RootBeanDefinition(TransactionInterceptor.class);
				interceptorDef.setSource(eleSource);
				interceptorDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
				//向BD 添加Properties：key --> transactionManagerBeanName  --> value 就是 如果配置了 transaction-manager，则读取其配置的值，否则默认读取 transactionManager
				//添加这个properties 有什么作用？ Spring容器创建 TransactionInterceptor 实例时，会向该实例 注入 transactionManagerBeanName(这个属性在TransactionAspectSupport#transactionManagerBeanName中) 属性值
				registerTransactionManager(element, interceptorDef);
				//向BD 添加Properties：key -->transactionAttributeSource -->value  new RuntimeBeanReference(sourceName))
				//添加这个properties 有什么作用？ Spring容器创建 TransactionInterceptor 实例时，会向该实例 注入 annotationTransactionAttributeSource 对象
				interceptorDef.getPropertyValues().add("transactionAttributeSource", new RuntimeBeanReference(sourceName));
				//又是生成一个名字
				String interceptorName = parserContext.getReaderContext().registerWithGeneratedName(interceptorDef);




				//创建 一个BD --> BeanFactoryTransactionAttributeSourceAdvisor.class(事务增强器)
				// Create the TransactionAttributeSourceAdvisor definition.
				RootBeanDefinition advisorDef = new RootBeanDefinition(BeanFactoryTransactionAttributeSourceAdvisor.class);
				advisorDef.setSource(eleSource);
				advisorDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);

				//往里面加了俩属性一个是 transactionAttributeSource，另一个是 adviceBeanName

				//向BD 添加Properties：key -->transactionAttributeSource -->value  new RuntimeBeanReference(sourceName))
				//添加这个properties 有什么作用？ Spring容器创建 BeanFactoryTransactionAttributeSourceAdvisor 实例时，会向该实例 注入 annotationTransactionAttributeSource 对象
				advisorDef.getPropertyValues().add("transactionAttributeSource", new RuntimeBeanReference(sourceName));

				//向BD 添加Properties：key --> adviceBeanName  --> value interceptorName
				//添加这个properties 有什么作用？ Spring容器创建 BeanFactoryTransactionAttributeSourceAdvisor 实例时，会向该实例 注入 interceptorName
				advisorDef.getPropertyValues().add("adviceBeanName", interceptorName);

				if (element.hasAttribute("order")) {
					advisorDef.getPropertyValues().add("order", element.getAttribute("order"));
				}
				parserContext.getRegistry().registerBeanDefinition(txAdvisorBeanName, advisorDef);

				CompositeComponentDefinition compositeDef = new CompositeComponentDefinition(element.getTagName(), eleSource);
				compositeDef.addNestedComponent(new BeanComponentDefinition(sourceDef, sourceName));
				compositeDef.addNestedComponent(new BeanComponentDefinition(interceptorDef, interceptorName));
				compositeDef.addNestedComponent(new BeanComponentDefinition(advisorDef, txAdvisorBeanName));
				parserContext.registerComponent(compositeDef);
			}
		}
	}

}
