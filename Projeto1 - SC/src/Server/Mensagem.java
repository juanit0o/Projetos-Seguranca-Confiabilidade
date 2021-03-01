package Server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Classe representativa de uma mensagem, que e composta pelo Id do grupo 
 * em que e enviada, data, remetente, mensagem, lista de Clientes que ainda 
 * nao leram a mensagem e lista de Clientes que ja leu a mensagem.
 * @author Diogo Pinto 52763 
 * @author Francisco Ramalho 53472
 * @author Joao Funenga 53504
 *
 */
public class Mensagem {

	private String grupoID; //p onde vai
	private String data;
	private Cliente remetente; //de onde vem
	private String msg;
	private ArrayList <Cliente> porLerMsg;
	private ArrayList <Cliente> leuMsg;

	/**
	 * Construtor da classe que inicia uma mensagem recebendo um id de grupo, 
	 * Cliente remetente, a mensagem e uma lista de membros do grupo para
	 * que se possa guardar e verificar melhor quem leu ou nao leu. Este construtor
	 * e usado quando e criada um mensagem nova.
	 * @param grupoID - id do grupo
	 * @param remetente - quem envia a mensagem
	 * @param msg - mensagem a enviar
	 * @param listaGrupo - lista de Clientes membros do grupo com grupoID
	 */
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

	/**
	 * Construtor da classe que imicia uma mensagem recebendo um id de grupo, 
	 * Cliente remetente, a mensagem e uma lista de membros do grupo para
	 * que se possa guardar e verificar melhor quem leu ou nao leu. Este construtor
	 * e usado quando e feito load dos ficheiros em disco.
	 * @param grupoID - id do grupo
	 * @param remetente - quem envia a mensagem
	 * @param msg - mensagem a enviar
	 * @param listaGrupo - lista de Clientes membros do grupo com grupoID
	 */
	public Mensagem(String grupoID, Cliente remetente, String msg, ArrayList<Cliente> listaLeuGrupo, ArrayList<Cliente> listaPorLerGrupo, String data) {
		this.grupoID = grupoID;
		this.remetente = remetente;
		this.msg = msg;
		this.leuMsg = listaLeuGrupo;
		this.porLerMsg = listaPorLerGrupo;
		this.data = data;
	}

	/**
	 * Metodo le a mensagem atual pelo userId recebido.
	 * @param userId - id do cliente que le a mensagem. 
	 */
	private void lerMensagem(int userId){
		leuMsg.add(porLerMsg.get(userId));
		ArrayList <Cliente> porLerMsgAux = new ArrayList<Cliente>();
		for(int i = 0; i < porLerMsg.size(); ++i){
			if(i != userId)
				porLerMsgAux.add(porLerMsg.get(i));
		}
		porLerMsg = porLerMsgAux;
		//se foi lida por todos, colocar no historico
		if(porLerMsg.isEmpty()){
			moverMensagemParaHistorico();
		}
	}

	/**
	 * Metodo retorna se a mensagem atual esta por ser lida pelo userId recebido.
	 * @param cliente - id do cliente.
	 * @return true se a mensagem ainda nao foi lida pelo cliente, senao false.
	 */
	public boolean porLerMensagem(String cliente){
		int i;
		for(i = 0; i < porLerMsg.size(); ++i){
			if(porLerMsg.get(i).getUser().equals(cliente)){
				lerMensagem(i);
				return true;
			}
		}
		return false;
	}

	/**
	 * Metodo retorna se a mensagem atual foi lida pelo userId recebido.
	 * @param cliente - id do cliente.
	 * @return true se a mensagem foi lida pelo cliente, senao false.
	 */
	public boolean jaLeuMensagem(String cliente){
		int i;
		for(i = 0; i < leuMsg.size(); ++i){
			if(leuMsg.get(i).getUser().equals(cliente)){
				return true;
			}
		}
		return false;
	}

	/**
	 * Metodo representativo textual de mensagem.
	 */
	public String toString(){
		return remetente.getUser() + " : " + msg;
	}

	/**
	 * Metodo coloca e guarda os dados da mensagem atual em disco.
	 */
	private void moverMensagemParaHistorico(){
		File groupFolder = new File("data\\Group Folder\\" + this.grupoID);
		File logGrupo = new File(groupFolder.getAbsolutePath(), this.grupoID + "_" + "historico" + ".txt");
		try {
			BufferedWriter bW = new BufferedWriter(new FileWriter(logGrupo, true));
			String output = data + "%%" + remetente.getUser() + "%%" + msg + "%%";
			for(int i = 0; i < leuMsg.size(); ++i){
				output += leuMsg.get(i).getUser();
				if(i < leuMsg.size() - 1){
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

	/**
	 * Metodo devolve informacao de mensagem a ser guardada em disco, em formato de string.
	 * @return string com conteudo a ser guardado em disco
	 */
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
		output += "**";
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
