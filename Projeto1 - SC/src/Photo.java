import java.io.File;
import java.util.ArrayList;

public class Photo {
	
	private String photoPath;
	private ArrayList<String> likesClientes;
	private String cliente; 

	public Photo(String photoPath, ArrayList<String> likesClientes, String cliente) {
		this.photoPath = photoPath;
		this.likesClientes = likesClientes;
		this.cliente = cliente;
	}

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

	public boolean samePath(String path) {
		return photoPath.equals(path);
	}

	public String getLikes() {
		return String.valueOf(likesClientes.size());
	}
	
}
