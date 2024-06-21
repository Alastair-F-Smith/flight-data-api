package com.example.afs.flightdataapi.model.dto;

import com.example.afs.flightdataapi.controllers.advice.NotValidatedMessages;
import com.example.afs.flightdataapi.model.entities.ContactData;
import com.example.afs.flightdataapi.model.entities.Ticket;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PersonalDetailsDto(
        @NotBlank(message = NotValidatedMessages.TEXT_FIELD_REQUIRED + "name")
        String name,
        @Email(message = NotValidatedMessages.INVALID_EMAIL)
        String email,
        @Pattern(regexp = "^\\+?[0-9]+$",
                message = NotValidatedMessages.INVALID_PHONE_NUMBER + " " + NotValidatedMessages.INVALID_PHONE_NUMBER_ADVICE)
        String phone) {

    public static PersonalDetailsDto from(Ticket ticket) {
        return new PersonalDetailsDto(ticket.getPassengerName(),
                                      ticket.getContactData().email(),
                                      ticket.getContactData().phone());
    }

    public ContactData contactDetails() {
        return new ContactData(email, phone);
    }
}
