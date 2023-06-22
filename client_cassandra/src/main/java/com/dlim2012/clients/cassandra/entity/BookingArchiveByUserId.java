package com.dlim2012.clients.cassandra.entity;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.dlim2012.clients.entity.BookingMainStatus;
import jakarta.persistence.Convert;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;



import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(value = "booking_archive_by_user_id")
public class BookingArchiveByUserId {
//    @PrimaryKey
//    BookingArchiveByUserIdKey bookingArchiveByUserIdKey;


    @PrimaryKeyColumn(name = "user_id", type = PrimaryKeyType.PARTITIONED)
    private Integer userId;

    @PrimaryKeyColumn(name = "main_status", type = PrimaryKeyType.CLUSTERED)
    private BookingMainStatus mainStatus;

    @PrimaryKeyColumn(name = "end_date_time", type = PrimaryKeyType.CLUSTERED)
//    @Convert(converter = CustomDateConverter.class)
    private LocalDateTime endDateTime;

    @PrimaryKeyColumn(name = "booking_id", type = PrimaryKeyType.CLUSTERED)
    private Long bookingId;
}
