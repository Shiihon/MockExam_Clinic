package app.entities;

import app.enums.Speciality;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "doctors")
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "birthdate")
    private LocalDate birthDate;

    @Column(name = "year_of_graduation")
    private Year yearOfGraduation;

    @Column(name = "clinic_name")
    private String clinicName;

    @Enumerated(EnumType.STRING)
    private Speciality speciality;

    @OneToMany(mappedBy = "doctor", fetch = FetchType.LAZY)
    private List<Appointment> appointments;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public Doctor(Long id, String name, LocalDate birthDate, Year yearOfGraduation, String clinicName, Speciality speciality, List<Appointment> appointments) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.yearOfGraduation = yearOfGraduation;
        this.clinicName = clinicName;
        this.speciality = speciality;
        if(appointments != null) {
            this.appointments = new ArrayList<>();
            for(Appointment appointment : appointments) {
                this.appointments.add(appointment);
            }
        } else {
            this.appointments = new ArrayList<>();
        }
    }

    public void addAppointment(Appointment appointment) {
        appointments.add(appointment);
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}


