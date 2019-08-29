package com.company.library.service;

import com.company.library.model.Book;
import com.company.library.model.ResponsePageList;

import java.util.List;

public interface BookServiceInterface {

    void addBook(Book b);
    List<Book> getBooks();
    void remove(Long bookId);
    ResponsePageList<Book> findPaginatedBooks(String orderBy, String direction, int page, int size, String query);
    //Book findBookByTitleAndAuthor(String title, String author);
    Book findBookByIsbn(String isbn);
}
