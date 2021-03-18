package Client;

import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import java.io.*;

/**
 * Classe 
 * @author Diogo Pinto 52763 
 * @author Francisco Ramalho 53472
 * @author Joao Funenga 53504
 *
 */
public class SeiTchiz {
	private static SSLSocket ssl;
	private static Socket cSoc = null; //esta socket vai desaparecer e vai passar a ser usada a ssl
	private static final int PORT_DEFAULT = 45678;
	private static ObjectInputStream inObj = null;
	private static ObjectOutputStream outObj = null;
	private static final Scanner inSc = new Scanner(System.in);

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		//System.setProperty("javax.net.ssl.trustStore", Client.ficheiroTrustStore);
		
		
		System.out.println("cliente: main");
		String serverIp = "";
		int serverPort = 0;
		String user = "";
		String pass = "";
		String truststore = ""; //p agora n fazer isto, esta relacionado com a parte do tls
		String keystoreFile = "";
		String keystorePassword = "";
		
		//if (args.length == 2) { //caso: 127.0.0.1:45500 clientId  || 127.0.0.1 clientId 
		//	user = args[1];
		//	System.out.println("You haven't inserted a password. Password?");
		//	pass = inSc.nextLine();
		//} else if(args.length == 3 && args[1].length() > 1) { //caso: 127.0.0.1:45500 clientId pwd || 127.0.0.1 clientId pwd
		//	user = args[1];
		//	pass = args[2];
		//} else if (args.length == 3 && args[1].length() <= 1) {
		//	System.out.println("Invalid username (least 2 characters)!");
		//	System.exit(-1);
		
		//fazer condicoes mais maricas para os args 
		if( args.length == 4) { //mudar para 5 depois, falta a truststore
			serverIp = args[0];
			truststore = args[1];  		//n percebo cm usamos
			keystoreFile = args[2]; 	//n percebo cm usamos
			keystorePassword = args[3]; //n percebo cm usamos
			user = args[3];
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

		//autenticacao vai ser mudada para incluir as chaves
		autenticacao(user, keystoreFile, keystorePassword);
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
	 * Metodo recebe um userId de um cliente que informa o cliente quais os comandos
	 * disponiveis de serem usados pelo cliente. Os comandos sao redirecionados para
	 * o servidor que trata dos mesmos.
	 * O metodo mantem-se ativo enquanto o cliente quiser fazer pedidos, caso contrario
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
			case "c":
			case "collect":
				if (comando.length != 2){
					System.out.println("Invalid command, please type help to check the available ones\nInsert a command or type help to see commands: ");
				} else {
					try {
						outObj.writeObject(output);
						String recebido = (String) inObj.readObject();
						System.out.println(recebido);
						
						//o recebido pode ter tanto o caso de sucesso como o de erro
						//fazer o split do recebido para conseguir pegar em cada mensagem em si e decifrar cada uma delas
						
						ArrayList<String> msgsDecoded = new ArrayList<>();
						
						/*Percorrer todas as mensagens recebidas (codificadas) e descodificar e adicionar ao array
						for(int i = 0; i < nrMensagens; i++) {
							SecretKeySpec keySpec2 = new SecretKeySpec(msg[i], "AES");
						    Cipher c2 = Cipher.getInstance("AES");
						    c2.init(Cipher.DECRYPT_MODE, keySpec2);
						    
						    byte[] arrayCoded;
						    ByteArrayInputStream ba = new ByteArrayInputStream(array);
						    CipherInputStream cis2 = new CipherInputStream(ba, c2);
						    
						    ByteArrayOutputStream bo = new ByteArrayOutputStream();
						    byte[] b3 = new byte[16];
						    int j = cis2.read(b3);
						    while(j != -1){
						        bo.write(b3, 0, j);
						        j = cis2.read(b3);
						    }
						    msgsDecoded.add(b3.toString());
						    bo.close();
						    cis2.close();
						}*/
						
						
						System.out.println("\nInsert a command or type help to see commands: ");
					} catch (IOException | ClassNotFoundException e) {
						System.out.println("The server is now offline :(");
					}
				}
				break;
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
			case "addu":
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
						//output tem "msg gr texto"
						//tem de se cifrar o texto (com a chave do grupo mais atual?/¿
						//reconstruir o output e mandá-lo com o texto cifrado
						
						//Gerar a chave
						KeyGenerator kg = KeyGenerator.getInstance("AES");
						kg.init(128);
						SecretKey key = kg.generateKey();
						Cipher c = Cipher.getInstance("AES");
						c.init(Cipher.ENCRYPT_MODE, key);
						
						String textoMensagem = comando[2];
						ByteArrayOutputStream bo = new ByteArrayOutputStream(100);
						CipherOutputStream cos = new CipherOutputStream(bo,c);
						
						byte[] b = textoMensagem.getBytes();  
					    cos.write(b);
					    cos.close();
					    //System.out.println(b.toString());
					    
					    //o que é o key/keyencoded/b? key=?/ keyEncoded=key em array de bytes/ b= o que supostamente mandamos?
					    //byte[] keyEncoded = key.getEncoded(); manda se o array de bytes ou a string correspondente? b ou key?
					    
						String outputCifrado = comando[0] + " " + comando[1] + " " + b;
						//envio da suposta mensagem cifrada (confirmar o que está a aparecer no b) depois de conseguir mandar msg)
						outObj.writeObject(outputCifrado);
						System.out.println((String) inObj.readObject());
						System.out.println("\nInsert a command or type help to see commands: ");
					} catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
						System.out.println("The server is now offline :(" + e);
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
						
