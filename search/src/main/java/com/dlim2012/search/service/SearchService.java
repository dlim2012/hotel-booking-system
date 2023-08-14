package com.dlim2012.search.service;

import com.dlim2012.clients.elasticsearch.config.ElasticSearchUtils;
import com.dlim2012.clients.entity.PropertyType;
import com.dlim2012.clients.entity.SharedIds;
import com.dlim2012.search.config.ElasticSearchHighLevelClientConfig;
import com.dlim2012.search.dto.hotelSearch.*;
import com.dlim2012.search.dto.priceAgg.PriceAggRequest;
import com.dlim2012.search.dto.priceAgg.PriceAggResponse;
import com.dlim2012.search.dto.quantity.RoomsAvailabilityRequest;
import com.dlim2012.search.dto.quantity.RoomsAvailabilityResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.*;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.PipelineAggregatorBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.ParsedFilter;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedReverseNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.ParsedCardinality;
import org.elasticsearch.search.aggregations.metrics.ParsedSum;
import org.elasticsearch.search.aggregations.metrics.ParsedTopHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.modelmapper.ModelMapper;
import org.ojalgo.optimisation.Expression;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Variable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static java.lang.Math.max;
import static java.lang.Math.min;

@Service
@Slf4j
public class SearchService {

    private final ElasticSearchUtils elasticSearchUtils;
    private final SharedIds sharedIds = new SharedIds();
    private final QueryBuilderService queryBuilderService;
    private final Integer MAX_HITS = 100;
    private final Integer MAX_RESIDUAL_ROOM = 1;
    private final ModelMapper modelMapper = new ModelMapper();

    private final RestHighLevelClient client;

    private Integer MAX_RETURN = 25;

    public SearchService(ElasticSearchUtils elasticSearchUtils, QueryBuilderService queryBuilderService, ElasticSearchHighLevelClientConfig highLevelClientConfig) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        this.elasticSearchUtils = elasticSearchUtils;
        this.queryBuilderService = queryBuilderService;
        this.client = highLevelClientConfig.getClient();
    }

    private final Integer SIZE = 100;

    private final Random rand = new Random();

    public NestedQueryBuilder nestHotelFacility(Integer facilityId){
//        System.out.println(facilityId);
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("facility.id", facilityId);
        return QueryBuilders.nestedQuery("facility", matchQueryBuilder, ScoreMode.Total);
    }

    public NestedQueryBuilder nestRoomsFacility(Integer facilityId){
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("rooms.facility.id", facilityId);
        return QueryBuilders.nestedQuery("rooms.facility", matchQueryBuilder, ScoreMode.Total);
    }

