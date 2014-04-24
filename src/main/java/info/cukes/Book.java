package info.cukes;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Table(name = "book")
public class Book
{
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name= "book")
  private Long book;

  @Column(name = "title", nullable = false)
  private String title;

  @ManyToMany
  @JoinTable(name = "book_authors",
    joinColumns = @JoinColumn(name = "book"),
    inverseJoinColumns = @JoinColumn(name = "author"))
  private List<Author> bookAuthors = new ArrayList<>();

  public Book() {}

  public Book(String bookTitle)
  {
    setTitle(bookTitle);
  }

  @SuppressWarnings("UnusedDeclaration")
  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public List<Author> getBookAuthors()
  {
    List<Author> immutableAuthors = ImmutableList.copyOf(bookAuthors);

    return immutableAuthors;
  }

  public void addAnAuthor(Author author)
  {
    bookAuthors.add(author);
  }

  @SuppressWarnings("UnusedDeclaration")
  public Long getBook()
  {
    return book;
  }

  private static Function<Book, String> extractTitles = new Function<Book, String>()
  {
    @Override
    public String apply(Book book)
    {
      return book.getTitle();
    }
  };

  public static List<String> getListOfTitles(List<Book> bookList)
  {
    List<String> titlesOfBooks = Lists.transform(bookList, extractTitles);

    return titlesOfBooks;
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (!(o instanceof Book))
    {
      return false;
    }

    Book book = (Book) o;

    return !(bookAuthors != null ? !bookAuthors.equals(book.bookAuthors) : book.bookAuthors != null)
      && title.equals(book.title);
  }

  @Override
  public int hashCode()
  {
    int result = title.hashCode();
    result = 31 * result + (bookAuthors != null ? bookAuthors.hashCode() : 0);
    return result;
  }

  @Override
  public String toString()
  {
    return "Book{" +
      "book=" + book +
      ", title='" + title + '\'' +
      ", bookAuthors=" + bookAuthors +
      '}';
  }
}
