[![Build Status](https://travis-ci.org/andyglick/cucumber-spring-eclipselink.png)](https://travis-ci.org/andyglick/cucumber-spring-eclipselink)
Cucumber, Spring and Eclipselink with Spring Data JPA
====================================================

this simple example project contains 2 Cucumber features, each one of which has 1 scenario. Cucumber is being used 
to drive Spring and Eclipselink which together write persistent data to an HSQL in memory database instance.

There are 2 JPA entity classes Book and Author. There is a bidirectional many to many relationship between them
as a Book will have one or more Authors, and an Author may contribute to zero or more Books.

There is a relish project site at **https://www.relishapp.com/zrgs-org/cucumber-spring-and-eclipselink/docs**

Found the beginnings of this example in the book Cucumber Recipes, you can find the book example at page 92 in 
chapter 2 Java. The original is Recipe 18 Drive a Spring + Hibernate Project.

I changed out Hibernate for Eclipselink firstly because I prefer Eclipselink and secondly because the project that I 
am doing at work uses Eclipselink. 

There are 2 libraries in use here that aren't being used by on my project at work, they are the AssertJ assertions 
library and the QueryDSL JPA querying library.

AssertJ allows for even more expressive fluent assertions than the fest assertions library,
which I tried out but abandoned once I realized how much more powerful AssertJ is.
(http://joel-costigliola.github.io/assertj/)

As example difference is the following:

using fest:

    Assertions.assertThat(authorNameList.size()).isEqualTo(authorNamesToFind.size());

using AssertJ:

    Assertions.assertThat(authorNameList).hasSameSizeAs(authorNamesToFind);


**QueryDSL** provides a good deal more flexibility in querying persistent data than does JQL, allowing us to derive 
even more benefits from the use of Spring Data JPA. **(http://www.querydsl.com/)**

An example query follows:

    QAuthor author = QAuthor.author1;

    BooleanExpression authorsToLookup = author.authorName.eq("Andy Glick")
      .or(author.authorName.eq("Jim Laspada")).or(author.authorName.eq("Jeffrey Braxton"));

    Iterable<Author> locatedAuthors = authorRepository.findAll(authorsToLookup);

The query returns only the entries specified in the BooleanExpression

An odd finding: attempted to configure and use AuthorDelegate and BookDelegate classes via Spring dependency injection.
This worked successfully everywhere except in the Author and Book classes. It turns out that the JPA Entity classes 
are not Spring beans managed by the Spring DI container, they are instead managed by the JPA container itself. I had 
hoped that I could use the AspectJ weaving technique to do the injection, from what I read in the documentation there
was a suggestion that AspectJ weaving could do injection into instances not managed by the Spring container but I 
couldn't get that to work, and I don't know why, so that is worth further investigation.

In addition I have attempted to configure CDI using a number of different CDI environments in particular the 
DeltaSpike environment. I couldn't actually get a fully working environment configured using DeltaSpike. What was 
interesting about running in the DeltaSpike environment is that the Spring Data JPA repository proxies seemed to be 
recognized and may actually work, so there appears to be some non-trivial interoperability between Spring Data JPA 
and CDI JPA and or Eclipselink JPA.

What seems to be the underlying issue is the interoperability between the JPA container, and one or more of the DI 
containers. I am not actually sure if the JPA managed Entities can be treated as beans managed by any of the DI 
containers, that may be the problem. That said, I would have expected the AspectJ weaving to do the injection. I'll 
have to do a better job of sorting out how AspectJ weaving is supposed to work for non-Spring managed beans and what 
can be expected from it. 

**Verbose Logging -- Another thing that surprised me as I implemented this small example**

I used slf4j (simple logging for java) to replace any logging to commons-logging, 
log4j or to the java 4 internal logging package.

I enabled test output logging to standard out (the console) by setting the surefire plugin's useFile property to 
false (see the properties block in the pom file. What surprised me was that If I configured logback classic in the 
project then it produced copious debugging output, and if I unconfigured it the test code produced no debugging 
output, logging only informational log statements.
  
I took advantage of this by creating a verbose-logging profile, which when enabled produces the debugging output. Try
running the test cases with and without enabling the verbose-logging profile. I think that you will be interested to 
see the difference. 


