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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * TODO Describe what it does
 *
 * @author zkz
 */
public class RestTemplateFallbackBeanPostProcessor implements BeanPostProcessor {
	private static final Logger log = LoggerFactory
			.getLogger(RestTemplateFallbackBeanPostProcessor.class);



	private final ApplicationContext applicationContext;

	public RestTemplateFallbackBeanPostProcessor(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}


	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {

		if (bean instanceof RestTemplateFallbackFactory) {
			// check for fallbackFactory {@link RestTemplateFallbackFactory}
			RestTemplateFallback fallback = AnnotationUtils.getAnnotation(bean.getClass(),
					RestTemplateFallback.class);
			if (null == fallback) {
				String msg = String.format(
						"The [%s] must be a valid spring bean and needs to be marked with @RestTemplateFallback",
						bean.getClass().getSimpleName());
				log.error(msg);
				throw new IllegalArgumentException(msg);
			}
			RestTemplateFallbackFactory factory = (RestTemplateFallbackFactory) bean;
			String prefix = fallback.prefix();
			List<Method> methodList = Stream.of(bean.getClass().getMethods())
					.filter(item -> item.getDeclaringClass() != Object.class)
					.collect(Collectors.toList());
			for (Method method : methodList) {
				factory.addMethodUriMapping(prefix,method);
			}
		}
		return bean;
	}




}
