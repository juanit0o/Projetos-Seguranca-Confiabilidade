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

	public boolean samePath(String path) {
		return photoPath.equals(path);
	}

	public String getLikes() {
		return String.valueOf(likesClientes.size());
	}
	
	public boolean samePhotoId(String phId) {
		String[] subDirs = photoPath.split(Pattern.quote(File.separator));
		String nomephoto = subDirs[subDirs.length-1];
		return phId.equals(nomephoto);
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
}
