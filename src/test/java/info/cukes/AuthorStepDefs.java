package info.cukes;

import org.assertj.core.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import cucumber.api.Scenario;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * <p>AuthorStepDefs cucumber glue class.</p>
 *
 * @author glick
 */
@SuppressWarnings("CdiInjectionPointsInspection")
@ContextConfiguration(locations = "classpath:cucumber.xml")
@EnableTransactionManagement
@Transactional
public class AuthorStepDefs
{
  private static final transient Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Inject
  private AuthorRepository authorRepository;

  @Inject
  private BookRepository bookRepository;

  @Inject
  AuthorDelegate authorDelegate;

  List<String> authorNames = new ArrayList<>();

  List<Author> authorList = new ArrayList<>();

  private int authorsAdded;
  private int booksAdded;

  @Before
  public void cucumberSetUp(Scenario scenario)
  {
    LOGGER.info("");
    LOGGER.info("executing cucumber scenario " + scenario.getName());
    LOGGER.info("");
  }

  /**
   * capture the author names in the authorNames list for later use
   * and create Author objects and store in authorList
   *
   * @param firstAuthorName  author name
   * @param secondAuthorName author name
   * @throws Throwable
   */
  @Given("^\"(.*?)\" and \"(.*?)\" are authors$")
  public void and_are_authors(String firstAuthorName, String secondAuthorName) throws Throwable
  {
    Author firstAuthor = new Author(firstAuthorName);
    Author secondAuthor = new Author(secondAuthorName);

    authorList.add(firstAuthor);
    authorList.add(secondAuthor);

    authorsAdded = authorList.size();

    authorNames.add(firstAuthorName);
    authorNames.add(secondAuthorName);
  }

  /**
   * create the book
   * fetch the authors from the persistent store
   * add the book to each author
   * add each author to the book
   * store the book persistently
   * spring/eclipselink dirty checking will cause the authors to be updated
   *
   * @param bookTitle the book title
   * @throws Throwable
   */
  @When("^they write a book entitled \"(.*?)\"$")
  public void they_write_a_book_entitled(String bookTitle) throws Throwable
  {

    Book localBook = new Book(bookTitle);

    List<Author> transformedAuthorList = authorDelegate.assignBookToAuthors(authorList, localBook);

    authorRepository.save(transformedAuthorList);

    booksAdded = 1;
  }

  @Then("^their names should be associated with that title in the persistent store$")
  public void their_names_should_be_associated_with_that_title_in_the_persistent_store() throws Throwable
  {
    List<Author> persistentAuthors = authorRepository.findAll();

    Assertions.assertThat(persistentAuthors).hasSize(authorsAdded);

    List<Book> books = bookRepository.findAll();

    Assertions.assertThat(books).hasSize(booksAdded);

    for (Author author : persistentAuthors)
    {
      Assertions.assertThat(authorNames).contains(author.getAuthorName());

      Assertions.assertThat(books).containsAll(author.getAuthoredBooks());

      for (Book book : books)
      {
        Assertions.assertThat(book.getBookAuthors()).contains(author);
      }
    }
  }
}
