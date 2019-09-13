package com.company.library.service;


import com.company.library.enums.Direction;
import com.company.library.enums.OrderBy;
import com.company.library.exceptions.BookOutOfStock;
import com.company.library.exceptions.PaginationSortingException;
import com.company.library.exceptions.UserHasPenaltiesException;
import com.company.library.model.Penalty;
import com.company.library.model.ResponsePageList;
import com.company.library.model.User;
import com.company.library.model.UserBook;
import com.company.library.repository.UserBookRepositoryInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Sort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class UserBookService implements UserBookServiceInterface {



    @Autowired
    private UserBookRepositoryInterface userBookRepositoryInterface;

    @Autowired
    private BookServiceInterface bookService;

    @Autowired
    private UserServiceInterface userService;

    @Autowired
    EmailService emailService;



    @Override
    public void addUserBook(UserBook userBook) throws UserHasPenaltiesException, BookOutOfStock {

        //check if we dont have any more books of this type
        //we use bookService because we want to check the stack value from database, not from the request we sended
        if (bookService.findBookById(userBook.getBook().getId()).getStock() <= 0)
            throw new BookOutOfStock();


        if (userBook.getUser().getPenalties().size() < Penalty.maxNumberOfPenalties) {

            //when we loan a book the stock should decrease with one unit
            //we use bookService so the value will change in database
            bookService.findBookById(userBook.getBook().getId()).decreseStock();
            userBookRepositoryInterface.save(userBook);
        } else
            //throws exception and doesn`t save userBook instance if user has 2 penalties
            throw new UserHasPenaltiesException();
    }

    @Override
    public List<UserBook> getUserBooks() {
        return userBookRepositoryInterface.findAll();
    }

    @Scheduled(cron = "0 * * * * *")
    public void sendReminder() {
        List<UserBook> list = userBookRepositoryInterface.remindUsers();
            list.forEach(t->emailService.sendAlmostReturnDateEmail(t));
    }



    @Override
    public ResponsePageList<UserBook> getBorrowedBooks(String orderBy, String direction, int page, int size, String id) {
        Sort sort = null;
        if (direction.equals("ASC")) {
            sort = new Sort(Sort.Direction.ASC, orderBy);
        }
        if (direction.equals("DESC")) {
            sort = new Sort(Sort.Direction.DESC, orderBy);
        }

        if (!(direction.equals(Direction.ASCENDING.getDirectionCode()) || direction.equals(Direction.DESCENDING.getDirectionCode()))) {
            throw new PaginationSortingException("Invalid sort direction");
        }
        if (!(orderBy.equals(OrderBy.ID.getOrderByCode()) || orderBy.equals(OrderBy.TITLE.getOrderByCode()))) {
            throw new PaginationSortingException("Invalid orderBy condition");
        }

        Predicate<UserBook> byId = userBook -> userBook.getUser().getId().toString().equals(id);
        List<UserBook> list = userBookRepositoryInterface.findAll(sort).stream().filter(byId).collect(Collectors.toList());

        PagedListHolder<UserBook> pagedListHolder = new PagedListHolder<>(list);
        pagedListHolder.setPageSize(size);
        pagedListHolder.setPage(page);
        ResponsePageList<UserBook> response = new ResponsePageList<>();
        response.setNrOfElements(pagedListHolder.getNrOfElements());
        response.setPageList(pagedListHolder.getPageList());
        return response;

    }


    @Override
    public void returnBorrowBook(Long borrowId) {

        //when the book is returned, the stock should increase with one unit
        userBookRepositoryInterface.findById(borrowId).ifPresent(t -> t.getBook().setStock(t.getBook().getStock() + 1));
        userBookRepositoryInterface.deleteById(borrowId);
    }

    public void changeUserBookPenalty(Long userBookId) {
        userBookRepositoryInterface.findById(userBookId).ifPresent(t -> {
            t.setGeneratedPenalty(true);
            userBookRepositoryInterface.saveAndFlush(t);
        });
    }

    @Override
    public void removeById(Long userBookId) {
        userBookRepositoryInterface.deleteById(userBookId);
    }
}

