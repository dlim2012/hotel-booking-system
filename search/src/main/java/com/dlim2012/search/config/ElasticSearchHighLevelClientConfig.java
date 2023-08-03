package com.dlim2012.search.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@Configuration
public class ElasticSearchHighLevelClientConfig {

    @Value("${custom.elasticsearch.host}")
    private String host;
    @Value("${custom.elasticsearch.port}")
    private Integer port;
    @Value("${custom.elasticsearch.username}")
    private String username;
    @Value("${custom.elasticsearch.password}")
    private String password;
    @Value("${custom.elasticsearch.useSsl}")
    private String useSsl;

    public RestHighLevelClient getClient() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        System.out.println("RestHighLevelClient (https) " + this.host + " " + this.port + " " + this.username + " " + this.password);
        if (Objects.equals(this.useSsl, "true")){
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(this.username, this.password));
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, ((x509Certificates, s) -> true))
                    .build();
            return new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost(this.host, this.port, "https")
                    ).setHttpClientConfigCallback(
                            new RestClientBuilder.HttpClientConfigCallback() {
                                @Override
                                public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpAsyncClientBuilder) {
                                    return httpAsyncClientBuilder
                                            .setDefaultCredentialsProvider(credentialsProvider)
                                            .setSSLContext(sslContext);
                                }
                            }
                    )
            );
        } else if (this.useSsl.equals("false")){
            return new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost(this.host, this.port, "http")
                    )
            );
        } else {
            throw new IllegalArgumentException("Invalid useSsl: " + this.useSsl);
        }


    }
}