//    public NestedQueryBuilder nestDates(HotelSearchRequest request){
//        BoolQueryBuilder datesBool = QueryBuilders.boolQuery();
//        if (request.getStartDate() != null && request.getEndDate() != null){
//            datesBool.must(QueryBuilders.rangeQuery("rooms.room.dates.term")
//                    .relation("contains")
//                    .gte(elasticSearchUtils.toInteger(request.getStartDate()))
//                    .lte(elasticSearchUtils.toInteger(request.getEndDate()))
//            );
//        }
//        return QueryBuilders.nestedQuery("rooms.room.dates", datesBool, ScoreMode.None);
//    }
//
//    public NestedQueryBuilder nestRooms(HotelSearchRequest request) throws IOException {
//
//        BoolQueryBuilder roomsBool = QueryBuilders.boolQuery();
//        if (request.getPriceMax() != null && request.getPriceMin() != null){
//            roomsBool.must(QueryBuilders.rangeQuery("rooms.price_range")
//                    .relation("within")
//                    .gte(request.getPriceMin()).lte(request.getPriceMax())
//            );
//        } else if (request.getPriceMin() != null){
//            roomsBool.must(QueryBuilders.rangeQuery("rooms.price_range")
//                    .relation("within")
//                    .gte(request.getPriceMin())
//            );
//        } else if (request.getPriceMax() != null){
//            roomsBool.must(QueryBuilders.rangeQuery("rooms.price_range")
//                    .relation("within")
//                    .lte(request.getPriceMax())
//            );
//
//        }
//
//        roomsBool.must(nestDates(request));
//
//        XContentParser xContentParser = XContentFactory.xContent(XContentType.JSON)
//                .createParser(
//                        NamedXContentRegistry.EMPTY,
//                        DeprecationHandler.IGNORE_DEPRECATIONS,
//                        "{\"fields\": \"rooms.rooms_id\"}");
//        System.out.println(xContentParser);
//        System.out.println(xContentParser.currentToken());
//        NestedQueryBuilder nestRoomsBuilder = QueryBuilders
//                .nestedQuery("rooms", roomsBool, ScoreMode.Total)
//                .innerHit(
//                    new InnerHitBuilder("rooms_inner_hits")
//                            .setSize(10)
//                            .setFetchSourceContext(FetchSourceContext.DO_NOT_FETCH_SOURCE)
//                            .addFetchField("rooms.id")
//                            .addFetchField("rooms.display_name")
//                            .addFetchField("rooms.max_adult")
//                            .addFetchField("rooms.max_child")
//                            .addFetchField("rooms.number_of_beds")
//                            .addFetchField("rooms.free_cancellation_days")
//                            .addFetchField("rooms.payment_option")
//                            .addFetchField("rooms.bed.*")
//        );
//        return nestRoomsBuilder;
//    }


    public SearchResponse fetchFromES(HotelSearchRequest request) throws IOException {
        Integer startDateInteger = elasticSearchUtils.toInteger(request.getStartDate());
        Integer endDateInteger = elasticSearchUtils.toInteger(request.getEndDate());
        boolean usePriceRange = request.getPriceMax() != null && request.getPriceMin() != null;
//        System.out.println("dates: " + startDateInteger + " ~ " + endDateInteger);
        Integer numDays = endDateInteger - startDateInteger;

        SearchRequest searchRequest = new SearchRequest("hotel");

        BoolQueryBuilder hotelBool = QueryBuilders.boolQuery();

        // match location
        if (request.getCountry() != null && !request.getCountry().isEmpty()) {
            hotelBool.must(QueryBuilders.matchQuery("country", request.getCountry()).fuzziness(Fuzziness.ZERO).operator(Operator.AND));
        }
        if (request.getState() != null && !request.getState().isEmpty()) {
            hotelBool.must(QueryBuilders.matchQuery("state", request.getState()).fuzziness(Fuzziness.ZERO).operator(Operator.AND));
        }
        if (request.getCity() != null && !request.getCity().isEmpty()){
            hotelBool.must(QueryBuilders.matchQuery("city", request.getCity()).fuzziness(Fuzziness.ZERO).operator(Operator.AND));
        }



        // match property type
        if (request.getPropertyTypes() != null && !request.getPropertyTypes().isEmpty()) {

            BoolQueryBuilder propertyBool = QueryBuilders.boolQuery();
            propertyBool.minimumShouldMatch(1);
            for (String propertyType: request.getPropertyTypes()){
                propertyBool.should(QueryBuilders.matchQuery("propertyTypeOrdinal", PropertyType.valueOf(propertyType).ordinal()));
//                propertyBool.should(QueryBuilders.matchQuery("propertyTypeOrdinal", propertyType));
            }
//            System.out.println(propertyBool);
            hotelBool.must(propertyBool);

        }

        // match propertyRating
        if (request.getPropertyRating() != null && !request.getPropertyRating().isEmpty()){
            BoolQueryBuilder propertyRatingBool = QueryBuilders.boolQuery();
            propertyRatingBool.minimumShouldMatch(1);
            for (Integer propertyRating: request.getPropertyRating()){
                propertyRatingBool.should(QueryBuilders.matchQuery("propertyRating", propertyRating));
            }
            hotelBool.must(propertyRatingBool);
        }

        // match hotel facilities
        if (request.getHotelFacility() != null){
            for (String facility: request.getHotelFacility()){
                Integer facilityId = sharedIds.getFacilityId(facility);
//                System.out.println("facilityId " + facilityId + " " + facility);
//                System.out.println(facilityId);
//                System.out.println(nestHotelFacility(facilityId));
                hotelBool.must(nestHotelFacility(facilityId));
            }
        }

        // intersect price range
//        if (usePriceRange){
//            RangeQueryBuilder priceRangeQuery = QueryBuilders.rangeQuery("rooms.priceRange");
//            priceRangeQuery.relation("intersects");
//            if (request.getPriceMax() != null){
//                priceRangeQuery.lte(request.getPriceMax() / request.getNumRoom() / numDays);
//            }
//            if (request.getPriceMin() != null){
//                priceRangeQuery.gte(request.getPriceMin() / request.getNumRoom() / numDays);
//            }
//
//            NestedQueryBuilder roomsNested = QueryBuilders.nestedQuery("rooms", priceRangeQuery, ScoreMode.Total);
//            hotelBool.must(roomsNested);
//        }


        BoolQueryBuilder roomFacilitiesNestedMatch = QueryBuilders.boolQuery();
        if (request.getRoomsFacility() != null){
            for (String facility: request.getRoomsFacility()){
                Integer facilityId = sharedIds.getFacilityId(facility);
//                System.out.println("roomFacilities " + facility + " "  + facilityId);
                roomFacilitiesNestedMatch.must(nestRoomsFacility(facilityId));
            }
        }

        AggregationBuilder aggregationBuilder = AggregationBuilders
            .nested("nest_rooms", "rooms")
            .subAggregation(AggregationBuilders.filter("filter_room_facilities", roomFacilitiesNestedMatch)
                .subAggregation(AggregationBuilders.nested("nest_dates", "rooms.room.dates")
                    .subAggregation(AggregationBuilders.filter("filter_by_dates",
                            QueryBuilders.rangeQuery("rooms.room.dates.dateRange")
                                    .relation("contains").gte(startDateInteger).lte(endDateInteger))
                        .subAggregation(AggregationBuilders.terms("terms_hotel_id").field("rooms.room.dates.hotelId").size(request.getCity() == null || request.getCity().isEmpty() ? 100 : 1000)
                            .subAggregation(AggregationBuilders.sum("sum_max_adult").field("rooms.room.dates.maxAdult"))
                            .subAggregation(AggregationBuilders.sum("sum_max_child").field("rooms.room.dates.maxChild"))
                            .subAggregation(AggregationBuilders.sum("sum_num_bed").field("rooms.room.dates.numBed"))
                            .subAggregation(AggregationBuilders.cardinality("num_room_by_hotel").field("rooms.room.dates.roomId"))
                            .subAggregation(PipelineAggregatorBuilders.bucketSelector("hotel_stats_selector",
                                    new HashMap<String, String>(){{
                                        put("numBed", "sum_num_bed");
                                        put("maxAdult", "sum_max_adult");
                                        put("maxChild", "sum_max_child");
                                        put("numRoom", "num_room_by_hotel");}},
                                    new Script(String.format("params.maxAdult >= %d && params.maxChild + params.maxAdult >= %d && params.numRoom >= %d && params.numBed >= %d",
                                            request.getNumAdult(), request.getNumAdult() + request.getNumChild(), request.getNumRoom(), request.getNumBed()))))
                            .subAggregation(AggregationBuilders.terms("terms_rooms_in_dates").field("rooms.room.dates.roomsId")
                                .subAggregation(AggregationBuilders.cardinality("num_room_by_rooms").field("rooms.room.dates.roomId")))
                            .subAggregation(AggregationBuilders.reverseNested("unnest_to_hotel")
                                    .subAggregation(AggregationBuilders.topHits("hotel_hits")
                                            .size(1)
                                            .fetchSource(new String[]{"name",
                                                    "propertyTypeOrdinal", "neighborhood", "city", "state", "country", "zipcode",
                                                    "geoPoint", "description", "facility"
                                            }, null)
                                    )
                            )
                            .subAggregation(AggregationBuilders.reverseNested("unnest_to_rooms").path("rooms")
                                .subAggregation(AggregationBuilders.topHits("rooms_hits")
                                    .size(100)
                                    .fetchSource(new String[]{
                                        "rooms.id", "rooms.displayName", "rooms.maxAdult", "rooms.maxChild",
                                        "rooms.numBeds", "rooms.freeCancellationDays", "rooms.noPrepaymentDays",
                                        "rooms.bed.*", "rooms.breakfast"}, null)
                                )
                                .subAggregation(AggregationBuilders.nested("nest_price", "rooms.price")
                                    .subAggregation(AggregationBuilders.filter("filter_price_by_dates",
                                                QueryBuilders.rangeQuery("rooms.price.date").gte(startDateInteger).lt(endDateInteger)
                                        )
                                        .subAggregation(AggregationBuilders.terms("terms_rooms_in_price").field("rooms.price.roomsId")
                                                        .subAggregation(AggregationBuilders.sum("price_sum").field("rooms.price.priceInCents"))
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            );

//        System.out.println(hotelBool);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(hotelBool);
        searchSourceBuilder.aggregation(aggregationBuilder);
        searchSourceBuilder.size(0);

        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        return searchResponse;
    }

    public HotelSearchResponse search(HotelSearchRequest request) throws IOException {
        long start = System.currentTimeMillis();
        System.out.println(request);

//        SearchRequest searchRequest = new SearchRequest("hotel");
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
//        searchRequest.source(searchSourceBuilder);
//        SearchResponse searchResponse1 = client.search(searchRequest, RequestOptions.DEFAULT);
//        System.out.println(searchResponse1);
//        System.out.println(searchResponse1.getHits().getTotalHits());
//        System.out.println(searchResponse1.getHits().getHits());

        if (request.getNumBed() == null){
            request.setNumBed(-1);
        }
        List<HotelSearchResponseItem> hotelSearchResponseItemList = new ArrayList<>();

        SearchResponse searchResponse = fetchFromES(request);
        log.info("Response received from ES (status: {}, took: {}, hits: {})",
                searchResponse.status(),
                searchResponse.getTook(),
                searchResponse.getHits().getTotalHits().value
        );



        ParsedNested nestRooms = searchResponse.getAggregations().get("nest_rooms");
        ParsedFilter filterRoomFacilities = nestRooms.getAggregations().get("filter_room_facilities");
        ParsedNested nestDates = filterRoomFacilities.getAggregations().get("nest_dates");
        ParsedFilter filterByDates = nestDates.getAggregations().get("filter_by_dates");
        ParsedTerms termsHotelId = filterByDates.getAggregations().get("terms_hotel_id");

//        System.out.println("searchResponse.getHits().getTotalHits(): " + searchResponse.getHits().getTotalHits());
//        System.out.println("nestRooms.getDocCount(): " + nestRooms.getDocCount());
//        System.out.println("filterRoomFacilities.getDocCount(): " + filterRoomFacilities.getDocCount());
//        System.out.println("nestDates.getDocCount(): " + nestDates.getDocCount());
//        System.out.println("filterByDates.getDocCount(): " + filterByDates.getDocCount());
        System.out.println("termsHotelId.getBuckets().size(): " + termsHotelId.getBuckets().size());


        long minPrice = 100000000L;
        long maxPrice = 0L;
        long count = 0;
        List<? extends Terms.Bucket> hotelBucketList = termsHotelId.getBuckets();
        Collections.shuffle(hotelBucketList);
        for (Terms.Bucket hotelBucket: hotelBucketList){
            long roomMinPrice = 100000000L;
            long roomMaxPrice = 0L;

            Integer hotelId;
            try {
                hotelId = Integer.valueOf(hotelBucket.getKeyAsString());
            } catch (Exception e){
                System.out.println(e.getMessage());
                continue;
            }



            ParsedReverseNested unnestToHotel = hotelBucket.getAggregations().get("unnest_to_hotel");
            ParsedTopHits hotelHits = unnestToHotel.getAggregations().get("hotel_hits");

            Map<String, Object> hotelMap = hotelHits.getHits().getAt(0).getSourceAsMap();
            ParsedReverseNested unnestToRooms = hotelBucket.getAggregations().get("unnest_to_rooms");
            ParsedTopHits roomsHits = unnestToRooms.getAggregations().get("rooms_hits");
            ParsedNested nestPrice = unnestToRooms.getAggregations().get("nest_price");
            ParsedFilter filterPriceByDates = nestPrice.getAggregations().get("filter_price_by_dates");
            ParsedTerms termsRoomsInPrice = filterPriceByDates.getAggregations().get("terms_rooms_in_price");

            if  (request.getPriceMin() <= 1 && request.getPriceMax() == null && count >= MAX_RETURN * 2){
                count += 1;
                continue;
            }

//            System.out.println("====================================================");
//            System.out.println(hotelMap);

            // Read rooms prices
            Map<Integer, Long> roomsPrice = new HashMap<>();
            for (Terms.Bucket roomsBucket: termsRoomsInPrice.getBuckets()){
                Integer roomsId = roomsBucket.getKeyAsNumber().intValue();

                Long priceSum;
                try{
                    priceSum = Math.round(((ParsedSum) roomsBucket.getAggregations().get("price_sum")).getValue());
                } catch (Exception e){
                    continue;
                }
                roomsPrice.put(roomsId, priceSum);
            }



            // Read rooms
            Map<Integer, Map<String, Object>> roomsMap = new HashMap<>();
            for (SearchHit roomsHit: roomsHits.getHits()){
                Map<String, Object> rooms = roomsHit.getSourceAsMap();
                Integer roomsId;
                try {
                    roomsId = Integer.valueOf((String) rooms.get("id"));
                } catch (Exception e){
                    continue;
                }

                Long priceSum = roomsPrice.getOrDefault(roomsId, null);
                if (priceSum == null){
                    continue;
                }
                rooms.put("priceSum", priceSum);
                roomsMap.put(Integer.valueOf((String) rooms.get("id")), rooms);
            }

            ParsedTerms termsRoomsInDates = hotelBucket.getAggregations().get("terms_rooms_in_dates");
//            List<Long> priceList = new ArrayList<>();
            for (Terms.Bucket roomsBucket: termsRoomsInDates.getBuckets()) {
                Integer roomsId = roomsBucket.getKeyAsNumber().intValue();
                Map<String, Object> rooms = roomsMap.getOrDefault(roomsId, null);
                if (rooms == null){
                    continue;
                }

                Integer quantity = Math.toIntExact(((ParsedCardinality) roomsBucket.getAggregations().get("num_room_by_rooms")).getValue());
                rooms.put("quantity", quantity);
                Long priceSum = (Long) rooms.get("priceSum");
                roomMinPrice = min(roomMinPrice, priceSum);
                roomMaxPrice = max(roomMaxPrice, priceSum);
            }

//            System.out.println(roomsMap);
//            // get minimal and maximal price
//            priceList.sort(((o1, o2) -> Math.toIntExact(o1 - o2)));
//            Long hotelMinPrice = 0L;
//            Long hotelMaxPrice = 0L;
//            for (int j=0; j<request.getNumRoom(); j++){
//                hotelMinPrice += priceList.get(j);
//                hotelMaxPrice += priceList.get(priceList.size()-1-j);
//            }
//            minPrice = min(minPrice, hotelMinPrice);
//            maxPrice = max(maxPrice, hotelMaxPrice);



            // set up linear programming model
            Optimisation.Options options = new Optimisation.Options();
            options.iterations_abort = request.getNumRoom() * 15;

            if (count > MAX_RETURN * 2){
                ExpressionsBasedModel model = new ExpressionsBasedModel(options);
                Expression priceExpr = model.addExpression("price").lower(request.getPriceMin()).upper(request.getPriceMax());
                Expression numRoom = model.addExpression("num_room").lower(request.getNumRoom()).upper(request.getNumRoom()+MAX_RESIDUAL_ROOM);

                for (Terms.Bucket roomsBucket: termsRoomsInDates.getBuckets()){
                    Integer roomsId = roomsBucket.getKeyAsNumber().intValue();
                    Map<String, Object> rooms = roomsMap.getOrDefault(roomsId, null);
                    if (rooms == null){
                        continue;
                    }

                    Long priceSum = (Long) rooms.get("priceSum");
                    Integer quantity = (Integer) rooms.get("quantity");

//                System.out.println(maxAdult + " " + maxChild + " " + numBeds + " " + breakfast + " " + priceSum + " " + quantity);

                    Integer weight = 1;
                    Variable v = model
                            .addVariable(roomsId.toString())
                            .weight(weight);
                    v.lower(0)
                            .upper(quantity)
                            .setInteger(true);

                    numRoom.set(v, 1);
                    priceExpr.set(v, priceSum);
                }

                // Run linear programming
                Optimisation.Result result = model.minimise();
//            System.out.println(result);
                if (!result.getState().isSuccess()){
//                log.error("Optimisation unsuccesful (state: {})", result.getState().name());
//                System.out.println(hotelMap);
//                System.out.println(roomsMap);
                    continue;
                }
                count++;

                minPrice = min(minPrice, roomMinPrice * request.getNumRoom());
                maxPrice = max(maxPrice, roomMaxPrice * (request.getNumRoom()+MAX_RESIDUAL_ROOM));
                continue;

            }

            ExpressionsBasedModel model = new ExpressionsBasedModel(options);
            Expression priceExpr = model.addExpression("price").lower(request.getPriceMin()).upper(request.getPriceMax());
            Expression numRoom = model.addExpression("num_room").lower(request.getNumRoom()).upper(request.getNumRoom()+MAX_RESIDUAL_ROOM);


            Expression numAdult = model.addExpression("num_adult").lower(request.getNumAdult());
            Expression numPeople = model.addExpression("num_people").lower(request.getNumAdult() + request.getNumChild());
            Expression numBed = model.addExpression("num_bed").lower(request.getNumBed());

//            System.out.println(request);

            // Set up linear programming equation
            Map<Integer, Integer> roomsOrderMap = new HashMap<>();
            int i = 0;
            for (Terms.Bucket roomsBucket: termsRoomsInDates.getBuckets()){
                Integer roomsId = roomsBucket.getKeyAsNumber().intValue();
                Map<String, Object> rooms = roomsMap.getOrDefault(roomsId, null);
                if (rooms == null){
                    continue;
                }

                Integer maxAdult = (Integer) rooms.get("maxAdult");
                Integer maxChild = (Integer) rooms.get("maxChild");
                Integer numBeds = (Integer) rooms.get("numBeds");
                Boolean breakfast = (Boolean) rooms.get("breakfast");
                Long priceSum = (Long) rooms.get("priceSum");
                Integer quantity = (Integer) rooms.get("quantity");

//                System.out.println(maxAdult + " " + maxChild + " " + numBeds + " " + breakfast + " " + priceSum + " " + quantity);

                Integer weight = ((maxAdult + maxChild + 5 ) + (numBeds > 0 ? 3 * numBeds : 0)) * 2 + (breakfast ? 1 : 0);
                Variable v = model
                        .addVariable(roomsId.toString())
                        .weight(weight);
                v.lower(0)
                        .upper(quantity)
                        .setInteger(true);

                numAdult.set(v, maxAdult);
                numPeople.set(v, maxAdult + maxChild);
                numBed.set(v, numBeds);
                numRoom.set(v, 1);
                priceExpr.set(v, priceSum);
                roomsOrderMap.put(roomsId, i);
                i += 1;
            }

            // Run linear programming
            Optimisation.Result result = model.minimise();
//            System.out.println(result);
            if (!result.getState().isSuccess()){
//                log.error("Optimisation unsuccesful (state: {})", result.getState().name());
//                System.out.println(hotelMap);
//                System.out.println(roomsMap);
                continue;
            }

            List<HotelSearchRooms> hotelSearchRoomsList = new ArrayList<>();
//            for (int i=0; i<roomsMaps.size(); i++){
            double [] results = result.toRawCopy1D();
            int resultsIndex = 0;
            Integer totalNumRoom = 0;
            Long totalPrice = 0L;
            Integer maxFreeCancellationDays = 0;
            Integer noPrepaymentDays = 0;
            Boolean hotelBreakfast = true;
            for (Map.Entry<Integer, Map<String, Object>> entry: roomsMap.entrySet()) {
                Map<String, Object> rooms = entry.getValue();

                Integer roomsId = entry.getKey();
                Long price = (Long) rooms.get("priceSum");
                Integer recommendedQuantity = (int) Math.round(results[roomsOrderMap.get(roomsId)]);
                resultsIndex += 1;

                if (recommendedQuantity > 0) {
                    totalNumRoom += recommendedQuantity;

                    totalPrice += price * recommendedQuantity;
                    if (!((Boolean) rooms.get("breakfast"))) {
                        hotelBreakfast = false;
                    }
                    if (roomsMap.containsKey("freeCancellationDays")) {
                        if (maxFreeCancellationDays >= 0) {
                            maxFreeCancellationDays = max(maxFreeCancellationDays, (int) rooms.get("freeCancellationDays"));
                        }
                    } else {
                        maxFreeCancellationDays = -1;
                    }
                    if (roomsMap.containsKey("noPrepaymentDays")) {
                        noPrepaymentDays = max(noPrepaymentDays, (int) rooms.get("noPrepaymentDays"));
                    }
                }


                HotelSearchRooms hotelSearchRooms = HotelSearchRooms.builder()
                        .roomsId(roomsId)
                        .displayName((String) rooms.get("displayName"))
                        .maxAdult((Integer) rooms.get("maxAdult"))
                        .maxChild((Integer) rooms.get("maxChild"))
                        .numBed((Integer) rooms.get("numBeds"))
                        .recommended(recommendedQuantity)
                        .quantity((Integer) rooms.get("quantity"))
                        .price(price)
                        .bedInfoList(
                                rooms.containsKey("bed") ?
                                ((List<Map<String, Object>>)rooms.get("bed")).stream()
                                .map(bedMap -> BedInfo.builder()
                                        .size((String) bedMap.get("size"))
                                        .quantity((Integer) bedMap.get("quantity"))
                                        .build())
                                .toList(): null)
                        .build();
                hotelSearchRoomsList.add(hotelSearchRooms);


                minPrice = min(minPrice, roomMinPrice * request.getNumRoom());
                maxPrice = max(maxPrice, roomMaxPrice * (request.getNumRoom() + MAX_RESIDUAL_ROOM));
            }

            count += 1;

            HotelSearchResponseItem hotelSearchResponseItem = HotelSearchResponseItem.builder()
                    .hotelId(Integer.valueOf(hotelId))
                    .hotelName((String) hotelMap.get("name"))
                    .propertyType(hotelMap.containsKey("propertyTypeOrdinal") ? (PropertyType.values()[Integer.valueOf((String) hotelMap.get("propertyTypeOrdinal"))]).name() : null)
                    .neighborhood(hotelMap.containsKey("neighborhood") ? (String) hotelMap.get("neighborhood") : null)
                    .city(hotelMap.containsKey("city") ? (String) hotelMap.get("city") : "")
                    .state(hotelMap.containsKey("state") ? (String) hotelMap.get("state") : "")
                    .zipcode(hotelMap.containsKey("zipcode") ? (String) hotelMap.get("zipcode") : "")
                    .numRoom(totalNumRoom)
                    .totalPrice(totalPrice)
                    .maxFreeCancellationDays(maxFreeCancellationDays)
                    .noPrepaymentDays(noPrepaymentDays)
                    .breakfast(hotelBreakfast)
                    .roomsList(hotelSearchRoomsList)
                    .score(result.getValue() * (1.0 + 0.2 * rand.nextDouble())) // give some randomness
                    .build();
            if (request.getLatitude() != null && request.getLongitude() != null && hotelMap.containsKey("geoPoint")){
                Map<String, Object> geoPoint = (Map<String, Object>) hotelMap.get("geoPoint");
//                System.out.println(request.getLatitude() + " " + request.getLongitude() + " " + (Double) geoPoint.get("lat") + " " + (Double) geoPoint.get("lon"));

                if (geoPoint.containsKey("lat") && geoPoint.containsKey("lon")) {
                    hotelSearchResponseItem.setDistance(DistanceService.distance(
                            request.getLatitude(), request.getLongitude(),
                            (Double) geoPoint.get("lat"), (Double) geoPoint.get("lon"),
                            "K"
                    ));
                }
            }


//            BasicLogger.debug();
//            BasicLogger.debug(result);
//            BasicLogger.debug();
//            BasicLogger.debug(model);
//            BasicLogger.debug();
//            System.out.println(hotelSearchResponseItem);

            hotelSearchResponseItemList.add(hotelSearchResponseItem);

        }

        System.out.println("time " + (System.currentTimeMillis() - start));
        System.out.println("count " + count);

        hotelSearchResponseItemList.sort(new Comparator<HotelSearchResponseItem>() {
            @Override
            public int compare(HotelSearchResponseItem o1, HotelSearchResponseItem o2) {
                return o1.getScore().compareTo(o2.getScore());
            }
        });
        hotelSearchResponseItemList = hotelSearchResponseItemList.subList(0, Math.min(hotelSearchResponseItemList.size(), MAX_RETURN));
        HotelSearchResponse hotelSearchResponse = HotelSearchResponse.builder()
                .hotelList(hotelSearchResponseItemList)
                .numResults((int) count)
                .maxPrice(maxPrice)
                .minPrice(minPrice)
                .build();


//        System.out.println(hotelSearchResponse);
        return hotelSearchResponse;
    }

    public List<PriceAggResponse> aggPrice(PriceAggRequest request) throws IOException {
        List<PriceAggResponse> result = new ArrayList<>();

        Integer startDateInteger = elasticSearchUtils.toInteger(request.getStartDate());
        Integer endDateInteger = elasticSearchUtils.toInteger(request.getEndDate());
//        System.out.println("DateInteger " + startDateInteger + " " + endDateInteger);


        SearchRequest searchRequest = new SearchRequest("hotel");

        BoolQueryBuilder hotelBool = QueryBuilders.boolQuery();
        hotelBool.must(QueryBuilders.matchQuery("id", request.getHotelId()));

        AggregationBuilder aggregationBuilder = AggregationBuilders
                .nested("nest_rooms", "rooms")
                .subAggregation(queryBuilderService.priceSubAggFromRooms(startDateInteger, endDateInteger));

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(hotelBool);
        searchSourceBuilder.aggregation(aggregationBuilder);
        searchSourceBuilder.size(0);

        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        ParsedNested nestRooms = searchResponse.getAggregations().get("nest_rooms");
        Map<Integer, Long> roomsPriceMap = queryBuilderService.parsePriceSubAggFromRooms(nestRooms);

        for (Map.Entry<Integer, Long> entry: roomsPriceMap.entrySet()) {
            Integer roomsId = entry.getKey();
            Long sumPrice = entry.getValue();
            result.add(PriceAggResponse.builder()
                    .roomsId(roomsId)
                    .sumPrice(sumPrice)
                    .build());

        }
        return result;
    }


    public RoomsAvailabilityResponse getRoomsAvailability(Integer hotelId, RoomsAvailabilityRequest request) throws IOException {

        Integer startDateInteger = elasticSearchUtils.toInteger(request.getStartDate());
        Integer endDateInteger = elasticSearchUtils.toInteger(request.getEndDate());
//        System.out.println("DateInteger " + startDateInteger + " " + endDateInteger);


        SearchRequest searchRequest = new SearchRequest("hotel");

        BoolQueryBuilder hotelBool = QueryBuilders.boolQuery();
        hotelBool.must(QueryBuilders.matchQuery("id", hotelId));

        AggregationBuilder aggregationBuilder = AggregationBuilders
                .nested("nest_rooms", "rooms")
                .subAggregation(queryBuilderService.priceSubAggFromRooms(startDateInteger, endDateInteger))
                .subAggregation(queryBuilderService.roomNumSubAggFromRooms(startDateInteger, endDateInteger));

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(hotelBool);
        searchSourceBuilder.aggregation(aggregationBuilder);
        searchSourceBuilder.size(0);

        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);


        ParsedNested nestRooms = searchResponse.getAggregations().get("nest_rooms");

        Map<Integer, Long> roomsPriceMap = queryBuilderService.parsePriceSubAggFromRooms(nestRooms);
        Map<Integer, Integer> roomsNumRoomMap = queryBuilderService.parseRoomNumSubAggFromRooms(nestRooms);


        Set<Integer> roomsIds = roomsPriceMap.keySet();
        roomsIds.retainAll(roomsNumRoomMap.keySet());

        Map<Integer, RoomsAvailabilityResponse.Rooms> roomsMap = new HashMap<>();
        for (Integer roomsId: roomsIds){
            roomsMap.put(roomsId, RoomsAvailabilityResponse.Rooms.builder()
                            .roomsId(roomsId)
                            .price(roomsPriceMap.get(roomsId))
                            .quantity(roomsNumRoomMap.get(roomsId)).build());
        }

        return RoomsAvailabilityResponse.builder()
                .rooms(roomsMap)
                .build();
    }
}
