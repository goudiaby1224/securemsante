package sn.goudiaby.msante.service;

import org.springframework.stereotype.Service;
import sn.goudiaby.msante.repository.AppointmentRepository;
import sn.goudiaby.msante.repository.DoctorRepository;
import sn.goudiaby.msante.repository.PatientRepository;
import sn.goudiaby.msante.repository.AvailabilityRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AvailabilityRepository availabilityRepository;

    // Manual constructor to ensure compilation works
    public AnalyticsService(AppointmentRepository appointmentRepository, 
                           DoctorRepository doctorRepository, 
                           PatientRepository patientRepository, 
                           AvailabilityRepository availabilityRepository) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.availabilityRepository = availabilityRepository;
    }

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Total counts
        stats.put("totalDoctors", doctorRepository.count());
        stats.put("totalPatients", patientRepository.count());
        stats.put("totalAppointments", appointmentRepository.count());
        
        // Today's appointments
        LocalDate today = LocalDate.now();
        stats.put("todayAppointments", appointmentRepository.countByDate(today));
        
        // This week's appointments
        LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDate weekEnd = weekStart.plusDays(6);
        stats.put("weekAppointments", appointmentRepository.countByDateRange(weekStart, weekEnd));
        
        // This month's appointments
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);
        stats.put("monthAppointments", appointmentRepository.countByDateRange(monthStart, monthEnd));
        
        return stats;
    }

    public Map<String, Object> getDoctorStats(Long doctorId) {
        Map<String, Object> stats = new HashMap<>();
        
        // Doctor's appointments
        stats.put("totalAppointments", appointmentRepository.countByDoctorId(doctorId));
        stats.put("completedAppointments", appointmentRepository.countByDoctorIdAndStatus(doctorId, "COMPLETED"));
        stats.put("cancelledAppointments", appointmentRepository.countByDoctorIdAndStatus(doctorId, "CANCELLED"));
        stats.put("upcomingAppointments", appointmentRepository.countByDoctorIdAndStatus(doctorId, "CONFIRMED"));
        
        // Doctor's availability
        LocalDate today = LocalDate.now();
        stats.put("availableSlotsToday", availabilityRepository.countAvailableSlotsByDoctorAndDate(doctorId, today));
        
        // Doctor's rating (placeholder)
        stats.put("averageRating", 4.5);
        stats.put("totalReviews", 25);
        
        return stats;
    }

    public Map<String, Object> getPatientStats(Long patientId) {
        Map<String, Object> stats = new HashMap<>();
        
        // Patient's appointments
        stats.put("totalAppointments", appointmentRepository.countByPatientId(patientId));
        stats.put("completedAppointments", appointmentRepository.countByPatientIdAndStatus(patientId, "COMPLETED"));
        stats.put("upcomingAppointments", appointmentRepository.countByPatientIdAndStatus(patientId, "CONFIRMED"));
        stats.put("cancelledAppointments", appointmentRepository.countByPatientIdAndStatus(patientId, "CANCELLED"));
        
        // Patient's preferences
        stats.put("preferredSpecialties", appointmentRepository.findPreferredSpecialtiesByPatientId(patientId));
        stats.put("preferredDoctors", appointmentRepository.findPreferredDoctorsByPatientId(patientId));
        
        return stats;
    }

    public Map<String, Object> getAppointmentTrends() {
        Map<String, Object> trends = new HashMap<>();
        
        // Last 7 days trend
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);
        
        List<Object> dailyAppointments = appointmentRepository.getDailyAppointmentCounts(startDate, endDate);
        trends.put("dailyTrend", dailyAppointments);
        
        // Monthly trend (last 6 months)
        LocalDate monthEnd = endDate.withDayOfMonth(1);
        LocalDate monthStart = monthEnd.minusMonths(5);
        
        List<Object> monthlyAppointments = appointmentRepository.getMonthlyAppointmentCounts(monthStart, monthEnd);
        trends.put("monthlyTrend", monthlyAppointments);
        
        // Specialty distribution
        List<Object> specialtyDistribution = appointmentRepository.getAppointmentCountsBySpecialty();
        trends.put("specialtyDistribution", specialtyDistribution);
        
        return trends;
    }

    public Map<String, Object> getSystemPerformance() {
        Map<String, Object> performance = new HashMap<>();
        
        // Response times (placeholder)
        performance.put("averageResponseTime", "150ms");
        performance.put("uptime", "99.9%");
        
        // Database performance
        performance.put("activeConnections", 15);
        performance.put("queryPerformance", "Good");
        
        // System health
        performance.put("cpuUsage", "45%");
        performance.put("memoryUsage", "60%");
        performance.put("diskUsage", "30%");
        
        return performance;
    }

    public Map<String, Object> getRevenueAnalytics() {
        Map<String, Object> revenue = new HashMap<>();
        
        // Monthly revenue (placeholder)
        revenue.put("currentMonthRevenue", "$15,000");
        revenue.put("previousMonthRevenue", "$12,500");
        revenue.put("revenueGrowth", "+20%");
        
        // Revenue by specialty
        revenue.put("cardiologyRevenue", "$5,200");
        revenue.put("dermatologyRevenue", "$3,800");
        revenue.put("orthopedicsRevenue", "$4,500");
        revenue.put("pediatricsRevenue", "$1,500");
        
        return revenue;
    }
}
