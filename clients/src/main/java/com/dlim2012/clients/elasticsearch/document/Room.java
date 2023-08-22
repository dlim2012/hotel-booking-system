package com.dlim2012.clients.elasticsearch.document;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "rooms")
public class Room {
    @Id
    String roomId;

//    @Field(type = FieldType.Long, name = "room_id")
//    Long roomId;

    @Field(type = FieldType.Long, name="dates_version")
    Long datesVersion;

    @Field(type = FieldType.Nested)
    Set<Dates> dates;

}
