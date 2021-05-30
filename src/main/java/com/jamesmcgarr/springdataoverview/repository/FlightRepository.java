package com.jamesmcgarr.springdataoverview.repository;

import com.jamesmcgarr.springdataoverview.entity.Flight;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface FlightRepository extends PagingAndSortingRepository<Flight, Long> {

    List<Flight> findByOrigin(String origin);

    List<Flight> findByOriginAndDestination(String origin, String destination);

    List<Flight> findByOriginIn(String ... origins);

    List<Flight> findByOriginIgnoreCase(String london);
}
