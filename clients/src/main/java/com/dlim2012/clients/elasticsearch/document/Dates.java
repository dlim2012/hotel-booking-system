package com.dlim2012.clients.elasticsearch.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "date")
public class Dates {

    @Id

//    @JsonIgnore
    private String id;

    @Field(type = FieldType.Integer)
    private Integer hotelId;

    @Field(type = FieldType.Integer)
    private Integer roomsId;

    @Field(type = FieldType.Long)
    private Long roomId;

    @Field(type = FieldType.Integer)
    private Integer maxAdult;

    @Field(type = FieldType.Integer)
    private Integer maxChild;

    @Field(type = FieldType.Integer_Range)
    private DateRange dateRange;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DateRange {
        @Field
        private Integer gte; // startDate
        @Field
        private Integer lte; // endDate

    }


}





//    @Id
////    @JsonIgnore
//    private String id;
//
//    @Field(type = FieldType.Integer, name = "roomId")
//    private String roomId;
//
//    @Field(type = FieldType.Integer, name = "date")
//    private Integer date;
//
//    @Field(type = FieldType.Integer, name = "quantity")
//    private Integer quantity;
//
//    @Field(type = FieldType.Long, name = "price")
//    private Long price;