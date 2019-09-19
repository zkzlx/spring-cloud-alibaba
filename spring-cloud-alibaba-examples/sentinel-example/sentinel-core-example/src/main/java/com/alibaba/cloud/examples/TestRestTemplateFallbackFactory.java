package com.alibaba.cloud.examples;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;

import com.alibaba.cloud.sentinel.rest.RestTemplateFallback;
import com.alibaba.cloud.sentinel.rest.RestTemplateFallbackFactory;
import com.alibaba.cloud.sentinel.rest.SentinelClientHttpResponse;
import com.alibaba.csp.sentinel.slots.block.BlockException;

/**
 * TODO Describe what it does
 *
 * @author zkz
 */
@RestTemplateFallback
public class TestRestTemplateFallbackFactory implements RestTemplateFallbackFactory {
    @Override
    public SentinelClientHttpResponse throttle(HttpRequest request, byte[] body, ClientHttpRequestExecution execution, BlockException ex) {
        return new SentinelClientHttpResponse("com.alibaba.cloud.examples.TestRestTemplateFallbackFactory.throttle");
    }

    @Override
    public SentinelClientHttpResponse degrade(HttpRequest request, byte[] body, ClientHttpRequestExecution execution, BlockException ex) {
        return new SentinelClientHttpResponse("com.alibaba.cloud.examples.TestRestTemplateFallbackFactory.degrade");
    }
}
