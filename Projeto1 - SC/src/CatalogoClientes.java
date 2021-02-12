import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class CatalogoClientes {

	private HashMap<String, Cliente> mapClientes;
	private File file = new File("users.txt");

	

	public CatalogoClientes() {		
		
		mapClientes = new HashMap<String, Cliente>();

		try {
			if (!file.createNewFile()) { //true- nao existe e cria   false: nada
				System.out.println("File created: " + file.getName()); //rever
				Scanner scReader = new Scanner(file);
				while (scReader.hasNextLine()) {
					String linha = scReader.nextLine();
					String[] aux = linha.split(":");
					mapClientes.put(aux[0], new Cliente(aux[0], aux[1]));
				}
				scReader.close();
			}
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	public void addClient(String user, String pass) {
		Cliente cliente = new Cliente(user,pass);
		mapClientes.put(user, cliente);
		try { //adicionamos ao users txt
			BufferedWriter bW = new BufferedWriter(new FileWriter(file, true));
			bW.write(user+":"+pass);
			bW.newLine();
			bW.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		//criar outro file por cliente (id.txt -> follow $ followers $ photos $ grupos $ mensagensPler +
		  //File file = new File("/data");
          //if (file.getParentFile() != null) {
          //    file.getParentFile().mkdirs();
          //}

		
		File fileCliente = new File (user +".txt");
		
		try {
			fileCliente.createNewFile();
			BufferedWriter bW = new BufferedWriter(new FileWriter(fileCliente, true));
			bW.write("$\n$\n$\n$\n+\n");
			//bW.newLine();
			bW.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean existeUser(String user) {
		return mapClientes.get(user) != null;
	}

	public boolean passCorreta(String user, String password) {
		return mapClientes.get(user).isPass(password);
	}


}
