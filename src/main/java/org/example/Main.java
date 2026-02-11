/*Vježba 19:
Kreirati novi Java projekt koji će sadržavati tri perzistentne klase:
- „Author” s varijablama „id” (Long), „name” (String), set objekata klase „Book” označenog s „@OneToMany” anotacijom i
        set objekata klase „Publisher” označenih s „@ManyToMany” anotacijom
- „Book” s varijablama „id” (Long), „title” (String) i „author” tipa „Author” označenog s „@ManyToOne” anotacijom
- „Publisher” s varijablama „id” (Long), „name” (String) i set objekata klase „Book”

- Kreirati odgovarajuću konfiguraciju u datoteci „hibernate.cfg.xml”.
- Iskoristiti klasu „HibernateUtil” iz prošlih vježbi.
- Kreirati klasu s „main” metodom u kojoj će se pozivati metode za kreiranje po dva entiteta svih kreiranih klasa i spremiti ih. ok
- Napisati metodu koja će dohvaćati sve autore s njihovim knjigama te ih ispisati u konzolu. ok
- Napisati metodu za ažuriranje naslova knjiga. ok
- Napisati metodu za brisanje knjige. ok*/

package org.example;

import org.example.model.Author;
import org.example.model.Book;
import org.example.model.Publisher;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            kreiranjeEntiteta();
            dohvatAutora();
            azuriranjeNaslovaKnjige(1L, "Drvodjelstvo 3: Pravac ga nije volio");
            brisanjeKnjige(2L);
        } finally {
            HibernateUtil.shutdown();
        }

    }


    public static void kreiranjeEntiteta() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            Author author1 = new Author("Ivan Ivić");
            Author author2 = new Author("Ana Anić");

            Book book1 = new Book("Drvodjelstvo 2: Povratak oštrice", author1);
            Book book2 = new Book("Vikend počinje u četvrtak", author2);

            Publisher publisher1 = new Publisher("Školska knjiga");
            Publisher publisher2 = new Publisher("Profil");


            author1.getPublishers().add(publisher1);
            author1.getPublishers().add(publisher2);
            author2.getPublishers().add(publisher1);


            session.persist(author1);
            session.persist(author2);
            session.persist(book1);
            session.persist(book2);
            session.persist(publisher1);
            session.persist(publisher2);

            tx.commit();

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static void dohvatAutora() {
        Session session = HibernateUtil.getSessionFactory().openSession();

        List<Author> authors = session.createQuery("FROM Author", Author.class).list();

        for (Author author : authors) {
            System.out.println("\nAutor: " + author.getName());

            System.out.println("\nKnjige:");

            for (Book book : author.getBooks()) {
                System.out.println("Naziv: " + book.getTitle());
            }
        }

        session.close();
    }


    public static void azuriranjeNaslovaKnjige(Long bookId, String newTitle) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        Book book = session.get(Book.class, bookId);
        if (book != null) {
            book.setTitle(newTitle);
            session.merge(book);
            System.out.println("Knjiga ažurirana: " + newTitle);
        }

        tx.commit();
        session.close();
    }


    public static void brisanjeKnjige(Long bookId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        Book book = session.get(Book.class, bookId);
        if (book != null) {
            session.remove(book);
            System.out.println("Knjiga obrisana: " + bookId);
        }

        tx.commit();
        session.close();
    }
}

