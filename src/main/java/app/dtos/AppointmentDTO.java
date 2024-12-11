package app.dtos;

import app.entities.Appointment;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import app.entities.Doctor;

import java.time.LocalDate;
import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AppointmentDTO {
    private Long id;
    @JsonProperty(namespace = "client_name")
    private String clientName;
    @JsonProperty(namespace = "doctor_id")
    private Long doctorId;
    private String userName;
    private LocalDate date;
    private LocalTime time;
    private String comment;

    public AppointmentDTO(Appointment appointment) {
        this.id = appointment.getId();
        this.doctorId = appointment.getDoctor().getId();
        this.userName = appointment.getUser() != null ? appointment.getUser().getUsername() : null;
        this.clientName = appointment.getClientName();
        this.date = appointment.getDate();
        this.time = appointment.getTime();
        this.comment = appointment.getComment();
    }

    @JsonIgnore
    public Appointment getAsEntity(){
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
