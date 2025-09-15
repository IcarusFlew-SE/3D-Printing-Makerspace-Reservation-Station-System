package makerspace.classModels;

import java.time.LocalDateTime;
import makerspace.utils.*;

public class Equipment {
	protected String equipmentId;
	public String getEquipmentId() { return equipmentId; }
	
	protected String name;
	public String getName() { return name; }
	
	protected String equipmentType;
	public String getEquipmentType() { return equipmentType; }
	
	protected String status; //Available, In Use, Maintenance, Down
	public String getStatus() { return status; }
	public void setStatus(String status) { this.status = EquipmentStatusUtil.normalizeStatus(status); }
	
	protected String location; //which room or area (facility)
	public String getLocation() { return location; }
	
	protected double hourCost;
	public double getHourCost() { return hourCost; }
	
	protected LocalDateTime lastMaintenance;
	public LocalDateTime getLastMaintenance() { return lastMaintenance; }
	
	protected String printerSpec;
	
	
	public Equipment(String equipmentId, String name, String equipmentType, double hourCost, String location) 
	{
		this.equipmentId = equipmentId;
		this.name = name;
		this.equipmentType = equipmentType;
		this.hourCost = hourCost;
		this.status = EquipmentStatusUtil.AVAILABLE;
		this.location = location;
		this.lastMaintenance = LocalDateTime.now();
	}
	
	public boolean isAvailable() { return EquipmentStatusUtil.AVAILABLE.equals("Available"); }
	
	public double calculateRate(int hours) { return hourCost * hours; }
	
	public String getEquipmentInfo()
	{
		return String.format("Equipment ID: %s --- %s (%s) | $%.2f/HR | Status: %s",
				equipmentId,
				name,
				equipmentType,
				hourCost,
				status);
	}
	
	public String getEquipmentStatus()
	{
		return EquipmentStatusUtil.getStatusDescription(status);
	}
	
	@Override
	public String toString()
	{
		return getEquipmentInfo();
	}
}
