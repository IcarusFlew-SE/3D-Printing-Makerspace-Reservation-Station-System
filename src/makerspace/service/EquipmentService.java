package makerspace.service;
import makerspace.classModels.*;
import makerspace.exceptions.*;
import java.util.*;
import java.time.*;

public class EquipmentService {
    private Map<String, Equipment> equipment;
    private DbService dbService;
    private static final Random random = new Random();
    
    public EquipmentService() {
        this.equipment = new HashMap<>();
        this.dbService = new DbService();
        
        // Try to load existing equipment from database first
        Map<String, Equipment> loadedEquipment = dbService.loadEquipment();
        this.equipment.putAll(loadedEquipment);
        
        // Only initialize default equipment if database is empty
        if (this.equipment.isEmpty()) {
            System.out.println("No equipment found in database. Initializing default equipment...");
            initializeDefaultEquipment();
        } else {
            System.out.println("Loaded " + this.equipment.size() + " equipment items from database.");
        }
        
        // Debug: Print all equipment status
        debugEquipmentStatus();
    }

    // Add this debug method to help troubleshoot
    public void debugEquipmentStatus() {
        System.out.println("\n=== Equipment Debug Info ===");
        System.out.println("Total equipment count: " + equipment.size());
        for (Equipment eq : equipment.values()) {
            System.out.printf("ID: %s | Name: %s | Type: %s | Status: %s | Available: %b%n", 
                             eq.getEquipmentId(), 
                             eq.getName(), 
                             eq.getEquipmentType(),
                             eq.getStatus(), 
                             eq.isAvailable());
        }
        System.out.println("============================\n");
    }
    
    public String addEquipment(Equipment newEquipment) {
        equipment.put(newEquipment.getEquipmentId(), newEquipment);
        dbService.saveEquipment(newEquipment);
        return newEquipment.getEquipmentId();
    }
    
    public String add3DPrinter(String name, double hourlyRate, String location, 
                              String printTechnology, String maxPrintSize) {
        String id = generateEquipmentId();
        Printer3D printer = new Printer3D(id, name, hourlyRate, location, 
                                         printTechnology, maxPrintSize);
        return addEquipment(printer);
    }
    
    public Equipment getEquipmentById(String equipmentId) throws EquipmentUnavailableException {
        Equipment eq = equipment.get(equipmentId);
        if (eq == null) {
            throw new EquipmentUnavailableException("Equipment not found: " + equipmentId);
        }
        return eq;
    }
    
    public List<Equipment> getAvailableEquipment() {
        return equipment.values().stream()
                       .filter(Equipment::isAvailable)
                       .collect(java.util.stream.Collectors.toList());
    }
    
    public List<Equipment> getEquipmentByType(String type) {
        return equipment.values().stream()
                       .filter(eq -> eq.getEquipmentType().equalsIgnoreCase(type))
                       .collect(java.util.stream.Collectors.toList());
    }
    
    public List<Printer3D> get3DPrinters() {
        return equipment.values().stream()
                       .filter(eq -> eq instanceof Printer3D)
                       .map(eq -> (Printer3D) eq)
                       .collect(java.util.stream.Collectors.toList());
    }
    
    public void setEquipmentStatus(String equipmentId, String status) 
            throws EquipmentUnavailableException {
        Equipment eq = getEquipmentById(equipmentId);
        eq.setStatus(status);
        dbService.updateEquipment(eq);
    }
    
    public boolean isEquipmentAvailable(String equipmentId, LocalDateTime startTime, LocalDateTime endTime) throws EquipmentUnavailableException {
        Equipment eq = getEquipmentById(equipmentId);
        return eq.isAvailable(); //used to check reservations (Simplified)
    }
    
    private static final int ID_LENGTH = 6;
    private String generateEquipmentId() {
    	StringBuilder sb = new StringBuilder(ID_LENGTH);
    	for (int i = 0; i < ID_LENGTH; i++) {
    		int digit = random.nextInt(10);
    		sb.append(digit);
    	}
        return "EQ_" + sb.toString();
    }
   
    private void initializeDefaultEquipment() {
        // Initial default 3D Printers
        add3DPrinter("Prusa i3 MK3S+", 15.0, "Lab A", "FDM", "250x210x210mm"); //printer name, hourly rate, location, print tech, max print size
        add3DPrinter("Formlabs Form 3", 25.0, "Lab B", "SLA", "145x145x185mm");
        add3DPrinter("Ultimaker S5", 20.0, "Lab A", "FDM", "330x240x300mm");
        
        // Other Default Equipment
        Equipment laserCutter = new Equipment("EQ_LASER_001", "Epilog Fusion", 
                                            "LASER_CUTTER", 30.0, "Lab C");
        addEquipment(laserCutter);
        
        Equipment cncMilling = new Equipment("EQ_CNC_001", "Haas Mini Mill", 
                                           "CNC_MACHINE", 40.0, "Workshop");
        addEquipment(cncMilling);
    }
}