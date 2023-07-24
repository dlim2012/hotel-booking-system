package com.dlim2012.clients.elasticsearch.document;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "price")
public class Price {
    @Id
    private String id;

    @Field(type = FieldType.Integer)
    private Integer date;

    @Field(type = FieldType.Integer)
    private Integer roomsId;

    @Field(type = FieldType.Long)
    private Long priceInCents;

}
