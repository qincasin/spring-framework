/*
 * Copyright 2002-2021 the original author or authors.
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

package org.springframework.aop.framework;

import java.io.Serializable;
import java.lang.reflect.Proxy;

import org.springframework.aop.SpringProxy;
import org.springframework.core.NativeDetector;

/**
 * Default {@link AopProxyFactory} implementation, creating either a CGLIB proxy
 * or a JDK dynamic proxy.
 *
 * <p>Creates a CGLIB proxy if one the following is true for a given
 * {@link AdvisedSupport} instance:
 * <ul>
 * <li>the {@code optimize} flag is set
 * <li>the {@code proxyTargetClass} flag is set
 * <li>no proxy interfaces have been specified
 * </ul>
 *
 * <p>In general, specify {@code proxyTargetClass} to enforce a CGLIB proxy,
 * or specify one or more interfaces to use a JDK dynamic proxy.
 *
 * 默认 {@link AopProxyFactory} 实现，创建 CGLIB 代理或 JDK 动态代理。
 * <p>如果给定的 {@link AdvisedSupport} 实例满足以下条件，则创建一个 CGLIB 代理：
 * <ul>
 *     <li>设置了 {@code optimize} 标志
 *     <li>设置了 {@code proxyTargetClass} 标志
 *     <li>没有指定代理接口
 *  <ul>
 * <p>一般来说，指定{@code proxyTargetClass}来强制使用CGLIB代理，或者指定一个或多个接口来使用JDK动态代理。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Sebastien Deleuze
 * @since 12.03.2004
 * @see AdvisedSupport#setOptimize
 * @see AdvisedSupport#setProxyTargetClass
 * @see AdvisedSupport#setInterfaces
 */
@SuppressWarnings("serial")
public class DefaultAopProxyFactory implements AopProxyFactory, Serializable {


	/**
	 * config 就是我们的 ProxyFactory 对象
	 *ProxyFactory 他是一个配置管理对象，保存着创建代理对象的所有生产资料
	 * @param config the AOP configuration in the form of an
	 * AdvisedSupport object
	 * @return
	 * @throws AopConfigException
	 */
	@Override
	public AopProxy createAopProxy(AdvisedSupport config) throws AopConfigException {
		/**
		 *  !NativeDetector.inNativeImage() 暂时先不考虑
		 *  config.isOptimize() 暂时先不考虑
		 *  config.isProxyTargetClass() ： true 表示强制使用cglig代理
		 *  hasNoUserSuppliedProxyInterfaces(config): 说明代理对象，没有实现任何接口，没有办法使用JDK动态代理，只能使用cglib动态代理
		 */
		if (!NativeDetector.inNativeImage() &&
				(config.isOptimize() || config.isProxyTargetClass() || hasNoUserSuppliedProxyInterfaces(config))) {
			Class<?> targetClass = config.getTargetClass();
			if (targetClass == null) {
				throw new AopConfigException("TargetSource cannot determine target class: " +
						"Either an interface or a target is required for proxy creation.");
			}
			//条件成立说明 targetClass 是接口 或者 已经被代理过的类型了，只能使用jdk动态代理
			if (targetClass.isInterface() || Proxy.isProxyClass(targetClass)) {
				return new JdkDynamicAopProxy(config);
			}
			return new ObjenesisCglibAopProxy(config);
		}
		else {
			//什么情况下会走到这里呢？
			//targetClass是实现了接口情况下，会走这个分支。
			return new JdkDynamicAopProxy(config);
		}
	}

	/**
	 * Determine whether the supplied {@link AdvisedSupport} has only the
	 * {@link org.springframework.aop.SpringProxy} interface specified
	 * (or no proxy interfaces specified at all).
	 * 确定提供的 {@link AdvisedSupport} 是否仅指定了
	 * {@link org.springframework.aop.SpringProxy} 接口（或根本没有指定代理接口）。
	 */
	private boolean hasNoUserSuppliedProxyInterfaces(AdvisedSupport config) {
		Class<?>[] ifcs = config.getProxiedInterfaces();
		return (ifcs.length == 0 || (ifcs.length == 1 && SpringProxy.class.isAssignableFrom(ifcs[0])));
	}

}
