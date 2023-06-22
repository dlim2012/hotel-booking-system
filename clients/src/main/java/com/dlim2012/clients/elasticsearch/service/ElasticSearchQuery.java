package com.dlim2012.clients.elasticsearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Repository
@Slf4j
@ComponentScan(basePackages = {"com.dlim2012.searchconsumer", "com.dlim2012.search"})
public class ElasticSearchQuery {

    @Autowired
    private ElasticsearchClient elasticsearchClient;
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;


    public <T> void createOrUpdateDocument(String indexName, String id, T t) throws IOException {
        IndexResponse response = elasticsearchClient.index(i -> i
                .index(indexName)
                .id(id)
                .document(t)
        );
        log.info("Create or update {} with id {}.", indexName, id);
    }

    public <T> T getDocumentById(String indexName, String id, Class<T> documentClass) throws IOException{
        T t = null;
        GetResponse<T> response = elasticsearchClient.get(g -> g
                        .index(indexName)
                        .id(id),
                documentClass
        );
        if (response.found()) {
            t = response.source();
            log.info("Found {} with id {}.", indexName, id);
        } else {
            log.info("Could not find {} with id {}.", indexName, id);
        }
        return t;
    }

    public <T> List<T> getDocumentByField(String indexName, String field, String query, Class<T> documentClass) throws IOException{
        SearchRequest searchRequest =  SearchRequest.of(
                s->s.index(indexName).query(q->q.match(m ->m.field(field).query(query))));
        SearchResponse searchResponse =  elasticsearchClient.search(searchRequest, documentClass);
        List<Hit> hits = searchResponse.hits().hits();
        log.info("Found {} {}(s) with {} of {}.", hits.size(), indexName, field, query);
        return hits.stream().map(hit->(T) hit.source()).toList();
    }


    public void deleteDocumentById(String indexName, String id) throws IOException {
        DeleteRequest request = DeleteRequest.of(d -> d.index(indexName).id(id));
        DeleteResponse deleteResponse = elasticsearchClient.delete(request);
        if (Objects.nonNull(deleteResponse.result()) && !deleteResponse.result().name().equals("NotFound")) {
            log.info("{} with id {} has been deleted.", indexName, id);
        } else {
            log.info("{} with id {} is not found while trying to delete.", indexName, id);
        }
    }

    public <T> List<T> searchAllDocuments(String indexName, Class<T> documentClass) throws IOException {
        SearchRequest searchRequest =  SearchRequest.of(s -> s.index(indexName));
        SearchResponse searchResponse =  elasticsearchClient.search(searchRequest, documentClass);
        List<Hit> hits = searchResponse.hits().hits();
        log.info("Found {} {}(s) by searching all.", hits.size(), indexName);
        return hits.stream().map(hit->(T) hit.source()).toList();
    }
}