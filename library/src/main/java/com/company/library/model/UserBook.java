package com.company.library.model;


import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class UserBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

     @ManyToOne(fetch = FetchType.EAGER)
    private Book book;

    private LocalDate return_date;

    private boolean generatedPenalty;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public LocalDate getReturn_date() {
        return return_date;
    }

    public void setReturn_date(LocalDate return_date) {
        this.return_date = return_date;
    }

    public boolean isGeneratedPenalty() {
        return generatedPenalty;
    }

    public void setGeneratedPenalty(boolean generatedPenalty) {
        this.generatedPenalty = generatedPenalty;
    }
}
