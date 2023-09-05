package com.dlim2012.clients.elasticsearch.document;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "rooms")
@Document(indexName = "rooms")
public class Rooms {

    @Id
    private String id;

    @Field(type=FieldType.Integer)
    private Integer roomsId;

    @Field(type = FieldType.Text)
    private String displayName;

//    @Field(type = FieldType.Nested, name = "hotel")
//    private Hotel hotel;

    @Field(type = FieldType.Nested)
    private List<Room> room;

    @Field(type = FieldType.Nested)
    private List<Price> price;

    @Field(type = FieldType.Integer)
    private Integer maxAdult;

    @Field(type = FieldType.Integer)
    private Integer maxChild;

//    @Field(type = FieldType.Text, name = "hotel_id")
//    private String hotelId;

    @Field(type = FieldType.Nested)
    private List<Facility> facility;

    @Field(type = FieldType.Nested)
    private List<RoomsBed> bed;

//    @Field(type = FieldType.Text, name = "price_min")
//    private Long priceMin;
//
//    @Field(type = FieldType.Text, name = "price_max")
//    private Long priceMax;
//
    @Field(type = FieldType.Long_Range)
    private PriceRange priceRange;

    @Field(type = FieldType.Integer)
    private Integer availableFromInteger; // Date since January 1, 1970

    @Field(type = FieldType.Integer)
    private Integer availableUntilInteger;

    @Field(type = FieldType.Integer)
    private Integer freeCancellationDays;

    @Field(type = FieldType.Integer)
    private Integer noPrepaymentDays;

    @Field(type = FieldType.Integer)
    private Integer numBeds;

    @Field(type =  FieldType.Boolean)
    private Boolean breakfast;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PriceRange {
        @Field
        private Long gte;
        @Field
        private Long lte;

    }

}