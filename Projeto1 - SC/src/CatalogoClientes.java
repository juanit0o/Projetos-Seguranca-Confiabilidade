import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class CatalogoClientes {

	private ArrayList<Cliente> listaClientes;
	//private HashMap<String, String> catalogoClientes;
	private String ficheiro = "users.txt";

	public CatalogoClientes() {
		//catalogoClientes = new HashMap<String, String>();
		listaClientes = new ArrayList<Cliente>();

		//para ler ficheiro
		File file = new File(ficheiro);
		try {
			
			Scanner scReader = new Scanner(file);
			while (scReader.hasNextLine()) {
				String linha = scReader.nextLine();
				System.out.println(linha);
				
			}
			scReader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


}