						String photoPath = comando[1]; //path para onde se encontra a fotografia
						for (int i = 2; i < comando.length; ++i) {
							photoPath += " " + comando[i];
						}
						File myPhoto = new File(photoPath);
						if (myPhoto.exists()) {
							outObj.writeObject("post");
							Long tamanho = (Long) myPhoto.length();
							byte[] buffer = new byte[tamanho.intValue()];
							outObj.writeObject(tamanho);
							InputStream part = new BufferedInputStream(new FileInputStream(myPhoto));
							part.read(buffer);
							outObj.writeObject(buffer);
							part.close();
							System.out.println((String) inObj.readObject());
							System.out.println("\nInsert a command or type help to see commands: ");
						} else {
							System.out.println("The file with the path " + photoPath + " doesn't exist");
							System.out.println("\nInsert a command or type help to see commands: ");
						}
					} catch (Exception e1) {
						System.out.println("Invalid path! (path doesn't exist or you don't have permissions)");
						System.out.println("\nInsert a command or type help to see commands: ");
					}
				}
				break;
			case "w":
			case "wall":
				if (comando.length != 2){
					System.out.println("Invalid command, please type help to check the available ones\nInsert a command or type help to see commands: ");
				} else {
					File wallFolder = new File("wall" + File.separator + user);
					try {
						outObj.writeObject("wall " + comando[1] + " " + user);
						int nrFotos = (int) inObj.readObject();
						for(int i = 0; i < nrFotos; i++) {
							File fileName = new File(wallFolder.getAbsolutePath(),"wall_" + user + "_"+ i + ".jpg");
							OutputStream photoRecebida = new BufferedOutputStream(new FileOutputStream(fileName));
							Long dimensao;
							try {
								dimensao = (Long) inObj.readObject();
								byte[] recebidos = (byte[]) inObj.readObject();
								photoRecebida.write(recebidos);
								photoRecebida.close();
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}
						System.out.println((String) inObj.readObject());
						System.out.println("\nInsert a command or type help to see commands: ");
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				break;
			default:
				System.out.println("Invalid command, please type help to check the available ones\nInsert a command or type help to see commands: ");
				break;
			}
		}
	}

	/**
	 * Metodo efetua a autentiacao de um cliente atraves do seu username 
	 * e da sua password.
	 * Sao mostradas mensagens informativas ao cliente.
	 * @param user - username do cliente a autenticar.
	 * @param pass - password do cliente a autenticar.
	 */
	private static void autenticacao(String user, String keystoreFile, String keystorePass) {
		try {
			outObj.writeObject(user);
			//outObj.writeObject(pass);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}
		// verificar autenticacao vai ser mudada para usar os ficheiros keystores
		try {
			//NONCE recebido do sv
			String resposta = (String) inObj.readObject();
			FileInputStream keyfile = new FileInputStream("data" + File.separator + "Personal User Files" + File.separator + user + File.separator + keystoreFile); //da access denied aqui
			//ficheiro keystore cliente
			KeyStore kstore = KeyStore.getInstance("JCEKS"); //TODO com jceks diz que nao tem este algoritmo e com keystore.getdefaulttype diz acesso negado
			kstore.load(keyfile, keystorePass.toCharArray());
			
			System.out.println(keystorePass);
			Key myPrivateKey = kstore.getKey(user, keystorePass.toCharArray());
			PrivateKey pk = (PrivateKey) myPrivateKey; //ver cm obter slides
			
			
			if(resposta.charAt(resposta.length()-1) =='D') {
				System.out.println("nao existo");
				//enviar a assinatura do nonce com a sua chave privada
				
				Signature signature = Signature.getInstance("MD5withRSA");
				signature.initSign(pk);
				byte buffer[] = resposta.getBytes();
				signature.update(buffer);
				
				//enviar o nonce e dps assinatura
				outObj.writeObject(resposta);
				outObj.writeObject(signature.sign());
				
				
				//enviar o certificado com a chave publica correspondente
				Certificate certificado = kstore.getCertificate(user); //alias do keypair
				
				
				outObj.writeObject(certificado.getEncoded()); //envia o array de bytes do certificado
				
				System.out.println("nao existo final");

				String askName = (String) inObj.readObject();
				if(askName.equals("What is your name?")) {
					System.out.println(askName);
					outObj.writeObject(inSc.nextLine());
					Boolean autenticated = inObj.readObject().equals("true"); //converter string p boolean
					System.out.println(autenticated ? "Client authenticated" : "Client not authenticated");
					if (!autenticated) {
						System.exit(-1);
					}
				}else {
					System.out.println("error");
					System.exit(-1);
				}
			}else { //qd ja existe no servidor
				
				System.out.println("ja existo");
				Signature signature = Signature.getInstance("MD5withRSA");
				signature.initSign(pk);
				byte buffer[] = resposta.getBytes();
				signature.update(buffer);
				
				//enviar o nonce
				outObj.writeObject(resposta);
				//enviar assinatura é preciso responder com o nonce?
				outObj.writeObject(signature.sign());
				
				String ans = (String) inObj.readObject();
				if(ans.equals("true")) {
					System.out.println("You are logged in");
				}else {
					System.out.println("Error logging in");
				}
			}
			
			
		} catch (Exception e1) {
			e1.printStackTrace();
			System.out.println("Failed to authenticate :(");
			System.exit(-1);
		} 
	}

	/**
	 * Metodo estabele ligacao ao servidor atraves de um ip e porto.
	 * @param ip - ip de ligacao.
	 * @param port - porto de ligacao.
	 */
	private static void conectToServer(String ip, int port) {
		try {
			///tentativa com ssl
			//SSLSocketFactory sslfact = (SSLSocketFactory) SSLSocketFactory.getDefault();
			//SSLSocket ssl = null;
			//ssl = (SSLSocket) sslfact.createSocket(ip, port);
			//if(ssl.isConnected())
			//    System.out.println("Connected.");
			
			//isto vai dar a excecao handshake certificate failed, e representa que nao temos o 
			//certificado publico do servidor a que nos estamos a tentar conectar na Java Truststore
			//P tratar, temos de apontar a variavel de ambiente javax.net.ssl.trustStore para o nosso
			//ficheiro truststore
			//https://www.baeldung.com/java-ssl
			//outObj = new ObjectOutputStream(ssl.getOutputStream());
			//inObj = new ObjectInputStream(ssl.getInputStream());
			//////
			
			//Forma antiga
			cSoc = new Socket(ip,port);
			outObj = new ObjectOutputStream(cSoc.getOutputStream());
			inObj = new ObjectInputStream(cSoc.getInputStream());
		} catch (IOException e) {
			System.out.println("Couldnt connect to the server! " + e);
			System.exit(-1);
		}		
	}

	/**
	 * Metodo devolve o porto recebido de um endereco recebido 
	 * no formato de endereco:porto.
	 * @param serverAdress - endereco e porto
	 * @return porto do endereco recebido.
	 */
	private static int getPort(String serverAdress) {
		String[] tudo = serverAdress.split(":");
		return Integer.parseInt(tudo[1]);
	}

	/**
	 * Metodo devolve o ip recebido de um endereco:porto.
	 * @param serverAdress - endereco e porto
	 * @return ip do endereco recebido.
	 */
	private static String getIp(String serverAdress) {
		String[] tudo = serverAdress.split(":");
		return tudo[0];
	}
}
