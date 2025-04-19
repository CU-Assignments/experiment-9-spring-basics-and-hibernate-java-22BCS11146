// File: Main.java

import org.springframework.context.annotation.*;

// Course class
class Course {
    private String courseName;
    private String duration;

    public Course(String courseName, String duration) {
        this.courseName = courseName;
        this.duration = duration;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getDuration() {
        return duration;
    }
}

// Student class
class Student {
    private String name;
    private Course course;

    public Student(String name, Course course) {
        this.name = name;
        this.course = course;
    }

    public void showDetails() {
        System.out.println("Student Name: " + name);
        System.out.println("Course Name: " + course.getCourseName());
        System.out.println("Duration: " + course.getDuration());
    }
}

// Spring Configuration
@Configuration
class AppConfig {
    @Bean
    public Course course() {
        return new Course("Spring Boot", "3 Months");
    }

    @Bean
    public Student student() {
        return new Student("John Doe", course());
    }
}

// Main class
public class Main {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        Student student = context.getBean(Student.class);
        student.showDetails();
        context.close();
    }
}
