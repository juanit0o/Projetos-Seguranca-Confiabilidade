package Server;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Classe representativa de uma fotografia, que e composta pelo path da 
 * fotografia, lista de Clientes que colocaram gosto e id do cliente que 
 * publicou a fotografia.
 * @author Diogo Pinto 52763 
 * @author Francisco Ramalho 53472
 * @author Joao Funenga 53504
 *
 */
public class Photo {

	private String photoPath;
	private ArrayList<String> likesClientes;
	private String cliente; 

	/**
	 * Construtor da classe que inicia uma fotografia recebendo um caminho para
	 * a fotografia, lista de id dos clientes que gostaram e id do cliente dono.
	 * @param photoPath - caminho da fotografia.
	 * @param likesClientes - lista de id dos clientes que gostaram.
	 * @param cliente - id do cliente que publicou.
	 */
	public Photo(String photoPath, ArrayList<String> likesClientes, String cliente) {
		this.photoPath = photoPath;
		this.likesClientes = likesClientes;
		this.cliente = cliente;
	}

	/**
	 * Metodo representativo textual de fotografia (nomeFoto + likes).
	 */
	public String toString() {
		String output = photoPath;
		output += "::";
		for(int i = 0; i < likesClientes.size(); i++) {
			output+=likesClientes.get(i);
			if( i + 1 < likesClientes.size()) {
				output += ";";
			}			
		}
		return output; 
	}

	/**
	 * Metodo adiciona like de um user.
	 * @param userID - id do user que coloca gosto.
	 */
	public void addLike(String userID) {
		this.likesClientes.add(userID);
	}

	/**
	 * Metodo devolve o caminho da fotografia atual.
	 * @return caminho da fotografia.
	 */
	public String getPhotoPath() {
		return this.photoPath;
	}

	/**
	 * Metodo devolve se a fotografia ja tem um gosto do user recebido.
	 * @param liker - userId recebido
	 * @return true se liker ja gosta da fotografia, senao false.
	 */
	public boolean alreadyLiked(String liker) {
		for(int i = 0; i < likesClientes.size(); i++) {
			if(likesClientes.get(i).equals(liker)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Metodo devolve informacao da fotografia agrupada numa
	 * string com a informacao: nome e quantos likes tem.
	 * @return String com nome e numero de likes.
	 */
	public String getPhoto(){
		String[] subDirs = photoPath.split(Pattern.quote(File.separator));
		String nomephoto = subDirs[subDirs.length-1];
		return nomephoto + " from '" + cliente + "' has " + likesClientes.size() + " likes\n";
	}
}
