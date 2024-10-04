package com.akshay.HotelBooking.service;

import com.akshay.HotelBooking.dto.Response;
import com.akshay.HotelBooking.entity.Booking;

public interface IBookingService {
	

    Response saveBooking(Long rooId, Long userId, Booking bookingRequest);
    Response findBookingByConfirmationCode(String confirmationCode);
    Response getAllBookings();
    Response cancelBooking(Long bookingId);

}
