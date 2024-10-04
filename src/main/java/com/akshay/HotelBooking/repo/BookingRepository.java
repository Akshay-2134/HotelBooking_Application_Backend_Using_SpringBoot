package com.akshay.HotelBooking.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.akshay.HotelBooking.entity.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {

	Optional<Booking> findByBookingConfirmationCode(String confirmationCode);
}
