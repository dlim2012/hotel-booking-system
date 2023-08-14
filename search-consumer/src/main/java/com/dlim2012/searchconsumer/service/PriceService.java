package com.dlim2012.searchconsumer.service;

import com.dlim2012.clients.elasticsearch.config.ElasticSearchUtils;
import com.dlim2012.clients.elasticsearch.document.Hotel;
import com.dlim2012.clients.elasticsearch.document.Price;
import com.dlim2012.clients.elasticsearch.document.Rooms;
import com.dlim2012.clients.exception.ResourceNotFoundException;
import com.dlim2012.clients.kafka.dto.search.price.PriceUpdateDetails;
import com.dlim2012.searchconsumer.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class PriceService {

    private final Integer NUM_RETRY_UPDATE = 2;
    private final HotelRepository hotelRepository;

    private final ElasticSearchUtils elasticSearchUtils;

    private final Random random = new Random();



    public void updatePrices(PriceUpdateDetails details) throws InterruptedException {

        for (int i=0; i<NUM_RETRY_UPDATE; i++){
            try {
                // fetch hotel
                Hotel hotel = hotelRepository.findById(details.getHotelId().toString())
                        .orElseThrow(() -> new ResourceNotFoundException("Hotel {} not found while updating."));

                if (hotel.getVersion() > details.getVersion()){
                    return;
                }

                if (hotel.getSeqNoPrimaryTerm() == null){
                    throw new ResourceNotFoundException("Hotel found without SeqNoPrimaryTerm");
                }

                if (hotel.getRooms() != null){
                    for (Rooms rooms: hotel.getRooms()){
                        if (rooms.getRoomsId().equals(details.getRoomsId())){
                            rooms.setPrice(details.getPriceDtoList().stream()
                                    .map(priceDto -> Price.builder()
                                            .id(priceDto.getPriceId().toString())
                                            .date(elasticSearchUtils.toInteger(priceDto.getDate()))
                                            .priceInCents(priceDto.getPriceInCents())
                                            .build())
                                    .toList()
                            );
                        }
                    }
                }

                hotelRepository.save(hotel);
                return;
            } catch (OptimisticLockingFailureException | ResourceNotFoundException e){
                log.error(e.getMessage());
                TimeUnit.MILLISECONDS.sleep((long) (random.nextDouble() * 1000));
            } catch (Exception e){
                log.error(e.getMessage());
                return;
            }
        }
        log.error("Updating dates for hotel {} failed after {} retries.", details.getHotelId(), NUM_RETRY_UPDATE);
    }
}
