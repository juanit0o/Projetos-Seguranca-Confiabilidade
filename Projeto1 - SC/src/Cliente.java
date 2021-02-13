import java.util.ArrayList;

public class Cliente {
	
	private int ID;
	private String user;
	private String pass;
	private ArrayList<String> seguidores; //TODO: uma hipotese
	
	public Cliente(String u, String p, int id) {
		this.ID = id;
		this.user = u;
		this.pass = p;
		System.out.println(ID);
	}
	
	public boolean isPass(String password) {
		return this.pass.equals(password);
	}
}
