package Server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Classe representativa de um grupo, que e composto por um id,
 * lista de clientes membros, lista de mensagens, lista de mensagens
 *  em historico, cliente dono do grupo, ficheiro de grupo, log de 
 *  mensagens, ficheiro de membros e ficheiro de historico mensagens.
 * @author Diogo Pinto 52763 
 * @author Francisco Ramalho 53472
 * @author Joao Funenga 53504
 *
 */
public class Grupo {

	private String grupoID;
	private ArrayList<Cliente> membros;
	private ArrayList<Mensagem> msgs;
	private ArrayList<Mensagem> historicoMsgs;
	private Cliente dono; // quem cria o grupo
	private File groupFolder;
	private File msgLog;
	private File membrosGrupo;
	private File msgHistorico;

	/**
	 * Construtor da classe que inicia um grupo recebendo um id 
	 * de grupo e cliente dono. 
	 * @param grupoID - id de grupo.
	 * @param dono - cliente dono do grupo.
	 */
	public Grupo(String grupoID, Cliente dono) {
		this.grupoID = grupoID;
		this.dono = dono;
		this.membros = new ArrayList<Cliente>();
		membros.add(dono);
		this.msgs = new ArrayList<Mensagem>();
		this.historicoMsgs = new ArrayList<Mensagem>();
		this.groupFolder = new File("data//Group Folder//" + this.grupoID);
		this.msgLog = new File(groupFolder.getAbsolutePath(), this.grupoID + "_" + "caixa" + ".txt");
		this.membrosGrupo = new File(groupFolder.getAbsolutePath(), this.grupoID + "_" + "membros" + ".txt");
		this.msgHistorico = new File(groupFolder.getAbsolutePath(), this.grupoID + "_" + "historico" + ".txt");

	}

	/**
	 * Construtor da classe que inicia um grupo recebendo um id 
	 * de grupo e cliente dono. Recebendo tambem listas de membros, 
	 * mensagens e historico.
	 * @param grupoID - id de grupo.
	 * @param dono - cliente dono do grupo.
	 * @param membros
	 * @param msgs
	 * @param historico
	 */
	public Grupo(String grupoID, Cliente dono, ArrayList<Cliente> membros, ArrayList<Mensagem> msgs, ArrayList<Mensagem> historico) {
		this.grupoID = grupoID;
		this.dono = dono;
		this.membros = membros;
		this.msgs = msgs;
		this.historicoMsgs = historico;
		this.groupFolder = new File("data//Group Folder//" + this.grupoID);
		this.msgLog = new File(groupFolder.getAbsolutePath(), this.grupoID + "_" + "caixa" + ".txt");
		this.membrosGrupo = new File(groupFolder.getAbsolutePath(), this.grupoID + "_" + "membros" + ".txt");
		this.msgHistorico = new File(groupFolder.getAbsolutePath(), this.grupoID + "_" + "historico" + ".txt");

	}

	/**
	 * Metodo que regista um grupo em disco.
	 */
	public void registaGrupo() {
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

	/**
	 * Metodo que recebe um cliente e verifica se este e dono do grupo.
	 * @param cliente - cliente
	 * @return true se cliente e dono do grupo, senao false.
	 */
	public boolean isDono(Cliente cliente){
		return cliente.getUser().equals(dono.getUser());
	}

	/**
	 * Metodo que identifica se um cliente recebido pertence ao grupo.
	 * @param cliente - cliente
	 * @return true se cliente pertence ao grupo, senao false.
	 */
	public boolean pertenceGrupo(String cliente){
		for(int i = 0; i < membros.size(); ++i){
			if(membros.get(i).getUser().equals(cliente)){
				return true;
			}
		}
		return false;
	}

	/**
	 * Metodo que adiciona um cliente ao grupo atual.
	 * @param cliente - cliente
	 */
	public void addMembro(Cliente cliente){
		membros.add(cliente);
		groupContentsToFile();
		cliente.entrarEmGrupo(grupoID);
	}

	/**
	 * Metodo que remove um cliente do grupo atual.
	 * @param cliente - cliente
	 */
	public void removeMembro(Cliente cliente){
		membros.remove(cliente);
		groupContentsToFile();
		cliente.sairDeGrupo(grupoID);
	}

	/**
	 * Metodo que guarda mensagem enviada de um cliente em listas 
	 * e em disco.
	 * @param msg - mensagem enviada
	 * @param cliente - cliente
	 */
	public void guardarMensagem(String msg, Cliente cliente){
		ArrayList<Cliente> membAux = new ArrayList<Cliente>();
		for(int i = 0; i < membros.size(); ++i){
			membAux.add(membros.get(i));
		}
		msgs.add(new Mensagem(this.grupoID, cliente, msg, membAux));
		groupContentsToFile();
	}

	/**
	 * Metodo que devolve o Id do grupo atual.
	 * @return groupID
	 */
	public String getGrupoID(){
		return this.grupoID;
	}

	/**
	 * Metodo devolve lista de id's dos membros do grupo.
	 * @return lista de id's de membros.
	 */
	public ArrayList<String> getMembros(){
		ArrayList<String> output = new ArrayList<String>();
		for(int i = 0; i < membros.size(); ++i){
			output.add(membros.get(i).getUser());
		}
		return output;
	}

	/**
	 * Metodo devolve as mensagens por ler de um determinado cliente dentro 
	 * do grupo.
	 * @param cliente - cliente a verificar
	 * @return lista de mensagens por ler
	 */
	public ArrayList<String> getMensagensPorLer(String cliente){
		ArrayList<String> output = new ArrayList<String>();
		for(int i = 0; i < msgs.size(); ++i){
			if(msgs.get(i).porLerMensagem(cliente)){
				output.add(msgs.get(i).toString());
			}
		}
		groupContentsToFile();
		return output;
	}

	/**
	 * Metodo devolve as mensagens lidas de um determinado cliente dentro 
	 * do grupo.
	 * @param cliente - cliente a verificar
	 * @return lista de mensagens lidas
	 */
	public ArrayList<String> getMensagensJaLidas(String cliente){
		ArrayList<String> output = new ArrayList<String>();
		//FICHEIRO HISTORICO
		for(int i = 0; i < historicoMsgs.size(); ++i){
			if(historicoMsgs.get(i).jaLeuMensagem(cliente)){
				output.add(historicoMsgs.get(i).toString());
			}
		}
		//FICHEIRO CAIXA
		for(int i = 0; i < msgs.size(); ++i){
			if(msgs.get(i).jaLeuMensagem(cliente)){
				output.add(msgs.get(i).toString());
			}
		}
		return output;
	}

	/**
	 * Metodo que escreve os dados de um grupo em disco.
	 */
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
		File caixaMsgGrupo = new File(groupFolder.getAbsolutePath(), this.grupoID + "_" + "caixa" + ".txt");
		try {
			BufferedWriter bW = new BufferedWriter(new FileWriter(caixaMsgGrupo));
			//MENSAGEM COM ELEMENTOS
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
