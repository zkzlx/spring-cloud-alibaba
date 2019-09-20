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

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.util.StringUtils;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;

/**
 * Handling of service throttling and degradation
 * @author zkz
 */
public abstract class AbstractFallbackFactory {

	private ConcurrentHashMap<String, Method> mappingCache = new ConcurrentHashMap<>(16);

	/**
	 * Handling of service throttling
	 * @param request the request, containing method, URI, and headers
	 * @param body the body of the request
	 * @param execution refer to {@link ClientHttpRequestExecution}
	 * @param ex refer to {@link BlockException}
	 * @return SentinelClientHttpResponse
	 */
	abstract SentinelClientHttpResponse throttle(HttpRequest request, byte[] body,
												 ClientHttpRequestExecution execution, BlockException ex);

	/**
	 * Handling of service degradation
	 * @param request the request, containing method, URI, and headers
	 * @param body the body of the request
	 * @param execution refer to {@link ClientHttpRequestExecution}
	 * @param ex refer to {@link BlockException}
	 * @return SentinelClientHttpResponse
	 */
	abstract SentinelClientHttpResponse degrade(HttpRequest request, byte[] body,
												ClientHttpRequestExecution execution, BlockException ex);

	/**
	 * initialize the map of uri to method mapping
	 * @return Map<String, Method> key is uri,value is method of mapping
	 */
	protected Map<String, Method> getMethodUriMapping(){
		return mappingCache;
	}


	final void putMethodUriMapping(String uri,Method method){
		if(StringUtils.isEmpty(uri) || method ==null){
			return;
		}
		if(mappingCache.containsKey(uri)){
			throw new IllegalArgumentException(String.format("The URI[%s] exist mapping",uri));
		}
		mappingCache.put(uri, method);
	}


	/**
	 * Gets the method of its mapping based on the uri
	 * @param request the request, containing method, URI, and headers
	 * @param ex refer to {@link BlockException}
	 * @return Method
	 */
	public Method getMethodByUri(HttpRequest request, BlockException ex) {
		if (request == null || StringUtils.isEmpty(request.getURI().getPath())) {
			return null;
		}
		Map<String, Method> methodMap = this.getMethodUriMapping();
		if (null == methodMap || methodMap.isEmpty()) {
			return null;
		}
		Class[] args = new Class[] { HttpRequest.class, byte[].class,
				ClientHttpRequestExecution.class, BlockException.class };
		String uri = request.getURI().getPath();
		Method method = methodMap.get(uri);
		if (method == null && BlockException.isBlockException(ex)) {
			try {
				if (ex instanceof DegradeException) {
					return this.getClass().getMethod("degrade", args);
				}
				return this.getClass().getMethod("throttle", args);
			}
			catch (NoSuchMethodException e) {
			}
		}
		return method;
	}
}
