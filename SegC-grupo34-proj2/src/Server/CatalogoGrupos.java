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
	private File groupFolder = new File("data" + File.separator + "Group Folder");
	private File groupFile = new File("data" + File.separator + "Server Files" + File.separator + "allGroups.cif");
	private String keyStoreFile;
	private String keyStorePassword;
	
	/**
	 * Construtor da classe que inicia uma lista onde os clientes
	 * sao guardados em grupos do tipo Server.Grupo, e no inicio do programa
	 * sao carregados todos os dados do disco para estruturas de dados para acesso mais rapido.
	 * @param catClientes - catalogo de clientes.
	 * @param keyStoreFile Path para o ficheiro keystore
	 * @param keyStorePassword Password para o ficheiro keystore
	 */
	public CatalogoGrupos(CatalogoClientes catClientes, String keyStoreFile, String keyStorePassword) {
		this.keyStoreFile = keyStoreFile;
		this.keyStorePassword = keyStorePassword;
		
		grupos = new ArrayList<Grupo>();
		this.catClientes = catClientes;
		groupFolder.mkdirs();
		Autenticacao aut = new Autenticacao();
		//carregar conteudo do ficheiro de grupos
		try {
			if(!groupFile.createNewFile()) {
				aut.decryptFile(groupFile, keyStoreFile, keyStorePassword);
				System.out.println("Group file loaded: " + groupFile.getName());
				
				File groupFileTxt = new File("data" + File.separator + "Server Files" + File.separator + "allGroups.txt");
				Scanner scReader = new Scanner(groupFileTxt);
				
				while (scReader.hasNextLine()) {
					
					//ver o nome de um grupo, ir a pasta desse grupo, ler o ficheiro de membros e 
					//mensagens para chamar o construtor do grupo
					//group id = cada linha tem o nome de um grupo
					String linha = scReader.nextLine();
					File fileGrupo = new File("data" + File.separator + "Group Folder" + File.separator + linha + File.separator +linha +"_membros.cif");
					
					
					aut.decryptFile(fileGrupo, keyStoreFile, keyStorePassword);
					File fileGrupoTXT = new File("data" + File.separator + "Group Folder" + File.separator + linha + File.separator +linha +"_membros.txt");
					
					
					BufferedReader rW = new BufferedReader(new FileReader(fileGrupoTXT));
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
					aut.encryptFile(fileGrupoTXT, keyStoreFile, keyStorePassword);
					
					
					File fileMsg= new File("data" + File.separator + "Group Folder" + File.separator + linha + File.separator + linha +"_caixa.cif");
					File fileAuxMsg;
					if(fileMsg.length() > 0) {
						aut.decryptFile(fileMsg, keyStoreFile, keyStorePassword);
						fileAuxMsg = new File("data" + File.separator + "Group Folder" + File.separator + linha + File.separator + linha +"_caixa.txt");
					}else {
						fileAuxMsg = new File("data" + File.separator + "Group Folder" + File.separator + linha + File.separator + linha +"_caixa.txt");
						fileAuxMsg.createNewFile();
					}
					
					BufferedReader rW2 = new BufferedReader(new FileReader(fileAuxMsg));
					ArrayList<Mensagem> msgs = new ArrayList<Mensagem>();
					while ((line = rW2.readLine()) != null) {
						String[] mensagem = line.split("%%");
						String[] ler = mensagem[3].split("//*//*");
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
						msgs.add(new Mensagem(linha, catClientes.getCliente(mensagem[1]), mensagem[2], listaLeu, listaPorLer, mensagem[0], Integer.parseInt(mensagem[4])));
					}
					rW2.close();
					aut.encryptFile(fileAuxMsg, keyStoreFile, keyStorePassword);
					System.out.println();
					
					//LOAD DA HISTORICO (SO ESTAO AS MENSAGENS LIDAS POR TODOS, SEM POR LER)
					File fileHist= new File("data" + File.separator + "Group Folder" + File.separator + linha + File.separator + linha + "_historico.cif");
					
					File fileAux;
					if(fileHist.length() > 0) {
						aut.decryptFile(fileHist, keyStoreFile, keyStorePassword);
						fileAux = new File("data" + File.separator + "Group Folder" + File.separator + linha + File.separator + linha + "_historico.txt");
					}else {
						fileAux = new File("data" + File.separator + "Group Folder" + File.separator + linha + File.separator + linha + "_historico.txt");
						fileAux.createNewFile();
					}							
					
					
					BufferedReader rW3 = new BufferedReader(new FileReader(fileAux));
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
						historico.add(new Mensagem(linha, catClientes.getCliente(mensagem[1]), mensagem[2], listaLeu, new ArrayList<Cliente>(), mensagem[0], Integer.parseInt(mensagem[4])));
					}
					rW3.close();
					grupos.add(new Grupo(linha, dono, clientes, msgs, historico, keyStoreFile, keyStorePassword));
					aut.encryptFile(fileAux, keyStoreFile, keyStorePassword);
				}	
				scReader.close();
				aut.encryptFile(groupFileTxt, keyStoreFile, keyStorePassword);
			}else {
				File fileAux = new File("data" + File.separator + "Server Files" + File.separator + "allGroups.txt");
				fileAux.createNewFile();
				aut.encryptFile(fileAux, keyStoreFile, keyStorePassword);
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
	 * @param keyStoreFile Path para o ficheiro keystore
	 * @param keyStorePassword Password para o ficheiro keystore
	 */
	public void addGrupo(String grupoID, Cliente dono, String keyStoreFile, String keyStorePassword) {
		Grupo grupo = new Grupo(grupoID, dono, keyStoreFile, keyStorePassword);
		grupo.registaGrupo();
		grupos.add(grupo);
		dono.entrarEmGrupo(grupoID, keyStoreFile,keyStorePassword);
		try {
			Autenticacao aut = new Autenticacao();
			aut.decryptFile(groupFile, keyStoreFile, keyStorePassword);
			File groupFileTxt = new File("data" + File.separator + "Server Files" + File.separator + "allGroups.txt");

			BufferedWriter bW = new BufferedWriter(new FileWriter(groupFileTxt, true));
			bW.write(grupoID);
			bW.newLine();
			bW.close();
			aut.encryptFile(groupFileTxt, keyStoreFile, keyStorePassword);
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
