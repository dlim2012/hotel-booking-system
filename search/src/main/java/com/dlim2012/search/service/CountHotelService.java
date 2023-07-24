package com.dlim2012.search.service;

import com.dlim2012.clients.entity.PropertyType;
import com.dlim2012.search.dto.count.NumberByCityRequest;
import com.dlim2012.search.dto.count.NumberByPropertyTypeRequest;
import com.dlim2012.search.dto.count.NumberResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CountHotelService {

    private final RestHighLevelClient client = new RestHighLevelClient(
            RestClient.builder(
                    new HttpHost("10.0.0.110", 9103, "http")
            )
    );

    public NumberResponse numHotelByCity(NumberByCityRequest request) throws IOException {

        CountRequest countRequest = new CountRequest("hotel");
        BoolQueryBuilder hotelBool = QueryBuilders.boolQuery();

        // match location
        if (request.getCountry() != null && !request.getCountry().isEmpty()) {
            hotelBool.must(QueryBuilders.matchQuery("country", request.getCountry()));
        }
        if (request.getState() != null && !request.getState().isEmpty()) {
            hotelBool.must(QueryBuilders.matchQuery("state", request.getState()));
        }
        if (request.getCity() != null && !request.getCity().isEmpty()){
            hotelBool.must(QueryBuilders.matchQuery("city", request.getCity()));
        }
        countRequest.query(hotelBool);
        CountResponse countResponse = client.count(countRequest, RequestOptions.DEFAULT);

        return NumberResponse.builder().count((int) countResponse.getCount()).build();
    }

    public List<NumberResponse> numHotelByCity(List<NumberByCityRequest> requests) throws IOException {

        return requests.stream().map(request -> {
            try {
                return numHotelByCity(request);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).toList();

    }

    public NumberResponse numHotelByPropertyType(NumberByPropertyTypeRequest request) throws IOException {
        CountRequest countRequest = new CountRequest("hotel");
        MatchQueryBuilder matchQuery = QueryBuilders.matchQuery("propertyTypeOrdinal", PropertyType.valueOf(request.getPropertyType()).ordinal());
        countRequest.query(matchQuery);
        CountResponse countResponse = client.count(countRequest, RequestOptions.DEFAULT);

        return NumberResponse.builder().count((int) countResponse.getCount()).build();

    }

    public List<NumberResponse> numHotelByPropertyType(List<NumberByPropertyTypeRequest> requests) throws IOException {

        return requests.stream().map(request -> {
            try {
                return numHotelByPropertyType(request);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).toList();

    }
}
