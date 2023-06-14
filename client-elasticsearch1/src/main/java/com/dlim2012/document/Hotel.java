package com.dlim2012.document;


import lombok.*;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(indexName = "hotel")
//@EntityScan(basePackages = {"com.dlim2012.elasticsearch", "com.dlim2012.search"})
public class Hotel {

    @Id
    private String id;

    @Field(type = FieldType.Text, name = "name")
    private String name;

    @Field(type = FieldType.Text, name = "description")
    private String description;

    @Field(type = FieldType.Text, name = "addressLine1")
    private String addressLine1;

    @Field(type = FieldType.Text, name = "addressLine2")
    private String addressLine2;

    @Field(type = FieldType.Text, name = "zipCode")
    private String zipCode;

    @Field(type = FieldType.Text, name = "cityName")
    private String cityName;

    @Field(type = FieldType.Text, name = "stateName")
    private String stateName;

    @Field(type = FieldType.Text, name = "countryName")
    private String countryName;
}
