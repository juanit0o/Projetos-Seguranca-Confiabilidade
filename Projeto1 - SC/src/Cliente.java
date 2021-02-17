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

	private String nome;
	private String user;
	private String pass;
	private ArrayList<String> followers;
	private ArrayList<String> follows;
	private ArrayList<String> grupos; 

	public Cliente(String u, String p, String nome) {
		this.nome = nome;
		this.user = u;
		this.pass = p;
		this.followers = new ArrayList<String>();
		this.follows = new ArrayList<String>();
		this.grupos = new ArrayList<String>();
	}

	// carregar os seus followers,quem segue....
	public void carregarConta() {
		// pegar no seu ficheiro e preencher os array lists
		File fileUser = new File("..\\data\\Personal User Files\\" + this.user + "\\info.txt");
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
						follows.add(line);
						break;
					case 2: // para os followers
						followers.add(line);
						break;
					case 3:
						grupos.add(line);
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

	public String getName() {
		return this.nome;
	}

	public boolean seguir(Cliente clientASeguir) {

		// verificar se o cliente ja segue essa pessoa (parte da pessoa exisitr ou nao
		// feito no sv - existeId)
		if (follows.contains(clientASeguir.getUser())) {
			return false;
		}

		follows.add(clientASeguir.getUser()); // adicionar ao arraylist dos followers
		userContentsToFile();
		clientASeguir.seguidoPor(this.user);
		return true;
	}

	public void seguidoPor(String follower) {
		// ADICIONAR ESTE CLIENTE A LISTA DE FOLLOWERS
		followers.add(follower);
		userContentsToFile();

	}
	
	public boolean deixarDeSeguir(Cliente pessoa) {
		//VERIFICAR SE JA DA FOLLOW A PESSOA 
		if(!follows.contains(pessoa.getUser())) {
			return false;
		}	
		// REMOVER ESTE CLIENTE A LISTA DE quem da follow
		//follows.remove(pessoa.getID());
		
		ArrayList<String> followsAux = new ArrayList<String>();
		for(int i = 0; i < follows.size(); i++) {
			if(follows.get(i) != pessoa.getUser()) {
				followsAux.add(follows.get(i));
			}
		}
		follows = followsAux;

		pessoa.removerFollower(this.user);
		userContentsToFile();

		return true;
	}

	public ArrayList<String> getGrupos(){
		return this.grupos;
	}

	public void removerFollower(String follower) {
		// ADICIONAR ESTE CLIENTE A LISTA DE FOLLOWERS
		ArrayList<String> followersAux = new ArrayList<String>();
		for(int i = 0; i < followers.size(); i++) {
			if(followers.get(i) != follower) {
				followersAux.add(followers.get(i));
			}
		}
		followers = followersAux;
		userContentsToFile();

	}
	
	public void entrarEmGrupo(String grupoID) {
		grupos.add(grupoID);
		userContentsToFile();
	}

	public void sairDeGrupo(String grupoID) {
		grupos.remove(grupoID);
		userContentsToFile();
	}
	
	public ArrayList<String> getFollowers (){
		return this.followers;
	}

	// por agora ainda so preenche o ficheiro com os followers e a quem da follow
	public void userContentsToFile() {

		File fileUser = new File("..\\data\\Personal User Files\\" + this.user + "\\info.txt"); // POR NO SITIO DOS
																								// FOLLOWERS
		try {
			BufferedWriter bW = new BufferedWriter(new FileWriter(fileUser));
			// seccao de quem da follow
			bW.write("$\n");
			for (int i = 0; i < follows.size(); i++) {
				bW.write(follows.get(i));
				bW.newLine();
			}
			// seccao de followers
			bW.write("$\n");
			for (int i = 0; i < followers.size(); i++) {
				bW.write(followers.get(i));
				bW.newLine();
			}
			//seccao de grupos a que pertence
			bW.write("$\n");
			for (int i = 0; i < grupos.size(); i++) {
				bW.write(grupos.get(i));
				bW.newLine();
			}
			bW.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int nrOfPhotos() {
		//percorrer a pasta 
		int nrPhotos = 0;
		File photoFolder = new File("..\\data\\Personal User Files\\"+ this.user + "\\Photos");
		nrPhotos = photoFolder.list().length;
		//System.out.println("nr photos na pasta: " + nrPhotos);
		
		return nrPhotos;
	}

}
