package com.example.afs.flightdataapi.model.entities;


import java.io.Serializable;

public record TranslatedField(String en, String ru) implements Serializable {

    public TranslatedField withEn(String english) {
        return new TranslatedField(english, ru);
    }

    public TranslatedField withRu(String russian) {
        return new TranslatedField(en,russian);
    }

    public TranslatedField with(String name, SupportedLanguages lang) {
        return switch(lang) {
            case ENGLISH -> withEn(name);
            case RUSSIAN -> withRu(name);
        };
    }

    public static TranslatedField fromPartial(String field, SupportedLanguages lang) {
        return switch(lang) {
            case ENGLISH -> new TranslatedField(field, "");
            case RUSSIAN -> new TranslatedField("", field);
        };
    }
}
