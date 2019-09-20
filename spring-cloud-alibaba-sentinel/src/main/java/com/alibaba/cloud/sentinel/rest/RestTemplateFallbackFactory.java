/*
 * Copyright (C) 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.cloud.sentinel.rest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StringUtils;

import com.alibaba.csp.sentinel.slots.block.BlockException;

/**
 * Handling of service throttling and degradation. Its inherited class must be used
 * {@link RestTemplateFallback}
 *
 * @author zkz
 */
public class RestTemplateFallbackFactory extends AbstractFallbackFactory {

	protected static final Logger log = LoggerFactory
			.getLogger(RestTemplateFallbackFactory.class);

	private static Pattern humpPattern = Pattern.compile("[A-Z]");
	private static String SPLIT = "/";
	private static String FILTER_METHOD=",throttle,degrade,";

	private Class<?>[] argsClass = new Class[] { HttpRequest.class, byte[].class,
			ClientHttpRequestExecution.class, BlockException.class };

	/**
	 * Handling of service throttling
	 * @param request the request, containing method, URI, and headers
	 * @param body the body of the request
	 * @param execution refer to {@link ClientHttpRequestExecution}
	 * @param ex refer to {@link BlockException}
	 * @return SentinelClientHttpResponse
	 */
	@Override
	public SentinelClientHttpResponse throttle(HttpRequest request, byte[] body,
											   ClientHttpRequestExecution execution, BlockException ex) {
		throw new RuntimeException("default exception for throttle", ex);
	}

	/**
	 * Handling of service degradation
	 * @param request the request, containing method, URI, and headers
	 * @param body the body of the request
	 * @param execution refer to {@link ClientHttpRequestExecution}
	 * @param ex refer to {@link BlockException}
	 * @return SentinelClientHttpResponse
	 */
	@Override
	public SentinelClientHttpResponse degrade(HttpRequest request, byte[] body,
											  ClientHttpRequestExecution execution, BlockException ex) {
		throw new RuntimeException("default exception for degrade", ex);
	}


	/**
	 * Handling of service degradation or throttling
	 * @param request the request, containing method, URI, and headers
	 * @param body the body of the request
	 * @param execution refer to {@link ClientHttpRequestExecution}
	 * @param ex refer to {@link BlockException}
	 * @return SentinelClientHttpResponse
	 */
	public final SentinelClientHttpResponse fallback(HttpRequest request, byte[] body,
			ClientHttpRequestExecution execution, BlockException ex) {
		Object[] args = new Object[] { request, body, execution, ex };
		Method method = this.getMethodByUri(request, ex);
		if (method != null) {
			try {
				return (SentinelClientHttpResponse) method.invoke(this,
						this.getMethodArgs(method, args));
			}
			catch (IllegalAccessException e) {
				throw new RuntimeException("occurred IllegalAccessException", e);
			}
			catch (InvocationTargetException e) {
				throw new RuntimeException("occurred InvocationTargetException", e);
			}
		}
		throw new RuntimeException("fallback error occurred ", ex);
	}

	private Object[] getMethodArgs(Method method, Object[] args) {
		if (null == method || args == null || args.length < 1) {
			return null;
		}
		Class<?>[] classes = method.getParameterTypes();
		if (classes.length < 1) {
			return null;
		}
		Object[] result = new Object[classes.length];
		for (int i = 0; i < classes.length; i++) {
			for (int j = 0; j < argsClass.length; j++) {
				if (argsClass[j].isAssignableFrom(classes[i])) {
					result[i] = args[j];
					break;
				}
			}
		}
		return result;
	}


	void addMethodUriMapping(String prefix, Method method) {
		if(method.getDeclaringClass().isAssignableFrom(RestTemplateFallbackFactory.class)){
			return;
		}
		if(FILTER_METHOD.contains(method.getName())){
			return;
		}
		if (!ClientHttpResponse.class.isAssignableFrom(method.getReturnType())) {
			return;
		}

		URIMapping mapping = AnnotationUtils.getAnnotation(method, URIMapping.class);
		if (mapping == null || mapping.value().length < 1) {
			this.putMethodUriMapping(joinPartialUri(prefix, humpToUri(method.getName())),
					method);
			return;
		}
		for (String suffix : mapping.value()) {
			if (StringUtils.isEmpty(suffix)) {
				continue;
			}
			this.putMethodUriMapping(joinPartialUri(prefix, suffix), method);
		}
	}

	private static String humpToUri(String str) {
		Matcher matcher = humpPattern.matcher(str);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(sb, SPLIT + matcher.group(0).toLowerCase());
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	private static String joinPartialUri(String prefix, String suffix) {
		prefix = StringUtils.isEmpty(prefix) ? SPLIT : prefix.trim();
		prefix = prefix.startsWith(SPLIT) ? prefix : SPLIT + prefix;
		suffix = StringUtils.isEmpty(suffix) ? SPLIT : suffix.trim();
		suffix = suffix.startsWith(SPLIT) ? suffix : SPLIT + suffix;
		return (prefix + suffix).replaceAll("//", SPLIT);
	}

}
