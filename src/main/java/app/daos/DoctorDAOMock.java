package app.daos;

import app.dtos.DoctorDTO;
import app.enums.Speciality;

import java.time.LocalDate;
import java.time.Year;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DoctorDAOMock implements IDAO<DoctorDTO, Speciality> {
    private static Set<DoctorDTO> doctors;
    private static Long nextId;

    public DoctorDAOMock() {
        doctors = new HashSet<DoctorDTO>();
        nextId = 1L;

        //pre-populate list.
        doctors.add(new DoctorDTO(nextId++, "Dr. Alice Smith", LocalDate.of(1975, 4, 12), Year.of(2000), "City Health Clinic", Speciality.FAMILY_MEDICINE, List.of()));
        doctors.add(new DoctorDTO(nextId++, "Dr. Bob Johnson", LocalDate.of(1980, 8, 5), Year.of(2005), "Downtown Medical Center", Speciality.SURGERY, List.of()));
        doctors.add(new DoctorDTO(nextId++, "Dr. Clara Lee", LocalDate.of(1983, 7, 22), Year.of(2008), "Green Valley Hospital", Speciality.PEDIATRICS, List.of()));
        doctors.add(new DoctorDTO(nextId++, "Dr. David Park", LocalDate.of(1978, 11, 15), Year.of(2003), "Hillside Medical Practice", Speciality.PSYCHIATRY, List.of()));
        doctors.add(new DoctorDTO(nextId++, "Dr. Emily White", LocalDate.of(1982, 9, 30), Year.of(2007), "Metro Health Center", Speciality.PEDIATRICS, List.of()));
        doctors.add(new DoctorDTO(nextId++, "Dr. Fiona Martinez", LocalDate.of(1985, 2, 17), Year.of(2010), "Riverside Wellness Clinic", Speciality.SURGERY, List.of()));
        doctors.add(new DoctorDTO(nextId++, "Dr. George Kim", LocalDate.of(1979, 5, 29), Year.of(2004), "Summit Health Institute", Speciality.FAMILY_MEDICINE, List.of()));
    }

    @Override
    public Set<DoctorDTO> getAll() {
        return doctors;
    }

    @Override
    public DoctorDTO getById(Long id) {
        return doctors.stream().filter(d -> d.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Set<DoctorDTO> getBySpeciality(Speciality speciality) {
        return doctors.stream().filter(d -> d.getSpeciality().equals(speciality))
                .collect(Collectors.toSet());
    }

    public Set<DoctorDTO> getByBirthdateRange(LocalDate from, LocalDate to) {
        return doctors.stream()
                .filter(d -> d.getBirthDate() != null &&
                        (d.getBirthDate().isEqual(from) || d.getBirthDate().isAfter(from)) &&
                        (d.getBirthDate().isEqual(to) || d.getBirthDate().isBefore(to)))
                .collect(Collectors.toSet());
    }

    @Override
    public DoctorDTO create(DoctorDTO doctorDTO) {
        doctorDTO.setId(nextId++);
        doctors.add(doctorDTO);
        return doctorDTO;
    }

    @Override
    public DoctorDTO update(Long id, DoctorDTO updatedDoctorDTO) {
        for (DoctorDTO doctor : doctors) {

            if (doctor.getId().equals(id)) {
                doctor.setName(updatedDoctorDTO.getName());
                doctor.setBirthDate(updatedDoctorDTO.getBirthDate());
                doctor.setYearOfGraduation(updatedDoctorDTO.getYearOfGraduation());
                doctor.setClinicName(updatedDoctorDTO.getClinicName());
                doctor.setSpeciality(updatedDoctorDTO.getSpeciality());

                return doctor;
            }
        }
        return null;
    }
}
