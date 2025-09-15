package makerspace.utils;

public class EquipmentStatusUtil {
    public static final String AVAILABLE = "AVAILABLE";
    public static final String IN_USE = "IN_USE";
    public static final String MAINTENANCE = "MAINTENANCE";
    public static final String DOWN = "DOWN";
    
    // Helper method to validate status
    public static boolean isValidStatus(String status) {
        if (status == null) return false;
        
        String upperStatus = status.toUpperCase().trim();
        return upperStatus.equals(AVAILABLE) || 
               upperStatus.equals(IN_USE) || 
               upperStatus.equals(MAINTENANCE) || 
               upperStatus.equals(DOWN);
    }
    
    // Method that normalizes various input formats to standard status
    public static String normalizeStatus(String status) {
        if (status == null) return AVAILABLE;
        
        String upperStatus = status.toUpperCase().trim();
        switch (upperStatus) {
            case "AVAILABLE":
            case "AVAIL":
                return AVAILABLE;
            case "IN_USE":
            case "IN USE":
            case "INUSE":
            case "BUSY":
                return IN_USE;
            case "MAINTENANCE":
            case "MAINT":
                return MAINTENANCE;
            case "DOWN":
            case "BROKEN":
                return DOWN;
            default:
                return AVAILABLE; // default to AVAILABLE if unrecognized
        }
    }
    
    // User-friendly statuses
    public static String getStatusDescription(String status) {
        switch (status) {
            case AVAILABLE:
                return "Available for reservation";
            case IN_USE:
                return "Currently being used";
            case MAINTENANCE:
                return "Under maintenance";
            case DOWN:
                return "Out of order";
            default:
                return "Unknown status";
        }
    }
}
