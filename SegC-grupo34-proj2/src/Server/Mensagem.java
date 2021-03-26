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
	private int chaveIdMsg;

	/**
	 * Construtor da classe que inicia uma mensagem recebendo um id de grupo, 
	 * Cliente remetente, a mensagem e uma lista de membros do grupo para
	 * que se possa guardar e verificar melhor quem leu ou nao leu. Este construtor
	 * e usado quando e criada um mensagem nova.
	 * @param grupoID - id do grupo
	 * @param remetente - quem envia a mensagem
	 * @param msg - mensagem a enviar
	 * @param listaGrupo - lista de Clientes membros do grupo com grupoID
	 * @param chaveIdMsg - indicador da chave utilizada para encriptar a mensagem
	 */
	public Mensagem(String grupoID, Cliente remetente, String msg, ArrayList<Cliente> listaGrupo, int chaveIdMsg) {
		this.grupoID = grupoID;
		this.remetente = remetente;
		this.msg = msg;
		this.porLerMsg = listaGrupo;
		this.chaveIdMsg = chaveIdMsg;
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
	 * @param listaLeuGrupo - lista de Clientes membros do grupo com grupoID que ja leram a mensagem
	 * @param listaPorLerGrupo - lista de Clientes membros do grupo com grupoID que ainda nao leram a mensagem
	 * @param data - data da mensagem
	 * @param chaveIdMsg - indicador da chave utilizada para encriptar a mensagem
	 */
	public Mensagem(String grupoID, Cliente remetente, String msg, ArrayList<Cliente> listaLeuGrupo, ArrayList<Cliente> listaPorLerGrupo, String data, int chaveIdMsg) {
		this.grupoID = grupoID;
		this.remetente = remetente;
		this.msg = msg;
		this.leuMsg = listaLeuGrupo;
		this.porLerMsg = listaPorLerGrupo;
		this.data = data;
		this.chaveIdMsg = chaveIdMsg;
	}

	/**
	 * Metodo le a mensagem atual pelo userId recebido.
	 * @param userId - id do cliente que le a mensagem.
	 * @param keystoreFile Path para o ficheiro keystore
	 * @param keystorePassword Password para o ficheiro keystore 
	 */
	private void lerMensagem(int userId, String keystoreFile, String keystorePassword){
		leuMsg.add(porLerMsg.get(userId));
		ArrayList <Cliente> porLerMsgAux = new ArrayList<Cliente>();
		for(int i = 0; i < porLerMsg.size(); ++i){
			if(i != userId)
				porLerMsgAux.add(porLerMsg.get(i));
		}
		porLerMsg = porLerMsgAux;
		//se foi lida por todos, colocar no historico
		if(porLerMsg.isEmpty()){
			moverMensagemParaHistorico(keystoreFile, keystorePassword);
		}
	}

	/**
	 * Metodo retorna se a mensagem atual esta por ser lida pelo userId recebido.
	 * @param cliente - id do cliente.
	 * @param keystoreFile Path para o ficheiro keystore
	 * @param keystorePassword Password para o ficheiro keystore
	 * @return true se a mensagem ainda nao foi lida pelo cliente, senao false.
	 */
	public boolean porLerMensagem(String cliente, String keystoreFile, String keystorePassword){
		int i;
		for(i = 0; i < porLerMsg.size(); ++i){
			if(porLerMsg.get(i).getUser().equals(cliente)){
				lerMensagem(i, keystoreFile, keystorePassword);
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
		return remetente.getUser() + ":" + msg + "$$" + chaveIdMsg;
	}

	/**
	 * Retorna se ja todos leram a msg
	 */
	public boolean jaLeramTodos(){
		return porLerMsg.size() <= 0;
	}

	/**
	 * Metodo coloca e guarda os dados da mensagem atual em disco.
	 * @param keystoreFile Path para o ficheiro keystore
	 * @param keystorePassword Password para o ficheiro keystore
	 */
	private void moverMensagemParaHistorico(String keystoreFile, String keystorePassword){
		
		File groupFolder = new File("data" + File.separator + "Group Folder" + File.separator + this.grupoID);		
		Autenticacao aut = new Autenticacao();
		File histCif = new File(groupFolder.getAbsolutePath(), this.grupoID + "_" + "historico" + ".cif");
		
		File logGrupo;
		if(histCif.length() > 0) {
			aut.decryptFile(histCif, keystoreFile, keystorePassword);
			logGrupo = new File(groupFolder.getAbsolutePath(), this.grupoID + "_" + "historico" + ".txt");
		}else {
			logGrupo = new File(groupFolder.getAbsolutePath(), this.grupoID + "_" + "historico" + ".txt");
			try {
				logGrupo.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
		//aut.decryptFile(histCif, keystoreFile, keystorePassword);
		
		
		try {
			BufferedWriter bW = new BufferedWriter(new FileWriter(logGrupo, true));
			String output = data + "%%" + remetente.getUser() + "%%" + msg + "%%";
			for(int i = 0; i < leuMsg.size(); ++i){
				output += leuMsg.get(i).getUser();
				if(i < leuMsg.size() - 1){
					output += "%";
				}
			}
			output+= "%%" + chaveIdMsg;
			bW.write(output);
			bW.newLine();
			bW.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		aut.encryptFile(logGrupo, keystoreFile, keystorePassword);
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
		output += "%%";
		output += chaveIdMsg;
		return output;
	}
}
