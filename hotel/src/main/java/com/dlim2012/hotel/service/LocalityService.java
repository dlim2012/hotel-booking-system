package com.dlim2012.hotel.service;

import com.dlim2012.clients.dto.IdItem;
import com.dlim2012.clients.exception.DeleteRuleException;
import com.dlim2012.clients.exception.EntityAlreadyExistsException;
import com.dlim2012.clients.exception.ResourceNotFoundException;
import com.dlim2012.hotel.dto.locality.CityItem;
import com.dlim2012.hotel.dto.locality.CountryItem;
import com.dlim2012.hotel.dto.locality.LocalityItem;
import com.dlim2012.hotel.dto.locality.StateItem;
import com.dlim2012.hotel.entity.Hotel;
import com.dlim2012.hotel.entity.locality.City;
import com.dlim2012.hotel.entity.locality.Country;
import com.dlim2012.hotel.entity.locality.Locality;
import com.dlim2012.hotel.entity.locality.State;
import com.dlim2012.hotel.repository.HotelRepository;
import com.dlim2012.hotel.repository.locality.CityRepository;
import com.dlim2012.hotel.repository.locality.CountryRepository;
import com.dlim2012.hotel.repository.locality.LocalityRepository;
import com.dlim2012.hotel.repository.locality.StateRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class LocalityService {

    private final HotelRepository hotelRepository;

    private final CountryRepository countryRepository;
    private final StateRepository stateRepository;
    private final CityRepository cityRepository;
    private final LocalityRepository localityRepository;

    private final ModelMapper modelMapper = new ModelMapper();
    private final EntityManager entityManager;

    public List<String> getAddressComponents(Locality locality){
        City city = locality.getCity();
        State state = city.getState();
        Country country = state.getCountry();
        List<String> addressComponents = new ArrayList<>();
        addressComponents.add(city.getName());
        addressComponents.add(state.getName());
        addressComponents.add(country.getName());
        addressComponents.add(locality.getZipcode());
        return addressComponents;
    }

    public String getFullAddress(Hotel hotel){
        Locality locality = hotel.getLocality();
        City city = locality.getCity();
        State state = city.getState();
        Country country = state.getCountry();
        List<String> addressComponents = new ArrayList<>();
        if (!hotel.getAddressLine1().isEmpty()){
            addressComponents.add(hotel.getAddressLine1());
        }
        if (!city.getName().isEmpty()){
            addressComponents.add(city.getName());
        }
        if (!state.getName().isEmpty()){
            addressComponents.add(state.getName());
        }
        if (!country.getName().isEmpty()){
            addressComponents.add(country.getName());
        }
        if (!locality.getZipcode().isEmpty()){
            addressComponents.add(locality.getZipcode());
        }
        return String.join(", ", addressComponents);
    }

    public void postCountry(CountryItem countryItem) {
        if (countryRepository.existsByName(countryItem.getName())) {
            throw new EntityAlreadyExistsException("Country already exists.");
        }
        Country country = modelMapper.map(countryItem, Country.class);
        country.setId(null);
        countryRepository.save(country);
    }

    public void putCountry(CountryItem countryItem) {
        if (!countryRepository.existsById(countryItem.getId())) {
            throw new ResourceNotFoundException("Country not found.");
        }
        Country country = modelMapper.map(countryItem, Country.class);
        country.setId(countryItem.getId());
        countryRepository.save(country);
    }

    public List<CountryItem> getAllCountries() {
        return countryRepository.findAll()
                .stream().map(entity -> modelMapper.map(entity, CountryItem.class)).toList();
    }

    public void deleteCountries(List<IdItem> idItemList) {
        List<Integer> toDelete = new ArrayList<>();
        for (IdItem idItem : idItemList) {
            if (stateRepository.existsByCountryId(idItem.getId())){
                throw new DeleteRuleException("The country is being used by one or more states.");
            }
            toDelete.add(idItem.getId());
        }
        countryRepository.deleteAllById(toDelete);
    }


    public void postState(StateItem stateItem) {
        if (!countryRepository.existsById(stateItem.getCountryId())) {
            throw new ResourceNotFoundException("Country not found.");
        }
        State state = modelMapper.map(stateItem, State.class);
        state.setCountry(entityManager.getReference(Country.class, stateItem.getCountryId()));
        state.setId(null);
        stateRepository.save(state);
    }


    public void putState(StateItem stateItem){
        if (!stateRepository.existsByCountryIdAndId(stateItem.getCountryId(), stateItem.getId())){
            throw new ResourceNotFoundException("State not found.");
        }
        State state = modelMapper.map(stateItem, State.class);
        state.setCountry(entityManager.getReference(Country.class, stateItem.getCountryId()));
        state.setId(stateItem.getId());
        stateRepository.save(state);
    }

    public List<StateItem> getStates(Integer countryId){
        return stateRepository.findByCountryId(countryId)
                .stream().map(entity -> modelMapper.map(entity, StateItem.class)).toList();
    }

    public void deleteStates(List<IdItem> idItemList) {
        List<Integer> toDelete = new ArrayList<>();
        for (IdItem idItem : idItemList) {
            if (cityRepository.existsByStateId(idItem.getId())){
                throw new DeleteRuleException("The state is being used by one or more cities.");
            }
            toDelete.add(idItem.getId());
        }
        stateRepository.deleteAllById(toDelete);
    }

    public void postCity(CityItem cityItem){
        if (!stateRepository.existsById(cityItem.getStateId())){
            throw new ResourceNotFoundException("State not found.");
        }
        City city = modelMapper.map(cityItem, City.class);
        city.setState(entityManager.getReference(State.class, cityItem.getStateId()));
        city.setId(null);
        cityRepository.save(city);
    }

    public void putCity(CityItem cityItem){
        if (!cityRepository.existsByStateIdAndId(cityItem.getStateId(), cityItem.getId())){
            throw new ResourceNotFoundException("City not found.");
        }
        City city = modelMapper.map(cityItem, City.class);
        city.setState(entityManager.getReference(State.class, cityItem.getStateId()));
        city.setId(cityItem.getId());
        cityRepository.save(city);
    }

    public List<CityItem> getCities(Integer stateId){
        return cityRepository.findByStateId(stateId)
                .stream().map(entity -> modelMapper.map(entity, CityItem.class)).toList();
    }

    public void deleteCities(List<IdItem> id) {
        List<Integer> toDelete = new ArrayList<>();
        for (IdItem idItem : id) {
            if (localityRepository.existsByCityId(idItem.getId())){
                throw new DeleteRuleException("The city is being used by one or more localities.");
            }
            toDelete.add(idItem.getId());
        }
        cityRepository.deleteAllById(toDelete);
    }

    public void postLocality(LocalityItem localityItem) {
        if (!cityRepository.existsById(localityItem.getCityId())){
            throw new ResourceNotFoundException("City not found.");
        }
        Locality locality = modelMapper.map(localityItem, Locality.class);
        locality.setCity(entityManager.getReference(City.class, localityItem.getCityId()));
        locality.setId(null);
        localityRepository.save(locality);
    }

    public List<LocalityItem> getLocalities(Integer cityId) {
        return localityRepository.findByCityId(cityId)
                .stream().map(entity -> modelMapper.map(entity, LocalityItem.class)).toList();
    }

    public void deleteLocalities(List<IdItem> idItemList) {
        List<Integer> toDelete = new ArrayList<>();
        for (IdItem idItem : idItemList) {
            if (hotelRepository.existsByLocalityId(idItem.getId())){
                throw new DeleteRuleException("The locality is being used by one or more hotels.");
            }
            toDelete.add(idItem.getId());
        }
        localityRepository.deleteAllById(toDelete);
    }

    public void putLocality(LocalityItem localityItem) {
        if (!localityRepository.existsByCityIdAndId(localityItem.getCityId(), localityItem.getId())){
            throw new ResourceNotFoundException("Locality not found.");
        }
        Locality locality = modelMapper.map(localityItem, Locality.class);
        locality.setCity(entityManager.getReference(City.class, localityItem.getCityId()));
        locality.setId(locality.getId());
        localityRepository.save(locality);
    }


    public Locality createOrGetLocality(String zipcode, String cityName, String stateName, String countryName){
        if (countryName.isEmpty()){
            throw new IllegalArgumentException("Country name cannot be empty.");
        }

        Country country;
        Optional<Country> optionalCountry = countryRepository.findByName(countryName);
        if (optionalCountry.isEmpty()){
            country = Country.builder().name(countryName).build();
            countryRepository.save(country);
        } else {
            country = optionalCountry.get();
        }

        // Get State
        State state;
        Optional<State> optionalState = stateRepository.findByNameAndCountryId(
                stateName,
                country.getId()
        );
        if (optionalState.isEmpty()) {
            state = State.builder().name(stateName).country(country).build();
            stateRepository.save(state);
        } else {
            state = optionalState.get();
        }

        // Get City
        City city;
        Optional<City> optionalCity = cityRepository.findByNameAndStateId(cityName, state.getId());
        if (optionalCity.isEmpty()) {
            city = City.builder().name(cityName).state(state).build();
            cityRepository.save(city);
        } else {
            city = optionalCity.get();
        }

        // Check duplicate
        Locality locality = null;
        Optional<Locality> optionalLocality = localityRepository.findByZipcodeAndCityId(
                zipcode, city.getId()
        );
        if (optionalLocality.isPresent()) {
            return optionalLocality.get();
        }

        locality = Locality.builder()
                .zipcode(zipcode)
                .city(city)
                .build();

        return localityRepository.save(locality);
    }

}
