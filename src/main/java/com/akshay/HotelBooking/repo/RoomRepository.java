package com.akshay.HotelBooking.repo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.akshay.HotelBooking.entity.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {

	@Query("SELECT DISTINCT r.roomType FROM Room r")
	List<String> findDistinctRoomTypes();

	@Query("SELECT r FROM Room r WHERE r.id NOT IN (SELECT b.room.id FROM Booking b)")
	List<Room> getAllAvailableRooms();

	@Query("SELECT r FROM Room r WHERE r.roomType LIKE %:roomType% AND r.id NOT IN (SELECT bk.room.id FROM Booking bk WHERE"
			+ "(bk.checkInDate <= :checkOutDate) AND (bk.checkOutDate >= :checkInDate))")
	List<Room> findAvailableRoomsByDateAndTypes(LocalDateTime checkInDate, LocalDateTime checkOutDate, String roomType);

}
