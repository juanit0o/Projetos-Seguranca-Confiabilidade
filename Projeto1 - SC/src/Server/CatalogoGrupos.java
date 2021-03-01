package Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Classe para objetos do tipo Grupos, permitindo um acesso 
 * mais facilitado aos mesmos. Composta por um catalogo de 
 * Clientes e por uma lista de grupos que contem.
 * @author Diogo Pinto 52763 
 * @author Francisco Ramalho 53472
 * @author Joao Funenga 53504
 *
 */
public class CatalogoGrupos {
	
	private CatalogoClientes catClientes;
	private ArrayList<Grupo> grupos;
	private File groupFolder = new File("data\\Group Folder");
	private File groupFile = new File("data\\Server Files\\allGroups.txt");
	
	/**
	 * Construtor da classe que inicia uma lista onde os clientes
	 * sao guardados em grupos do tipo Server.Grupo, e no inicio do programa
	 * sao carregados todos os dados do disco para estruturas de dados para acesso mais rapido.
	 * @param catClientes - catalogo de clientes.
	 */
	public CatalogoGrupos(CatalogoClientes catClientes) {
		grupos = new ArrayList<Grupo>();
		this.catClientes = catClientes;
		groupFolder.mkdirs();
		//carregar conteudo do ficheiro de grupos
		try {
			if(!groupFile.createNewFile()) {
				System.out.println("Group file loaded: " + groupFile.getName());
				Scanner scReader = new Scanner(groupFile);
				while (scReader.hasNextLine()) {
					//ver o nome de um grupo, ir a pasta desse grupo, ler o ficheiro de membros e 
					//mensagens para chamar o construtor do grupo
					//group id = cada linha tem o nome de um grupo
					String linha = scReader.nextLine();
					File fileGrupo = new File("data\\Group Folder\\" + linha + "\\"+linha +"_membros.txt");
					BufferedReader rW = new BufferedReader(new FileReader(fileGrupo));
					String line = rW.readLine();
					//Apanhar o dono
					Cliente dono = catClientes.getCliente(line);
					ArrayList<Cliente> clientes = new ArrayList<Cliente>();
					clientes.add(dono);
					//Apanhar os membros
					while ((line = rW.readLine()) != null) {
						clientes.add(catClientes.getCliente(line));
					}
					rW.close();

					File fileMsg= new File("data\\Group Folder\\" + linha + "\\"+linha +"_caixa.txt");
					BufferedReader rW2 = new BufferedReader(new FileReader(fileMsg));
					ArrayList<Mensagem> msgs = new ArrayList<Mensagem>();
					while ((line = rW2.readLine()) != null) {
						String[] mensagem = line.split("%%");
						String[] ler = mensagem[3].split("\\*\\*");
						ArrayList<Cliente> listaPorLer = new ArrayList<Cliente>();
						String[] listaPorLerDeClientes = ler[0].split("%");
						if(!listaPorLerDeClientes[0].equals("")){
							for(int i = 0; i < listaPorLerDeClientes.length; ++i){
								listaPorLer.add(catClientes.getCliente(listaPorLerDeClientes[i]));
							}
						}
						ArrayList<Cliente> listaLeu = new ArrayList<Cliente>();
						if(ler.length >= 2){
							String[] listaLeuClientes = ler[1].split("%");
							if(!listaLeuClientes[0].equals("")) {
								for (int i = 0; i < listaLeuClientes.length; ++i) {
									listaLeu.add(catClientes.getCliente(listaLeuClientes[i]));
								}
							}
						}
						//grupoID, remetente, msg, listagrupo, data
						msgs.add(new Mensagem(linha, catClientes.getCliente(mensagem[1]), mensagem[2], listaLeu, listaPorLer, mensagem[0]));
					}
					rW2.close();
					System.out.println();
					//LOAD DA HISTORICO (SO ESTAO AS MENSAGENS LIDAS POR TODOS, SEM POR LER)
					File fileHist= new File("data\\Group Folder\\" + linha + "\\"+linha +"_historico.txt");
					BufferedReader rW3 = new BufferedReader(new FileReader(fileHist));
					ArrayList<Mensagem> historico = new ArrayList<Mensagem>();
					//Enquanto houver historico para ler
					while ((line = rW3.readLine()) != null) {
						String[] mensagem = line.split("%%");
						ArrayList<Cliente> listaLeu = new ArrayList<Cliente>();
						String[] listaLeuClientes = mensagem[3].split("%");
						if(!listaLeuClientes[0].equals("")) {
							for (int i = 0; i < listaLeuClientes.length; ++i) {
								listaLeu.add(catClientes.getCliente(listaLeuClientes[i]));
							}
						}
						historico.add(new Mensagem(linha, catClientes.getCliente(mensagem[1]), mensagem[2], listaLeu, new ArrayList<Cliente>(), mensagem[0]));
					}
					rW3.close();
					grupos.add(new Grupo(linha, dono, clientes, msgs, historico));
				}
				scReader.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Metodo que adiciona um grupo ao catalogo de grupos com 
	 * um id de grupo e quem cria o grupo, que sera o dono.
	 * @param grupoID - id para o grupo.
	 * @param dono - cliente que cria e a dono do grupo.
	 */
	public void addGrupo(String grupoID, Cliente dono) {
		Grupo grupo = new Grupo(grupoID, dono);
		grupo.registaGrupo();
		grupos.add(grupo);
		dono.entrarEmGrupo(grupoID);
		try {
			BufferedWriter bW = new BufferedWriter(new FileWriter(groupFile, true));
			bW.write(grupoID);
			bW.newLine();
			bW.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Metodo que adiciona um cliente a um grupo.
	 * @param cliente - userId do cliente a adicionar.
	 * @param groupID - id do grupo.
	 */
	public void addMembro(String cliente, String groupID){
		getGrupo(groupID).addMembro(catClientes.getCliente(cliente));
	}

	/**
	 * Metodo que remove um cliente de um grupo.
	 * @param cliente - userId do cliente a remover.
	 * @param groupID - id do grupo.
	 */
	public void removeMembro(String cliente, String groupID){
		getGrupo(groupID).removeMembro(catClientes.getCliente(cliente));
	}

	/**
	 * Metodo que retorna um grupo atraves do id de grupo.
	 * @param groupID - id do grupo.
	 * @return Se existe, Grupo com groupId, senao null.
	 */
	private Grupo getGrupo(String groupID){
		for(int i = 0; i < grupos.size(); ++i){
			if(grupos.get(i).getGrupoID().equals(groupID)){
				return grupos.get(i);
			}
		}
		return null;
	}

	/**
	 * Metodo que retorna se um grupo existe.
	 * @param groupID - id do grupo.
	 * @return true se o grupo existe, senao false.
	 */
	public boolean existeGrupo(String groupID){
		for(int i = 0; i < grupos.size(); ++i){
			if(grupos.get(i).getGrupoID().equals(groupID)){
				return true;
			}
		}
		return false;
	}

	/**
	 * Metodo que retorna se um cliente pertence a um grupo.
	 * @param cliente - userId do cliente.
	 * @param groupID - groupId do grupo.
	 * @return true se pertencer, senao false.
	 */
	public boolean pertenceAoGrupo(String cliente, String groupID) {
		return getGrupo(groupID).pertenceGrupo(cliente);
	}

	/**
	 * Metodo que retorna se um cliente e dono de um grupo.
	 * @param cliente - userId do cliente.
	 * @param groupID - groupId do grupo.
	 * @return true se cliente e dono, senao false.
	 */
	public boolean isDono(Cliente cliente, String groupID) {
		return getGrupo(groupID).isDono(cliente);
	}

	/**
	 * Metodo que retorna uma lista de membros pertencentes a um grupo.
	 * @param groupID - groupId do grupo. 
	 * @return lista de userId's dos membros do grupo.
	 */
	public ArrayList<String> getMembros(String groupID){
		return getGrupo(groupID).getMembros();
	}

	/**
	 * Metodo que guarda uma mensagem enviada por um cliente num dado grupo.
	 * @param groupID - groupId do grupo.
	 * @param msg - mensagem do cliente.
	 * @param cliente - Cliente que envia a mensagem.
	 */
	public void guardarMensagem(String groupID, String msg, Cliente cliente){
		getGrupo(groupID).guardarMensagem(msg, cliente);
	}

	/**
	 * Metodo que devolve uma lista das mensagens por ler de um cliente,
	 * num grupo.
	 * @param grupoID - groupId do grupo.
	 * @param cliente - Server.Cliente a ver mensagens.
	 * @return lista de mensagens por ler no grupo.
	 */
	public ArrayList<String> getMensagensPorLer(String grupoID, Cliente cliente){
		return getGrupo(grupoID).getMensagensPorLer(cliente.getUser());
	}

	/**
	 * Metodo que retorna uma lista das mensagens lidas de um cliente,
	 * num grupo.
	 * @param grupoID - groupId do grupo.
	 * @param cliente - Server.Cliente a ver mensagens.
	 * @return lista de mensagens lidas no grupo.
	 */
	public ArrayList<String> getMensagensJaLidas(String grupoID, Cliente cliente){
		return getGrupo(grupoID).getMensagensJaLidas(cliente.getUser());
	}
}
