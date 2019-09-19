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

	@GetMapping(value = "/fbk1")
	public String fallbackTest1() {
		return restTemplateFallback.getForObject("http://www.taobao.com/test/fbk1", String.class);
	}

	@GetMapping(value = "/fbk2")
	public String fallbackTest2() {
		return restTemplateFallback.getForObject("http://www.taobao.com/testFbk2", String.class);
	}

	@GetMapping(value = "/fbk3")
	public String fallbackTest3() {
		return restTemplateFallback.getForObject("http://www.taobao.com/testFbk3", String.class);
	}

}
