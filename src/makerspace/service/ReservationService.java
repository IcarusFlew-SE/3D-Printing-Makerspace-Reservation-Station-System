package makerspace.service;
import makerspace.classModels.*;
import makerspace.exceptions.*;
import java.util.*;
import java.time.*;

public class ReservationService {
    private Map<String, Reservation> reservations;
    private EquipmentService equipmentService;
    private UserService userService;
    private DbService dbService;
    private static final Random random = new Random();
    
    public ReservationService(EquipmentService equipmentService, UserService userService) {
        this.reservations = new HashMap<>();
        this.equipmentService = equipmentService;
        this.userService = userService;
        this.dbService = new DbService();
    }
    
    public String createReservation(String clientId, String equipmentId, 
                                   LocalDateTime startTime, LocalDateTime endTime) throws Exception {
        // Validation
        validateReservationInput(clientId, equipmentId, startTime, endTime);
        
        // Check equipment availability
        if (!equipmentService.isEquipmentAvailable(equipmentId, startTime, endTime)) {
            throw new EquipmentUnavailableException("Equipment not available for selected time");
        }
        
        // Check user exists and is a client
        User user = userService.getUserById(clientId);
        if (!(user instanceof Client)) {
            throw new InvalidReservationException("Only clients can make reservations");
        }
        
        Client client = (Client) user;
        Equipment equipment = equipmentService.getEquipmentById(equipmentId);
        
        // Calculate cost
        int duration = (int) java.time.Duration.between(startTime, endTime).toHours();
        double cost = equipment.calculateRate(duration);
        
        // Check client balance
        if (!client.enoughBalance(cost)) {
            throw new InvalidReservationException(
                String.format("Insufficient balance. Required: $%.2f, Available: $%.2f", 
                             cost, client.getAccountBalance()));
        }
        
        // Create reservation
        String reservationId = generateReservationId();
        Reservation reservation = new Reservation(reservationId, clientId, equipmentId, startTime, endTime);
        reservation.setCost(cost);
        
        // Process payment
        client.deductFromBalance(cost);
        
        dbService.saveReservation(reservation);
        
        // Save reservation
        reservations.put(reservationId, reservation);
        client.addReservation(reservation);
        
        dbService.updateUser(client);
        
        // Confirm reservation ~ approved
        reservation.approve();
        
        
        // Update equipment status if reservation is immediate
        if (startTime.isBefore(LocalDateTime.now().plusMinutes(30))) 
        {
            equipmentService.setEquipmentStatus(equipmentId, "IN_USE");
        }
        
        return reservationId;
    }
    
    public void cancelReservation(String reservationId, String userId) throws Exception
    {
        Reservation reservation = reservations.get(reservationId);
        if (reservation == null) {
            throw new InvalidReservationException("Reservation not found");
        }
        
        if (!reservation.getClientId().equals(userId)) {
            throw new InvalidReservationException("You can only cancel your own reservations");
        }
       
        if (!reservation.canBeCancelled()) {
            throw new InvalidReservationException("Reservation cannot be cancelled");
        }
        
        // Process refund 
        Client client = (Client) userService.getUserById(userId);
        double refundAmount = calculateRefund(reservation);
        client.updateAccountBalance(refundAmount);
        
        // Cancel reservation
        reservation.cancel();
        
        dbService.updateReservation(reservation);
        dbService.updateUser(client);
        
        // Free up equipment
        equipmentService.setEquipmentStatus(reservation.getEquipmentId(), "AVAILABLE");
        
        System.out.printf("Reservation cancelled. Refund: $%.2f%n", refundAmount);
    }
    
    public List<Reservation> getReservationsByUser(String userId) {
        return reservations.values().stream()
                          .filter(r -> r.getClientId().equals(userId))
                          .sorted((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()))
                          .collect(java.util.stream.Collectors.toList());
    }
    
    public List<Reservation> getAllReservations() {
        return new ArrayList<>(reservations.values());
    }
    
    public Reservation getReservationById(String reservationId) throws InvalidReservationException {
        Reservation reservation = reservations.get(reservationId);
        if (reservation == null) {
            throw new InvalidReservationException("Reservation not found");
        }
        return reservation;
    }
    
    public List<Reservation> getActiveReservations() {
        return reservations.values().stream()
                          .filter(Reservation::isActive)
                          .collect(java.util.stream.Collectors.toList());
    }
    
    public void completeReservation(String reservationId) throws Exception {
        Reservation reservation = getReservationById(reservationId);
        reservation.complete();
        dbService.updateReservation(reservation);
        equipmentService.setEquipmentStatus(reservation.getEquipmentId(), "AVAILABLE");
    }
    
    
    private void validateReservationInput(String clientId, String equipmentId,
                                        LocalDateTime startTime, LocalDateTime endTime) 
            throws InvalidReservationException {
        if (clientId == null || clientId.trim().isEmpty()) {
            throw new InvalidReservationException("Client ID cannot be empty");
        }
        if (equipmentId == null || equipmentId.trim().isEmpty()) {
            throw new InvalidReservationException("Equipment ID cannot be empty");
        }
        if (startTime == null || endTime == null) {
            throw new InvalidReservationException("Start and end times cannot be empty");
        }
        if (startTime.isAfter(endTime)) {
            throw new InvalidReservationException("Start time must be before end time");
        }
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new InvalidReservationException("Cannot make reservations in the past");
        }
        
        long duration = java.time.Duration.between(startTime, endTime).toHours();
        if (duration < 1) {
            throw new InvalidReservationException("Minimum reservation duration is 1 hour");
        }
        if (duration > 24) {
            throw new InvalidReservationException("Maximum reservation duration is 24 hours");
        }
    }
    
    private double calculateRefund(Reservation reservation) {
        LocalDateTime now = LocalDateTime.now();
        long hoursUntilStart = java.time.Duration.between(now, reservation.getStartTime()).toHours();
        
        // Refund: Full refund if cancelled >24h before, 50% if 5hrs before, 0% otherwise
        if (hoursUntilStart > 24) {
            return reservation.getCost();
        } else if (hoursUntilStart > 5) {
            return reservation.getCost() * 0.5;
        } else {
            return 0.0;
        }
    }
    
    private static final int ID_LENGTH = 6;
    private String generateReservationId() {
    	StringBuilder sb = new StringBuilder(ID_LENGTH);
    	for (int i = 0; i < ID_LENGTH; i++) {
    		int digit = random.nextInt(10);
    		sb.append(digit);
    	}
        return "RES_" + sb.toString();
    }
}


