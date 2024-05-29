package pl.edu.pw.mini.ingreedio.api.review.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import pl.edu.pw.mini.ingreedio.api.user.model.User;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column
    private Long productId;
    @Column
    private Integer rating;
    @Column
    private String content;
    @CreationTimestamp
    @Column
    private Timestamp createdAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "users_liked_reviews",
        joinColumns = @JoinColumn(name = "review_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id"))
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<User> likingUsers = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "users_disliked_reviews",
        joinColumns = @JoinColumn(name = "review_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id"))
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<User> dislikingUsers = new HashSet<>();
}
