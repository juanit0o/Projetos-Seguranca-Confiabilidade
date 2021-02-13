import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class CatalogoClientes {

	private HashMap<Integer, Cliente> mapClientes;
	//private File file = new File("allUsers.txt"); lixo
	
	//Para criar a diretoria com os ficheiros do servidor
	File fileDirectory = new File("..\\data\\Server Files");

    //tenta criar esta nova diretoria caso nao exista
	boolean value = fileDirectory.mkdirs();

	//para o ficheiro allUsers dentro do Server Files com todos os users e passes
	File file = new File (fileDirectory.getAbsolutePath(),"allUsers.txt");


	public CatalogoClientes() {		
		
		mapClientes = new HashMap<Integer, Cliente>();

		try {
			if (!file.createNewFile()) { //true- nao existe e cria   false: nada ||| cria o ficheiro do allUsers
				System.out.println("File loaded: " + file.getName());
				Scanner scReader = new Scanner(file);
				while (scReader.hasNextLine()) {
					String linha = scReader.nextLine();
					String[] aux = linha.split(":");
					
					Cliente newClient = new Cliente(aux[1], aux[2], Integer.parseInt(aux[0]));
					newClient.carregarConta();
					mapClientes.put(Integer.parseInt(aux[0]), newClient);
					
				}
				scReader.close();
			}else
				System.out.println("File created: " + file.getName());
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	public void addClient(String user, String pass) {
		Cliente cliente = new Cliente(user,pass,mapClientes.size()+1);
		mapClientes.put(mapClientes.size()+1, cliente);

		//Informaçao sobre o cliente no Server Files
		try {
			BufferedWriter bW = new BufferedWriter(new FileWriter(file, true));
			bW.write(mapClientes.size()+":"+user+":"+pass);
			bW.newLine();
			bW.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		//criar outro file por cliente (id.txt -> follow $ followers $ photos $ grupos $ mensagensPler +
		//criar a diretoria para os personal files
		File file = new File("..\\data\\Personal User Files\\" + mapClientes.size());

		// tenta criar essa diretoria
		boolean value = file.mkdirs();
		
		//para o ficheiro pesssoal por cliente
		File fileCliente = new File (file.getAbsolutePath(),"info.txt");
		
		try {
			fileCliente.createNewFile(); //cria o ficheiro para o cliente
			BufferedWriter bW = new BufferedWriter(new FileWriter(fileCliente, true));
			cliente.userContentsToFile();
			bW.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Cliente getCliente(String user){
		for(int i = 0; i < mapClientes.size(); ++i){
			Cliente client = mapClientes.get(mapClientes.keySet().toArray()[i]);
			if(client.getUser().equals(user)){
				return client;
			}
		}
		return null;	
		//return mapClientes.get(user);
	}

	public Cliente getCliente(int id){
		return mapClientes.get(id);
	}

	public boolean existeUser(String user) {
		for(int i = 0; i < mapClientes.size(); ++i){
			Cliente client = mapClientes.get(mapClientes.keySet().toArray()[i]);
			if(client.getUser().equals(user)){
				return true;
			}
		}
		return false;
		//return mapClientes.get(user) != null;
	}

	public boolean existeID(int ID) {
		return mapClientes.get(ID) != null;
	}

	public boolean passCorreta(String user, String password) {
		for(int i = 0; i < mapClientes.size(); ++i){
			Cliente client = mapClientes.get(mapClientes.keySet().toArray()[i]);
			if(client.getUser().equals(user)){
				return client.isPass(password);
			}
		}
		return false;
		//return mapClientes.get(user).isPass(password);
	}


}
