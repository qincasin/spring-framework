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

package org.springframework.aop;

import java.lang.reflect.Method;

/**
 * Part of a {@link Pointcut}: Checks whether the target method is eligible for advice.
 * {@link Pointcut} 的一部分：检查目标方法是否有资格获得通知。
 *
 * <p>A MethodMatcher may be evaluated <b>statically</b> or at <b>runtime</b> (dynamically).
 * Static matching involves method and (possibly) method attributes. Dynamic matching
 * also makes arguments for a particular call available, and any effects of running
 * previous advice applying to the joinpoint.
 * <p>可以<b>静态<b>或在<b>运行时<b>（动态）评估MethodMatcher。
 * 静态匹配涉及方法和（可能）方法属性。动态匹配还使特定调用的参数可用，并且运行先前建议的任何效果都适用于连接点
 *
 * <p>If an implementation returns {@code false} from its {@link #isRuntime()}
 * method, evaluation can be performed statically, and the result will be the same
 * for all invocations of this method, whatever their arguments. This means that
 * if the {@link #isRuntime()} method returns {@code false}, the 3-arg
 * {@link #matches(java.lang.reflect.Method, Class, Object[])} method will never be invoked.
 * <p>如果实现从其 {@link isRuntime()} 方法返回 {@code false}，则可以静态执行评估，并且该方法的所有调用的结果将相同，无论其参数如何。
 * 这意味着如果 {@link isRuntime()} 方法返回 {@code false}，则永远不会调用 3-arg {@link matches(java.lang.reflect.Method, Class, Object[])} 方法。
 *
 * <p>If an implementation returns {@code true} from its 2-arg
 * {@link #matches(java.lang.reflect.Method, Class)} method and its {@link #isRuntime()} method
 * returns {@code true}, the 3-arg {@link #matches(java.lang.reflect.Method, Class, Object[])}
 * method will be invoked <i>immediately before each potential execution of the related advice</i>,
 * to decide whether the advice should run. All previous advice, such as earlier interceptors
 * in an interceptor chain, will have run, so any state changes they have produced in
 * parameters or ThreadLocal state will be available at the time of evaluation.
 * <p>如果一个实现从它的 2-arg {@link matches(java.lang.reflect.Method, Class)} 方法返回 {@code true}
 * 并且它的 {@link isRuntime()} 方法返回 {@code true} ,
 * 3-arg {@link matches(java.lang.reflect.Method, Class, Object[])} 方法将在每次可能执行相关通知之前立即调用<i>，
 * 以确定通知是否应该运行。
 * 所有先前的通知，例如拦截器链中的早期拦截器，都将运行，因此它们在参数或 ThreadLocal 状态中产生的任何状态更改都将在评估时可用。
 *
 * <p>Concrete implementations of this interface typically should provide proper
 * implementations of {@link Object#equals(Object)} and {@link Object#hashCode()}
 * in order to allow the matcher to be used in caching scenarios &mdash; for
 * example, in proxies generated by CGLIB.
 * <p>此接口的具体实现通常应提供 {@link Objectequals(Object)} 和 {@link ObjecthashCode()} 的正确实现，
 * 以便允许在缓存场景中使用匹配器 - 例如，在由CGLIB
 *
 * @author Rod Johnson
 * @since 11.11.2003
 * @see Pointcut
 * @see ClassFilter
 */
public interface MethodMatcher {

	/**
	 * Perform static checking whether the given method matches.
	 * <p>If this returns {@code false} or if the {@link #isRuntime()}
	 * method returns {@code false}, no runtime check (i.e. no
	 * {@link #matches(java.lang.reflect.Method, Class, Object[])} call)
	 * will be made.
	 * 执行静态检查给定方法是否匹配。
	 * <p>如果返回 {@code false} 或 {@link isRuntime()} 方法返回 {@code false}，
	 * 则不进行运行时检查（即没有 {@link matches(java.lang.reflect.Method, Class, Object[])} 调用）将进行。
	 * @param method the candidate method
	 * @param targetClass the target class
	 * @return whether or not this method matches statically
	 * 返回当前方法是否符合切点条件，符合的话 返回true，否则返回false
	 * 两个参数的matched它是静态匹配，咱们大部分情况下 都是使用静态匹配
	 */
	boolean matches(Method method, Class<?> targetClass);

	/**
	 * Is this MethodMatcher dynamic, that is, must a final call be made on the
	 * {@link #matches(java.lang.reflect.Method, Class, Object[])} method at
	 * runtime even if the 2-arg matches method returns {@code true}?
	 * <p>Can be invoked when an AOP proxy is created, and need not be invoked
	 * again before each method invocation,
	 * @return whether or not a runtime match via the 3-arg
	 * {@link #matches(java.lang.reflect.Method, Class, Object[])} method
	 * is required if static matching passed.
	 * 这个 MethodMatcher 是动态的吗，也就是说，即使 2-arg 匹配方法返回 {@code true}？ <p>可以在创建AOP代理时调用，每次方法调用前不需要再次调用，
	 * return 是否通过3-arg {@link match(java.lang.reflect.Method,如果静态匹配通过，则需要 Class, Object[])}
	 * 返回true 代表需要做运行时匹配，返回false不需要做运行时匹配
	 */
	boolean isRuntime();

	/**
	 * Check whether there a runtime (dynamic) match for this method,
	 * which must have matched statically.
	 * <p>This method is invoked only if the 2-arg matches method returns
	 * {@code true} for the given method and target class, and if the
	 * {@link #isRuntime()} method returns {@code true}. Invoked
	 * immediately before potential running of the advice, after any
	 * advice earlier in the advice chain has run.
	 * 检查此方法是否存在运行时（动态）匹配，该匹配必须是静态匹配的。
	 * <p>仅当 2-arg 匹配方法为给定方法和目标类返回 {@code true} 并且 {@link isRuntime()} 方法返回 {@code true} 时才会调用此方法。
	 * 在建议链中较早的任何建议运行之后，在建议可能运行之前立即调用。
	 * @param method the candidate method
	 * @param targetClass the target class
	 * @param args arguments to the method
	 * @return whether there's a runtime match
	 * @see MethodMatcher#matches(Method, Class)
	 * 根据参数做运行时匹配，只有 isRuntime 返回true时，才代表这个 切面需要做 运行时匹配。
	 * 可以做到 根据参数判断是否增强该方法
	 */
	boolean matches(Method method, Class<?> targetClass, Object... args);


	/**
	 * Canonical instance that matches all methods.
	 */
	MethodMatcher TRUE = TrueMethodMatcher.INSTANCE;

}
