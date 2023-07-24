package com.dlim2012.hotel.service;

import com.dlim2012.clients.entity.PropertyType;
import com.dlim2012.clients.exception.EntityAlreadyExistsException;
import com.dlim2012.clients.exception.ResourceNotFoundException;
import com.dlim2012.clients.kafka.dto.booking.hotel.HotelBookingDeleteRequest;
import com.dlim2012.clients.kafka.dto.booking.hotel.HotelBookingDetails;
import com.dlim2012.clients.kafka.dto.search.hotel.HotelSearchDeleteRequest;
import com.dlim2012.clients.kafka.dto.search.hotel.HotelSearchDetails;
import com.dlim2012.hotel.dto.hotel.list.HotelRowItem;
import com.dlim2012.hotel.dto.hotel.profile.HotelAddressItem;
import com.dlim2012.hotel.dto.hotel.profile.HotelFacilityItem;
import com.dlim2012.hotel.dto.hotel.profile.HotelGeneralInfoItem;
import com.dlim2012.hotel.dto.hotel.registration.HotelRegisterRequest;
import com.dlim2012.hotel.dto.hotel.registration.HotelRoomsInfoResponse;
import com.dlim2012.hotel.entity.Hotel;
import com.dlim2012.hotel.entity.facility.HotelFacility;
import com.dlim2012.hotel.entity.locality.City;
import com.dlim2012.hotel.entity.locality.Country;
import com.dlim2012.hotel.entity.locality.Locality;
import com.dlim2012.hotel.entity.locality.State;
import com.dlim2012.hotel.repository.HotelRepository;
import com.dlim2012.hotel.repository.facility.HotelFacilityRepository;
import com.dlim2012.hotel.repository.saved.SavedUserRepository;
import com.dlim2012.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;
    private final HotelFacilityRepository hotelFacilityRepository;
    private final SavedUserRepository savedUserRepository;

    private final JwtService jwtService;;


    private final KafkaTemplate<String, HotelSearchDetails> hotelSearchKafkaTemplate;
    private final KafkaTemplate<String, HotelSearchDeleteRequest> hotelSearchDeleteKafkaTemplate;
    private final KafkaTemplate<String, HotelBookingDeleteRequest> hotelBookingDeleteKafkaTemplate;
    private final KafkaTemplate<String, HotelBookingDetails> hotelBookingKafkaTemplate;

    private final RoomsService roomsService;
    private final ImageService imageService;
    private final LocalityService localityService;
    private final FacilityService facilityService;



    public List<HotelRowItem> getUserHotels(Integer userId) {
        return hotelRepository.findByHotelManagerId(userId)
                .stream().map(hotel -> HotelRowItem.builder()
                        .id(hotel.getId())
                        .name(hotel.getName())
                        .address(localityService.getFullAddress(hotel))
                        .build()
                ).toList();

    }



    public HotelRoomsInfoResponse getHotelRooms(Integer hotelId){
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found."));
        Locality locality = hotel.getLocality();
        City city = locality.getCity();
        State state = city.getState();
        Country country = state.getCountry();

        boolean frontDesk24h = false;
        if (hotel.getHotelFacilities() != null) {
            for (HotelFacility hotelFacility : hotel.getHotelFacilities()) {
                if (hotelFacility.getFacility().getId() == 1) {
                    frontDesk24h = true;
                }
            }
        }

        boolean saved = false;
        try{
            Integer userId = jwtService.getId();
            saved = savedUserRepository.existsByHotelIdAndUserId(hotelId, userId);
        } catch (Exception e){

        }


        HotelRoomsInfoResponse hotelRoomsInfoResponse = HotelRoomsInfoResponse.builder()
                .isActive(hotel.getIsActive())
                .id(hotel.getId())
                .name(hotel.getName())
                .description(hotel.getDescription())
                .propertyType(hotel.getPropertyType().name())
                .addressLine1(hotel.getAddressLine1())
                .addressLine2(hotel.getAddressLine2())
                .zipcode(locality.getZipcode())
                .neighborhood(hotel.getNeighborhood())
                .city(city.getName())
                .state(state.getName())
                .country(country.getName())
                .latitude(hotel.getLatitude())
                .longitude(hotel.getLongitude())
                .propertyRating(hotel.getPropertyRating())
                .frontDesk24h(frontDesk24h)
                .facilityDisplayNameList(
                        hotel.getHotelFacilities() == null ? new ArrayList<>() : hotel.getHotelFacilities().stream()
                        .map(hotelFacility -> hotelFacility.getFacility().getDisplayName()).toList())
                .roomsInfoList(roomsService.getRoomsInfo(hotelId))
                .saved(saved)
                .build();

        return hotelRoomsInfoResponse;
    }

    public Hotel postHotel(
            Integer userId,
            HotelRegisterRequest request,
            boolean internal
            ) {
        /*
         * While countries are pre-populated beforehand, state, city, and localities can be added by a post hotel request
         * */
//        if (hotelRepository.existsByNameAndHotelManagerId(request.getName(), userId)){
//            throw new EntityAlreadyExistsException(String.format("Hotel with the name %s is already registered by the user %s.", request.getName(), userId));
//        }

        Locality locality = localityService.createOrGetLocality(
                request.getZipcode() == null ? "" : request.getZipcode(),
                request.getCity() == null ? "" : request.getCity(),
                request.getState() == null ? "" : request.getState(),
                request.getCountry() == null ? "" : request.getCountry()
        );

        Hotel hotel = Hotel.builder()
                .hotelManagerId(userId)
                .name(request.getName())
                .description(request.getDescription())
                .isActive(true)
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .neighborhood(request.getNeighborhood())
                .locality(locality)
                .propertyType(PropertyType.valueOf(request.getPropertyType()))
                .phone(request.getPhone())
                .fax(request.getFax())
                .website(request.getWebsite())
                .email(request.getEmail())
                .propertyRating(request.getPropertyRating())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
//                .distanceFromCenter(request.getDistanceFromCenter())
                .updatedTime(LocalDateTime.now())
                .hotelFacilities(new ArrayList<>())
                .build();
//        hotel.setPropertyType(PropertyType.Inn);

        hotel = hotelRepository.save(hotel);

        List<HotelFacility> savedHotelFacilities = facilityService.saveHotelFacilities(hotel, request.getFacilityDisplayNameList());


//        hotel = hotelRepository.findById(hotel.getId()).orElseThrow(() -> new ResourceNotFoundException("Hotel not found."));
        hotel.setHotelFacilities(savedHotelFacilities);
        sendHotelKafka(hotel, true);
        return hotel;
    }

    public HotelGeneralInfoItem getGeneralInfo(Integer hotelId, Integer userId) {
        Hotel hotel = hotelRepository.findByIdAndHotelManagerId(hotelId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found."));
        return HotelGeneralInfoItem.builder()
                .name(hotel.getName())
                .description(hotel.getDescription())
                .propertyType(hotel.getPropertyType().name())
                .phone(hotel.getPhone())
                .fax(hotel.getFax())
                .website(hotel.getWebsite())
                .email(hotel.getEmail())
                .propertyRating(hotel.getPropertyRating())
                .build();
    }


    public void putHotelGeneralInfo(Integer hotelId, Integer userId, HotelGeneralInfoItem info) {
        Hotel hotel = hotelRepository.findByIdAndHotelManagerId(hotelId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found."));
        hotel.setName(info.getName());
        hotel.setDescription(info.getDescription());
        hotel.setPropertyType(PropertyType.valueOf(info.getPropertyType()));
        hotel.setPhone(info.getPhone());
        hotel.setFax(info.getFax());
        hotel.setWebsite(info.getWebsite());
        hotel.setEmail(info.getEmail());
        hotel.setPropertyRating(info.getPropertyRating());
        hotelRepository.save(hotel);

        sendHotelKafka(hotel, false);
    }
    public HotelFacilityItem getHotelFacilities(Integer hotelId, Integer userId) {
        Hotel hotel = hotelRepository.findByIdAndHotelManagerId(hotelId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found."));
        return HotelFacilityItem.builder()
                .facility(hotel.getHotelFacilities().stream()
                        .map(hotelFacility -> hotelFacility.getFacility().getDisplayName())
                        .toList())
                .build();
    }

    public void putHotelFacilities(Integer hotelId, Integer userId, HotelFacilityItem hotelFacilityItem) {

        Hotel hotel = hotelRepository.findByIdAndHotelManagerId(hotelId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found."));
        Map<String, HotelFacility> map = hotel.getHotelFacilities().stream()
                .collect(Collectors.toMap(hotelFacility -> hotelFacility.getFacility().getDisplayName(),
                        hotelFacility -> hotelFacility));
        List<String> hotelFacilityToAdd = new ArrayList<>();
        boolean sendKafka = false;
        for (String facility: hotelFacilityItem.getFacility()){
            if (map.containsKey(facility)){
                map.remove(facility);
            } else {
                hotelFacilityToAdd.add(facility);
                sendKafka = true;
            }
        }
        for (Map.Entry<String, HotelFacility> entry: map.entrySet()){
            hotel.getHotelFacilities().remove(entry.getValue());
            sendKafka = true;
        }
        hotelRepository.save(hotel);
        facilityService.saveHotelFacilities(hotel, hotelFacilityToAdd);

        if (sendKafka) {
            sendHotelKafka(hotel, false);
        }
    }

    public HotelAddressItem getHotelAddress(Integer hotelId, Integer userId) {
        Hotel hotel = hotelRepository.findByIdAndHotelManagerId(hotelId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found."));
        Locality locality = hotel.getLocality();
        City city = locality.getCity();
        State state = city.getState();
        Country country = state.getCountry();
        return HotelAddressItem.builder()
                .addressLine1(hotel.getAddressLine1())
                .addressLine2(hotel.getAddressLine2())
                .neighborhood(hotel.getNeighborhood())
                .city(city.getName())
                .state(state.getName())
                .country(country.getName())
                .zipcode(locality.getZipcode())
                .latitude(hotel.getLatitude())
                .longitude(hotel.getLongitude())
                .build();
    }

    public void putHotelAddress(Integer hotelId, Integer userId, HotelAddressItem item) {
        Hotel hotel = hotelRepository.findByIdAndHotelManagerId(hotelId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found."));
        Locality locality = hotel.getLocality();
        City city = locality.getCity();
        State state = city.getState();
        Country country = state.getCountry();

        boolean sendKafka = !Objects.equals(hotel.getAddressLine1(), item.getAddressLine1())
                || !Objects.equals(hotel.getAddressLine2(), item.getAddressLine2())
                || !Objects.equals(hotel.getNeighborhood(), item.getNeighborhood())
                || !Objects.equals(hotel.getLatitude(), item.getLatitude())
                || !Objects.equals(hotel.getLongitude(), item.getLongitude())
                || !Objects.equals(locality.getZipcode(), item.getZipcode())
                || !Objects.equals(city.getName(), item.getCity())
                || !Objects.equals(state.getName(), item.getState())
                || !Objects.equals(country.getName(), item.getCountry());

        locality = localityService.createOrGetLocality(
                item.getZipcode(), item.getCity(),
                item.getState(), item.getCountry()
        );

        hotel.setLocality(locality);
        hotel.setAddressLine1(item.getAddressLine1());
        hotel.setAddressLine2(item.getAddressLine2());
        hotel.setNeighborhood(item.getNeighborhood());
        hotel.setLatitude(item.getLatitude());
        hotel.setLongitude(item.getLongitude());
        hotelRepository.save(hotel);

        if (sendKafka) {
            sendHotelKafka(hotel, false);
        }
    }

    public void sendHotelKafka(Hotel hotel, boolean createNew){
        Locality locality = hotel.getLocality();
        City city = locality.getCity();
        State state = city.getState();
        Country country = state.getCountry();

        HotelSearchDetails hotelSearchDetails = HotelSearchDetails.builder()
                .createNew(createNew)
                .id(hotel.getId())
                .name(hotel.getName())
                .propertyTypeOrdinal(hotel.getPropertyType().ordinal())
                .neighborhood(hotel.getNeighborhood())
                .zipcode(locality.getZipcode())
                .city(city.getName())
                .state(state.getName())
                .country(country.getName())
                .latitude(hotel.getLatitude())
                .longitude(hotel.getLongitude())
                .propertyRating(hotel.getPropertyRating())
                .facility(hotel.getHotelFacilities().stream()
                        .map(entity -> HotelSearchDetails.FacilityDto.builder()
                                .id(entity.getId())
                                .displayName(entity.getFacility().getDisplayName())
                                .build())
                        .toList()
                )
                .build();
        hotelSearchKafkaTemplate.send("hotel-search", hotelSearchDetails);

        HotelBookingDetails hotelBookingDetails = HotelBookingDetails.builder()
                .hotelId(hotel.getId())
                .hotelManagerId(hotel.getHotelManagerId())
                .build();
        hotelBookingKafkaTemplate.send("hotel-booking", hotelBookingDetails);
    }


    public void deleteHotel(Integer hotelId) {
        if (!hotelRepository.existsById(hotelId)){
            throw new ResourceNotFoundException("Hotel not found for deletion.");
        }
        hotelRepository.deleteById(hotelId);
        hotelSearchDeleteKafkaTemplate.send("hotel-search-delete",
                HotelSearchDeleteRequest.builder().hotelId(hotelId).build());
        hotelBookingDeleteKafkaTemplate.send("hotel-booking-delete",
                HotelBookingDeleteRequest.builder().hotelId(hotelId).build());
    }
}
