package com.akshay.HotelBooking.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.akshay.HotelBooking.dto.LoginRequest;
import com.akshay.HotelBooking.dto.Response;
import com.akshay.HotelBooking.dto.UserDTO;
import com.akshay.HotelBooking.entity.User;
import com.akshay.HotelBooking.exception.OurException;
import com.akshay.HotelBooking.repo.UserRepository;
import com.akshay.HotelBooking.service.IUserService;
import com.akshay.HotelBooking.utils.JWTUtils;
import com.akshay.HotelBooking.utils.Utils;




@Service
public class UserService implements IUserService{

	 @Autowired
	    private UserRepository userRepository;

	    @Autowired
	    private PasswordEncoder passwordEncoder;

	    @Autowired
	    private JWTUtils jwtUtils;

	    @Autowired
	    private AuthenticationManager authenticationManager;


	    @Override
	    public Response register(User user) {
	        Response response = new Response();

	        try {
	          
	            if (userRepository.count() == 0) {
	                user.setRole("ADMIN");
	            } else {
	              
	                if (user.getRole() == null || user.getRole().isBlank()) {
	                    user.setRole("USER");
	                } else {
	                  
	                    String role = user.getRole().toUpperCase();
	                    if (!role.equals("ADMIN") && !role.equals("USER")) {
	                        throw new OurException("Invalid role provided. Allowed roles are 'ADMIN' or 'USER'.");
	                    }
	                    user.setRole(role); 
	                }
	            }

	           
	            if (userRepository.existsByEmail(user.getEmail())) {
	                throw new OurException(user.getEmail() + " already exists.");
	            }

	            // Encode the password before saving
	            user.setPassword(passwordEncoder.encode(user.getPassword()));

	           
	            User savedUser = userRepository.save(user);
	            UserDTO userDTO = Utils.mapUserEntityToUserDTO(savedUser);

	    
	            response.setStatusCode(200);
	            response.setUser(userDTO);
	            response.setMessage("User registered successfully");

	        } catch (OurException e) {
	            response.setStatusCode(400);
	            response.setMessage(e.getMessage());

	        } catch (Exception e) {
	            response.setStatusCode(500);
	            response.setMessage("Error saving the user: " + e.getMessage());
	        }

	        return response;
	    }

	    @Override
	    public Response login(LoginRequest loginRequest) {
	        Response response = new Response();

	        try {
	            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
	            var user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new OurException("User Not Found"));
	            var token = jwtUtils.generateToken(user);

	            response.setToken(token);
	            response.setExpirationTime("7 days");
	            response.setRole(user.getRole());
	            response.setMessage("successful");
	            response.setStatusCode(200);

	        } catch (OurException e) {
	            response.setStatusCode(404);
	            response.setMessage(e.getMessage());

	        } catch (Exception e) {
	            response.setStatusCode(500);
	            response.setMessage("Error Logging in " + e.getMessage());

	        }
	        return response;
	    }

	    @Override
	    public Response getAllUsers() {
	        Response response = new Response();

	        try {
	            List<User> userList = userRepository.findAll();
	            List<UserDTO> userDTOList = Utils.mapUserListEntityToUserListDTO(userList);

	            response.setUserList(userDTOList);
	            response.setMessage("successful");
	            response.setStatusCode(200);

	        } catch (Exception e) {
	            response.setStatusCode(500);
	            response.setMessage("Error getting all users " + e.getMessage());

	        }
	        return response;
	    }

	    @Override
	    public Response getUSerBookingHistory(String userId) {
	        Response response = new Response();

	        try {
	            User user = userRepository.findById(Long.valueOf(userId)).orElseThrow(()-> new OurException("User Not Found"));
	            UserDTO userDTO = Utils.mapUserEntityToUserDTOPlusUserBookingsAndRoom(user);

	            response.setMessage("successful");
	            response.setStatusCode(200);
	            response.setUser(userDTO);

	        } catch (OurException e) {
	            response.setStatusCode(404);
	            response.setMessage(e.getMessage());

	        } catch (Exception e) {
	            response.setStatusCode(500);
	            response.setMessage("Error getting user bookings in " + e.getMessage());

	        }
	        return response;
	    }

	    @Override
	    public Response deleteUser(String userId) {
	        Response response = new Response();

	        try {
	            userRepository.findById(Long.valueOf(userId)).orElseThrow(()-> new OurException("User Not Found"));
	            userRepository.deleteById(Long.valueOf(userId));

	            response.setMessage("successful");
	            response.setStatusCode(200);

	        } catch (OurException e) {
	            response.setStatusCode(404);
	            response.setMessage(e.getMessage());

	        } catch (Exception e) {
	            response.setStatusCode(500);
	            response.setMessage("Error deleting a user " + e.getMessage());

	        }
	        return response;
	    }

	    @Override
	    public Response getUserById(String userId) {
	        Response response = new Response();

	        try {
	            User user = userRepository.findById(Long.valueOf(userId)).orElseThrow(()-> new OurException("User Not Found"));
	            UserDTO userDTO = Utils.mapUserEntityToUserDTO(user);

	            response.setMessage("successful");
	            response.setStatusCode(200);
	            response.setUser(userDTO);

	        } catch (OurException e) {
	            response.setStatusCode(404);
	            response.setMessage(e.getMessage());

	        } catch (Exception e) {
	            response.setStatusCode(500);
	            response.setMessage("Error getting a user by id " + e.getMessage());

	        }
	        return response;
	    }

	    @Override
	    public Response getMyInfo(String email) {
	        Response response = new Response();

	        try {
	            User user = userRepository.findByEmail(email).orElseThrow(()-> new OurException("User Not Found"));
	            UserDTO userDTO = Utils.mapUserEntityToUserDTO(user);

	            response.setMessage("successful");
	            response.setStatusCode(200);
	            response.setUser(userDTO);

	        } catch (OurException e) {
	            response.setStatusCode(404);
	            response.setMessage(e.getMessage());

	        } catch (Exception e) {
	            response.setStatusCode(500);
	            response.setMessage("Error getting a user info " + e.getMessage());

	        }
	        return response;
	    }
}
