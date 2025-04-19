// File: HibernateCRUD.java

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import javax.persistence.*;

@Entity
@Table(name = "students")
class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private int age;

    // Constructors, Getters and Setters
    public Student() {
    }

    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String toString() {
        return "Student{id=" + id + ", name='" + name + "', age=" + age + "}";
    }
}

public class HibernateCRUD {
    public static void main(String[] args) {
        Configuration cfg = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(Student.class);
        SessionFactory factory = cfg.buildSessionFactory();
        Session session = factory.openSession();
        Transaction tx = session.beginTransaction();

        // Create
        Student s1 = new Student("Alice", 22);
        session.save(s1);

        // Read
        Student retrieved = session.get(Student.class, s1.id);
        System.out.println("Retrieved: " + retrieved);

        // Update
        retrieved.age = 23;
        session.update(retrieved);

        // Delete
        // session.delete(retrieved);

        tx.commit();
        session.close();
        factory.close();
    }
}
