package com.luotuo.user.entity;

import org.springframework.data.domain.Page;

/**
 * Created by luotuo on 17-7-24.
 */
public class PageResponse {
    private Object content;
    private Boolean last;
    private Integer totalPages;
    private Long totalElements;
    private Boolean first;
    private Object sort;
    private Integer numberOfElements;
    private Integer size;
    private Integer number;

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public Boolean getLast() {
        return last;
    }

    public void setLast(Boolean last) {
        this.last = last;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Long totalElements) {
        this.totalElements = totalElements;
    }

    public Boolean getFirst() {
        return first;
    }

    public void setFirst(Boolean first) {
        this.first = first;
    }

    public Object getSort() {
        return sort;
    }

    public void setSort(Object sort) {
        this.sort = sort;
    }

    public Integer getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(Integer numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public void set(Page pages) {
        this.setFirst(pages.isFirst());
        this.setLast(pages.isLast());
        this.setNumber(pages.getNumber());
        this.setNumberOfElements(pages.getNumberOfElements());
        this.setSize(pages.getSize());
        this.setSort(pages.getSort());
        this.setTotalElements(pages.getTotalElements());
        this.setTotalPages(pages.getTotalPages());
    }
}
