package com.soriaajose.control.asistencia.qr.backend.employee.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;


import java.util.List;

@Getter
@Setter
public class PageDTO<T> {


    private List<T> content;

    private int pageNumber;
    private int pageSize;

    private long totalElements;
    private int totalPages;

    private int numberOfElements;

    private boolean first;
    private boolean last;

    private boolean hasNext;
    private boolean hasPrevious;

    public PageDTO(Page<T> page) {
        this.content = page.getContent();
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.numberOfElements = page.getNumberOfElements();
        this.first = page.isFirst();
        this.last = page.isLast();
        this.hasNext = page.hasNext();
        this.hasPrevious = page.hasPrevious();
    }

}
