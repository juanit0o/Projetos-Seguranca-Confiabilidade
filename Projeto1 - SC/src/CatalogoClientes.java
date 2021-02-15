import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class CatalogoClientes {

	private HashMap<String, Cliente> mapClientes;
	// private File file = new File("allUsers.txt"); lixo

	// Para criar a diretoria com os ficheiros do servidor
	File fileDirectory = new File("..\\data\\Server Files");

	// tenta criar esta nova diretoria caso nao exista
	boolean value = fileDirectory.mkdirs();

	// para o ficheiro allUsers dentro do Server Files com todos os users e passes
	File file = new File(fileDirectory.getAbsolutePath(), "allUsers.txt");

	public CatalogoClientes() {

		mapClientes = new HashMap<String, Cliente>();

		try {
			if (!file.createNewFile()) { // true- nao existe e cria false: nada ||| cria o ficheiro do allUsers
				System.out.println("File loaded: " + file.getName());
				Scanner scReader = new Scanner(file);
				while (scReader.hasNextLine()) {
					String linha = scReader.nextLine();
					String[] aux = linha.split(":");

					Cliente newClient = new Cliente(aux[0], aux[2], aux[1]); // userID, nome real, pass
					newClient.carregarConta();
					mapClientes.put(aux[0], newClient);

				}
				scReader.close();
			} else
				System.out.println("File created: " + file.getName());
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	public void addClient(String user, String pass, Socket socket) {

		try {
			ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
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
			File file = new File("..\\data\\Personal User Files\\" + user);

			// tenta criar essa diretoria
			boolean value = file.mkdirs();

			// para o ficheiro pesssoal por cliente
			File fileCliente = new File(file.getAbsolutePath(), "info.txt");

			fileCliente.createNewFile(); // cria o ficheiro para o cliente
			cliente.userContentsToFile();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	public Cliente getCliente(String user) {
		for (int i = 0; i < mapClientes.size(); ++i) {
			Cliente client = mapClientes.get(mapClientes.keySet().toArray()[i]);
			if (client.getUser().equals(user)) {
				return client;
			}
		}
		return null;
	}*/

	public Cliente getCliente(String username) {
		return mapClientes.get(username);
	}

	public boolean existeUser(String user) {
		return mapClientes.get(user) != null;
	}


	public boolean passCorreta(String user, String password) {
		return mapClientes.get(user).isPass(password);
	}

}
