package makerspace.main;

import makerspace.classModels.*;
import makerspace.service.*;
import makerspace.exceptions.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReservationMain {
	
	public static void main(String[] args) {
        ReservationMain app = new ReservationMain();
        app.run();
    }
	
    private UserService userService;
    private EquipmentService equipmentService;
    private ReservationService reservationService;
    private Scanner scanner;
    private User currentUser;
    
    public ReservationMain() {
        this.userService = new UserService();
        this.equipmentService = new EquipmentService();
        this.reservationService = new ReservationService(equipmentService, userService);
        this.scanner = new Scanner(System.in);
    }
    
    public void run() {
        System.out.println("=== Welcome to MakerSpace Reservation System ===");
        
        while (true) {
            try {
                if (currentUser == null) {
                    showLoginMenu();
                } else {
                    showMainMenu();
                }
            } catch (Exception e) {
                System.err.println("An error occurred: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    private void showLoginMenu() {
        System.out.println("\n=== Login Menu ===");
        System.out.println("1. Login");
        System.out.println("2. Register as Client");
        System.out.println("3. Register as Admin");
        System.out.println("4. Exit");
        System.out.println("What Would You Like To Do?");
        System.out.print("OPTION: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1:
                login();
                break;
            case 2:
                registerClient();
                break;
            case 3:
                registerAdmin();
                break;
            case 4:
                System.out.println("THANK YOU FOR USING THE RESERVATION SYSTEM");
                System.exit(0);
                break;
            default:
                System.out.println("INVALID OPTION. PLEASE TRY AGAIN.");
        }
    }
    
    private void showMainMenu() {
        System.out.println("\n=== Main Menu ===");
        System.out.println("Current User: " + currentUser.getUsername() + " (" + currentUser.getUserType() + ")");
        
        if (currentUser instanceof Client) {
            showClientMenu();
        } else if (currentUser instanceof Admin) {
            showAdminMenu();
        }
    }
    
    private void showClientMenu() {
        Client client = (Client) currentUser;
        System.out.println("Account Balance: $" + String.format("%.2f", client.getAccountBalance()));
        System.out.println("\n1. Browse Equipment");
        System.out.println("2. Make Reservation");
        System.out.println("3. View My Reservations");
        System.out.println("4. Cancel Reservation");
        System.out.println("5. Add Money to Account");
        System.out.println("6. View 3D Printers");
        System.out.println("7. Logout");
        System.out.print("Choose option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1:
                browseEquipment();
                break;
            case 2:
                makeReservation();
                break;
            case 3:
                viewMyReservations();
                break;
            case 4:
                cancelReservation();
                break;
            case 5:
                addFunds();
                break;
            case 6:
                view3DPrinters();
                break;
            case 7:
                logout();
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }
    
    private void showAdminMenu() {
        Admin admin = (Admin) currentUser;
        System.out.println("\n1. Manage Equipment");
        System.out.println("2. View All Reservations");
        System.out.println("3. Manage Users");
        System.out.println("4. Add New Equipment");
        System.out.println("5. Generate Reports");
        System.out.println("6. Equipment Maintenance");
        System.out.println("7. Logout");
        System.out.print("Choose option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1:
                manageEquipment();
                break;
            case 2:
                viewAllReservations();
                break;
            case 3:
                manageUsers();
                break;
            case 4:
                addNewEquipment();
                break;
            case 5:
                generateReports();
                break;
            case 6:
                equipmentMaintenance();
                break;
            case 7:
                logout();
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }
    
    private void login() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
        try {
            currentUser = userService.authenticate(username, password);
            System.out.println("Login successful! Welcome " + currentUser.getUsername());
        } catch (UserException e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }
    
    private void registerClient() {
        System.out.println("\n=== Client Registration ===");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
        try {
            String userId = userService.registerClient(username, email, password);
            System.out.println("Registration successful! Your ID is: " + userId);
        } catch (InvalidReservationException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }
    
    private void registerAdmin() {
        System.out.println("\n=== Admin Registration ===");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Admin Level (TOP/MANAGER/BASIC): ");
        String adminLevel = scanner.nextLine().toUpperCase().trim().replaceAll("\\s+", "_");
        
        try {
            String userId = userService.registerAdmin(username, email, password, adminLevel);
            System.out.println("Admin registration successful! Your ID is: " + userId);
        } catch (InvalidReservationException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }
    
    private void browseEquipment() {
        System.out.println("\n=== Available Equipment ===");
        List<Equipment> available = equipmentService.getAvailableEquipment();
        
        if (available.isEmpty()) {
            System.out.println("No equipment available at the moment.");
            return;
        }
        
        for (int i = 0; i < available.size(); i++) {
            Equipment eq = available.get(i);
            System.out.printf("%d. %s\n", i + 1, eq.getEquipmentInfo());
            System.out.printf("   Location: %s | Status: %s\n", eq.getLocation(), eq.getStatus());
        }
    }
    
    private void view3DPrinters() {
        System.out.println("\n=== 3D Printers ===");
        List<Printer3D> printers = equipmentService.get3DPrinters();
        
        if (printers.isEmpty()) {
            System.out.println("No 3D printers available.");
            return;
        }
        
        for (Printer3D printer : printers) {
            System.out.println("┌─────────────────────────────────────┐");
            System.out.printf("│ %s\n", printer.getName());
            System.out.printf("│ Technology: %s\n", printer.getPrintTech());
            System.out.printf("│ Max Print Size: %s\n", printer.getPrintVolume());
            System.out.printf("│ Rate: $%.2f/hour + materials\n", printer.getHourCost());
            System.out.printf("│ Location: %s\n", printer.getLocation());
            System.out.printf("│ Status: %s\n", printer.getStatus());
            System.out.printf("│ Heated Bed: %s\n", printer.heatedBed() ? "YES" : "NO");
            System.out.println("└─────────────────────────────────────┘");
        }
    }
    
    private void makeReservation() {
        System.out.println("\n=== Make Reservation ===");
        
        // Show available equipment
        List<Equipment> available = equipmentService.getAvailableEquipment();
        if (available.isEmpty()) {
            System.out.println("No equipment available for reservation.");
            return;
        }
        
        System.out.println("Available Equipment:");
        for (int i = 0; i < available.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, available.get(i).getEquipmentInfo());
        }
        
        System.out.print("Select equipment (number): ");
        int equipmentChoice = getIntInput() - 1;
        
        if (equipmentChoice < 0 || equipmentChoice >= available.size()) {
            System.out.println("Invalid equipment selection.");
            return;
        }
        
        Equipment selectedEquipment = available.get(equipmentChoice);
        
        // Get reservation details
        System.out.print("Start date and time (yyyy-MM-dd HH:mm): ");
        String startTimeStr = scanner.nextLine();
        System.out.print("Duration in hours: ");
        int duration = getIntInput();
        if (duration <= 0) {
			System.out.println("Invalid duration.");
			return;
		}
        
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime startTime = LocalDateTime.parse(startTimeStr, formatter);
            LocalDateTime endTime = startTime.plusHours(duration);
            
            String reservationId = reservationService.createReservation(
                currentUser.getUserId(), selectedEquipment.getEquipmentId(),
                startTime, endTime);
                
            System.out.println("Reservation created successfully!");
            System.out.println("Reservation ID: " + reservationId);
            
            // Show cost calculation
            double cost = selectedEquipment.calculateRate(duration);
            System.out.printf("Total cost: $%.2f\n", cost);
            
        } catch (Exception e) {
            System.out.println("Failed to create reservation: " + e.getMessage());
        }
    }
    
    private void viewMyReservations() {
        System.out.println("\n=== My Reservations ===");
        try {
            List<Reservation> reservations = reservationService.getReservationsByUser(currentUser.getUserId());
            
            if (reservations.isEmpty()) {
                System.out.println("You have no reservations.");
                return;
            }
            
            for (Reservation reservation : reservations) {
                System.out.println("┌─────────────────────────────────────┐");
                System.out.printf("│ ID: %s\n", reservation.getReservationId());
                System.out.printf("│ Equipment: %s\n", reservation.getEquipmentId());
                System.out.printf("│ Start: %s\n", reservation.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                System.out.printf("│ End: %s\n", reservation.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                System.out.printf("│ Duration: %d hours\n", reservation.getDuration());
                System.out.printf("│ Status: %s\n", reservation.getStatus());
                System.out.printf("│ Cost: $%.2f\n", reservation.getCost());
                System.out.printf("│ Created At: %s\n", reservation.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                System.out.println("└─────────────────────────────────────┘");
            }
        } catch (Exception e) {
            System.out.println("Error retrieving reservations: " + e.getMessage());
        }
    }
    
    private void cancelReservation() {
        System.out.println("\n=== Cancel Reservation ===");
        System.out.print("Enter Reservation ID: ");
        String reservationId = scanner.nextLine();
        
        try {
            reservationService.cancelReservation(reservationId, currentUser.getUserId());
            System.out.println("Reservation cancelled successfully!");
        } catch (Exception e) {
            System.out.println("Failed to cancel reservation: " + e.getMessage());
        }
    }
    
    private void addFunds() {
        System.out.println("\n=== Add Funds ===");
        System.out.print("Enter amount to add: $");
        double amount = getDoubleInput();
        if (amount <= 0) {
            System.out.println("Invalid amount.");
            return;
        }
        try {
            Client client = (Client) currentUser;
            client.updateAccountBalance(amount);
            
            userService.updateUserBalance(client.getUserId(), amount);
            
            System.out.printf("$%.2f added to your account. New balance: $%.2f\n", 
                             amount, 
                             client.getAccountBalance());
            
        } catch (Exception e) {
            System.out.println("Error updating balance: " + e.getMessage());
        }
    }
    
    // Admin Functions
    private void manageEquipment() {
        System.out.println("\n=== Equipment Management ===");
        List<Equipment> allEquipment = equipmentService.getAvailableEquipment();
        
        for (Equipment eq : allEquipment) {
            System.out.println(eq.getEquipmentInfo());
        }
        
        System.out.println("\n1. Change Equipment Status");
        System.out.println("2. View Equipment Details");
        System.out.print("Choose action: ");
        
        int choice = getIntInput();
        if (choice == 1) {
            changeEquipmentStatus();
        }
        else if (choice == 2) {
			System.out.print("Enter Equipment ID: ");
			String equipmentId = scanner.nextLine();
			try {
				Equipment eq = equipmentService.getEquipmentById(equipmentId);
				System.out.println(eq);
			} catch (EquipmentUnavailableException e) {
				System.out.println("Retrieval Error: " + e.getMessage());
			}
		} else {
			System.out.println("Invalid Option.");
		}
    }
    
    private void changeEquipmentStatus() {
        System.out.print("Enter Equipment ID: ");
        String equipmentId = scanner.nextLine();
        System.out.print("New Status (AVAILABLE/MAINTENANCE/DOWN): ");
        String status = scanner.nextLine();
        
        try {
            equipmentService.setEquipmentStatus(equipmentId, status);
            System.out.println("Equipment status updated successfully!");
        } catch (EquipmentUnavailableException e) {
            System.out.println("Update Error: " + e.getMessage());
        }
    }
    
    private void viewAllReservations() {
        System.out.println("\n=== All Reservations ===");
        List<Reservation> allReservations = reservationService.getAllReservations();
        
        if (allReservations.isEmpty()) {
            System.out.println("No reservations found.");
            return;
        }
        
        for (Reservation reservation : allReservations) {
            System.out.println(reservation);
        }
    }
    
    private void manageUsers() {
        System.out.println("\n=== User Management ===");
        List<User> allUsers = userService.getAllUsers();
        
        System.out.println("All Users:");
        for (User user : allUsers) {
            System.out.printf("ID: %s | Username: %s | Type: %s | Email: %s\n",
                             user.getUserId(), user.getUsername(), 
                             user.getUserType(), user.getEmail());
        }
        System.out.println("\n1. Change User Email");
        System.out.println("2. Change User Password");
        System.out.println("3. Delete User");
        System.out.println("0. Back to Main Menu");
        System.out.print("Choose action: ");
        int choice = getIntInput();
        
        if (choice == 0) return;
        
        System.out.print("Enter User ID: ");
        String userId = scanner.nextLine();
        try {
			User user = userService.getUserById(userId);
			if (choice == 1) {
				System.out.print("New Email: ");
				String newEmail = scanner.nextLine();
				user.setEmail(newEmail);
				System.out.println("Email updated successfully!");
			} 
			else if (choice == 2) {
				System.out.print("New Password: ");
				String newPassword = scanner.nextLine();
				user.setPassword(newPassword);
				System.out.println("Password updated successfully!");
			} 
			else if (choice == 3) {
				System.out.println("Are You Sure You Want To Delete This User? (yes/no)");
				String confirm = scanner.nextLine();
				
				if (confirm.equalsIgnoreCase("yes") || confirm.equalsIgnoreCase("y")) {
					userService.deleteUser(userId);
					System.out.println("User deleted successfully!");
				} else {
					System.out.println("User deletion cancelled.");
				}
			} 
			else {
				System.out.println("Invalid action.");
			}
		} catch (UserException e) {
			System.out.println("Error: " + e.getMessage());
		} 
    }
    
    private void addNewEquipment() {
        System.out.println("\n=== Add New Equipment ===");
        System.out.println("1. Add 3D Printer");
        System.out.println("2. Add Other Equipment");
        System.out.print("Choose type: ");
        
        int choice = getIntInput();
        
        System.out.println("Equipment Name: ");
        String name = scanner.nextLine();
        System.out.println("Hourly Rate: $");
        double rate = getDoubleInput();
        System.out.println("Location: ");
        String location = scanner.nextLine();
        
        try {
            if (choice == 1) {
                System.out.print("Print Technology (FDM/SLA/SLS): ");
                String tech = scanner.nextLine();
                System.out.print("Max Print Size (XxYxZ mm): ");
                String maxSize = scanner.nextLine();
                
                String id = equipmentService.add3DPrinter(name, rate, location, tech, maxSize);
                System.out.println("3D Printer added successfully! ID: " + id);
            } else {
                System.out.print("Equipment Type: ");
                String type = scanner.nextLine();
                
                Equipment equipment = new Equipment("", name, type, rate, location);
                String id = equipmentService.addEquipment(equipment);
                System.out.println("Equipment added successfully! ID: " + id);
            }
        } catch (Exception e) {
            System.out.println("Error adding equipment: " + e.getMessage());
        }
    }
    
    private void generateReports() {
        Admin admin = (Admin) currentUser;
        if (!admin.hasPerm("REPORTS")) {
            System.out.println("You don't have permission to generate reports.");
            return;
        }
        
        System.out.println("\n=== Reports ===");
        System.out.println("1. Equipment Usage Report");
        System.out.println("2. Revenue Report");
        System.out.println("3. User Activity Report");
        System.out.print("Choose report type: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1:
                generateEquipmentUsageReport();
                break;
            case 2:
                generateRevenueReport();
                break;
            case 3:
                generateUserActivityReport();
                break;
            default:
                System.out.println("Invalid report type.");
        }
    }
    
    private void generateEquipmentUsageReport() {
        System.out.println("\n=== Equipment Usage Report ===");
        List<Equipment> equipment = equipmentService.getAvailableEquipment();
        List<Reservation> reservations = reservationService.getAllReservations();
        
        System.out.println("Equipment Usage:");
        for (Equipment eq : equipment) {
            long count = reservations.stream()
                                   .filter(r -> r.getEquipmentId().equals(eq.getEquipmentId()))
                                   .count();
            System.out.printf("%s: %d reservations\n", eq.getName(), count);
        }
    }
    
    private void generateRevenueReport() {
        System.out.println("\n=== Revenue Report ===");
        List<Reservation> reservations = reservationService.getAllReservations();
        
        double totalRevenue = reservations.stream()
                                        .filter(r -> "COMPLETED".equals(r.getStatus()))
                                        .mapToDouble(Reservation::getCost)
                                        .sum();
        
        System.out.printf("Total Revenue: $%.2f\n", totalRevenue);
        System.out.printf("Completed Reservations: %d\n", 
                         (int) reservations.stream()
                                         .filter(r -> "COMPLETED".equals(r.getStatus()))
                                         .count());
    }
    
    private void generateUserActivityReport() {
        System.out.println("\n=== User Activity Report ===");
        List<Client> clients = userService.getAllClients();
        
        System.out.println("Client Activity:");
        for (Client client : clients) {
            System.out.printf("%s: %d reservations\n", 
                             client.getUsername(), 
                             client.getReservationHistory().size());
        }
    }
    
    private void equipmentMaintenance() {
        System.out.println("\n=== Equipment Maintenance ===");
        System.out.print("Enter Equipment ID for maintenance: ");
        String equipmentId = scanner.nextLine();
        
        try {
            equipmentService.setEquipmentStatus(equipmentId, "MAINTENANCE");
            System.out.println("Equipment marked for maintenance.");
        } catch (EquipmentUnavailableException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void logout() {
        currentUser = null;
        System.out.println("Logged out successfully!");
    }
    
    // Utility for Inputs
    private int getIntInput() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format. Please try again.");
            return getIntInput();
        }
    }
    
    private double getDoubleInput() {
        try {
            return Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format. Please try again.");
            return getDoubleInput();
        }
    }
}