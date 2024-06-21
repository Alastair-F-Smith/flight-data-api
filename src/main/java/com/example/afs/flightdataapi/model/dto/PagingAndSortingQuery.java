package com.example.afs.flightdataapi.model.dto;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public record PagingAndSortingQuery(Integer pageNumber, Integer pageSize, String sortField, String sortDirection) {

    public PagingAndSortingQuery {
        if (pageNumber == null || pageNumber < 0) {
            pageNumber = 0;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 20;
        }
        if (sortField == null) {
            sortField = "scheduledDeparture";
        }
        if (sortDirection == null) {
            sortDirection = "asc";
        }
    }

    public PageRequest pageRequest() {
        Sort sort = Sort.by(sortField);
        sort = sortDirection.equals("asc") ? sort.ascending() : sort.descending();
        return PageRequest.of(pageNumber, pageSize, sort);
    }
}
