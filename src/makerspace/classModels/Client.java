package makerspace.classModels;
import java.util.ArrayList;
import java.util.List;

public class Client extends User {
	private double accountBalance;
	public double getAccountBalance() { return accountBalance; }
	
	private List<Reservation> reservationHistory;
	public List<Reservation> getReservationHistory() { return reservationHistory; }
	
	private String userLevel;
	public String getUserLevel() { return userLevel; }
	public void setUserLevel(String userLevel) { this.userLevel = userLevel; }	
	
	public Client(String userId, String username, String email, String password)
	{
		super(userId, username, email, password);
		this.accountBalance = 0.0;
		this.reservationHistory = new ArrayList<>();
		this.userLevel = "Standard";
	}
	
	@Override
	public String getUserType() { return "Client"; }
	
	public void addReservation(Reservation reservation)
	{
		reservationHistory.add(reservation);
	}
	
	public void updateAccountBalance(double amt)
	{
		this.accountBalance += amt;
	}
	
	public void deductFromBalance(double amt)
	{
		if(enoughBalance(amt))
		{
			accountBalance -= amt;
		}
	}
	
	public boolean enoughBalance(double amt)
	{
		return accountBalance >= amt;
	}
}
