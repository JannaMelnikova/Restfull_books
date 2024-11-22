package org.example.restfull_books.service;

import org.example.restfull_books.entity.Book;
import org.example.restfull_books.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    @Autowired //внедрение зависимости
    private BookRepository bookRepository;

    public Book save(Book book) {
        return bookRepository.save(book);
    }

    public Book getByBookId(long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
    }

    public Book deleteBookById(long id) {
        Book book = getByBookId(id);
        bookRepository.delete(book);
        return book;
    }

}
