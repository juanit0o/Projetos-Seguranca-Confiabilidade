import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class SeiTchiz {
	private static Socket cSoc = null;
	private static final int PORT_DEFAULT = 45678;

	private static ObjectInputStream in = null;
	private static ObjectOutputStream out = null;

	public static void main(String[] args) {
		System.out.println("cliente: main");
		Scanner inSc = new Scanner(System.in);
		String serverIp = "";
		int serverPort = 0;
		String user = "";
		String pass = "";

		if (args.length == 2) { //caso: 127.0.0.1:45500 clientId  || 127.0.0.1 clientId 
			user = args[1];
			System.out.println("Nao inseriu password. Password?");
			pass = inSc.nextLine();
		} else if(args.length == 3) { //caso: 127.0.0.1:45500 clientId pwd || 127.0.0.1 clientId pwd
			user = args[1];
			pass = args[2];
		} else {
			System.out.println("Wrong commands latah");
			System.exit(-1);
		}

		//verifica se tem porto ou nao, caso nao PORT_DEFAULT
		if (args[0].contains(":")) {
			serverIp = getIp(args[0]);
			serverPort = getPort(args[0]);
		} else {
			serverIp = getIp(args[0]);
			serverPort = PORT_DEFAULT;
		}
		System.out.println("serverIp = "+serverIp+"\nserverPort = "+serverPort);
		//conectar ao server
		conectToServer(serverIp,serverPort);

		//autenticacao 
		autenticacao(user, pass);

		//loop do metodo das acoes
		/*
		 * while(true){
		 * 		metodo_switch();
		 * }
		 */

		
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

	private static void autenticacao(String user, String pass) {
		try {
			out.writeObject(user);
			out.writeObject(pass);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}

		System.out.println("cliente enviou nome e pass");

		// verificar autenticacao
		try {
			Boolean autenticated = (Boolean) in.readObject();
			System.out.println(autenticated ? "cliente autenticado" : "cliente nao autenticado");
			if (!autenticated) {
				System.exit(-1);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			System.out.println("Falha na autenticacao.");
			System.exit(-1);
		} 
	}

	private static void conectToServer(String ip, int port) {
		try {
			cSoc = new Socket(ip,port);
			in = new ObjectInputStream(cSoc.getInputStream());
			out = new ObjectOutputStream(cSoc.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}


	private static int getPort(String serverAdress) {
		String[] tudo = serverAdress.split(":");
		return Integer.parseInt(tudo[1]);
	}

	private static String getIp(String serverAdress) {
		String[] tudo = serverAdress.split(":");
		return tudo[0];
	}

}
