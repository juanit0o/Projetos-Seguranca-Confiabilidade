package Client;

import java.net.Socket;
import java.util.Scanner;
import java.io.*;

/**
 * Classe 
 * @author Diogo Pinto 52763 
 * @author Francisco Ramalho 53472
 * @author Joao Funenga 53504
 *
 */
public class SeiTchiz {
	private static Socket cSoc = null;
	private static final int PORT_DEFAULT = 45678;
	private static ObjectInputStream inObj = null;
	private static ObjectOutputStream outObj = null;
	private static final Scanner inSc = new Scanner(System.in);

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("cliente: main");
		String serverIp = "";
		int serverPort = 0;
		String user = "";
		String pass = "";
		if (args.length == 2) { //caso: 127.0.0.1:45500 clientId  || 127.0.0.1 clientId 
			user = args[1];
			System.out.println("You haven't inserted a password. Password?");
			pass = inSc.nextLine();
		} else if(args.length == 3 && args[1].length() > 1) { //caso: 127.0.0.1:45500 clientId pwd || 127.0.0.1 clientId pwd
			user = args[1];
			pass = args[2];
		} else if (args.length == 3 && args[1].length() <= 1) {
			System.out.println("Invalid username (least 2 characters)!");
			System.exit(-1);
		} else {
			System.out.println("Invalid commands");
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
		try{
			conectToServer(serverIp,serverPort);
		} catch (SecurityException e) {
			System.out.println("[ERROR]: Couldnt connect to the server!");
			System.exit(-1);
		}

		//autenticacao 
		autenticacao(user, pass);
		sendReceiveComando(user);
		// fechar tudo
		try {
			outObj.close();
			inObj.close();
			inSc.close();
			cSoc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * M�todo recebe um userId de um cliente que informa o cliente quais os comandos
	 * disponiveis de serem usados pelo cliente. Os comandos s�o redirecionados para
	 * o servidor que trata dos mesmos.
	 * O m�todo mantem-se ativo enquanto o cliente quiser fazer pedidos, caso contr�rio
	 * basta sair, executando o comando quit ou exit. 
	 * @param user - id do cliente que executa os comandos.
	 */
	private static void sendReceiveComando(String user) {
		System.out.println("Available commands:\n"+"follow/f <userID>\n"+
				"unfollow/u <userID>\n"+"viewfollowers/v\n"+"post/p <photo>\n"+
				"wall/w <nPhotos>\n"+"like/l <photoId>\n"+"newGroup/n <groupID>\n"+
				"addu/a <userID> <groupID>\n"+"removeu/r <userID> <groupID>\n"+
				"ginfo/g [groupID]\n"+"msg/m <groupID> <msg>\n"+"collect/c <groupID>\n"+
				"history/h <groupID>\n"+"help\n"+"exit\n");
		System.out.println("Insert a command or type help to see commands: ");
		while(true) {
			String output = inSc.nextLine();
			String[] comando = output.split(" "); //p pegar o comando (cena da foto leva o path)
			switch (comando[0]) {
			case "help":
				System.out.println("Available commands:\n"+"follow/f <userID>\n"+
						"unfollow/u <userID>\n"+"viewfollowers/v\n"+"post/p <photo>\n"+
						"wall/w <nPhotos>\n"+"like/l <photoId>\n"+"newGroup/n <groupID>\n"+
						"addu/a <userID> <groupID>\n"+"removeu/r <userID> <groupID>\n"+
						"ginfo/g [groupID]\n"+"msg/m <groupID> <msg>\n"+"collect/c <groupID>\n"+
						"history/h <groupID>\n"+"help\n"+"exit\n");
				System.out.println("Insert a command or type help to see commands: ");
				break;
			case "quit":
			case "exit":
				try {
					outObj.writeObject(output);
					System.out.println((String) inObj.readObject());
				} catch (IOException | ClassNotFoundException e1) {
					e1.printStackTrace();
				}
				return;
			case "v":
			case "viewfollowers":
				if (comando.length != 1){
					System.out.println("Invalid command, please type help to check the available ones\nInsert a command or type help to see commands: ");
				} else {
					try {
						outObj.writeObject(output);
						System.out.println((String) inObj.readObject());
						System.out.println("\nInsert a command or type help to see commands: ");
					} catch (IOException | ClassNotFoundException e) {
						System.out.println("The server is now offline :(");
					}
				}
				break;
			case "f":
			case "follow":
			case "u":
			case "unfollow":
			case "l":
			case "like":
			case "n":
			case "newgroup":
			case "c":
			case "collect":
			case "h":
			case "history":
				if (comando.length != 2){
					System.out.println("Invalid command, please type help to check the available ones\nInsert a command or type help to see commands: ");
				} else {
					try {
						outObj.writeObject(output);
						System.out.println((String) inObj.readObject());
						System.out.println("\nInsert a command or type help to see commands: ");
					} catch (IOException | ClassNotFoundException e) {
						System.out.println("The server is now offline :(");
					}
				}
				break;
			case "a":
			case "add":
			case "r":
			case "removeu":
				if (comando.length != 3){
					System.out.println("Invalid command, please type help to check the available ones\nInsert a command or type help to see commands: ");
				} else {
					try {
						outObj.writeObject(output);
						System.out.println((String) inObj.readObject());
						System.out.println("\nInsert a command or type help to see commands: ");
					} catch (IOException | ClassNotFoundException e) {
						System.out.println("The server is now offline :(");
					}
				}
				break;
			case "m":
			case "msg":
				if (comando.length < 3){
					System.out.println("Invalid command, please type help to check the available ones\nInsert a command or type help to see commands: ");
				} else {
					try {
						outObj.writeObject(output);
						System.out.println((String) inObj.readObject());
						System.out.println("\nInsert a command or type help to see commands: ");
					} catch (IOException | ClassNotFoundException e) {
						System.out.println("The server is now offline :(");
					}
				}
				break;
			case "g":
			case "ginfo":
				if (comando.length >= 3){
					System.out.println("Invalid command, please type help to check the available ones\nInsert a command or type help to see commands: ");
				} else {
					try {
						outObj.writeObject(output);
						System.out.println((String) inObj.readObject());
						System.out.println("\nInsert a command or type help to see commands: ");
					} catch (IOException | ClassNotFoundException e) {
						System.out.println("The server is now offline :(");
					}
				}
				break;
			case "p":
			case "post":
				if (comando.length <= 1){
					System.out.println("Invalid command, please type help to check the available ones\nInsert a command or type help to see commands: ");
				} else {
					//pegar no path, ir ao path, converter foto para bytes, enviar bytes para o server
					try {
						//enviar comando
						outObj.writeObject("post");
						String photoPath = comando[1]; //path para onde se encontra a fotografia
						for (int i = 2; i < comando.length; ++i) {
							photoPath += " " + comando[i];
						}
						File myPhoto = new File(photoPath);
						if (myPhoto.exists()) {
							Long tamanho = (Long) myPhoto.length();
							byte[] buffer = new byte[tamanho.intValue()];
							//outObj.reset(); //same aqui
							outObj.writeObject(tamanho);
							InputStream part = new BufferedInputStream(new FileInputStream(myPhoto));
							part.read(buffer);
							outObj.writeObject(buffer);
							part.close();
							System.out.println((String) inObj.readObject());
							//System.out.println((String) inObj.readObject()); /*crash aqui*/
							System.out.println("\nInsert a command or type help to see commands: ");
						} else {
							System.out.println("The file with the path " + photoPath + " doesn't exist");
							System.out.println("\nInsert a command or type help to see commands: ");
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				break;
			case "w":
			case "wall":
				if (comando.length != 2){
					System.out.println("Invalid command, please type help to check the available ones\nInsert a command or type help to see commands: ");
				} else {
					File wallFolder = new File("wall\\" + user);
					try {
						outObj.writeObject("wall " + comando[1] + " " + user);
						int nrFotos = (int) inObj.readObject();
						for(int i = 0; i < nrFotos; i++) {
							File fileName = new File(wallFolder.getAbsolutePath(),"wall_" + user + "_"+ i + ".jpg");
							OutputStream photoRecebida = new BufferedOutputStream(new FileOutputStream(fileName));
							Long dimensao;
							try {
								dimensao = (Long) inObj.readObject();
								//byte[] buffer = new byte[dimensao.intValue()]; //TODO: tirar?
								byte[] recebidos = (byte[]) inObj.readObject();
								photoRecebida.write(recebidos);
								photoRecebida.close();
							} catch (Exception e1) {
								e1.printStackTrace();
							}
							System.out.println("foto " + i + "recebida");
						}
						System.out.println((String) inObj.readObject());
						System.out.println("\nInsert a command or type help to see commands: ");
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				break;
			default: //QUANDO O SERVIDOR SE DESLIGA E VOLTA A LIGAR, O CLIENTE JA NAO CONSEGUE COMUNICAR C ELE, TENTAR LIGA-LOS OUTRA X
				//try {
				//outObj.writeObject(output);
				System.out.println("Invalid command, please type help to check the available ones\nInsert a command or type help to see commands: ");
				//} catch (IOException | ClassNotFoundException e) {
				//	System.out.println("The server is now offline :(");
				//e.printStackTrace();
				//}
				break;
			}
		}
	}

	/**
	 * M�todo efetua a autentiacao de um cliente atraves do seu username 
	 * e da sua password.
	 * S�o mostradas mensagens informativas ao cliente.
	 * @param user - username do cliente a autenticar.
	 * @param pass - password do cliente a autenticar.
	 */
	private static void autenticacao(String user, String pass) {
		try {
			outObj.writeObject(user);
			outObj.writeObject(pass);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}
		System.out.println("Server.Cliente enviou nome e pass");
		// verificar autenticacao
		try {
			String resposta = (String) inObj.readObject();
			if(resposta.equals("What is your name?")) {
				System.out.println(resposta);
				outObj.writeObject(inSc.nextLine());
				Boolean autenticated = inObj.readObject().equals("true"); //converter string p boolean
				System.out.println(autenticated ? "cliente autenticado" : "cliente nao autenticado");
				if (!autenticated) {
					System.exit(-1);
				}
			}else {
				//Boolean autenticated = Boolean.parseBoolean(resposta);
				Boolean autenticated = (resposta.equals("true"));
				System.out.println(autenticated ? "cliente autenticado" : "cliente nao autenticado");
				if (!autenticated) {
					System.exit(-1);
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			System.out.println("Falha na autenticacao.");
			System.exit(-1);
		} 
	}

	/**
	 * M�todo estabele liga��o ao servidor atrav�s de um ip e porto.
	 * @param ip - ip de ligacao.
	 * @param port - porto de ligacao.
	 */
	private static void conectToServer(String ip, int port) {
		try {
			cSoc = new Socket(ip,port);
			outObj = new ObjectOutputStream(cSoc.getOutputStream());
			inObj = new ObjectInputStream(cSoc.getInputStream());
		} catch (IOException e) {
			System.out.println("Couldnt connect to the server!");
			System.exit(-1);
		}		
	}

	/**
	 * M�todo devolve o porto recebido de um endere�o recebido 
	 * no formato de endereco:porto.
	 * @param serverAdress - endereco e porto
	 * @return porto do endereco recebido.
	 */
	private static int getPort(String serverAdress) {
		String[] tudo = serverAdress.split(":");
		return Integer.parseInt(tudo[1]);
	}

	/**
	 * M�todo devolve o ip recebido de um endereco:porto.
	 * @param serverAdress - endereco e porto
	 * @return ip do endereco recebido.
	 */
	private static String getIp(String serverAdress) {
		String[] tudo = serverAdress.split(":");
		return tudo[0];
	}
}
