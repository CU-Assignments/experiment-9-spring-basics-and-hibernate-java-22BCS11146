// File: BankApp.java

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.*;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

// --- Entities ---
@Entity
@Table(name = "accounts")
class Account {
    @Id
    private int accountId;
    private String holderName;
    private double balance;

    public Account() {
    }

    public Account(int id, String name, double bal) {
        this.accountId = id;
        this.holderName = name;
        this.balance = bal;
    }

    public void deposit(double amount) {
        this.balance += amount;
    }

    public void withdraw(double amount) {
        this.balance -= amount;
    }

    public double getBalance() {
        return balance;
    }

    public int getAccountId() {
        return accountId;
    }
}

@Entity
@Table(name = "transactions")
class TransactionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String description;

    public TransactionLog() {
    }

    public TransactionLog(String desc) {
        this.description = desc;
    }
}

// --- Service ---
interface BankService {
    void transferMoney(int fromId, int toId, double amount);
}

class BankServiceImpl implements BankService {
    @Autowired
    SessionFactory sessionFactory;

    @Override
    @Transactional
    public void transferMoney(int fromId, int toId, double amount) {
        var session = sessionFactory.getCurrentSession();
        Account from = session.get(Account.class, fromId);
        Account to = session.get(Account.class, toId);

        if (from.getBalance() < amount) {
            throw new RuntimeException("Insufficient Balance!");
        }

        from.withdraw(amount);
        to.deposit(amount);

        session.update(from);
        session.update(to);
        session.save(new TransactionLog("Transferred ₹" + amount + " from " + fromId + " to " + toId));
    }
}

// --- Spring Configuration ---
@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = "")
class SpringHibernateConfig {

    @Bean
    public SessionFactory sessionFactory() {
        return new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Account.class)
                .addAnnotatedClass(TransactionLog.class)
                .buildSessionFactory();
    }

    @Bean
    public HibernateTransactionManager txManager() {
        return new HibernateTransactionManager(sessionFactory());
    }

    @Bean
    public BankService bankService() {
        return new BankServiceImpl();
    }
}

// --- Main App ---
public class BankApp {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
                SpringHibernateConfig.class);
        BankService service = context.getBean(BankService.class);

        try {
            service.transferMoney(101, 102, 2000);
            System.out.println("Transaction Successful");
        } catch (Exception e) {
            System.out.println("Transaction Failed: " + e.getMessage());
        }
        context.close();
    }
}
