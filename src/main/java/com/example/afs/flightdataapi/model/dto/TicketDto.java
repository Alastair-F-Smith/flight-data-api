package com.example.afs.flightdataapi.model.dto;

import com.example.afs.flightdataapi.model.entities.ContactData;
import com.example.afs.flightdataapi.model.entities.Ticket;

import java.util.List;

public record TicketDto(String ticketNo, PersonalDetailsDto details) {

    public static TicketDto from(Ticket ticket) {
        return new TicketDto(ticket.getTicketNo(), PersonalDetailsDto.from(ticket));
    }

    public static List<TicketDto> from(List<Ticket> tickets) {
        return tickets.stream()
                      .map(TicketDto::from)
                      .toList();
    }

    public String name() {
        return details.name();
    }

    public String email() {
        return details.email();
    }

    public String phone() {
        return details.phone();
    }

    public ContactData contactDetails() {
        return details.contactDetails();
    }
}
