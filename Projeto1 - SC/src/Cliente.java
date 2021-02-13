import java.util.ArrayList;
import java.util.Arrays;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Cliente {

	private int ID;
	private String user;
	private String pass;
	private ArrayList<Integer> followers; // TODO: uma hipotese
	private ArrayList<Integer> follows; // TODO: uma hipotese

	public Cliente(String u, String p, int id) {
		this.ID = id;
		this.user = u;
		this.pass = p;
		this.followers = new ArrayList<Integer>();
		this.follows = new ArrayList<Integer>();

		// TODO CARREGAR CONTEUDO DO FICHEIRO para guar
	}

	// carregar os seus followers,quem segue....
	public void carregarConta() {
		// pegar no seu ficheiro e preencher os array lists
		File fileUser = new File("..\\data\\Personal User Files\\" + this.ID + "\\info.txt");
		try {
			BufferedReader rW = new BufferedReader(new FileReader(fileUser));
			String line;
			int counter = 0;
			while ((line = rW.readLine()) != null) {
				if (line.equals("$")) {
					counter++;
				} else {
					switch (counter) {
					case 1: // para os follows
						follows.add(Integer.valueOf(line));
						break;
					case 2: // para os followers
						followers.add(Integer.valueOf(line));
						break;
					default:
						System.out.println("Error beep bop");
						break;
					}
					System.out.println(line);
				}

			}
			rW.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// rW.read(mapClientes.size()+":"+user+":"+pass);

	}

	public boolean isPass(String password) {
		return this.pass.equals(password);
	}

	public String getUser() {
		return this.user;
	}

	public int getID() {
		return this.ID;
	}

	public boolean seguir(Cliente clientASeguir) {

		// verificar se o cliente ja segue essa pessoa (parte da pessoa exisitr ou nao
		// feito no sv - existeId)
		if (follows.contains(clientASeguir.getID())) {
			return false;
		}

		follows.add(clientASeguir.getID()); // adicionar ao arraylist dos followers
		userContentsToFile();
		clientASeguir.seguidoPor(this.ID);
		return true;
	}

	public void seguidoPor(int follower) {
		// ADICIONAR ESTE CLIENTE A LISTA DE FOLLOWERS
		followers.add(follower);
		userContentsToFile();

	}
	
	public boolean deixarDeSeguir(Cliente pessoa) {
		//VERIFICAR SE JA DA FOLLOW A PESSOA 
		if(!follows.contains(pessoa.getID())) {
			return false;
		}	
		// REMOVER ESTE CLIENTE A LISTA DE quem da follow
		//follows.remove(pessoa.getID());
		
		ArrayList<Integer> followsAux = new ArrayList<Integer>();
		for(int i = 0; i < follows.size(); i++) {
			if(follows.get(i) != pessoa.getID()) {
				followsAux.add(follows.get(i));
			}
		}
		follows = followsAux;

		pessoa.removerFollower(this.ID);
		userContentsToFile();

		return true;
	}
	
	public void removerFollower(int follower) {
		// ADICIONAR ESTE CLIENTE A LISTA DE FOLLOWERS
		ArrayList<Integer> followersAux = new ArrayList<Integer>();
		for(int i = 0; i < followers.size(); i++) {
			if(followers.get(i) != follower) {
				followersAux.add(followers.get(i));
			}
		}
		followers = followersAux;
		userContentsToFile();

	}
	
	public ArrayList<Integer> getFollowers (){
		return this.followers;
	}

	// por agora ainda so preenche o ficheiro com os followers e a quem da follow
	public void userContentsToFile() {

		File fileUser = new File("..\\data\\Personal User Files\\" + this.ID + "\\info.txt"); // POR NO SITIO DOS
																								// FOLLOWERS
		try {
			BufferedWriter bW = new BufferedWriter(new FileWriter(fileUser));
			// seccao de quem da follow
			bW.write("$\n");
			for (int i = 0; i < follows.size(); i++) {
				bW.write(String.valueOf(follows.get(i)));
				bW.newLine();
			}
			// seccao de followers
			bW.write("$\n");
			for (int i = 0; i < followers.size(); i++) {
				bW.write(String.valueOf(followers.get(i)));
				bW.newLine();
			}
			bW.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
