package com.akshay.HotelBooking.service.Impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.akshay.HotelBooking.dto.Response;
import com.akshay.HotelBooking.dto.RoomDTO;
import com.akshay.HotelBooking.entity.Room;
import com.akshay.HotelBooking.exception.OurException;
import com.akshay.HotelBooking.repo.BookingRepository;
import com.akshay.HotelBooking.repo.RoomRepository;
import com.akshay.HotelBooking.service.IRoomService;
import com.akshay.HotelBooking.utils.Utils;

@Service
public class RoomService implements IRoomService {
	
	
	

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BookingRepository bookingRepository;

    // Directory where images will be stored
    private final String imageDirectory = "C:/hotelbooking/images/";

    // Save the room details and photo locally
    @Override
    public Response addNewRoom(MultipartFile photo, String roomType, BigDecimal roomPrice, String description) {
        Response response = new Response();

        try {
            // Ensure the directory exists
            File dir = new File(imageDirectory);
            if (!dir.exists()) {
                dir.mkdirs();
            }

//            // Save image locally and get the image URL (local path)
//            String imageUrl = saveImageLocally(photo);

            // Create room entity and set its fields
            Room room = new Room();
//            room.setRoomPhotoUrl(imageUrl);
            room.setRoomType(roomType);
            room.setRoomPrice(roomPrice);
            room.setRoomDescription(description);
            
            
            if (photo != null && !photo.isEmpty()) {
                String imageUrl = saveImageLocally(photo);
                room.setRoomPhotoUrl(imageUrl);
            } else {
                room.setRoomPhotoUrl(null); // Or set a default image URL
            }

            Room savedRoom = roomRepository.save(room);
            RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTO(savedRoom);

            response.setRoom(roomDTO);
            response.setMessage("successful");
            response.setStatusCode(200);

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error saving a room " + e.getMessage());
        }
        return response;
    }

    // Helper method to save image locally
    private String saveImageLocally(MultipartFile photo) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + photo.getOriginalFilename();
        File imageFile = new File(imageDirectory + fileName);
        FileOutputStream fos = new FileOutputStream(imageFile);
        fos.write(photo.getBytes());
        fos.close();
//        return imageFile.getAbsolutePath();  // Returns the local path of the saved image
        System.out.println("Image saved at: " + imageFile.getAbsolutePath());  // Debugging
        return "http://localhost:8080/images/" + fileName;  // Return full URL
    }

    @Override
    public List<String> getAllRoomTypes() {
        return roomRepository.findDistinctRoomTypes();
    }

    @Override
    public Response getAllRooms() {
        Response response = new Response();

        try {
            List<Room> roomList = roomRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
            List<RoomDTO> roomDTOList = Utils.mapRoomListEntityToRoomListDTO(roomList);

            response.setMessage("successful");
            response.setStatusCode(200);
            response.setRoomList(roomDTOList);

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error getting all rooms " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response deleteRoom(Long roomId) {
        Response response = new Response();

        try {
            Room room = roomRepository.findById(roomId).orElseThrow(() -> new OurException("Room Not Found"));
            roomRepository.deleteById(roomId);

            // Delete image file from local storage
            Files.deleteIfExists(Paths.get(room.getRoomPhotoUrl()));

            response.setMessage("successful");
            response.setStatusCode(200);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error deleting a room " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response updateRoom(Long roomId, String description, String roomType, BigDecimal roomPrice, MultipartFile photo) {
        Response response = new Response();

        try {
            Room room = roomRepository.findById(roomId).orElseThrow(() -> new OurException("Room Not Found"));

            // Update room details
            if (roomType != null) room.setRoomType(roomType);
            if (roomPrice != null) room.setRoomPrice(roomPrice);
            if (description != null) room.setRoomDescription(description);

            // If a new photo is provided, update it
            if (photo != null && !photo.isEmpty()) {
                // Delete old image file
                Files.deleteIfExists(Paths.get(room.getRoomPhotoUrl()));

                // Save new image locally
//                String imageUrl = saveImageLocally(photo);
                String imageUrl = "http://localhost:8080/images/" ;
                room.setRoomPhotoUrl(imageUrl);
            }

            Room updatedRoom = roomRepository.save(room);
            RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTO(updatedRoom);

            response.setMessage("successful");
            response.setStatusCode(200);
            response.setRoom(roomDTO);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error updating a room " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getRoomById(Long roomId) {
        Response response = new Response();

        try {
            Room room = roomRepository.findById(roomId).orElseThrow(() -> new OurException("Room Not Found"));
            RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTOPlusBookings(room);

            response.setMessage("successful");
            response.setStatusCode(200);
            response.setRoom(roomDTO);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Getting a room By Id " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getAvailableRoomsByDateAndType(LocalDateTime checkInDate, LocalDateTime checkOutDate, String roomType) {
        Response response = new Response();

        try {
            List<Room> availableRooms = roomRepository.findAvailableRoomsByDateAndTypes(checkInDate, checkOutDate, roomType);
            List<RoomDTO> roomDTOList = Utils.mapRoomListEntityToRoomListDTO(availableRooms);

            response.setMessage("successful");
            response.setStatusCode(200);
            response.setRoomList(roomDTOList);

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error getting available rooms " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getAllAvailableRooms() {
        Response response = new Response();

        try {
            List<Room> roomList = roomRepository.getAllAvailableRooms();
            List<RoomDTO> roomDTOList = Utils.mapRoomListEntityToRoomListDTO(roomList);

            response.setMessage("successful");
            response.setStatusCode(200);
            response.setRoomList(roomDTOList);

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error getting available rooms " + e.getMessage());
        }
        return response;
    }
}
