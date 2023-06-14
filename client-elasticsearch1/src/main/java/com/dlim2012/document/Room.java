package com.dlim2012.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(indexName = "room")
public class Room {

    @Id
    private String id;

    @Field(type = FieldType.Text, name = "displayName")
    private String displayName;

    @Field(type = FieldType.Text, name = "description")
    private String description;

    @Field(type = FieldType.Integer)
    private Integer maxAdult;

    @Field(type = FieldType.Integer)
    private Integer maxChild;

    @Field(type = FieldType.Integer)
    private Integer quantity;

    @Field(type = FieldType.Double)
    private Double priceMin;

    @Field(type = FieldType.Double)
    private Double priceMax;
}
