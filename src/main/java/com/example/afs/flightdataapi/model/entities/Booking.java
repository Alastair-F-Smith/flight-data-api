package com.example.afs.flightdataapi.model.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @NotBlank
    @Length(min = 6, max = 6)
    String bookRef;

    @NotNull
    ZonedDateTime bookDate;

    @NotNull
    BigDecimal totalAmount;

    public Booking(String bookRef, ZonedDateTime bookDate, BigDecimal totalAmount) {
        this.bookRef = bookRef;
        this.bookDate = bookDate;
        this.totalAmount = totalAmount;
    }

    public Booking() {
    }

    public @NotBlank @Length(min = 6, max = 6) String getBookRef() {
        return bookRef;
    }

    public void setBookRef(@NotBlank @Length(min = 6, max = 6) String bookRef) {
        this.bookRef = bookRef;
    }

    public @NotNull ZonedDateTime getBookDate() {
        return bookDate;
    }

    public void setBookDate(@NotNull ZonedDateTime bookDate) {
        this.bookDate = bookDate;
    }

    public @NotNull BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(@NotNull BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookRef='" + bookRef + '\'' +
                ", bookDate=" + bookDate +
                ", totalAmount=" + totalAmount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Booking booking = (Booking) o;
        return bookRef.equals(booking.bookRef);
    }

    @Override
    public int hashCode() {
        return bookRef.hashCode();
    }
}