import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class CatalogoGrupos {

	private CatalogoClientes catClientes;

	private ArrayList<Grupo> grupos;
	private File groupFolder = new File("..\\data\\Group Folder");
	private File serverFolder = new File("..\\data\\Server Files");
	private File groupFile = new File(serverFolder.getAbsolutePath(), "allGroups.txt");
	
	
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
					//ver o nome de um grupo, ir � pasta desse grupo, ler o ficheiro de membros e 
					//mensagens para chamar o construtor do grupo
					
					//group id = cada linha tem o nome de um grupo
					String linha = scReader.nextLine();
					File fileGrupo = new File("..\\data\\Group Folder\\" + linha + "\\"+linha +"_membros.txt");
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

					//TODO
					//DA NULLPOINTER
					//FALHA NA LEITURA DOS DADOS APOS RENICIAR O SV


					//LOAD DA CAIXA
					File fileMsg= new File("..\\data\\Group Folder\\" + linha + "\\"+linha +"_caixa.txt");
					BufferedReader rW2 = new BufferedReader(new FileReader(fileMsg));
					String line2 = rW2.readLine();
					ArrayList<Mensagem> msgs = new ArrayList<Mensagem>();

					while ((line = rW2.readLine()) != null) {
						String[] mensagem = line.split("%%");

						String[] ler = mensagem[3].split("\\*\\*");

						if(ler.length == 2){
							ArrayList<Cliente> listaPorLer = new ArrayList<Cliente>();
							String[] listaPorLerDeClientes = mensagem[3].split("%");
							for(int i = 0; i < listaPorLerDeClientes.length; ++i){
								listaPorLer.add(catClientes.getCliente(listaPorLerDeClientes[i]));
							}

							ArrayList<Cliente> listaLeu = new ArrayList<Cliente>();
							String[] listaLeuClientes = mensagem[3].split("%");
							for(int i = 0; i < listaLeuClientes.length; ++i){
								listaLeu.add(catClientes.getCliente(listaLeuClientes[i]));
							}

							//grupoID, remetente, msg, listagrupo, data
							msgs.add(new Mensagem(linha, catClientes.getCliente(mensagem[1]), mensagem[2], listaLeu, listaPorLer, mensagem[0]));

						} else {
							if(mensagem[3].substring(0,2).equals("\\*\\*")){
								ArrayList<Cliente> listaLeu = new ArrayList<Cliente>();
								String[] listaLeuClientes = mensagem[3].split("%");
								for(int i = 0; i < listaLeuClientes.length; ++i){
									listaLeu.add(catClientes.getCliente(listaLeuClientes[i]));
								}

								//grupoID, remetente, msg, listagrupo, data
								msgs.add(new Mensagem(linha, catClientes.getCliente(mensagem[1]), mensagem[2], listaLeu, new ArrayList<Cliente>(), mensagem[0]));

							} else {
								ArrayList<Cliente> listaPorLer = new ArrayList<Cliente>();
								String[] listaPorLerDeClientes = mensagem[3].split("%");
								for(int i = 0; i < listaPorLerDeClientes.length; ++i){
									listaPorLer.add(catClientes.getCliente(listaPorLerDeClientes[i]));
								}
								msgs.add(new Mensagem(linha, catClientes.getCliente(mensagem[1]), mensagem[2], new ArrayList<Cliente>(), listaPorLer, mensagem[0]));


							}

						}


						}

					rW2.close();
/*
					File fileMsg= new File("..\\data\\Group Folder\\" + linha + "\\"+linha +"_caixa.txt");
					BufferedReader rW2 = new BufferedReader(new FileReader(fileMsg));
					String line2 = rW2.readLine();*/
					ArrayList<Mensagem> historico = new ArrayList<Mensagem>();


					grupos.add(new Grupo(linha, dono, clientes, msgs, historico));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void addMembro(String cliente, String groupID){

		getGrupo(groupID).addMembro(catClientes.getCliente(cliente));

	}

	public void removeMembro(String cliente, String groupID){

		getGrupo(groupID).removeMembro(catClientes.getCliente(cliente));

	}

	private Grupo getGrupo(String groupID){
		for(int i = 0; i < grupos.size(); ++i){
			if(grupos.get(i).getGrupoID().equals(groupID)){
				return grupos.get(i);
			}
		}
		return null;
	}

	public boolean existeGrupo(String groupID){
		for(int i = 0; i < grupos.size(); ++i){
			if(grupos.get(i).getGrupoID().equals(groupID)){
				return true;
			}
		}
		return false;
	}

	public boolean pertenceAoGrupo(String cliente, String groupID) {
		return getGrupo(groupID).pertenceGrupo(cliente);
	}

	public boolean isDono(Cliente cliente, String groupID) {
		return getGrupo(groupID).isDono(cliente);
	}

	public ArrayList<String> getMembros(String groupID){
		return getGrupo(groupID).getMembros();
	}

	public void guardarMensagem(String groupID, String msg, Cliente cliente){
		getGrupo(groupID).guardarMensagem(msg, cliente);
	}

	public ArrayList<String> getMensagensPorLer(String grupoID, Cliente cliente){
		return getGrupo(grupoID).getMensagensPorLer(cliente.getUser());

	}

	public ArrayList<String> getMensagensJaLidas(String grupoID, Cliente cliente){
		return getGrupo(grupoID).getMensagensJaLidas(cliente.getUser());

	}
}
