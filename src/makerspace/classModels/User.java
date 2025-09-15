package makerspace.classModels;
import java.time.LocalDateTime;

public abstract class User {
   
	protected String userId;
	public String getUserId() { return userId; }
	
	protected String username;
	public String getUsername() { return username; }
	
	protected String email;
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	
    protected String password;
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    protected LocalDateTime registerDate;
    public LocalDateTime getRegisterDate() { return registerDate; }
    
    // Abstract method to get user type
    public abstract String getUserType();
    
    // Constructor to initialize user attributes
    public User(String userId, String username, String email, String password)
    {
    	this.userId = userId;
    	this.username = username;
    	this.email = email;
    	this.password = password;
    	this.registerDate = LocalDateTime.now();
    }
    
    public boolean authentication(String password)
    {
    	return this.password.equals(password);
    }
    
    @Override
    public String toString()
    {
    	return String.format("User ID: '%s', Username: '%s', Email: '%s', User Type: '%s'",
    			userId,
				username,
				email,
				getUserType());
    }
}
