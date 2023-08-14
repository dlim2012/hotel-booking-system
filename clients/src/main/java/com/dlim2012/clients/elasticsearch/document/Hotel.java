package com.dlim2012.clients.elasticsearch.document;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.query.SeqNoPrimaryTerm;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "hotel")
//@EntityScan(basePackages = {"com.dlim2012.elasticsearch", "com.dlim2012.search"})
public class Hotel {

    @Id
    private String id;

    private SeqNoPrimaryTerm seqNoPrimaryTerm;

    @Field(type = FieldType.Text)
    private String name;

//    @Field(type = FieldType.Text, name = "description")
//    private String description;

//    @Field(type = FieldType.Integer, name="hotel_id")
//    private Integer hotelId;

    @Field(type = FieldType.Integer)
    private String propertyTypeOrdinal;

    @Field(type = FieldType.Integer)
    private Integer propertyRating;

    @Field(type = FieldType.Nested)
    private List<Rooms> rooms;

//    @Field(type = FieldType.Text, name = "facility")
//    private List<String> facilityId;

    @Field(type = FieldType.Nested)
    private List<Facility> facility;

    @Field(type = FieldType.Text)
    private String addressLine1;

    @Field(type = FieldType.Text)
    private String addressLine2;

    @Field(type = FieldType.Text)
    private String neighborhood;

    @Field(type = FieldType.Text)
    private String zipcode;

    @Field(type = FieldType.Text)
    private String city;

    @Field(type = FieldType.Text)
    private String state;

    @Field(type = FieldType.Text)
    private String country;

    @GeoPointField
    private GeoPoint geoPoint;

    @Field(type = FieldType.Long)
    private Long version;

}
