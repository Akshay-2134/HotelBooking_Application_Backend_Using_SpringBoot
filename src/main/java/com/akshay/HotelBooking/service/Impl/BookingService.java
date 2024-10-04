package com.akshay.HotelBooking.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.akshay.HotelBooking.dto.BookingDTO;
import com.akshay.HotelBooking.dto.Response;
import com.akshay.HotelBooking.entity.Booking;
import com.akshay.HotelBooking.entity.Room;
import com.akshay.HotelBooking.entity.User;
import com.akshay.HotelBooking.exception.OurException;
import com.akshay.HotelBooking.repo.BookingRepository;
import com.akshay.HotelBooking.repo.RoomRepository;
import com.akshay.HotelBooking.repo.UserRepository;
import com.akshay.HotelBooking.service.IBookingService;
import com.akshay.HotelBooking.service.IRoomService;
import com.akshay.HotelBooking.utils.Utils;




@Service
public class BookingService implements IBookingService {

	  @Autowired
	    private BookingRepository bookingRepository;
	    @Autowired
	    private IRoomService roomService;
	    @Autowired
	    private RoomRepository roomRepository;
	    @Autowired
	    private UserRepository userRepository;


	    @Override
	    public Response saveBooking(Long rooId, Long userId, Booking bookingRequest) {
	        Response response = new Response();

	        try {
	            if(bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())){
	                throw new IllegalArgumentException("Check in date must come before check out date");
	            }
	            Room room = roomRepository.findById(rooId).orElseThrow(()-> new OurException("Room Not Found"));
	            User user = userRepository.findById(userId).orElseThrow(()-> new OurException("User Not Found"));

	            List<Booking> existingBookings = room.getBookings();
	            if (!roomIsAvailable(bookingRequest, existingBookings)){
	                throw new OurException("Room not Available for the selected date range");
	            }
	            bookingRequest.setRoom(room);
	            bookingRequest.setUser(user);
	            String bookingConfirmationCode = Utils.generateRandomConfirmationCode(10);
	            bookingRequest.setBookingConfirmationCode(bookingConfirmationCode);
	            bookingRepository.save(bookingRequest);

	            response.setStatusCode(200);
	            response.setMessage("successful");
	            response.setBookingConfirmationCode(bookingConfirmationCode);


	            response.setMessage("successful");
	            response.setStatusCode(200);

	        }catch (OurException e) {
	            response.setStatusCode(404);
	            response.setMessage(e.getMessage());
	        }catch (Exception e) {
	            response.setStatusCode(500);
	            response.setMessage("Error saving a  booking " + e.getMessage());
	        }
	        return response;
	    }

	    @Override
	    public Response findBookingByConfirmationCode(String confirmationCode) {
	        Response response = new Response();

	        try {
	            Booking booking = bookingRepository.findByBookingConfirmationCode(confirmationCode).orElseThrow(()-> new OurException("Booking Not Found"));
	            BookingDTO bookingDTO = Utils.mapBookingEntityToBookingDTOPlusBookedRooms(booking, true);
	            response.setMessage("successful");
	            response.setStatusCode(200);
	            response.setBooking(bookingDTO);

	        }catch (OurException e) {
	            response.setStatusCode(404);
	            response.setMessage(e.getMessage());

	        }catch (Exception e) {
	            response.setStatusCode(500);
	            response.setMessage("Error getting booking by confirmation code " + e.getMessage());
	        }
	        return response;
	    }

	    @Override
	    public Response getAllBookings() {
	        Response response = new Response();

	        try {
	            List<Booking> bookingList = bookingRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
	            List<BookingDTO> bookingDTOList = Utils.mapBookingListEntityToBookingListDTO(bookingList);
	            response.setMessage("successful");
	            response.setStatusCode(200);
	            response.setBookingList(bookingDTOList);

	        }catch (Exception e) {
	            response.setStatusCode(500);
	            response.setMessage("Error getting all bookings " + e.getMessage());
	        }
	        return response;
	    }

	    @Override
	    public Response cancelBooking(Long bookingId) {
	        Response response = new Response();

	        try {
	            bookingRepository.findById(bookingId).orElseThrow(()-> new OurException("Booking Not Found"));
	            bookingRepository.deleteById(bookingId);
	            response.setMessage("successful");
	            response.setStatusCode(200);

	        }catch (OurException e) {
	            response.setStatusCode(404);
	            response.setMessage(e.getMessage());

	        }catch (Exception e) {
	            response.setStatusCode(500);
	            response.setMessage("Error cancelling a bookings " + e.getMessage());
	        }
	        return response;
	    }

	    private boolean roomIsAvailable(Booking bookingRequest, List<Booking> existingBookings){
	        return existingBookings.stream()
	                .noneMatch(existingBooking ->
	                        bookingRequest.getCheckInDate().equals(existingBooking.getCheckInDate())
	                                || bookingRequest.getCheckOutDate().isBefore(existingBooking.getCheckOutDate())
	                                || (bookingRequest.getCheckInDate().isAfter(existingBooking.getCheckInDate())
	                                && bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckOutDate()))
	                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

	                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckOutDate()))
	                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

	                                && bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckOutDate()))

	                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
	                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckInDate()))

	                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
	                                && bookingRequest.getCheckOutDate().equals(bookingRequest.getCheckInDate()))
	                );

	    }

}
