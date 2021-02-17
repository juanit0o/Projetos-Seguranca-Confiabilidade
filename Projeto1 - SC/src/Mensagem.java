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
	public Mensagem(String grupoID, Cliente remetente, String msg, ArrayList<Cliente> listaLeuGrupo, ArrayList<Cliente> listaPorLerGrupo, String data) {
		this.grupoID = grupoID;
		this.remetente = remetente;
		this.msg = msg;
		this.leuMsg = listaLeuGrupo;
		this.porLerMsg = listaPorLerGrupo;
		this.data = data;
	}


	public void lerMensagem(int id, String cliente){

		leuMsg.add(porLerMsg.get(id));

		ArrayList <Cliente> porLerMsgAux = new ArrayList<Cliente>();
		for(int i = 0; i < porLerMsg.size(); ++i){
			if(i != id)
				porLerMsgAux.add(porLerMsg.get(i));
		}
		porLerMsg = porLerMsgAux;

		//VERIFICA SE JÁ FOI LIDA POR TODOS E MANDA PARA HISTORICO
		if(porLerMsg.isEmpty()){
			moverMensagemParaHistorico();
		}
	}

	public boolean porLerMensagem(String cliente){
		int i;
		for(i = 0; i < porLerMsg.size(); ++i){
			if(porLerMsg.get(i).getUser().equals(cliente)){
				lerMensagem(i, cliente);
				return true;
			}
		}
		return false;
	}

	public boolean jaLeuMensagem(String cliente){
		int i;
		for(i = 0; i < leuMsg.size(); ++i){
			if(leuMsg.get(i).getUser().equals(cliente)){
				return true;
			}
		}
		return false;
	}

	public String toString(){
		return remetente.getUser() + " : " + msg;
	}

	private void moverMensagemParaHistorico(){

		File groupFolder = new File("..\\data\\Group Folder\\" + this.grupoID);
		File logGrupo = new File(groupFolder.getAbsolutePath(), this.grupoID + "_" + "historico" + ".txt");
		try {
			BufferedWriter bW = new BufferedWriter(new FileWriter(logGrupo, true));
			String output = data + "%%" + remetente.getUser() + "%%" + msg + "%%";
			for(int i = 0; i < leuMsg.size(); ++i){
				output += leuMsg.get(i).getUser();
				if(i < porLerMsg.size() - 1){
					output += "%";
				}
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

		output += "%%";

		//remetente
		output += remetente.getUser();
		output += "%%";

		//Mensagem
		output += msg;
		output += "%%";

		//Membros por ler
		for(int i = 0; i < porLerMsg.size(); ++i){
			output += porLerMsg.get(i).getUser();
			if(i < porLerMsg.size() - 1){
				output += "%";
			}
		}
		output += "%%";

		//Membros que ja leram
		for(int i = 0; i < leuMsg.size(); ++i){
			output += leuMsg.get(i).getUser();
			if(i < leuMsg.size() - 1){
				output += "%";
			}
		}

		return output;
	}
	
}
