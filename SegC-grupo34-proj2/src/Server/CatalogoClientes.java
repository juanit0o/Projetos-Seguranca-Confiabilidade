package Server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Classe para objetos do tipo Server.Cliente, permitindo um acesso
 * mais facilitado aos mesmos atraves de um mapa.
 * @author Diogo Pinto 52763 
 * @author Francisco Ramalho 53472
 * @author Joao Funenga 53504
 */
public class CatalogoClientes {

	private HashMap<String, Cliente> mapClientes;
	
	// Para criar a diretoria com os ficheiros do servidor
	File fileDirectory = new File("data" + File.separator + "Server Files");
	
	// tenta criar esta nova diretoria caso nao exista
	boolean value = fileDirectory.mkdirs();
	
	// para o ficheiro allUsers dentro do Server Files com todos os users,nomes e passes
	File file = new File("data" + File.separator + "Server Files" + File.separator + "allUsers.txt");
	File photoFile = new File("data" + File.separator + "Server Files" + File.separator + "allPhotos.txt");

	/**
	 * Construtor da classe que incia um mapa onde os clientes
	 * sao guardados por chave userId e valor classe Cliente.
	 */
	public CatalogoClientes() {
		mapClientes = new HashMap<String, Cliente>();
		try {
			if (!file.createNewFile()) { // true- nao existe e cria allUsers || false: load do ficheiro
				System.out.println("User file loaded: " + file.getName());
				Scanner scReader = new Scanner(file);
				while (scReader.hasNextLine()) {
					String linha = scReader.nextLine();
					String[] aux = linha.split(":");
					Cliente newClient = new Cliente(aux[0], aux[2], aux[1]); // userID, nome real, pass
					newClient.carregarConta();
					mapClientes.put(aux[0], newClient);
				}
				scReader.close();
			} else {
				System.out.println("File created: " + file.getName());
			}
			if(!photoFile.createNewFile()) {
				System.out.println("File loaded: " + photoFile.getName());
			}else {
				System.out.println("File created: " + photoFile.getName());
			}
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	/**
	 * Metodo para adicionar um cliente ao catalogo de clientes.
	 * @param user - userId do cliente.
	 * @param pass - password do cliente.
	 * @param outStream - stream de escrita.
	 * @param inStream - stream de leitura.
	 */
	public void addClient(String user, PublicKey pubk, ObjectOutputStream outStream, ObjectInputStream inStream) {
		try {
			outStream.writeObject("What is your name?");
			String name = (String) inStream.readObject();
			Cliente cliente = new Cliente(user, pubk, name);
			mapClientes.put(user, cliente);
			BufferedWriter bW = new BufferedWriter(new FileWriter(file, true));
			bW.write(user + ":" + pubk);
			bW.newLine();
			bW.close();
			//TODO allusers tem de ser cifrado pelo servidor (guardado cifrado)
			
			// criar outro file por cliente (id.txt -> follow $ followers $ photos $ grupos
			// $ mensagensPler +
			// criar a diretoria para os personal files
			File clientFolder = new File("data" + File.separator + "Personal User Files" + File.separator + user);
			File photoFolder = new File("data" + File.separator + "Personal User Files" + File.separator + user + File.separator + "Photos");
			// tenta criar essa diretoria
			clientFolder.mkdirs();
			photoFolder.mkdirs();
			
			// para o ficheiro pesssoal por cliente
			File fileCliente = new File("data" + File.separator + "Personal User Files" + File.separator + user + File.separator + "info.txt");
			fileCliente.createNewFile();
			cliente.userContentsToFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Metodo devolve um objeto Cliente, com base no userId recebido.
	 * @param username - userId do cliente.
	 * @return objeto do tipo Cliente.
	 */
	public Cliente getCliente(String username) {
		return mapClientes.get(username);
	}

	/**
	 * Metodo que retorna se o user existe no catalogo de clientes.
	 * @param user - userId do cliente.
	 * @return true se user existe no catalogo de clientes, senao false.
	 */
	public boolean existeUser(String user) {
		return mapClientes.get(user) != null;
	}

	/**
	 * Metodo que retorna se a password do cliente esta correta.
	 * @param user - userId do cliente.
	 * @param password - password do cliente.
	 * @return True se a password e a correta, senao false.
	 */
	public boolean passCorreta(String user, String password) {
		return mapClientes.get(user).isPass(password);
	}
}
