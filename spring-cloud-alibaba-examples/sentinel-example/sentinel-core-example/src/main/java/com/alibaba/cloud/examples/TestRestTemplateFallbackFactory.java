package com.alibaba.cloud.examples;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;

import com.alibaba.cloud.sentinel.rest.RestTemplateFallback;
import com.alibaba.cloud.sentinel.rest.RestTemplateFallbackFactory;
import com.alibaba.cloud.sentinel.rest.SentinelClientHttpResponse;
import com.alibaba.cloud.sentinel.rest.URIMapping;
import com.alibaba.csp.sentinel.slots.block.BlockException;

/**
 * TODO Describe what it does
 *
 * @author zkz
 */
@RestTemplateFallback
public class TestRestTemplateFallbackFactory extends RestTemplateFallbackFactory {

    public SentinelClientHttpResponse testFbk1(HttpRequest request, byte[] body,
                                               ClientHttpRequestExecution execution, BlockException ex){
        return new SentinelClientHttpResponse("oh，testFbk1,my god!");
    }

    public SentinelClientHttpResponse testFbk2(HttpRequest request, byte[] body,
                                               ClientHttpRequestExecution execution, BlockException ex){
        return new SentinelClientHttpResponse("oh，testFbk2,my god!");
    }
    @URIMapping({"testFbk3"})
    public SentinelClientHttpResponse test(HttpRequest request, byte[] body,
                                                ClientHttpRequestExecution execution, BlockException ex){
        return new SentinelClientHttpResponse("oh,my god!");
    }


}
