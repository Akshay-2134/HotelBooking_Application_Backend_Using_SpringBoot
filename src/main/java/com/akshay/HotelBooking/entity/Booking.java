package com.akshay.HotelBooking.entity;

import java.math.BigDecimal;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
@Table(name = "bookings")
public class Booking {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull(message = "Check in date is required")
	private LocalDateTime checkInDate;

	@Future(message = "Check out date must be in the future")
	private LocalDateTime checkOutDate;

	@Min(value = 1, message = "Number of adults should not be less than one")
	private int numofAdults;

	@Min(value = 0, message = "Number of children should not be less than zero")
	private int numofChildren;

	private int totalNumOfGuest;

	private String bookingConfirmationCode;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "room_id")
	private Room room;

	public void calculateTotalNumberOfGuests() {
		this.totalNumOfGuest = this.numofAdults - this.numofChildren;
	}

	public void setNumOfAdults(int numOfAdults) {
		this.numofAdults = numOfAdults;
		calculateTotalNumberOfGuests();
	}

	public void setNumOfChildren(int numOfChildren) {
		this.numofChildren = numOfChildren;
		calculateTotalNumberOfGuests();
	}
	
	 @Override
	    public String toString() {
	        return "Booking{" +
	                "id=" + id +
	                ", checkInDate=" + checkInDate +
	                ", checkOutDate=" + checkOutDate +
	                ", numOfAdults=" + numofAdults +
	                ", numOfChildren=" + numofChildren +
	                ", totalNumOfGuest=" + totalNumOfGuest +
	                ", bookingConfirmationCode='" + bookingConfirmationCode + '\'' +
	                '}';
	    }
}
