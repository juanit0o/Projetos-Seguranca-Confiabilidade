import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Grupo {

	private String grupoID;
	private ArrayList<Cliente> membros;
	private ArrayList<Mensagem> msgs;
	private Cliente dono; // quem cria o grupo
	
	private File groupFolder;

	public Grupo(String grupoID, Cliente dono) {
		this.grupoID = grupoID;
		this.dono = dono;
		membros = new ArrayList<Cliente>();
		membros.add(dono);
		msgs = new ArrayList<Mensagem>();
		groupFolder = new File("..\\data\\Group Folder\\" + this.grupoID);

	}
	
	public Grupo(String grupoID, Cliente dono, ArrayList<Cliente> membros, ArrayList<Mensagem> msgs) {
		this.grupoID = grupoID;
		this.dono = dono;
		this.membros = membros;
		this.msgs = msgs;
		groupFolder = new File("..\\data\\Group Folder\\" + this.grupoID);

	}

	public void registaGrupo() {
		
		File msgLog = new File(groupFolder.getAbsolutePath(), this.grupoID + "_" + "log" + ".txt");
		File membrosGrupo = new File(groupFolder.getAbsolutePath(), this.grupoID + "_" + "membros" + ".txt");
		try {
			groupFolder.mkdirs();
			msgLog.createNewFile();
			membrosGrupo.createNewFile();
			groupContentsToFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void groupContentsToFile() {
		
		File membrosGrupo = new File(groupFolder.getAbsolutePath(), this.grupoID + "_" + "membros" + ".txt");
		try {
			BufferedWriter bW = new BufferedWriter(new FileWriter(membrosGrupo));
			// membros
			for (int i = 0; i < membros.size(); i++) {
				bW.write(membros.get(i).getUser());
				bW.newLine();
			}
			bW.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
