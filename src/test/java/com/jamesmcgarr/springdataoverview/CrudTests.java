package com.jamesmcgarr.springdataoverview;

import com.jamesmcgarr.springdataoverview.entity.Flight;
import com.jamesmcgarr.springdataoverview.repository.FlightRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
class CrudTests {

	@Autowired
	private FlightRepository flightRepository;

	@Test
	public void shouldPerformCrudOperations() {
		final Flight flight = new Flight();
		flight.setOrigin("Amsterdam");
		flight.setDestination("New York");
		flight.setScheduledAd(LocalDateTime.parse("2011-12-13T12:12:00"));

		flightRepository.save(flight);

		assertThat(flightRepository.findAll())
				.hasSize(1)
				.first()
				.isEqualToComparingFieldByField(flight);

		flightRepository.deleteById(flight.getId());

		assertThat(flightRepository.count()).isZero();
	}

}
