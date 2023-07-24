package com.dlim2012.search;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import com.dlim2012.clients.elasticsearch.config.ElasticSearchUtils;
import com.dlim2012.clients.elasticsearch.document.Rooms;
import com.dlim2012.clients.elasticsearch.service.ElasticSearchQuery;
import com.dlim2012.search.dto.hotelSearch.HotelSearchRequest;
import com.dlim2012.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.SearchTemplateQuery;
import org.springframework.data.elasticsearch.core.script.Script;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchRunner implements CommandLineRunner {

    private final ElasticSearchQuery elasticSearchQuery;
    private final ElasticsearchOperations elasticsearchOperations;
    private final ElasticSearchUtils elasticSearchUtils;
    private final SearchService searchService;

    @Override
    public void run(String... args) throws Exception {
//        criteriaQuery();
    }

//        // https://stackoverflow.com/questions/58441302/aggregation-performance-issue-in-elasticsearch-with-hotel-availability-data

    public void roomQuery(){
        //https://www.bmc.com/blogs/elasticsearch-nested-searches-embedded-documents/
        // https://opster.com/guides/elasticsearch/data-architecture/elasticsearch-nested-field-object-field/
        Integer dates = 19538;
        Integer quantity = 10;
        String dateId = "1-19538";
        Integer price = 1400;
        elasticsearchOperations.putScript(
                Script.builder()
                        .withId("nested")
                        .withLanguage("mustache")
                        .withSource("""
                                {
                                    "query": {
                                        "nested": {
                                            "path": "dates",
                                            "query": {
                                                "bool": {
                                                    "must": [
                                                        { "match": { "dates.dates": "{{dates}}" } },
                                                        { "range": { "dates.price": { "gte": "{{price}}" } } }
                                                    ]
                                                }
                                            }
                                        }
                                    }
                                }
                                """)
                        .build()
        );
        Query templateQuery = SearchTemplateQuery.builder()
                        .withId("nested")
                        .withParams(
                                Map.of (
                                        "dates", dates, "price", price
                                )
                        )
                        .build();
        Query query = NativeQuery.builder()
//                .withQuery(new CriteriaQuery(new Criteria("maxAdult").lessThanEqual(100)))
//                .withQuery(quantityQuery)
                .withQuery(templateQuery)
                .withAggregation("priceSum", Aggregation.of(a->a.sum(r->r.field("dates.price"))))
//                .withQuery(q->q.match(m->m.field("dates.price").query(1000)))
//                .withAggregation("maxAdults", Aggregation.of(
//                        a -> a.sum(ta -> ta.field("maxAdult"))
//                ))
                .build();

        SearchHits<Rooms> searchHits = elasticsearchOperations.search(
                templateQuery, Rooms.class
        );

        System.out.println(searchHits);
        System.out.println(searchHits.getAggregations().aggregations().toString());
        searchHits.forEach(searchHit -> System.out.println(searchHit.getContent()));

    }

    public void criteriaQuery() throws IOException {
        HotelSearchRequest request = HotelSearchRequest.builder()
                .propertyTypes(Arrays.asList("0", "1")) // optional
                .propertyRating(Arrays.asList(3, 4))
                .country("United States") // optional
                .state("Massachusetts") // optional
                .city("Amherst") // optional
                .latitude(42.0) // optional
                .longitude(-72.0) // optional
                .priceMin(100L) // optional, works if priceMin and priceMax are both given
                .priceMax(100000000L) // optional, works if priceMin and priceMax are both given
                .roomsFacility(List.of("Kitchen")) // optional
                .hotelFacility(List.of("Conference Hall")) // optional
                .numAdult(1) // not optional
                .numChild(0) // not optional
                .numBed(1) // not optional, ignored when given as 0
                .numRoom(2) // not optional
                .startDate(LocalDate.now().plusDays(0)) // not optional
                .endDate(LocalDate.now().plusDays(5)) // not optional
                .build();
//        System.out.println(elasticSearchUtils.toInteger(request.getStartDate()));
//        System.out.println(elasticSearchUtils.toInteger(request.getEndDate()));
//        lowLevelClientSearchExample(request);
        searchService.search(request);

    }


    public void lowLevelClientSearchExample(HotelSearchRequest searchRequest) throws IOException {
        Header[] defaultHeaders = new Header[]{
                new BasicHeader("accept","application/json"),
                new BasicHeader("content-type","application/json")
        };
        RestClient restClient = RestClient
                .builder(new HttpHost("10.0.0.110", 9103, "http"))
                .setDefaultHeaders(defaultHeaders)
                .build();


        // Note: the date-related fields may be outdated if the following json is used.
        // https://stackoverflow.com/questions/46044671/elasticsearch-aggregations-on-nested-inner-hits
        String requestJson = """
                {
                    "size": 0,
                    "query": {
                        "bool": {
                            "must": [
                                { "match": { "country": "United States" } },
                                { "match": { "state": "Massachusetts" } },
                                { "match": { "city": "Amherst" } }
                            ]
                        }
                    },
                    "aggs": {
                        "nest_rooms": {
                            "nested": {
                                "path": "rooms"
                            },
                            "aggs": {
                                "filter_room_facilities": {
                                    "filter": {
                                        "bool": {
                                            "must": [
                                                {
                                                    "nested": {
                                                        "path": "rooms.facility",
                                                        "query": { "match" : { "rooms.facility.id": "30" } }
                                                    }
                                                }
                                            ]
                                        }
                                    },
                                     "aggs": {
                                        "nest_dates":{
                                            "nested": {
                                                "path": "rooms.room.dates"
                                            },
                                            "aggs": {
                                                "filter_by_dates":{
                                                    "filter": {
                                                        "range": {
                                                            "rooms.room.dates.term": {
                                                                "gte": "19546",
                                                                "lte": "19548",
                                                                "relation": "contains"
                                                            }
                                                        }
                                                    },
                                                     "aggs": {
                                                          "terms_hotel_id": {
                                                              "terms": {
                                                                  "field": "rooms.room.dates.hotel_id"
                                                              },
                                                              "aggs": {
                                                                  "sum_max_adult": {
                                                                      "sum": {
                                                                          "field": "rooms.room.dates.max_adult"
                                                                      }
                                                                  },
                                                                   "sum_max_child": {
                                                                       "sum": {
                                                                           "field": "rooms.room.dates.max_child"
                                                                       }
                                                                   },
                                                                  "num_room_by_hotel": {
                                                                      "cardinality": {
                                                                          "field": "rooms.room.dates.room_id"
                                                                      }
                                                                  },
                                                                  "hotel_stats_selector": {
                                                                      "bucket_selector": {
                                                                          "buckets_path": {
                                                                              "maxAdult": "sum_max_adult",
                                                                              "maxChild": "sum_max_child",
                                                                              "numRoom": "num_room_by_hotel"
                                                                          },
                                                                          "script": "params.maxAdult > 1 && params.maxChild + params.maxAdult > 2 && params.numRoom > 1"
                                                                      }
                                                                  },
                                                                  "terms_rooms_in_dates": {
                                                                    "terms": {
                                                                        "field": "rooms.room.dates.rooms_id"
                                                                    },
                                                                    "aggs": {
                                                                        "num_room_by_rooms": {
                                                                            "cardinality": {
                                                                                "field": "rooms.room.dates.room_id"
                                                                            }
                                                                        }
                                                                    }
                                                                  },
                                                                  "unnest_to_rooms": {
                                                                    "reverse_nested": {
                                                                        "path": "rooms"
                                                                    },
                                                                    "aggs": {
                                                                        "nest_price": {
                                                                            "nested": {
                                                                                "path": "rooms.price"
                                                                            },
                                                                              "aggs": {
                                                                                  "filter_price_by_dates": {
                                                                                      "filter": {
                                                                                          "range": {
                                                                                              "rooms.price.date": {
                                                                                                  "gte": 19546,
                                                                                                  "lte": 19548
                                                                                              }
                                                                                          }
                                                                                      },
                                                                                        
                                                                                     "aggs": {
                                                                                        "hotel_hits": {
                                                                                            "top_hits": {
                                                                                                "size": 100,
                                                                                                "_source": {
                                                                                                    "includes": [ "hotel_id", "name", "property_type_ordinal", "neighborhood", "city", "propertyRating" ]
                                                                                                }
                                                                                            }
                                                                                        }           
                                                                                      }
                                                                                  }
                                                                              }
                                                                            
                                                                        }
                                                                    }
                                                                  }
                                                              }
                                                          }
                                                      }
                                                 }
                                             }
                                        }
                                    }
                                }
                            }
                        }
                    },
                    "fields": [
                        "id",
                        "name",
                        "property_type_ordinal",
                        "neighborhood",
                        "city",
                        "propertyRating"
                    ],
                    "_source": false
                }
                """;


//        "filter_hotel": {
//            "filter": {
//                "bool": {
//                    "must": [
//                    {
//                        "bool": {
//                        "should": [
//                        { "match": { "property_type_ordinal": "0" } },
//                        { "match": { "property_type_ordinal": "1" } }
//                                                                                                                     ],
//                        "minimum_should_match": 1
//                    }
//                    },
//                    {
//                        "nested": {
//                        "path": "facility",
//                                "query": { "match": { "facility.id": "9" } }
//                    }
//                    },
//                    {
//                        "nested": {
//                        "path": "rooms",
//                                "query": {
//                            "range": {
//                                "rooms.price_range": {
//                                    "relation": "intersects",
//                                            "gte": "0",
//                                            "lte": "10000"
//                                }
//                            }
//                        }
//                    }
//                    },
//                    { "range": { "propertyRating": { "gte": "3", "lte": "4" } } }
//                                                                                                             ]
//                }
//            },



        Request request = new Request("POST", "/hotel/_search");
        request.setJsonEntity(requestJson);
//        request.setJsonEntity(" { \"query\": { \"match_all\": {} }, \"fields\": [ \"rooms.id\" ], \"_source\": false } ");
//        request.setJsonEntity(" { \"query\": { \"match_all\": {} } }");
        Response response = restClient.performRequest(request);
//        System.out.println(response.getRequestLine());
//        System.out.println("--------------------------------------------------------");
//        System.out.println(response.getHost());
//        System.out.println("--------------------------------------------------------");
//        System.out.println(response.getStatusLine().getStatusCode());
//        System.out.println("--------------------------------------------------------");
//        System.out.println(EntityUtils.toString(response.getEntity()));
//        System.out.println("--------------------------------------------------------");
//        System.out.println(response.getEntity().getContent());
//        System.out.println("--------------------------------------------------------");

        JsonParser jsonParser = JsonParserFactory.getJsonParser();
//        System.out.println(EntityUtils.toString(response.getEntity()));
        Map<String, Object> map = jsonParser.parseMap(EntityUtils.toString(response.getEntity()));
        System.out.println("--------------------------------------------------------");
        System.out.println(map);
        System.out.println("--------------------------------------------------------");


    }
}



//,
//        "rooms_price_selector": {
//        "bucket_selector": {
//        "buckets_path": {
//        "sumPrice": "sum_price"
//        },
//        "script": "params.sumPrice > 100 && params.sumPrice < 10000"
//        }
//        },