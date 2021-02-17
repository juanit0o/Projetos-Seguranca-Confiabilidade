import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Grupo {

	private String grupoID;
	private ArrayList<Cliente> membros;
	private ArrayList<Mensagem> msgs;
	private ArrayList<Mensagem> historicoMsgs;
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
		
		File msgLog = new File(groupFolder.getAbsolutePath(), this.grupoID + "_" + "caixa" + ".txt");
		File membrosGrupo = new File(groupFolder.getAbsolutePath(), this.grupoID + "_" + "membros" + ".txt");
		File msgHistorico = new File(groupFolder.getAbsolutePath(), this.grupoID + "_" + "historico" + ".txt");
		try {
			groupFolder.mkdirs();
			msgLog.createNewFile();
			membrosGrupo.createNewFile();
			msgHistorico.createNewFile();
			groupContentsToFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public boolean isDono(Cliente cliente){
		return cliente.getUser().equals(dono.getUser());
	}

	public boolean pertenceGrupo(String cliente){
		for(int i = 0; i < membros.size(); ++i){
			if(membros.get(i).getUser().equals(cliente)){
				return true;
			}
		}
		return false;
	}

	public void addMembro(Cliente cliente){
		membros.add(cliente);
		groupContentsToFile();
		cliente.entrarEmGrupo(grupoID);
	}

	public void removeMembro(Cliente cliente){
		membros.remove(cliente);

		groupContentsToFile();

		cliente.sairDeGrupo(grupoID);


	}

	public String getGrupoID(){
		return this.grupoID;
	}

	public ArrayList<String> getMembros(){
		ArrayList<String> output = new ArrayList<String>();
		for(int i = 0; i < membros.size(); ++i){
			output.add(membros.get(i).getUser());
		}
		return output;
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

		File logGrupo = new File(groupFolder.getAbsolutePath(), this.grupoID + "_" + "caixa" + ".txt");
		try {
			BufferedWriter bW = new BufferedWriter(new FileWriter(logGrupo));
			// membros
			for (int i = 0; i < msgs.size(); i++) {
				bW.write(msgs.get(i).msgContentToFile());
				bW.newLine();
			}
			bW.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
