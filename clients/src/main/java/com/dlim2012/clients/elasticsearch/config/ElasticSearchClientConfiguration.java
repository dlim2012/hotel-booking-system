package com.dlim2012.clients.elasticsearch.config;

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
//
//    @Bean(destroyMethod = "close")
//    public RestHighLevelClient restClient() {
//
////        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
////        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));
//
//        RestClientBuilder builder = RestClient.builder(new HttpHost("10.0.0.110", 9103))
////                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
//                .setDefaultHeaders(compatibilityHeaders());
//
//        return new RestHighLevelClient(builder);
//    }
//
//    private Header[] compatibilityHeaders() {
//        return new Header[]{new BasicHeader(HttpHeaders.ACCEPT, "application/vnd.elasticsearch+json;compatible-with=7"), new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/vnd.elasticsearch+json;compatible-with=7")};
//    }
}
