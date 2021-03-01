package Server;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Classe representativa de um cliente, que e composta por nome,
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
	 * Construtor da classe que inicia um cliente recebendo
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


	/**
	 * Metodo que carrega a informacao previa armazenada em disco para as listas
	 * de quem segue, de seguidores, grupos e fotografias.
	 */
	public void carregarConta() {
		// pegar no seu ficheiro e preencher os array lists
		File fileUser = new File("data" + File.separator + "Personal User Files" + File.separator + this.user + File.separator + "info.txt");
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
					case 3: // para os grupos
						grupos.add(line);
						break;
					case 4: //para o path e likes das fotografias
						//path::like1;like
						String[] splittada = line.split("::");
						if(splittada.length == 2) {
							ArrayList<String> likes = new ArrayList<String>();
							//cada posicao com o userID de quem deu like
							String[] likesFicheiros = splittada[1].split(";"); 
							for(int i = 0; i< likesFicheiros.length; i++) {
								likes.add(likesFicheiros[i]);
							}
  							photos.add(new Photo(splittada[0],likes,this.user));
						}else {
							photos.add(new Photo(splittada[0],new ArrayList<String>(),this.user));
						}
						break;
					default:
						System.out.println("[Error] Couldnt load client files information :(");
						break;
					}
				}
			}
			rW.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Metodo que retorna se a password dada e a password do cliente.
	 * @param password - password do cliente.
	 * @return true se a password e do cliente, senao false.
	 */
	public boolean isPass(String password) {
		return this.pass.equals(password);
	}

	/**
	 * Metodo que retorna o userId do cliente.
	 * @return userId do cliente.
	 */
	public String getUser() {
		return this.user;
	}

	/**
	 * Metodo que retorna se segue um cliente, recebendo o mesmo.
	 * @param clientASeguir - cliente a seguir.
	 * @return true se passou a seguir o cliente, false se ja seguia o cliente
	 */
	public boolean seguir(Cliente clientASeguir) {
		// verificar se o cliente ja segue essa pessoa (parte da pessoa exisitr ou nao
		// feito no sv - existeId)
		if (follows.contains(clientASeguir.getUser())) {
			return false;
		}
		// adicionar ao arraylist de quem da follow
		follows.add(clientASeguir.getUser()); 
		userContentsToFile();
		clientASeguir.seguidoPor(this.user);
		return true;
	}

	/**
	 * Metodo que coloca um cliente como seguidor deste.
	 * @param follower - seguidor.
	 */
	public void seguidoPor(String follower) {
		followers.add(follower);
		userContentsToFile();
	}
	
	/**
	 * Metodo que retorna se deixou de seguir um cliente, recebendo o mesmo.
	 * @param pessoa - cliente a deixar de seguir.
	 * @return true se tiver a seguir e de seguida remove o follow, false se nao o estiver a seguir.
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
	 * Retorna a lista de grupos em que o cliente esta envolvido,
	 * seja como dono ou participante.
	 * @return - lista de grupos que o cliente participa.
	 */
	public ArrayList<String> getGrupos(){
		return this.grupos;
	}

	/**
	 * Metodo que remove um seguidor atraves do seu userId.
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
	 * Metodo que adiciona o grupo do argumento aos grupos do cliente
	 * @param grupoID - groupId do grupo.
	 */
	public void entrarEmGrupo(String grupoID) {
		grupos.add(grupoID);
		userContentsToFile();
	}

	/**
	 * Metodo que remove o cliente atual do grupo atraves do grupoId.
	 * @param grupoID - groupId do grupo.
	 */
	public void sairDeGrupo(String grupoID) {
		grupos.remove(grupoID);
		userContentsToFile();
	}
	
	/**
	 * Metodo que devolve a lista dos seguidores.
	 * @return lista de seguidores.
	 */
	public ArrayList<String> getFollowers (){
		return this.followers;
	}

	/**
	 * Metodo que devolve o numero de fotos.
	 * @return numero de fotos.
	 */
	public int nrOfPhotos() {
		int nrPhotos = 0;
		File photoFolder = new File("data" + File.separator + "Personal User Files" + File.separator + this.user + File.separator + "Photos");
		nrPhotos = photoFolder.list().length;
		return nrPhotos;
	}

	/**
	 * Metodo que publica uma fotografia, colocando-a na lista de fotografias publicadas.
	 * @param filePath - nome da fotografia.
	 */
	public void publishPhoto(String filePath) {
		photos.add(new Photo(filePath, new ArrayList<String>(), this.user));
		userContentsToFile();
	}

	/**
	 * Metodo que escreve os conteudos do cliente para o disco.
	 */
	public void userContentsToFile() {

		File fileUser = new File("data" + File.separator + "Personal User Files" + File.separator + this.user + File.separator + "info.txt");
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
	 * Metodo que coloca like de um utilizador numa fotografia do cliente atual.
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
	 * Metodo que retorna se segue um cliente recebido por userId.
	 * @param uid - userId do cliente.
	 * @return true se segue o user, senao false.
	 */
	public boolean follows(String uid) {
		return follows.contains(uid);
	}

	/**
	 * Metodo que retorna id da fotografia correspondente ao caminho recebido.
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
	 * Metodo que retorna se uma fotografia ja tem gosto de um cliente.
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
