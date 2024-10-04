package com.akshay.HotelBooking.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.akshay.HotelBooking.repo.UserRepository;

@Service
public class CustomerUserDetailService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	
		 return userRepository.findByEmail(username).orElseThrow(()-> new UsernameNotFoundException("User Name Not Found"));
	}
       
	
	/*
	 * List<GrantedAuthority> authorities = userRepository.getRoles().stream()
	 * .map(role -> new SimpleGrantedAuthority(role.getName()))
	 * .collect(Collectors.toList());
	 * 
	 * return new org.springframework.security.core.userdetails.User(
	 * user.getUsername(), user.getPassword(), authorities);
	 */
	
}
