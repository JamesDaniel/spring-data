package com.jamesmcgarr.springdataoverview;

import com.jamesmcgarr.springdataoverview.entity.Flight;
import com.jamesmcgarr.springdataoverview.repository.FlightRepository;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
class PagingAndSortingTests {

	@Autowired
	private FlightRepository flightRepository;

	@Before
	private void setUp() {
		flightRepository.deleteAll();
	}

	@Test
	public void shouldSortFlightsByDestination() {
		final Flight flight1 = createFlight("Madrid");
		final Flight flight2 = createFlight("London");
		final Flight flight3 = createFlight("Paris");

		flightRepository.save(flight1);
		flightRepository.save(flight2);
		flightRepository.save(flight3);

		final Iterable<Flight> flights = flightRepository.findAll(Sort.by("destination"));
//		final Iterable<Flight> flights = flightRepository.findAll(Sort.by(Sort.Direction.DESC, "destination"));

		assertThat(flights).hasSize(3);

		final Iterator<Flight> iterator = flights.iterator();

		assertThat(iterator.next().getDestination()).isEqualTo("London");
		assertThat(iterator.next().getDestination()).isEqualTo("Madrid");
		assertThat(iterator.next().getDestination()).isEqualTo("Paris");
	}

	private Flight createFlight(String origin, String destination) {
		final Flight flight = new Flight();
		flight.setOrigin(origin);
		flight.setDestination(destination);
		flight.setScheduledAt(LocalDateTime.parse("2011-12-13T12:12:00"));
		return flight;
	}

	private Flight createFlight(String destination) {
		return createFlight("London", destination);
	}

	@Test
	public void shouldSortFlightsByScheduledAndThenName() {
		final LocalDateTime now = LocalDateTime.now();
		final Flight paris1 = createFlight("Paris", now);
		final Flight paris2 = createFlight("Paris", now.plusHours(2));
		final Flight paris3 = createFlight("Paris", now.minusHours(1));

		final Flight london1 = createFlight("London", now.plusHours(1));
		final Flight london2 = createFlight("London", now);


		flightRepository.save(paris1);
		flightRepository.save(paris2);
		flightRepository.save(paris3);
		flightRepository.save(london1);
		flightRepository.save(london2);

		final Iterable<Flight> flights = flightRepository
				.findAll(Sort.by("destination", "scheduledAt"));

		assertThat(flights).hasSize(5);

		final Iterator<Flight> iterator = flights.iterator();

		assertThat(iterator.next()).isEqualToComparingFieldByField(london2);
		assertThat(iterator.next()).isEqualToComparingFieldByField(london1);
		assertThat(iterator.next()).isEqualToComparingFieldByField(paris3);
		assertThat(iterator.next()).isEqualToComparingFieldByField(paris1);
		assertThat(iterator.next()).isEqualToComparingFieldByField(paris2);
	}

	@Test
	public void shouldPageResults() {
		for (int i = 0; i < 50; i++) {
			flightRepository.save(createFlight(String.valueOf(i)));
		}

		final Page<Flight> page = flightRepository.findAll(PageRequest.of(2, 5));

		assertThat(page.getTotalElements()).isEqualTo(50);
		assertThat(page.getNumberOfElements()).isEqualTo(5);
		assertThat(page.getTotalPages()).isEqualTo(10);
		assertThat(page.getContent())
				.extracting(Flight::getDestination)
				.containsExactly("10", "11", "12", "13", "14");
	}

	@Test
	public void shouldPageAndSortResults() {
		for (int i = 0; i < 50; i++) {
			flightRepository.save(createFlight(String.valueOf(i)));
		}

		final Page<Flight> page = flightRepository.findAll(PageRequest.of(2, 5, Sort.by(Sort.Direction.DESC, "destination")));

		assertThat(page.getTotalElements()).isEqualTo(50);
		assertThat(page.getNumberOfElements()).isEqualTo(5);
		assertThat(page.getTotalPages()).isEqualTo(10);
		assertThat(page.getContent())
				.extracting(Flight::getDestination)
				.containsExactly("44", "43", "42", "41", "40");
	}

	@Test
	public void shouldPageAndSortADerivedQuery() {
		for (int i = 0; i < 10; i++) {
			final Flight flight = createFlight(String.valueOf(i));
			flight.setOrigin("Paris");
			flightRepository.save(flight);
		}

		for (int i = 0; i < 10; i++) {
			final Flight flight = createFlight(String.valueOf(i));
			flight.setOrigin("London");
			flightRepository.save(flight);
		}

		PageRequest sortByDestination = PageRequest.of(0, 5, Sort.by("destination").descending());

		final Page<Flight> page = flightRepository.findByOrigin("London", sortByDestination);

		assertThat(page.getTotalElements()).isEqualTo(10);
		assertThat(page.getNumberOfElements()).isEqualTo(5);
		assertThat(page.getTotalPages()).isEqualTo(2);
		assertThat(page.getContent())
				.extracting(Flight::getDestination)
				.containsExactly("9", "8", "7", "6", "5");
	}

	private Flight createFlight(String destination, LocalDateTime scheduledAt) {
		final Flight flight = new Flight();
		flight.setOrigin("London");
		flight.setDestination(destination);
		flight.setScheduledAt(scheduledAt);
		return flight;
	}


}
