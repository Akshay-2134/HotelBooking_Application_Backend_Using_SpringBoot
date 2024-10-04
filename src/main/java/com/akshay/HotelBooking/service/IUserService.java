package com.akshay.HotelBooking.service;

import com.akshay.HotelBooking.dto.LoginRequest;
import com.akshay.HotelBooking.dto.Response;
import com.akshay.HotelBooking.entity.User;

public interface IUserService {

    Response register(User user);
    Response login(LoginRequest loginRequest);
    Response getAllUsers();
    Response getUSerBookingHistory(String userId);
    Response deleteUser(String userId);
    Response getUserById(String userId);
    Response getMyInfo(String email);
}
