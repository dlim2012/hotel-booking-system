package com.dlim2012.clients.elasticsearch.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;

public class CustomLocalDateTimeDeserializer extends StdDeserializer<LocalDateTime> {

    public CustomLocalDateTimeDeserializer() {
        this(null);
    }

    private CustomLocalDateTimeDeserializer(Class<LocalDateTime> t) {
        super(t);
    }

    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        String date = jsonParser.getText();
        try {
            return LocalDateTime.parse(date);
        } catch (Exception ex) {
            throw new RuntimeException("Cannot Parse Date");
        }
    }
}