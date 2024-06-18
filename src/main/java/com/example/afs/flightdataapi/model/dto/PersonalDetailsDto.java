package com.example.afs.flightdataapi.model.dto;

import com.example.afs.flightdataapi.model.entities.ContactData;
import com.example.afs.flightdataapi.model.entities.Ticket;

public record PersonalDetailsDto(String name, String email, String phone) {

    public static PersonalDetailsDto from(Ticket ticket) {
        return new PersonalDetailsDto(ticket.getPassengerName(),
                                      ticket.getContactData().email(),
                                      ticket.getContactData().phone());
    }

    public ContactData contactDetails() {
        return new ContactData(email, phone);
    }
}
