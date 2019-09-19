package com.alibaba.cloud.examples;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.alibaba.csp.sentinel.annotation.SentinelResource;

/**
 * @author xiaojing
 */
@RestController
public class TestController {

	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private RestTemplate restTemplateFallback;

	@GetMapping(value = "/hello")
	@SentinelResource("resource")
	public String hello() {
		return "Hello";
	}

	@GetMapping(value = "/aa")
	@SentinelResource("aa")
	public String aa(int b, int a) {
		return "Hello test";
	}

	@GetMapping(value = "/test")
	public String test1() {
		return "Hello test";
	}

	@GetMapping(value = "/template")
	public String client() {
		return restTemplate.getForObject("http://www.taobao.com/test", String.class);
	}

	@GetMapping(value = "/fallback1")
	public String fallback1() {
		return restTemplateFallback.getForObject("http://www.taobao.com/test", String.class);
	}

	@GetMapping(value = "/fallback2")
	public String fallback2() {
		return restTemplateFallback.getForObject("http://www.taobao.com/test", String.class);
	}

}
