import java.util.ArrayList;

public class Mensagem {
	private String grupoID; //p onde vai
	private Cliente remetente; //de onde vem
	private String msg;
	private ArrayList <Cliente> jaLeuMsg; //nao por este no construtor maybe?
	
	public Mensagem(String grupoID, Cliente remetente, String msg) {
		this.grupoID = grupoID;
		this.remetente = remetente;
		this.msg = msg;
		
	}
	
}
