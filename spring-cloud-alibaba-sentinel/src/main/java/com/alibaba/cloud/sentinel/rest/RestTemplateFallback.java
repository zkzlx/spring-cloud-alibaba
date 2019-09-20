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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.stereotype.Component;

import com.alibaba.csp.sentinel.slots.block.BlockException;

/**
 * Annotation to mark a RestTemplate bean to be configured to use a fallback factory.
 * {@link URIMapping} can be used for the uri mapping of its internal methods. If the
 * method does not use {@link URIMapping}, its mapping uri is converted based on the
 * method's hump. If the mapping uri is not found by either uri or methods, the default
 * methods is invoked in
 * {@link RestTemplateFallbackFactory#fallback(HttpRequest, byte[], ClientHttpRequestExecution, BlockException)}.
 * sample:
 * //todo
 *
 * @author zkz
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Qualifier
@Component
public @interface RestTemplateFallback {

	/**
	 * prefix of uri.Provide prefix support for internal methods or {@link URIMapping}
	 */
	String prefix() default "";
}
