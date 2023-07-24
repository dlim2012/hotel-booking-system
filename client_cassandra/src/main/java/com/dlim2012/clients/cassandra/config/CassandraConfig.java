package com.dlim2012.clients.cassandra.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@Configuration
@EnableCassandraRepositories
public class CassandraConfig
        extends AbstractCassandraConfiguration {
    @Value("${spring.data.cassandra.keyspace-name}")
    private String keyspaceName;


    @Value("${spring.data.cassandra.contact-points}")
    private String contactPoints;

    @Value("${spring.data.cassandra.port}")
    private Integer port;


    @Override
    public SchemaAction getSchemaAction() {
        // schema create did not work using SchemaAction.CREATE and SchemaAction.CREATE_IF_NOT_EXISTS
        // manually adding schema through cqlsh instead
        return SchemaAction.CREATE_IF_NOT_EXISTS;
    }

    @Override
    protected String getKeyspaceName() {
        return this.keyspaceName;
    }

    @Override
    protected String getContactPoints() {
        return this.contactPoints;
    }

    @Override
    protected int getPort() {
        return this.port;
    }

}

