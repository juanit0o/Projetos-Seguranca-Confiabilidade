import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class CatalogoClientes {

	private ArrayList<Cliente> listaClientes;
	//private HashMap<String, String> catalogoClientes;
	private File file = new File("users.txt");

	public CatalogoClientes() {
		//catalogoClientes = new HashMap<String, String>();
		listaClientes = new ArrayList<Cliente>();

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
					this.listaClientes.add(new Cliente(aux[0], aux[1]));

				}
				scReader.close();
			}
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}



		//para ler ficheiro


	}

	public void addClient(Cliente c) {
		this.listaClientes.add(c);
		try {
			Scanner sc = new Scanner(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public ArrayList<Cliente> getListaClientes() {
		return listaClientes;
	}



	public boolean existeUser(String user) {
		// TODO percorrer client ids e compara se ja existe
		return false;
	}

	public Boolean passCorreta(String user, String password) {
		// TODO percorrer client ids e compara se ja existe
		return null;
	}


}
