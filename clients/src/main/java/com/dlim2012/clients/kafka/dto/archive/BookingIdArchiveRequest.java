package com.dlim2012.clients.kafka.dto.archive;

import lombok.*;

import java.util.List;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BookingIdArchiveRequest {
    List<Long> bookingIds;
}
