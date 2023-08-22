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

    @Value("${spring.data.cassandra.local-datacenter}")
    private String localDataCenter;

//    @Value("${spring.data.cassandra.username}")
//    private String username;
//
//    @Value("${spring.data.cassandra.password}")
//    private String password;


    @Override
    public SchemaAction getSchemaAction() {
        // schema create did not work using SchemaAction.CREATE and SchemaAction.CREATE_IF_NOT_EXISTS
        // manually adding schema through cqlsh instead
        return SchemaAction.CREATE_IF_NOT_EXISTS;
    }

    @Override
    protected String getKeyspaceName() {
        System.out.println("keyspace " + this.keyspaceName);
        return this.keyspaceName;
    }

    @Override
    protected String getContactPoints() {
        System.out.println("contact-points " + this.contactPoints);
        return this.contactPoints;
    }

    @Override
    protected String getLocalDataCenter() {
        System.out.println("local_datacenter " + this.localDataCenter);
        return this.localDataCenter;
    }

    @Override
    protected int getPort() {
        System.out.println("port " + this.port);
        return this.port;
    }

//    @Bean
//    @Override
//    public CqlSessionFactoryBean cassandraSession() {
//        CqlSessionFactoryBean cassandraSession = super.cassandraSession();//super session should be called only once
//        cassandraSession.setUsername(username);
//        cassandraSession.setPassword(password);
//        return cassandraSession;
//    }

}

