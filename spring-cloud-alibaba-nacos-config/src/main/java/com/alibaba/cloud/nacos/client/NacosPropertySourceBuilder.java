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

package com.alibaba.cloud.nacos.client;

import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.alibaba.cloud.nacos.NacosPropertySourceRepository;
import com.alibaba.cloud.nacos.parser.NacosDataParserHandler;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.StringUtils;

/**
 * @author xiaojing
 * @author pbting
 */
public class NacosPropertySourceBuilder {

	private static final Logger log = LoggerFactory
			.getLogger(NacosPropertySourceBuilder.class);

	private ConfigService configService;

	private long timeout;

	private String encode;

	public NacosPropertySourceBuilder(ConfigService configService, long timeout) {
		this.configService = configService;
		this.timeout = timeout;
	}

	/**
	 * @param dataId Nacos dataId
	 * @param group Nacos group
	 */
	NacosPropertySource build(String dataId, String group, String fileExtension,
			boolean isRefreshable) {
		Map<String, Object> objectMap = loadNacosData(dataId, group, fileExtension);
		NacosPropertySource nacosPropertySource = new NacosPropertySource(group, dataId,
				objectMap, new Date(), isRefreshable);
		NacosPropertySourceRepository.collectNacosPropertySources(nacosPropertySource);
		return nacosPropertySource;
	}

	private Map<String, Object> loadNacosData(String dataId, String group,
			String fileExtension) {
		String data = null;
		try {
			data = configService.getConfig(dataId, group, timeout);
			if (StringUtils.isEmpty(data)) {
				log.warn(
						"Ignore the empty nacos configuration and get it based on dataId[{}] & group[{}]",
						dataId, group);
				return Collections.emptyMap();
			}
			log.info(String.format(
					"Loading nacos data, dataId: '%s', group: '%s', data: %s", dataId,
					group, data));

			Map<String, Object> objectMap = NacosDataParserHandler.getInstance()
					.parseNacosData(data, fileExtension, getEncode());
			return objectMap == null ? Collections.emptyMap() : objectMap;
		}
		catch (NacosException e) {
			log.error("get data from Nacos error,dataId:{}, ", dataId, e);
		}
		catch (Exception e) {
			log.error("parse data from Nacos error,dataId:{},data:{},", dataId, data, e);
		}
		return Collections.emptyMap();
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> propertiesToMap(Properties properties) {
		Map<String, Object> result = new HashMap<>(16);
		Enumeration<String> keys = (Enumeration<String>) properties.propertyNames();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			String value = properties.getProperty(key);
			if (value != null) {
				result.put(key, value.trim());
			}
			else {
				result.put(key, null);
			}
		}
		return result;
	}

	public String getEncode() {
		return encode;
	}

	public void setEncode(String encode) {
		this.encode = encode;
	}

}
