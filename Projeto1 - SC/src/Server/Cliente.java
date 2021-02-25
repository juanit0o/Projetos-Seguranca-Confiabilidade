package Server;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Classe representativa de um cliente, que � composta por nome,
 * username, password, lista de seguidores, lista de quem segue,
 * lista de grupos a que pertence e lista de fotografias publicadas.
 * @author Diogo Pinto 52763 
 * @author Francisco Ramalho 53472
 * @author Joao Funenga 53504
 *
 */
public class Cliente {

	private String nome;
	private String user;
	private String pass;
	private ArrayList<String> followers;
	private ArrayList<String> follows;
	private ArrayList<String> grupos; 
	private ArrayList<Photo> photos; 

	/**
	 * Construtor da classe que incia um cliente recebendo
	 * um username, password e nome.
	 * @param u - userId do cliente.
	 * @param p - password do cliente.
	 * @param nome - nome do cliente.
	 */
	public Cliente(String u, String p, String nome) {
		this.nome = nome;
		this.user = u;
		this.pass = p;
		this.followers = new ArrayList<String>();
		this.follows = new ArrayList<String>();
		this.grupos = new ArrayList<String>();
		this.photos = new ArrayList<Photo>();
	}

	// carregar os seus followers,quem segue....
	/**
	 * M�todo que carrega a informa��o pr�via armazenada em disco para as listas
	 * de seguidores, quem segue, grupos e fotografias.
	 */
	public void carregarConta() {
		// pegar no seu ficheiro e preencher os array lists
		File fileUser = new File("data\\Personal User Files\\" + this.user + "\\info.txt");
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
					case 4: 
						//path::like1;like
						String[] splittada = line.split("::");
						if(splittada.length == 2) {
							ArrayList<String> likes = new ArrayList<String>();
							String[] likesFicheiros = splittada[1].split(";"); //cada posicao com o userID de quem deu like
							for(int i = 0; i< likesFicheiros.length; i++) {
								likes.add(likesFicheiros[i]);
							}
							
  							photos.add(new Photo(splittada[0],likes,this.user));
						}else {
							photos.add(new Photo(splittada[0],new ArrayList<String>(),this.user));
						}
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

	}

	/**
	 * M�todo que retorna se a password dada � a password do cliente.
	 * @param password - password do cliente.
	 * @return true se a password pertence ao cliente, senao false.
	 */
	public boolean isPass(String password) {
		return this.pass.equals(password);
	}

	/**
	 * M�todo que retorna o userId do cliente.
	 * @return userId do cliente.
	 */
	public String getUser() {
		return this.user;
	}

	/**
	 * M�todo que retorna se come�ou a seguir um cliente, recebendo o mesmo.
	 * @param clientASeguir - cliente a seguir.
	 * @return true se seguiu o cliente, senao false.
	 */
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

	/**
	 * M�todo que coloca um cliente como seguidor deste.
	 * @param follower - seguidor.
	 */
	public void seguidoPor(String follower) {
		followers.add(follower);
		userContentsToFile();
	}
	
