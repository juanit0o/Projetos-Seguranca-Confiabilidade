import com.sun.xml.internal.ws.client.ClientTransportException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Mensagem {

	private String grupoID; //p onde vai
	private String data;
	private Cliente remetente; //de onde vem
	private String msg;
	private ArrayList <Cliente> porLerMsg;
	private ArrayList <Cliente> leuMsg;

	//USAR ESTE CONSTRUTOR QUANDO É CRIADA UMA MENSAGEM NOVA
	public Mensagem(String grupoID, Cliente remetente, String msg, ArrayList<Cliente> listaGrupo) {
		this.grupoID = grupoID;
		this.remetente = remetente;
		this.msg = msg;
		this.porLerMsg = listaGrupo;
		this.leuMsg = new ArrayList<Cliente>();

		LocalDateTime myDateObj = LocalDateTime.now();
		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		this.data = myDateObj.format(myFormatObj);
	}

	//USAR ESTE CONSTRUTOR QUANDO DA LOAD DOS FICHEIROS
	public Mensagem(String grupoID, Cliente remetente, String msg, ArrayList<Cliente> listaGrupo, String data) {
		this.grupoID = grupoID;
		this.remetente = remetente;
		this.msg = msg;
		this.leuMsg = listaGrupo;
		this.data = data;
	}


	public void lerMensagem(Cliente leitor){
		if(porLerMsg.contains(leitor)){
			porLerMsg.remove(leitor);
			leuMsg.add(leitor);

			//VERIFICA SE JÁ FOI LIDA POR TODOS E MANDA PARA HISTORICO
			if(porLerMsg.isEmpty()){
				moverMensagemParaHistorico();
			}
		}
	}

	private void moverMensagemParaHistorico(){

		File groupFolder = new File("..\\data\\Group Folder\\" + this.grupoID);
		File logGrupo = new File(groupFolder.getAbsolutePath(), this.grupoID + "_" + "historico" + ".txt");
		try {
			BufferedWriter bW = new BufferedWriter(new FileWriter(logGrupo, true));
			String output = data + "§§" + remetente + "§§" + msg + "§§";
			for(int i = 0; i < leuMsg.size(); ++i){
				output += leuMsg.get(i).getUser() + "§";
			}
			bW.write(output);
			bW.newLine();
			bW.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String msgContentToFile(){

		String output = "";

		//DATA ATUAL
		output += data;

		output += "§§";

		//remetente
		output += remetente.getName();
		output += "§§";

		//Mensagem
		output += msg;
		output += "§§";

		//Membros por ler
		for(int i = 0; i < porLerMsg.size(); ++i){
			output += porLerMsg.get(i).getUser() + "§";
		}
		output += "§§";

		//Membros por ler
		for(int i = 0; i < leuMsg.size(); ++i){
			output += leuMsg.get(i).getUser() + "§";
		}

		return output;
	}
	
}
