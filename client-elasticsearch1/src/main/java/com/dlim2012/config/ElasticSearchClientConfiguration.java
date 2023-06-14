package com.dlim2012.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

@Configuration
@ComponentScan(basePackages = {"com.dlim2012.searchconsumer", "com.dlim2012.search"})
public class ElasticSearchClientConfiguration extends ElasticsearchConfiguration {

    @Value("${custom.elasticsearch.server}")
    private String hostAndPort;

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(hostAndPort).build();
    }

}
