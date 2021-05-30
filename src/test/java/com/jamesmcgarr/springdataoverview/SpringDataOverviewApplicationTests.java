package com.jamesmcgarr.springdataoverview;

import com.jamesmcgarr.springdataoverview.entity.Flight;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
class SpringDataOverviewApplicationTests {

	@Autowired
	private EntityManager entityManager;

	@Test
	@Transactional
	void verifyFlightCanBeSaved() {
		final Flight flight = new Flight();
		flight.setOrigin("Amsterdam");
		flight.setDestination("New York");
		flight.setScheduledAt(LocalDateTime.parse("2011-12-13T12:12:00"));

		entityManager.persist(flight);

		final TypedQuery<Flight> results = entityManager
				.createQuery("SELECT f FROM Flight f", Flight.class);

		final List<Flight> resultList = results.getResultList();

		Assertions.assertThat(resultList)
				.hasSize(1)
				.first()
				.isEqualTo(flight);
	}

}
