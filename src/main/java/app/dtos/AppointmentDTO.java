package app.dtos;

import app.entities.Appointment;
import app.security.entities.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import app.entities.Doctor;

import java.time.LocalDate;
import java.time.LocalTime;

@NoArgsConstructor
@Data
@JsonIgnoreProperties({"user"})
public class AppointmentDTO {
    private Long id;
    @JsonProperty("client_name")
    private String clientName; //client full name
    @JsonProperty("doctor_id")
    private Long doctorId;
    private String userName; //client username
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime time;
    private String comment;
    private User user;

    public AppointmentDTO(Appointment appointment) {
        this.id = appointment.getId();
        this.doctorId = appointment.getDoctor().getId();
        this.userName = appointment.getUser() != null ? appointment.getUser().getUsername() : null;
        this.clientName = appointment.getClientName();
        if (this.clientName == null && appointment.getUser() != null) {
            setClientNameFromUser(appointment.getUser()); // Set client name from user if it's null
        }
        this.date = appointment.getDate();
        this.time = appointment.getTime();
        this.comment = appointment.getComment();
    }

    public void setClientNameFromUser(User user) {
        if (user != null) {
            this.clientName = user.getFirstname() + " " + user.getLastname();
        }
    }

    //constructor for test
    public AppointmentDTO(Long id, String clientName, Long doctorId, String userName,
                          LocalDate date, LocalTime time, String comment, User user) {
        this.id = id;
        this.clientName = clientName;
        this.doctorId = doctorId;
        this.userName = userName;
        this.date = date;
        this.time = time;
        this.comment = comment;
        this.user = user;
    }

    @JsonIgnore
    public Appointment getAsEntity() {
        Doctor doctor = Doctor.builder()
                .id(this.doctorId)
                .build();

        return Appointment.builder()
                .id(id)
                .doctor(doctor)
                .clientName(clientName)
                .date(date)
                .time(time)
                .comment(comment)
                .build();
    }
}
