package Server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Classe para objetos do tipo Server.Cliente, permitindo um acesso
 * mais facilitado aos mesmos atrav�s de um mapa.
 * @author Diogo Pinto 52763 
 * @author Francisco Ramalho 53472
 * @author Joao Funenga 53504
 */
public class CatalogoClientes {

	private HashMap<String, Cliente> mapClientes;
	// Para criar a diretoria com os ficheiros do servidor
	File fileDirectory = new File("data\\Server Files");
	// tenta criar esta nova diretoria caso nao exista
	boolean value = fileDirectory.mkdirs();
	// para o ficheiro allUsers dentro do Server Files com todos os users e passes
	File file = new File("data\\Server Files\\allUsers.txt");
	File photoFile = new File("data\\Server Files\\allPhotos.txt");

	/**
	 * Construtor da classe que incia um mapa onde os clientes
	 * s�o guardados por chave userId e valor classe Server.Cliente.
	 */
	public CatalogoClientes() {
		mapClientes = new HashMap<String, Cliente>();
		try {
			if (!file.createNewFile()) { // true- nao existe e cria false: nada ||| cria o ficheiro do allUsers
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
	 * M�todo para adicionar um cliente ao cat�logo de clientes.
	 * @param user - userId do cliente.
	 * @param pass - password do cliente.
	 * @param outStream - stream de escrita.
	 * @param inStream - stream de leitura.
	 */
	public void addClient(String user, String pass, ObjectOutputStream outStream, ObjectInputStream inStream) {
		try {
			outStream.writeObject("What is your name?");
			String name = (String) inStream.readObject();
			Cliente cliente = new Cliente(user, pass, name);
			mapClientes.put(user, cliente);
			BufferedWriter bW = new BufferedWriter(new FileWriter(file, true));
			bW.write(user + ":" + name + ":" + pass);
			bW.newLine();
			bW.close();
			// criar outro file por cliente (id.txt -> follow $ followers $ photos $ grupos
			// $ mensagensPler +
			// criar a diretoria para os personal files
			File clientFolder = new File("data\\Personal User Files\\" + user);
			File photoFolder = new File("data\\Personal User Files\\"+ user + "\\Photos");
			// tenta criar essa diretoria
			clientFolder.mkdirs();
			photoFolder.mkdirs();
			// para o ficheiro pesssoal por cliente
			File fileCliente = new File("data\\Personal User Files\\" + user + "\\info.txt");
			fileCliente.createNewFile(); // cria o ficheiro para o cliente
			cliente.userContentsToFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * M�todo devolve um objeto Server.Cliente, com base no userId recebido.
	 * @param username - userId do cliente.
	 * @return objeto do tipo Server.Cliente.
	 */
	public Cliente getCliente(String username) { //TODO: PUBLIC?
		return mapClientes.get(username);
	}

	/**
	 * M�todo que retorna se o user existe no cat�logo de clientes.
	 * @param user - userId do cliente.
	 * @return true se user existe no cat�logo de clientes, senao false.
	 */
	public boolean existeUser(String user) {
		return mapClientes.get(user) != null;
	}

	/**
	 * M�todo que retorna se a password do cliente est� correta.
	 * @param user - userId do cliente.
	 * @param password - password do cliente.
	 * @return True se a password � a correta, senao false.
	 */
	public boolean passCorreta(String user, String password) {
		return mapClientes.get(user).isPass(password);
	}
}
