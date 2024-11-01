package app.dtos;

import app.entities.Appointment;
import app.entities.Doctor;
import app.enums.Speciality;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDTO {
    private Long id;
    private String name;
    @JsonProperty("birthdate")
    private LocalDate birthDate;
    @JsonProperty("year_of_graduation")
    private Year yearOfGraduation;
    @JsonProperty("clinic_name")
    private String clinicName;
    private Speciality speciality;
    private List<AppointmentDTO> appointmentsDTO;

    public DoctorDTO(Doctor doctor) {
        this.id = doctor.getId();
        this.name = doctor.getName();
        this.birthDate = doctor.getBirthDate();
        this.yearOfGraduation = doctor.getYearOfGraduation();
        this.clinicName = doctor.getClinicName();
        this.speciality = doctor.getSpeciality();
        this.appointmentsDTO = doctor.getAppointments().stream().map(AppointmentDTO::new).collect(Collectors.toList());
    }

    @JsonIgnore
    public Doctor getAsEntity (){
        List<Appointment> appointmentEntities;
        if (this.appointmentsDTO != null) {
            appointmentEntities = this.appointmentsDTO.stream()
                    .map(AppointmentDTO::getAsEntity) // convert each RoomDTO to Room
                    .collect(Collectors.toList());
        } else {
            appointmentEntities = new ArrayList<>();
        }

        return Doctor.builder()
                .id(id)
                .name(name)
                .birthDate(birthDate)
                .yearOfGraduation(yearOfGraduation)
                .clinicName(clinicName)
                .speciality(speciality)
                .appointments(appointmentEntities)
                .build();
    }
}
