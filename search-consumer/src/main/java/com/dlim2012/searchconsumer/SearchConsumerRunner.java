package com.dlim2012.searchconsumer;

import com.dlim2012.searchconsumer.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchConsumerRunner implements CommandLineRunner {

    private final HotelRepository hotelRepository;

    @Override
    public void run(String... args) throws Exception {
//        System.out.println("Runner");
//        System.out.println("-------------------------------");
//        System.out.println(hotelRepository.findAll());
    }


}
