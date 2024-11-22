package org.example.restfull_books.controller;

import org.example.restfull_books.entity.Book;
import org.example.restfull_books.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;


    @GetMapping("/{id}")
    public ResponseEntity<Book> getBook(@PathVariable long id) {
        return ResponseEntity.ok(bookService.getByBookId(id));
    }
@DeleteMapping("/{id}")
@ResponseStatus(HttpStatus.NO_CONTENT)
public void deleteBook(@PathVariable long id) {
        bookService.deleteBookById(id); //204 No Content
}

    @PostMapping("/new")
  public ResponseEntity<Book> newBook(@RequestBody Book book) {
    Book bookSaved = bookService.save(book);
    return ResponseEntity.status(HttpStatus.OK).body(bookSaved);
    }

}
