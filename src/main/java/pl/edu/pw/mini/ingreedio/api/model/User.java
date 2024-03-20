package pl.edu.pw.mini.ingreedio.api.model;

import jakarta.persistence.*;

@Entity
@Table(name="IngreedioUser")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String email;
    private String displayName;

    public User(String email,
                String displayName) {
        this.email = email;
        this.displayName = displayName;
    }

    public Integer getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public User() {

    }
}
