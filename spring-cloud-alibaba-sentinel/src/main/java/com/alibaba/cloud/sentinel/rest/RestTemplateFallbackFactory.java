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

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;

import com.alibaba.csp.sentinel.slots.block.BlockException;

/**
 * Handling of service throttling and degradation
 * @author zkz
 */
public interface RestTemplateFallbackFactory {



	/**
	 * Handling of service throttling
	 * @param request the request, containing method, URI, and headers
	 * @param body the body of the request
	 * @param execution refer to {@link ClientHttpRequestExecution}
	 * @param ex refer to {@link BlockException}
	 * @return SentinelClientHttpResponse
	 */
	SentinelClientHttpResponse throttle(HttpRequest request, byte[] body,
			ClientHttpRequestExecution execution, BlockException ex);

	/**
	 * Handling of service degradation
	 * @param request the request, containing method, URI, and headers
	 * @param body the body of the request
	 * @param execution refer to {@link ClientHttpRequestExecution}
	 * @param ex refer to {@link BlockException}
	 * @return SentinelClientHttpResponse
	 */
	SentinelClientHttpResponse degrade(HttpRequest request, byte[] body,
			ClientHttpRequestExecution execution, BlockException ex);
//	/**
//	 * Gets the method of its mapping based on the uri
//	 * @param request the request, containing method, URI, and headers
//	 * @param ex refer to {@link BlockException}
//	 * @return Method
//	 */
//	 Method getMethodByUri(HttpRequest request,BlockException ex);

}
