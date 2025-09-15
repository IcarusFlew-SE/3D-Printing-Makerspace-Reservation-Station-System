package makerspace.classModels;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class Reservation {
	private String reservationId;
    private String clientId;
    private String equipmentId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status; //pending, approved, cancelled, in_progress, completed
    private LocalDateTime createdAt;
    private double cost;

    public String getReservationId() { return reservationId; }
    public String getClientId() { return clientId; }
    public String getEquipmentId() { return equipmentId; }
    public String getStatus() { return status; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public double getCost() { return cost; }

    public void setStatus(String status) { this.status = status; }//~
    public void setCost(double cost) { this.cost = cost;}

    //constructor
    public Reservation (String reservationId, String clientId, String equipmentId, LocalDateTime startTime, LocalDateTime endTime) 
    { this.reservationId = reservationId; 
       this.clientId = clientId;
       this.equipmentId = equipmentId;
       this.startTime = startTime;
       this.endTime = endTime;
       this.status = "PENDING"; 
       this.createdAt = LocalDateTime.now();
    }
    
    public boolean canBeCancelled()
    {
    	LocalDateTime now = LocalDateTime.now();
    	return now.isBefore(startTime) && ("PENDING".equals(status) || "APPROVED".equals(status));
    }
    public void cancel() { this.status = "CANCELLED"; }
    public void approve() { this.status = "APPROVED"; }
    public void complete() { this.status = "COMPLETED"; }
    
    public int getDuration()
    {
    	return (int) Duration.between(startTime, endTime).toHours(); //body type is long converted to integer
    }
    
    public boolean isActive() 
    {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(startTime) && now.isBefore(endTime) && "APPROVED".equals(status);
    }
    
    @Override
    public String toString()
    {
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    	return String.format("Reservation ID: %s, Equipment ID: %s, Time: %s to %s, Status: %s, Total Cost: $%.2f",
    			reservationId,
    			equipmentId,
    			startTime.format(formatter),
    			endTime.format(formatter),
    			status,
    			cost);
    }
	public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt;}
}
