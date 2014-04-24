package info.cukes;

import org.apache.commons.lang3.StringUtils;

import org.fest.assertions.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import java.lang.invoke.MethodHandles;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

@ContextConfiguration(locations = "/cucumber.xml")
public class BookStepdefs
{
  @SuppressWarnings("UnusedDeclaration")
  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  List<Book> books = null;

  List<Author> authors = new ArrayList<>();

  List<String> bookTitles = null;

  List<String> authorNames;

  int booksAdded = 0;

  int authorsAdded = 0;

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private AuthorRepository authorRepository;

  @Before
  public void beforeStepDefs()
  {
    bookRepository.deleteAll();
    authorRepository.deleteAll();
  }

  @Transactional
  @Given("^\"(.*?)\" has contributed to the following titles:$")
  public void has_contributed_to_the_following_titles(String author, List<Book> books) throws Throwable
  {
    Assertions.assertThat(books.size()).isEqualTo(2);

    Assertions.assertThat((StringUtils.isNotBlank(author)));

    booksAdded = books.size();

    bookTitles = Book.getListOfTitles(books);

    authorsAdded = 1;

    Author anAuthor = new Author(author);

    authorRepository.save(anAuthor);

    List<Author> authorList = new ArrayList<>();

    authorList.add(anAuthor);

    authorNames = Author.getListOfAuthorNames(authorList);

    for (Book book : books)
    {
      book.addAnAuthor(anAuthor);
      bookRepository.save(book);
    }
  }

  @When("^someone fetches the books$")
  public void someone_fetches_the_books() throws Throwable
  {
    books = bookRepository.findAll();

    Assertions.assertThat(books.size()).isEqualTo(booksAdded);

    authors = authorRepository.findAll();

    Assertions.assertThat(authors.size()).isEqualTo(authorsAdded);
  }

  @Then("^(\\d+) titles named as above have been stored persistently$")
  public void titles_named_as_above_have_been_stored_persistently(int booksStored) throws Throwable
  {
    Assertions.assertThat(books.size()).isEqualTo(booksStored);

    List<String> localBookTitles = Book.getListOfTitles(books);

    Assertions.assertThat(localBookTitles.size()).isEqualTo(bookTitles.size());

    Assertions.assertThat(localBookTitles.containsAll(bookTitles));
  }

  @Then("^have \"(.*?)\" as an author$")
  public void have_as_an_author(String author) throws Throwable
  {
    for (Book aBook : books)
    {
      Assertions.assertThat(Author.getListOfAuthorNames(aBook.getBookAuthors()).contains(author));
    }
  }
}
