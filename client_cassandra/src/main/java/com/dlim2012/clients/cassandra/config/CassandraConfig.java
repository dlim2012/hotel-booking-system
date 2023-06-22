package com.dlim2012.clients.cassandra.config;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.type.codec.ExtraTypeCodecs;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.cassandra.SessionFactory;
import org.springframework.data.cassandra.config.*;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.convert.CassandraConverter;
import org.springframework.data.cassandra.core.convert.CassandraCustomConversions;
import org.springframework.data.cassandra.core.convert.MappingCassandraConverter;
import org.springframework.data.cassandra.core.mapping.CassandraMappingContext;
import org.springframework.data.cassandra.core.mapping.SimpleUserTypeResolver;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;
import org.springframework.data.convert.CustomConversions;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableCassandraRepositories
public class CassandraConfig
        extends AbstractCassandraConfiguration {

    @Override
    public SchemaAction getSchemaAction() {
        // schema create did not work using SchemaAction.CREATE and SchemaAction.CREATE_IF_NOT_EXISTS
        // manually adding schema through cqlsh instead
        return SchemaAction.CREATE_IF_NOT_EXISTS;
    }

    @Override
    protected String getKeyspaceName() {
        return "mykeyspace";
    }

    @Override
    protected String getContactPoints() {
        return "192.168.1.20";
    }

    @Override
    protected int getPort() {
        return 9106;
    }

}

