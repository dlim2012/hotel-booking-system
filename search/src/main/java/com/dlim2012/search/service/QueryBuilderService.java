package com.dlim2012.search.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.ParsedFilter;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.ParsedCardinality;
import org.elasticsearch.search.aggregations.metrics.ParsedSum;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class QueryBuilderService {

    public NestedAggregationBuilder priceSubAggFromRooms(Integer startDateInteger, Integer endDateInteger){
        return AggregationBuilders.nested("nest_price", "rooms.price")
                .subAggregation(AggregationBuilders.filter("filter_price_by_dates",
                                        QueryBuilders.rangeQuery("rooms.price.date").gte(startDateInteger).lt(endDateInteger)
                                )
                                .subAggregation(AggregationBuilders.terms("terms_rooms").field("rooms.price.roomsId")
                                        .subAggregation(AggregationBuilders.sum("sum_price").field("rooms.price.priceInCents"))
                                )
                );
    }

    public Map<Integer, Long> parsePriceSubAggFromRooms(ParsedNested nestRooms){
        ParsedNested nestPrice = nestRooms.getAggregations().get("nest_price");
        ParsedFilter filterPriceByDates = nestPrice.getAggregations().get("filter_price_by_dates");
        ParsedTerms termsRooms = filterPriceByDates.getAggregations().get("terms_rooms");

        Map<Integer, Long> roomsPriceMap = new HashMap<>();
        for (Terms.Bucket roomsBucket : termsRooms.getBuckets()) {
            Integer roomsId = roomsBucket.getKeyAsNumber().intValue();
            Long sumPrice = Math.round(((ParsedSum) roomsBucket.getAggregations().get("sum_price")).getValue());
            roomsPriceMap.put(roomsId, sumPrice);
        }

        return roomsPriceMap;
    }

    public NestedAggregationBuilder roomNumSubAggFromRooms(Integer startDateInteger, Integer endDateInteger){
        return AggregationBuilders.nested("nest_dates", "rooms.room.dates")
                .subAggregation(AggregationBuilders.filter("filter_by_dates",
                                QueryBuilders.rangeQuery("rooms.room.dates.dateRange")
                                        .relation("contains").gte(startDateInteger).lte(endDateInteger))
                        .subAggregation(AggregationBuilders.terms("terms_rooms_in_dates").field("rooms.room.dates.roomsId")
                                .subAggregation(AggregationBuilders.cardinality("num_room_by_rooms").field("rooms.room.dates.roomId")))

                );
    }

    public Map<Integer, Integer> parseRoomNumSubAggFromRooms(ParsedNested nestRooms){
        ParsedNested nestDates = nestRooms.getAggregations().get("nest_dates");
        ParsedFilter filterByDates = nestDates.getAggregations().get("filter_by_dates");
        ParsedTerms termsRoomsInDates = filterByDates.getAggregations().get("terms_rooms_in_dates");
        Map<Integer, Integer> roomsNumRoomMap = new HashMap<>();
        for (Terms.Bucket roomsBucket: termsRoomsInDates.getBuckets()){
            Integer roomsId = roomsBucket.getKeyAsNumber().intValue();
            Integer quantity = Math.toIntExact(((ParsedCardinality) roomsBucket.getAggregations().get("num_room_by_rooms")).getValue());
            roomsNumRoomMap.put(roomsId, quantity);
        }
        return roomsNumRoomMap;
    }


}
