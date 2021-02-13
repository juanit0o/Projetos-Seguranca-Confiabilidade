import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class Cliente {
	
	private int ID;
	private String user;
	private String pass;
	private ArrayList<String> seguidores; //TODO: uma hipotese
	
	public Cliente(String u, String p, int id) {
		this.ID = id;
		this.user = u;
		this.pass = p;
	}
	
	public boolean isPass(String password) {
		return this.pass.equals(password);
	}

	public String getUser(){
		return this.user;
	}

	public int getID(){
		return this.ID;
	}

	public boolean seguir(Cliente clientASeguir){


		//TODO
		//ADICIONAR AO info.txt do cliente


		//ADICIONAR A SUA LISTA DE PESSOAS DE QUEM O CLIENTE SEGUE
		File file = new File("..\\data\\Personal User Files\\" + clientASeguir.getID() + "\\info.txt");
		try {
			BufferedWriter bW = new BufferedWriter(new FileWriter(file, true));
			bW.write(String.valueOf(clientASeguir.getID()));
			bW.close();
		} catch (IOException e) {
			e.printStackTrace();
		}


		//ADICIONAR ESTE CLIENTE A LISTA DO SEGUIDOS DE ID 
		
		

		return true;
	}



}
