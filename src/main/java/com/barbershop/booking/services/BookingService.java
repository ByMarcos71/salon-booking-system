package com.barbershop.booking.services;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.barbershop.booking.models.Appointment;
import com.barbershop.booking.models.AppointmentService;
import com.barbershop.booking.models.Employee;
import com.barbershop.booking.models.Schedule;
import com.barbershop.booking.repositories.AppointmentRepository;
import com.barbershop.booking.repositories.AppointmentServiceRepository;
import com.barbershop.booking.repositories.ScheduleRepository;

@Service
public class BookingService {
    private final AppointmentServiceRepository appointmentServiceRepository;
    private final AppointmentRepository appointmentRepository;
    private final ScheduleRepository scheduleRepository;

    @Autowired
    public BookingService(AppointmentServiceRepository appointmentServiceRepository,
            AppointmentRepository appointmentRepository, ScheduleRepository scheduleRepository) {
        this.appointmentServiceRepository = appointmentServiceRepository;
        this.appointmentRepository = appointmentRepository;
        this.scheduleRepository = scheduleRepository;
    }

    /**
     * Checks whether two time ranges overlap.
     *
     * @param startA start time of the first range
     * @param endA   end time of the first range
     * @param startB start time of the second range
     * @param endB   end time of the second range
     * @return true if the ranges overlap, false otherwise
     */
    private boolean overlaps(LocalDateTime startA, LocalDateTime endA,
            LocalDateTime startB, LocalDateTime endB) {

        return endA.isAfter(startB) && endB.isAfter(startA);
    }

    /**
     * Calculates the total duration of an appointment by summing
     * the duration of all its associated services.
     *
     * @param appointment the appointment to calculate the duration for
     * @return the total duration in minutes
     */

    private int calculateTotalDuration(Appointment appointment) {
        List<AppointmentService> services = appointmentServiceRepository.findByAppointment(appointment);
        int total = 0;
        for (int i = 0; i < services.size(); i++) {
            total += services.get(i).getService().getDuration();
        }
        return total;
    }

    /**
     * Checks whether a new appointment can be booked for a given employee,
     * validating that it does not overlap with existing appointments and
     * that it falls within the employee's working schedule.
     *
     * @param employee             the employee the appointment is assigned to
     * @param proposedStart        the proposed start time of the new appointment
     * @param totalDurationMinutes the total duration of the new appointment, in
     *                             minutes
     * @return true if the slot is available, false if it conflicts
     */
    public boolean isAvailable(Employee employee, LocalDateTime proposedStart, int proposedDurationMinutes) {

        LocalDateTime proposedEnd = proposedStart.plusMinutes(proposedDurationMinutes);

        // 1. Validate against the employee's schedule for that day
        DayOfWeek dayOfWeek = proposedStart.getDayOfWeek();
        Optional<Schedule> scheduleOpt = scheduleRepository.findByEmployeeAndDayOfWeek(employee, dayOfWeek);

        if (scheduleOpt.isEmpty()) {
            return false; // employee doesn't work that day
        }
        Schedule schedule = scheduleOpt.get();

        LocalDate proposedDate = proposedStart.toLocalDate();
        LocalDateTime workStart = proposedDate.atTime(schedule.getStartTime());
        LocalDateTime workEnd = proposedDate.atTime(schedule.getEndTime());

        // proposed appointment must fit entirely within working hours
        if (proposedStart.isBefore(workStart) || proposedEnd.isAfter(workEnd)) {
            return false;
        }

        // must not overlap with the break, if there is one
        if (schedule.getBreakStart() != null && schedule.getBreakEnd() != null) {
            LocalDateTime breakStart = proposedDate.atTime(schedule.getBreakStart());
            LocalDateTime breakEnd = proposedDate.atTime(schedule.getBreakEnd());
            if (overlaps(proposedStart, proposedEnd, breakStart, breakEnd)) {
                return false;
            }
        }

        // 2. Validate against existing appointments that day
        LocalDateTime dayStart = proposedDate.atStartOfDay();
        LocalDateTime dayEnd = proposedDate.atTime(23, 59, 59);

        List<Appointment> existingAppointments = appointmentRepository.findByEmployeeAndDateTimeBetween(employee,
                dayStart, dayEnd);

        for (Appointment existing : existingAppointments) {
            LocalDateTime existingEnd = existing.getDateTime().plusMinutes(calculateTotalDuration(existing));
            if (overlaps(proposedStart, proposedEnd, existing.getDateTime(), existingEnd)) {
                return false;
            }
        }

        return true;
    }

}
