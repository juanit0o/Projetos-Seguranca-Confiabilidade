import java.util.ArrayList;

public class Cliente {
	
	private String user;
	private String pass;
	private ArrayList<String> seguidores; //TODO: uma hipotese
	
	public Cliente(String u, String p) {
		this.user = u;
		this.pass = p;
		System.out.println(user+" - "+pass);
	}
	
	public boolean isPass(String password) {
		System.out.println(password + "=? "+ this.pass);
		return this.pass.equals(password);
	}
}
