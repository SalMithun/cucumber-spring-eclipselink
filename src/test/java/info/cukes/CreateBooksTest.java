package info.cukes;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.assertj.core.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import java.lang.invoke.MethodHandles;

import cucumber.api.java.After;
import cucumber.api.java.Before;

/**
 * @author glick
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:cucumber.xml"})
public class CreateBooksTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Autowired
  BookRepository bookRepository;

  @Before
  public void setUp()
  {
    bookRepository.deleteAll();
  }

  @After
  public void tearDown()
  {
    bookRepository.deleteAll();
  }

  @Test
  public void testSpringEclipselink()
  {
    List<String> bookTitles = new ArrayList<>();

    Assertions.assertThat(bookTitles).hasSize(0);

    Book book = new Book();

    String bookTitle = "The Surefire Book";

    book.setTitle(bookTitle);

    bookRepository.save(book);

    bookTitles.add(bookTitle);

    book = new Book();

    bookTitle = "Surefire Recipes";

    book.setTitle(bookTitle);

    bookRepository.save(book);

    bookTitles.add(bookTitle);

    List<Book> books = bookRepository.findAll();

    List<String> allBookTitles = Book.getListOfTitles(books);

    LOGGER.info("all book titles are " + allBookTitles);
    LOGGER.info("book titles written are " + bookTitles);

    Assertions.assertThat(allBookTitles).containsAll(bookTitles);
  }
}
