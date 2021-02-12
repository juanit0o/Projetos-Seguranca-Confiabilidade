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
	private static final String IP_DEFAULT = "127.0.0.1";

	public static void main(String[] args) {
		System.out.println("cliente: main");
		String serverIp = null;
		int serverPort = 0;
		String serverAdress = null;
		Scanner inSc = new Scanner(System.in);

		if (args.length == 1) { //caso: clientId
			serverPort = PORT_DEFAULT;
			serverIp = IP_DEFAULT;
			System.out.println("Nao inseriu password. Password?");
			pass = inSc.nextLine();
		} else if (args.length == 2) { //caso: serverAdress clientId || clientId password
			//serverAdress clientId
			if (args[0].contains(":")) {
				serverAdress = args[0];
				serverIp = getIp(serverAdress);
				serverPort = getPort(serverAdress);
				System.out.println("serverIp = "+serverIp+"\nserverPort = "+serverPort);
				user = args[1];
				System.out.println("Nao inseriu password. Password?");
				pass = inSc.nextLine();
			} else { //caso: clientId password
				serverPort = PORT_DEFAULT;
				serverIp = IP_DEFAULT;
				user = args[0];
				pass = args[1];
			}



			//CLIENT ID
			//ver se o user ja existe, caso n exista efetua o registo adicionar ao ficheiro dos clientes com user + pass (como args foram 2 pedir a passe agora)
			//caso id exista, ver se a passe corresponde ao q ta no ficheiro
			user = args[1];
			//pedir aqui a pass

		} else if(args.length == 3) {

			//mm shit mas tendo logo a passe para comparar a priori
			user = args[1];
			pass = args[2];

		} else {
			System.err.println("Wrong commands latah");
			System.exit(-1);
		}


		try {
			cSoc = new Socket(serverIp, serverPort);
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
