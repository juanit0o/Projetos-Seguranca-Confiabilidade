import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

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

	public void addLike(String userLiking) {
		this.likesClientes.add(userLiking);
	}
	
	public String getPhotoPath() {
		return this.photoPath;
	}

	public boolean alreadyLiked(String liker) {
		for(int i = 0; i < likesClientes.size(); i++) {
			if(likesClientes.get(i).equals(liker)) {
				return true;
			}
		}
		return false;
	}

	public String getPhoto(){
		String[] subDirs = photoPath.split(Pattern.quote(File.separator));
		String nomephoto = subDirs[subDirs.length-1];
		return nomephoto + " from " + cliente + " has " + likesClientes.size() + " likes\n";
	}
}
