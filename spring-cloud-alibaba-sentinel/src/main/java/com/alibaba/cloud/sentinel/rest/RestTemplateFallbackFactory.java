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
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import com.alibaba.cloud.sentinel.rest.SentinelClientHttpResponse;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;

/**
 * Handling of service throttling and degradation
 * @author zkz
 */
public class RestTemplateFallbackFactory extends AbstractFallbackFactory{

	protected static final Logger log = LoggerFactory
			.getLogger(RestTemplateFallbackFactory.class);


	public final SentinelClientHttpResponse fallback(HttpRequest request, byte[] body,
													 ClientHttpRequestExecution execution, BlockException ex){

		Object[] args = new Object[] { request, body, execution, ex };
		Method method  = this.getMethodByUri(request, ex);
		if(method != null){
			try {
				return (SentinelClientHttpResponse) method.invoke(this,args);
			} catch (IllegalAccessException e) {
				throw new RuntimeException("occurred IllegalAccessException",e);
			}
			catch (InvocationTargetException e) {
				throw new RuntimeException("occurred InvocationTargetException",e);
			}
		}
		throw new RuntimeException("fallback error occurred ",ex);
	}

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
											   ClientHttpRequestExecution execution, BlockException ex){
		throw new RuntimeException("default exception for throttle",ex);
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
											  ClientHttpRequestExecution execution, BlockException ex){
		throw new RuntimeException("default exception for degrade",ex);
	}





}
