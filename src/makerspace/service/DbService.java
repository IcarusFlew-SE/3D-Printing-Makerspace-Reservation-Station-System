package makerspace.service;
import makerspace.classModels.*;
import java.io.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DbService {
	private static final String DATA_DIRECTORY = "data/";
	private static final String USERS_FILE = DATA_DIRECTORY + "users.txt";
	private static final String EQUIPMENT_FILE = DATA_DIRECTORY + "equipment.txt";
	private static final String RESERVATIONS_FILE = DATA_DIRECTORY + "reservations.txt";
	
	public DbService()
	{
		createDataDirectory();
	}
	
	private void createDataDirectory()
	{
		File dir = new File(DATA_DIRECTORY);
		if (!dir.exists())
		{
			dir.mkdirs(); // create directory if it doesn't exist
		}
	}
	
	private String reservationToString(Reservation reservation) {
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	    return String.format("RESERVATION|%s|%s|%s|%s|%s|%s|%.2f|%s",
	            reservation.getReservationId(),
	            reservation.getClientId(),
	            reservation.getEquipmentId(),
	            reservation.getStartTime().format(formatter),
	            reservation.getEndTime().format(formatter),
	            reservation.getStatus(),
	            reservation.getCost(),
	            reservation.getCreatedAt().format(formatter));
	}

	// Method to convert String back to Reservation
	private Reservation stringToReservation(String line) {
	    String[] parts = line.split("\\|");
	    if (parts.length < 8 || !parts[0].equals("RESERVATION")) return null;
	    
	    try {
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	        
	        String reservationId = parts[1];
	        String clientId = parts[2];
	        String equipmentId = parts[3];
	        LocalDateTime startTime = LocalDateTime.parse(parts[4], formatter);
	        LocalDateTime endTime = LocalDateTime.parse(parts[5], formatter);
	        String status = parts[6];
	        double cost = Double.parseDouble(parts[7]);
	        LocalDateTime createdAt = LocalDateTime.parse(parts[8], formatter);
	        
	        Reservation reservation = new Reservation(reservationId, clientId, equipmentId, startTime, endTime);
	        reservation.setStatus(status);
	        reservation.setCost(cost);
	        reservation.setCreatedAt(createdAt);
	        
	        return reservation;
	    } catch (Exception e) {
	        System.err.println("Error parsing reservation: " + line);
	        return null;
	    }
	}

	// Save a reservation to file
	public void saveReservation(Reservation reservation) {
	    try (PrintWriter writer = new PrintWriter(new FileWriter(RESERVATIONS_FILE, true))) {
	        writer.println(reservationToString(reservation));
	    } catch (IOException e) {
	        System.err.println("Error saving reservation: " + e.getMessage());
	    }
	}

	// Load all reservations from file
	public Map<String, Reservation> loadReservations() {
	    Map<String, Reservation> reservations = new HashMap<>();
	    try (BufferedReader reader = new BufferedReader(new FileReader(RESERVATIONS_FILE))) {
	        String line;
	        while ((line = reader.readLine()) != null) {
	            Reservation reservation = stringToReservation(line);
	            if (reservation != null) {
	                reservations.put(reservation.getReservationId(), reservation);
	            }
	        }
	    } catch (FileNotFoundException e) {
	        // File doesn't exist yet, return empty map
	    } catch (IOException e) {
	        System.err.println("Error loading reservations: " + e.getMessage());
	    }
	    return reservations;
	}

	// Update a reservation (replace old entry)
	public void updateReservation(Reservation reservation) {
	    removeReservationFromFile(reservation.getReservationId());
	    saveReservation(reservation);
	}

	// Delete a reservation
	public void deleteReservation(String reservationId) {
	    removeReservationFromFile(reservationId);
	}

	// Helper method to remove reservation from file
	private void removeReservationFromFile(String reservationId) {
	    File inputFile = new File(RESERVATIONS_FILE);
	    File tempFile = new File(DATA_DIRECTORY + "temp_reservations.txt");
	    
	    try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
	         PrintWriter writer = new PrintWriter(new FileWriter(tempFile))) {
	        
	        String line;
	        while ((line = reader.readLine()) != null) {
	            if (!line.contains(reservationId)) {
	                writer.println(line);
	            }
	        }
	    } catch (FileNotFoundException e) {
	        return; // File doesn't exist
	    } catch (IOException e) {
	        System.err.println("Error removing reservation: " + e.getMessage());
	        return;
	    }
	    
	    // Replace original file with temp file
	    if (inputFile.delete()) {
	        if (!tempFile.renameTo(inputFile)) {
	            System.err.println("Error replacing reservation file");
	        }
	    }
	}
	
	private String userToString(User user) {
		if (user instanceof Admin) {
			Admin admin = (Admin) user;
			return String.format("Admin|%s|%s|%s|%s|%s", 
					admin.getUserId(),
					admin.getUsername(), 
					admin.getEmail(),
					admin.getPassword(),
					admin.getAdminTier());
		} else if (user instanceof Client) {
			Client client = (Client) user;
			return String.format("Client|%s|%s|%s|%s|%.2f|%s", 
					client.getUserId(),
					client.getUsername(), 
					client.getEmail(),
					client.getPassword(),
					client.getAccountBalance(),
					client.getUserType());
		}
		return "";
	}
	
	 private User stringToUser(String line) {
	        String[] parts = line.split("\\|");
	        if (parts.length < 5) return null;
	        
	        String type = parts[0];
	        String id = parts[1];
	        String username = parts[2];
	        String email = parts[3];
	        String password = parts[4]; 
	        
	        if ("Client".equals(type) && parts.length >= 6) {
	            Client client = new Client(id, username, email, password);
	            // Format: Client|userId|username|email|password|accountBalance|userLevel
	            try {
	            double balance = (Double.parseDouble(parts[5]));
	            client.updateAccountBalance(balance);
	            } catch (NumberFormatException e) {
	            	System.err.println("Invalid Account Balance Format For Client " + username + ": " + parts[5]);
	            }
	            if (parts.length >= 7) {
	                client.setUserLevel(parts[6]);
	            }
	            return client;
	            
	            //Format: Admin|userId|username|email|password|adminTier
	        } else if ("Admin".equals(type) && parts.length >= 5) {
	        	String adminTier = parts.length > 5 ? parts[5] : parts[4];
	            return new Admin(id, username, email, password, adminTier);
	        }
	        
	        return null;
	    }
	
	// Save a new user to file
	public void saveUser(User user) {
		try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE, true))) { // true for append mode
			writer.println(userToString(user));
		} catch (IOException e) {
			System.err.println("Error saving user: " + e.getMessage());
		}
	}
	
	// Load all users from file into a map
	 public Map<String, User> loadUsers() {
	        Map<String, User> users = new HashMap<>();
	        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) { // buffered for efficiency
	            String line;
	            while ((line = reader.readLine()) != null) {
	                User user = stringToUser(line);
	                if (user != null) {
	                    users.put(user.getUserId(), user);
	                }
	            }
	        } catch (FileNotFoundException e) {
	            // File doesn't exist yet, return empty map
	        } catch (IOException e) {
	            System.err.println("Error loading users: " + e.getMessage());
	        }
	        return users;
	        
	 }
	 
	 public void updateUser(User user) {
		 removeUserFromFile(USERS_FILE, user.getUserId());
	     saveUser(user);
	 }
	 
	 public void deleteUser(String userId) {
		 removeUserFromFile(USERS_FILE, userId);
		 
	 }
	 
	private void removeUserFromFile(String usersFile, String userId) {
		File inputFile = new File(usersFile);
		File tempFile = new File("tempfile.txt");
		try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
				PrintWriter writer = new PrintWriter(new FileWriter(tempFile))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (!line.contains(userId)) {
					writer.println(line);
					writer.flush();
				}
			}
		} catch (IOException e) {
			System.err.println("Error deleting user: " + e.getMessage());
			return;
		}
		
		// Replaces original file with temp file
		if (inputFile.delete()) {
			tempFile.renameTo(inputFile);
		}
	}

	private String equipmentToString(Equipment equipment) {
        if (equipment instanceof Printer3D) {
            Printer3D printer = (Printer3D) equipment;
            return String.format("3D_PRINTER|%s|%s|%.2f|%s|%s|%s",
                               printer.getEquipmentId(), printer.getName(),
                               printer.getHourCost(), printer.getLocation(),
                               printer.getPrintTech(), printer.getPrintVolume());
        } else {
            return String.format("EQUIPMENT|%s|%s|%s|%.2f|%s",
                               equipment.getEquipmentId(), equipment.getName(),
                               equipment.getEquipmentType(), equipment.getHourCost(),
                               equipment.getLocation());
        }
	}
	
	public void saveEquipment(Equipment equipment) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(EQUIPMENT_FILE, true))) {
            writer.println(equipmentToString(equipment));
        } catch (IOException e) {
            System.err.println("Error saving equipment: " + e.getMessage());
        }
    }
    
    public void updateEquipment(Equipment equipment) {
        //append the updated equipment to file
        saveEquipment(equipment);
    }
	
}

