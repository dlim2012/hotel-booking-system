package com.dlim2012.clients.elasticsearch.config;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.tomcat.jni.SSL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.support.HttpHeaders;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Objects;
import java.util.function.Supplier;

@Configuration
@ComponentScan(basePackages = {"com.dlim2012.searchconsumer", "com.dlim2012.search"})
public class ElasticSearchClientConfiguration extends ElasticsearchConfiguration {

    @Value("${custom.elasticsearch.server}")
    private String hostAndPort;
    @Value("${custom.elasticsearch.username}")
    private String username;
    @Value("${custom.elasticsearch.password}")
    private String password;
    @Value("${custom.elasticsearch.useSsl}")
    private String useSsl;

    private SSLContext createSSLContext(){
        try{
//            SSLContext sslContext = SSLContext.getInstance("TLS");
//            sslContext.init(null, new TrustManager[]{new UnsafeX509ExtendedTrustManager()}, null);
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, ((x509Certificates, s) -> true))
                    .build();
            return sslContext;
//            KeyManager[] keyManagers = getKeyManagers();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Bean
    public ClientConfiguration clientConfiguration() {
        if (Objects.equals(this.useSsl, "true")){
            return ClientConfiguration.builder()
                    .connectedTo(this.hostAndPort)
                    .usingSsl(createSSLContext())
                    .withBasicAuth(this.username, this.password)
                    .build();
        } else if (this.useSsl.equals("false")){
            return ClientConfiguration.builder()
                    .connectedTo(this.hostAndPort)
                    .build();
        } else {
            throw new IllegalArgumentException("Invalid useSsl: " + this.useSsl);
        }
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
