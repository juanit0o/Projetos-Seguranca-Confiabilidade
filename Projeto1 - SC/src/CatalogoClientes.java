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

		try {/*
			if (file.createNewFile()) {
				System.out.println("File created: " + file.getName());
				Scanner scReader = new Scanner(file);
				while (scReader.hasNextLine()) {
					String linha = scReader.nextLine();
					System.out.println(linha);
					String[] aux = linha.split(":");
					this.listaClientes.add(new Cliente(aux[0], aux[1]));
				}
				//scReader.close();
			} else {
				System.out.println("File already exists.");
				Scanner scReader = new Scanner(file);
				while (scReader.hasNextLine()) {
					String linha = scReader.nextLine();
					System.out.println(linha);
					String[] aux = linha.split(":");
					this.listaClientes.add(new Cliente(aux[0], aux[1]));
				}
			}*/
			if (!file.createNewFile()) { //true- nao existe e cria   false: nada
				System.out.println("File created: " + file.getName());
				Scanner scReader = new Scanner(file);
				while (scReader.hasNextLine()) {
					String linha = scReader.nextLine();
					System.out.println(linha);
					String[] aux = linha.split(":");
					mapClientes.put(aux[0], new Cliente(aux[0], aux[1]));
				}
				scReader.close();
			}
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}



		//para ler ficheiro


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

	}

	
	
	public boolean existeUser(String user) {
		return mapClientes.get(user) != null;
	}

	public boolean passCorreta(String user, String password) {
		return mapClientes.get(user).isPass(password);
	}


}
