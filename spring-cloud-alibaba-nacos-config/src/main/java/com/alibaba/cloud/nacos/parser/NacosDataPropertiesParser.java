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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.core.CollectionFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

/**
 * @author zkz
 */
public class NacosDataPropertiesParser extends AbstractNacosDataParser {

	public NacosDataPropertiesParser() {
		super("properties");
	}

	@Override
	protected Map<String, Object> doParse(String data, String encode) throws IOException {
		// Construct a new Properties instance, after 5.2 version is sorted Properties .
		// @see ${link org.springframework.core.SortedProperties }
		Properties properties = CollectionFactory.createStringAdaptingProperties();
		PropertiesLoaderUtils.fillProperties(properties, new EncodedResource(
				new ByteArrayResource(data.getBytes(encode)), encode));
		if(properties.isEmpty()){
			return null;
		}
		Map<String, Object> map = new LinkedHashMap<>(32);
		properties.forEach((key, value) -> map.put(key.toString(), value));
		return map;
	}

}
