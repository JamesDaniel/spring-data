package com.jamesmcgarr.springdataoverview.repository;

import com.jamesmcgarr.springdataoverview.entity.Flight;
import org.springframework.data.repository.CrudRepository;

public interface FlightRepository extends CrudRepository<Flight, Long> {

}
