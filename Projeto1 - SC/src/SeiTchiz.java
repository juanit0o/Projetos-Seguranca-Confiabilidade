import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class SeiTchiz {

	static String usr;
	static String pass;
	static Socket cSoc = null;
	static Boolean autenticated;

	public static void main(String[] args) {
		System.out.println("cliente: main");
		int serverIp = 0;
		int serverPort = 0;
		
		if (args.length == 2) {
			 //ADDRESS IP E PORTA
			 String serverIPPort = args[0];

		     String[] ipPort = serverIPPort.split(":");
		     serverIp= Integer.parseInt(ipPort[0]);
		     serverPort = Integer.parseInt(ipPort[1]);

		     System.out.println("Server IP " + serverIp);
		     System.out.println("Server PORT: " + serverPort);
		     
		     //CLIENT ID
		     //ver se o user ja existe, caso n exista efetua o registo adicionar ao ficheiro dos clientes com user + pass (como args foram 2 pedir a passe agora)
		     //caso id exista, ver se a passe corresponde ao q ta no ficheiro
		     usr = args[1];
		     //pedir aqui a pass
			
		}else if(args.length == 3) {
			//mm shit mas tendo logo a passe para comparar a priori
			usr = args[1];
			pass = args[2];
			
		}else {
			System.err.println("Wrong commands latah");
			System.exit(-1);
		}
			

		try {
			cSoc = new Socket(Integer.toString(serverIp), serverPort);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}
		
		//PAREI AQUI P AGORA
		
		ObjectOutputStream out = null;
		ObjectInputStream in = null;

		try {
			out = new ObjectOutputStream(cSoc.getOutputStream());
			in = new ObjectInputStream(cSoc.getInputStream());
		} catch (IOException e1) {
			System.err.println(e1.getMessage());
			System.exit(-1);
		}


		// enviar nome e pass
		try {
			out.writeObject(usr);
			out.writeObject(pass);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}

		System.out.println("cliente enviou nome e pass");

		// verificar autenticacao
		try {

			autenticated = (Boolean) in.readObject();
			if (autenticated) {
				System.out.println("cliente autenticado");
			} else {
				System.out.println("cliente nao autenticado");
			}

		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Falta enviar um ficheiro do cliente para o servidor
		File myFile = new File("ficheirozinho.txt");
		try {
			out.writeObject(myFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			// fechar as streams
			out.close();
			in.close();

			// fechar socket
			cSoc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
