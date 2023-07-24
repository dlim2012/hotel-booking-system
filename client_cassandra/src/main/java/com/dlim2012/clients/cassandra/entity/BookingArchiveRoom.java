package com.dlim2012.clients.cassandra.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
//@Table(value = "booking_archive_rooms")
@UserDefinedType(value = "booking_archive_room")
public class BookingArchiveRoom {

    @CassandraType(type = CassandraType.Name.INT, userTypeName = "roomsId")
    private Integer roomsId;

//    @Column(value = "room")
//    private List<@Frozen BookingArchiveRoom> room;

    @CassandraType(type = CassandraType.Name.TEXT, userTypeName = "roomsName")
    private String roomsName;

    @CassandraType(type = CassandraType.Name.BIGINT, userTypeName = "roomId")
    private Long roomId;

    @CassandraType(type = CassandraType.Name.TIMESTAMP, userTypeName = "startDateTime")
    private LocalDateTime startDateTime;

    @CassandraType(type = CassandraType.Name.TIMESTAMP, userTypeName = "endDateTime")
    private LocalDateTime endDateTime;


    @CassandraType(type = CassandraType.Name.TEXT, userTypeName = "guestName")
    private String guestName;

    @CassandraType(type = CassandraType.Name.TEXT, userTypeName = "guestEmail")
    private String guestEmail;
}
