import java.util.ArrayList;

public class Cliente {
	
	private String user;
	private String pass;
	private ArrayList<String> seguidores; //TODO
	
	public Cliente(String u, String p) {
		this.user = u;
		this.pass = p;
		System.out.println(user+" - "+pass);
	}
}
