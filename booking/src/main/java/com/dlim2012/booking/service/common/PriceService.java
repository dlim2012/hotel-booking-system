package com.dlim2012.booking.service.common;

import com.dlim2012.booking.dto.profile.RoomsPriceItem;
import com.dlim2012.clients.entity.UserRole;
import com.dlim2012.clients.kafka.dto.search.price.PriceUpdateDetails;
import com.dlim2012.clients.mysql_booking.entity.Price;
import com.dlim2012.clients.mysql_booking.entity.Rooms;
import com.dlim2012.clients.mysql_booking.repository.PriceRepository;
import com.dlim2012.clients.mysql_booking.repository.RoomsRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PriceService {

    private final GetEntityService getEntityService;
    private final MappingService mappingService;

    private final PriceRepository priceRepository;
    private final EntityManager entityManager;
    private final KafkaTemplate<String, PriceUpdateDetails> roomsSearchPriceUpdateKafkaTemplate;

    private final RoomsRepository roomsRepository;

    /*
    Internal Use
     */

    public Long getDefaultPriceInCents(
            Long priceMin, Long priceMax
    ) {
        return (priceMin + priceMax) / 2;
    }


    public void adjust(Rooms rooms, LocalDate startDate, LocalDate endDate, Long priceMin, Long priceMax) {

        List<Price> priceList = priceRepository.findByRoomsIdWithLock(rooms.getId());
        List<Price> priceToRemoveList = new ArrayList<>();
        Set<LocalDate> existingDates = new HashSet<>();
        for (Price price : priceList) {
            if (price.getDate().isBefore(startDate) || !price.getDate().isBefore(endDate)) {
                priceToRemoveList.add(price);
            } else {
                existingDates.add(price.getDate());
            }
        }

        List<Price> newPriceList = new ArrayList<>();
        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            if (existingDates.contains(date)) {
                continue;
            }
            Price price = Price.builder()
                    .rooms(rooms)
                    .date(date)
                    .priceInCents(getDefaultPriceInCents(priceMin, priceMax))
                    .version(0)
                    .build();
            newPriceList.add(price);
        }

        priceRepository.deleteAll(priceToRemoveList);
        priceRepository.saveAll(newPriceList);


    }


    /*
    For REST APIs
     */

    public void putRoomsPrice(
            UserRole userRole, Integer userId, Integer hotelId,
            RoomsPriceItem roomsPriceItem
    ) {
        /* This function does not add price for new dates */
        List<Price> priceList = getEntityService.getPriceByManagerWithLock(userRole, userId, roomsPriceItem.getRoomsId());

        Map<LocalDate, Long> newPriceMap = new HashMap<>();
        for (RoomsPriceItem.PriceDto priceDto : roomsPriceItem.getPriceDtoList()) {
            newPriceMap.put(priceDto.getDate(), priceDto.getPriceInCents());
        }

        for (Price price : priceList) {
            Long newPriceInCents = newPriceMap.get(price.getDate());
            if (newPriceInCents == null) {
                continue;
            }
            newPriceMap.remove(price.getDate());
            price.setPriceInCents(newPriceInCents);
            price.setVersion(price.getVersion() + 1);
        }

        priceRepository.saveAll(priceList);

        PriceUpdateDetails priceUpdateDetails = mappingService.getPriceUpdateDetails(hotelId, roomsPriceItem.getRoomsId(), priceList);
        roomsSearchPriceUpdateKafkaTemplate.send("rooms-search-price-update", priceUpdateDetails);
    }


    public void removeAllPrice(Integer roomsId) {
        // when 'rooms' is not called with lock
        priceRepository.deleteAllByRoomsId(roomsId);
    }

}
