/*
 * Copyright 2013-2018 the original author or authors.
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

package com.alibaba.cloud.nacos.parser;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import org.springframework.beans.factory.config.YamlMapFactoryBean;
import org.springframework.core.CollectionFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/**
 * @author zkz
 */
public class NacosDataYamlParser extends AbstractNacosDataParser {

	public NacosDataYamlParser() {
		super(",yml,yaml,");
	}

	@Override
	protected Map<String, Object> doParse(String data, String encode) throws IOException {
		YamlListMapFactoryBean yamlFactory = new YamlListMapFactoryBean();
		yamlFactory.setResources(new ByteArrayResource(data.getBytes(encode)));
		return yamlFactory.getFlattenedMap();
	}

	private static class YamlListMapFactoryBean extends YamlMapFactoryBean {

		private Properties properties;

		private Properties getProperties() {
			if (null == properties) {
                // Construct a new Properties instance, after 5.2 version is sorted Properties .
                // @see ${link org.springframework.core.SortedProperties }
				properties = CollectionFactory.createStringAdaptingProperties();
				properties.putAll(this.getFlattenedMap());
			}
			return properties;
		}

		private Map<String, Object> getFlattenedMap() {
			return this.getFlattenedMap(this.getAssembledMap());
		}

		private Map<String, Object> getAssembledMap() {
			Map<String, Object> result = new LinkedHashMap<>();
			assembleMap(result, Objects.requireNonNull(this.getObject()), null);
			return result;
		}

		private void assembleMap(Map<String, Object> result, Map<String, Object> source,
				@Nullable String path) {
			source.forEach((key, value) -> {
				if (StringUtils.hasText(path)) {
					key = path + DOT + key;
				}
				if (value instanceof String || value instanceof Collection) {
					result.put(key, value);
				}
				else if (value instanceof Map) {
					@SuppressWarnings("unchecked")
					Map<String, Object> map = (Map<String, Object>) value;
					assembleMap(result, map, key);
				}
				else {
					result.put(key, (value != null ? value : ""));
				}
			});
		}

	}

}
