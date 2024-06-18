package com.example.afs.flightdataapi.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigInteger;
import java.util.*;

@Entity
@Table(name = "tickets")
public class Ticket {

    private static final BigInteger MAX_TICKET_NO = BigInteger.valueOf(9_999_999_999_999L);

    @Id
    @NotBlank
    String ticketNo;

    @ManyToOne
    @JoinColumn(name = "bookRef", nullable = false)
    Booking bookRef;

    @NotBlank
    String passengerId;

    @NotBlank
    String passengerName;

    @NotNull
    @JdbcTypeCode(SqlTypes.JSON)
    ContactData contactData;

    @OneToMany(mappedBy = "ticketFlightsId.ticketNo", cascade = CascadeType.ALL, orphanRemoval = true)
    List<TicketFlights> ticketFlights;

    public Ticket(String ticketNo, Booking bookRef, String passengerId, String passengerName, ContactData contactData) {
        this.ticketNo = ticketNo;
        this.bookRef = bookRef;
        this.passengerId = passengerId;
        this.passengerName = passengerName;
        this.contactData = contactData;
        ticketFlights = new ArrayList<>();
    }

    public Ticket(Booking bookRef, String passengerName, ContactData contactData) {
        this.bookRef = bookRef;
        this.passengerName = passengerName;
        this.contactData = contactData;
        ticketNo = null;
        passengerId = generatePassengerId();
        ticketFlights = new ArrayList<>();
    }

    public Ticket() {
    }

    public static String generateTicketNo() {
        BigInteger num;
        do {
            num = new BigInteger(MAX_TICKET_NO.bitLength(), new Random());
        } while (num.compareTo(MAX_TICKET_NO) > 0);
        return StringUtils.leftPad(num.toString(), 13, '0');
    }

    public static String generatePassengerId() {
        Random random = new Random();
        int firstPart = random.nextInt(10_000);
        int secondPart = random.nextInt(1_000_000);
        String fourDigits = StringUtils.leftPad(String.valueOf(firstPart), 4, '0');
        String sixDigits = StringUtils.leftPad(String.valueOf(secondPart), 6, '0');
        return fourDigits + " " + sixDigits;
    }

    public String getTicketNo() {
        return ticketNo;
    }

    public void setTicketNo(String ticketNo) {
        this.ticketNo = ticketNo;
    }

    public void setTicketNo(long ticketNo) {
        this.ticketNo = StringUtils.leftPad(String.valueOf(ticketNo), 13, '0');
    }

    public Booking getBookRef() {
        return bookRef;
    }

    public void setBookRef(Booking bookRef) {
        this.bookRef = bookRef;
    }

    public @NotBlank String getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(@NotBlank String passengerId) {
        this.passengerId = passengerId;
    }

    public @NotBlank String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(@NotBlank String passengerName) {
        this.passengerName = passengerName;
    }

    public @NotNull ContactData getContactData() {
        return contactData;
    }

    public void setContactData(@NotNull ContactData contactData) {
        this.contactData = contactData;
    }

    public List<TicketFlights> getTicketFlights() {
        return new ArrayList<>(this.ticketFlights);
    }

    public void setTicketFlights(List<TicketFlights> ticketFlights) {
        this.ticketFlights = ticketFlights;
    }

    public void addTicketFlight(TicketFlights ticketFlight) {
        this.ticketFlights.add(ticketFlight);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ticket ticket = (Ticket) o;
        return ticketNo.equals(ticket.ticketNo);
    }

    @Override
    public int hashCode() {
        return ticketNo.hashCode();
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "ticketNo='" + ticketNo + '\'' +
                ", bookRef=" + bookRef +
                ", passengerId='" + passengerId + '\'' +
                ", passengerName='" + passengerName + '\'' +
                ", contactData=" + contactData +
                ", ticketFlights=" + ticketFlights +
                '}';
    }
}