	/**
	 * M�todo que retorna se deixou de seguir um cliente, recebendo o mesmo.
	 * @param pessoa - cliente a deixar de seguir.
	 * @return true se deixou de seguir o cliente, senao false.
	 */
	public boolean deixarDeSeguir(Cliente pessoa) {
		//verifica se a pessoa ja existe
		if(!follows.contains(pessoa.getUser())) {
			return false;
		}
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

	/**
	 * Retorna a lista de grupos em que o cliente est� envolvido,
	 * seja como dono ou participante.
	 * @return - lista de grupos que o cliente participa.
	 */
	public ArrayList<String> getGrupos(){
		return this.grupos;
	}

	/**
	 * M�todo que remove um seguidor atrav�s do seu userId.
	 * @param follower - userId do cliente a deixar de ser seguidor.
	 */
	public void removerFollower(String follower) {
		ArrayList<String> followersAux = new ArrayList<String>();
		for(int i = 0; i < followers.size(); i++) {
			if(followers.get(i) != follower) {
				followersAux.add(followers.get(i));
			}
		}
		followers = followersAux;
		userContentsToFile();
	}
	
	/**
	 * M�todo que adiciona o cliente atual aos grupos que participa.
	 * @param grupoID - groupId do grupo.
	 */
	public void entrarEmGrupo(String grupoID) {
		grupos.add(grupoID);
		userContentsToFile();
	}

	/**
	 * M�todo que remove o cliente atual do grupo atrav�s do grupoId.
	 * @param grupoID - groupId do grupo.
	 */
	public void sairDeGrupo(String grupoID) {
		grupos.remove(grupoID);
		userContentsToFile();
	}
	
	/**
	 * M�todo que devolve uma lista dos seguidores.
	 * @return lista de seguidores.
	 */
	public ArrayList<String> getFollowers (){
		return this.followers;
	}

	/**
	 * M�todo que devolve o n�mero de seguidores.
	 * @return n�mero de seguidores.
	 */
	public int nrOfPhotos() {
		int nrPhotos = 0;
		File photoFolder = new File("data\\Personal User Files\\"+ this.user + "\\Photos");
		nrPhotos = photoFolder.list().length;
		return nrPhotos;
	}

	/**
	 * M�todo que publica uma fotografia, colocando-a na lista de fotografias publicadas.
	 * @param filePath - nome da fotografia.
	 */
	public void publishPhoto(String filePath) {
		photos.add(new Photo(filePath, new ArrayList<String>(), this.user));
		userContentsToFile();
	}

	/**
	 * M�todo que carrega os conteudos do cliente para o disco.
	 */
	public void userContentsToFile() {

		File fileUser = new File("data\\Personal User Files\\" + this.user + "\\info.txt"); // POR NO SITIO DOS FOLLOWERS
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
			//seccao de photos que tem
			bW.write("$\n");
			for (int i = 0; i < photos.size(); i++) {
				bW.write(photos.get(i).toString());
				bW.newLine();
			}
			bW.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * M�todo que coloca like de um utilizador numa fotografia do cliente atual.
	 * @param phId - id da fotografia.
	 * @param userLiking - utilizador que gostou.
	 */
	public void putLike(String phId, String userLiking) {
		for (int i = 0; i < photos.size(); i++) {
			if (photos.get(i).getPhotoPath().equals(phId)) {
				photos.get(i).addLike(userLiking);
				userContentsToFile();
				return;
			}
		}
	}

	/**
	 * M�todo que retorna se segue um cliente recebido por userId.
	 * @param uid - userId do cliente.
	 * @return true se segue o user, senao false.
	 */
	public boolean follows(String uid) {
		return follows.contains(uid);
	}

	/**
	 * M�todo que retorna id da fotografia correspondente ao caminho recebido.
	 * @param path - caminho da fotografia.
	 * @return id da fotografia se existir, senao "".
	 */
	public String getPhoto(String path){
		for(int i = 0; i < photos.size(); ++i){
			System.out.println(photos.get(i).getPhotoPath());
			System.out.println(path + "\n");

			if(photos.get(i).getPhotoPath().equals(path)){
				return photos.get(i).getPhoto();
			}
		}
		return "";
	}

	/**
	 * M�todo que retorna se uma fotografia j� tem gosto de um cliente.
	 * @param uidLiker - userId do cliente.
	 * @param pathFoto - caminho da fotografia.
	 * @return true se ja tem gosto, senao false.
	 */
	public boolean alreadyLiked(String uidLiker, String pathFoto) {
		for(int i = 0; i < photos.size(); i++) {
			if(photos.get(i).getPhotoPath().equals(pathFoto)) {
				return photos.get(i).alreadyLiked(uidLiker);
			}
		}
		return false;
	}	
}
