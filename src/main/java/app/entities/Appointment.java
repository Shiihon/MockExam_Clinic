package app.entities;

import app.security.entities.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "appointments")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_name")
    private String clientName;

    private LocalDate date;

    private LocalTime time;

    private String comment;

    @ManyToOne
    @JoinColumn(name = "user_username")
    private User user;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

