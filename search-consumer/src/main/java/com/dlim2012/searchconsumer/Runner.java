package com.dlim2012.searchconsumer;

import com.dlim2012.clients.elasticsearch.document.Product;
import com.dlim2012.clients.elasticsearch.service.ElasticSearchQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class Runner implements CommandLineRunner {

    private final ElasticSearchQuery elasticSearchQuery;

    @Autowired
    public Runner(ElasticSearchQuery elasticSearchQuery) {
        this.elasticSearchQuery = elasticSearchQuery;
    }

    @Override
    public void run(String... args) throws Exception {
        elasticSearchExample();
    }

    public void elasticSearchExample() throws IOException {
        System.out.println("Runner");
        String name = "myProduct";
        elasticSearchQuery.createOrUpdateDocument(
                "product",
                "0",
                new Product(
                        "0",
                        name,
                        "description",
                        10.0
                )
        );
        System.out.println(elasticSearchQuery.searchAllDocuments("product", Product.class));
        System.out.println("--------------------------------------------------");
        System.out.println(elasticSearchQuery.getDocumentByField("product","name", name, Product.class));
        System.out.println("--------------------------------------------------");
        System.out.println(elasticSearchQuery.getDocumentById("product","0", Product.class));
        System.out.println("--------------------------------------------------");
    }

}
