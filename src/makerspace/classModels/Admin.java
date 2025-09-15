package makerspace.classModels;
import java.util.ArrayList;
import java.util.List;

public class Admin extends User {
	
	private String adminTier;
	public String getAdminTier() { return adminTier; }
	
	private List<String> permissions;
	public List<String> getPermissions() { return permissions; }
	
	public Admin(String userId, String username, String email, String password, String adminTier)
	{
		super(userId, username, email, password);
		this.adminTier = adminTier;
		this.permissions = getDefaultPermissions(adminTier);
	}
	
	@Override
	public String getUserType() { return "Admin";}
	
	private List<String> getDefaultPermissions(String level) {
		List<String> perm = new ArrayList<>();
		switch (level) {
		case "TOP":
			perm.addAll(List.of("USER MANAGEMENT", "EQUIPMENT MANAGEMENT", "SYSTEM CONFIG", "REPORTS", "BILLING"));
			break;
		case "MANAGER":
			perm.addAll(List.of("EQUIPMENT MANAGEMENT", "REPORTS", "BILLING"));
			break;
		case "BASIC":
			perm.addAll(List.of("USER MANAGEMENT", "BASIC REPORTS"));
			break;
		default:
			perm.add("No Permissions Assigned");
			break;
		}
		
		return perm;
	}
	
	public boolean hasPerm(String permission)
	{
		return permissions.contains(permission);
	}
}
