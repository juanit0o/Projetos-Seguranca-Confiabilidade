import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class SeiTchiz {
	private static String user;
	private static String pass;
	private static Socket cSoc = null;
	private static Boolean autenticated;
	private static final int PORT_DEFAULT = 45678;

	private static ObjectInputStream in = null;
	private static ObjectOutputStream out = null;

	public static void main(String[] args) {
		System.out.println("cliente: main");
		Scanner inSc = new Scanner(System.in);
		String serverIp = null;
		int serverPort = 0;

		if (args.length == 2) { //caso: 127.0.0.1:45500 clientId  || 127.0.0.1 clientId 
			user = args[1];
			System.out.println("Nao inseriu password. Password?");
			pass = inSc.nextLine();


			//CLIENT ID
			//ver se o user ja existe, caso n exista efetua o registo adicionar ao ficheiro dos clientes com user + pass
			//caso id exista, ver se a passe corresponde ao q ta no ficheiro
			user = args[1];
			//TODO:
			//pedir aqui a pass

		} else if(args.length == 3) {
			user = args[1];
			pass = args[2];
		} else {
			System.err.println("Wrong commands latah");
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


		//PAREI AQUI P AGORA



		// enviar nome e pass
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
		System.out.println(tudo);
		return Integer.parseInt(tudo[1]);
	}

	private static String getIp(String serverAdress) {
		String[] tudo = serverAdress.split(":");
		return tudo[0];
	}

}
