package Server;

import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.crypto.KeyGenerator;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import java.io.*;

/**
 * 
 * @author Diogo Pinto 52763 
 * @author Francisco Ramalho 53472
 * @author Joao Funenga 53504
 *
 */
public class SeiTchizServer {

	private CatalogoClientes catClientes;
	private CatalogoGrupos catGrupos;
	
	//criar aqui a keystore do servidor ou dentro do main??
	//private static File keystoreFile = new File("data" + File.separator + "Server Files" + File.separator + "keystore.jks");
	
	//onde gerar? no inicio do main ou fazer outra class para isto? (p agora ponho no main)
	//KeyGenerator AESKeyGen = KeyGenerator.getInstance("AES");
	//KeyStore keystore = KeyStore.getInstance("JKS");
	//String keystorePassord;
	//keystore.load(keystoreFile, keystorePassword);
	
	//criar aqui a pasta PubKeys?
	File PubKeys = new File("PubKeys");
	boolean value = PubKeys.mkdirs();
	
	//adicionar ao pubkeys o .cert do servidor (X509 auto-assinado)
	
	//adicionar o .cert do servidor tambem a uma truststore usada p todos os clientes
	
	public static void main(String[] args) {
		//System.setProperty("javax.net.ssl.keyStore",Server.ficheiroKeyStore);
		
		System.out.println("Server");
		SeiTchizServer server = new SeiTchizServer();
		if (args.length != 3) {
			System.out.println("Server is started by typing 'Server.SeiTchizServer (PORT) (KEYSTORE) (KEYSTORE-PASSWORD)'!");
			System.exit(-1);
		}
		System.out.println("- - - - - - - - - - -");
		
		//System.setProperty("javax.net.ssl.keyStore",args[1]); ver com o que isto esta relacionado
		//System.setProperty("javax.net.ssl.keyStorePassword",args[2]);
		
		//try {
			//onde gerar? no inicio do main ou fazer outra class para isto? (p agora ponho no construtor)
			//KeyGenerator AESKeyGen = KeyGenerator.getInstance("AES");
			//AESKeyGen.init(128);
			//KeyStore keystore = KeyStore.getInstance("JKS");
			//String keystorePassword = args[2];
			//keystore.load(new FileInputStream(keystoreFile), keystorePassword.toCharArray());
		//} catch (NoSuchAlgorithmException  | CertificateException | IOException e) {
		//	e.printStackTrace();
		//} catch (KeyStoreException e) {
		//	e.printStackTrace();
		//}
		
		//aqui vao mudar os argumentos
		//String keyStoresFile = args[1];      ficheiro que contem o par de chaves do sv
		//String keyStoresPassword = args[2];  password do ficheiro
		//System.setProperty("javax.net.ssl.keyStorePassword", keyStoresPassword);
		server.startServer(Integer.parseInt(args[0]), args[1], args[2]);
	}

	//metodo para iniciar o servidor
	public void startServer (int port, String keyStoresFile, String keyStoresPassword) {
		//SSLServerSocketFactory sslfact = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
		//SSLServerSocket ssl = null;
		ServerSocket sSoc = null;
		try {
			//ssl =  (SSLServerSocket) sslfact.createServerSocket(port);
			sSoc = new ServerSocket(port);
		} catch (IOException | SecurityException e) {
			System.err.println("[ERROR]: Couldnt accept the socket!");
			System.exit(-1);
		}
		catClientes = new CatalogoClientes();
		catGrupos = new CatalogoGrupos(catClientes);
		//servidor vai estar em loop a receber comandos dos clientes sem se desligar
		while(true) {
			try {
				//Socket inSoc = ssl.accept();
				Socket inSoc = sSoc.accept();
				ServerThread newServerThread = new ServerThread(inSoc, keyStoresFile, keyStoresPassword);
				newServerThread.start();
			}
			catch (IOException e) {
				System.out.println("[ERROR]: Couldnt accept the client socket!");
			}
		}
	}

	//Threads utilizadas para comunicacao com os clientes(1 p cliente)
	class ServerThread extends Thread {
		private Socket socket = null;
		private String keyStoreFile;
		private String keyStorePassword;
		
		ServerThread(Socket inSoc, String keyStoreFile, String keyStorePassword) {
			socket = inSoc;
			this.keyStoreFile = keyStoreFile;
			this.keyStorePassword = keyStorePassword;
			System.out.println("Connected w client constructor test");
		}
		public void run(){
			String user = null;
			try {
				ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
			
				//String password = null;
				boolean autenticou = false;
				try {
					user = (String) inStream.readObject();
					//password = (String) inStream.readObject();
				}catch (ClassNotFoundException e1) {
					System.out.println("[ERROR]: Couldnt read user or pass from client!");
				}
				//gerar nonce para devolver
				Autenticacao aut = new Autenticacao();
				long nonce = aut.generateNonce();
				
				
				//D=desconhecido
				//comparar se ja existe user
				if (catClientes.existeUser(user)) {
					
					outStream.writeObject(String.valueOf(nonce));
					
					//assinatura do nonce com chave privada do cliente
					byte assinatura[] = (byte[]) inStream.readObject();
					//certificado com chave publica do cliente, ver se recebe o nome do file(acho q sim)
					
					//como obter o certificado dele, supostamente foi exportado e tem pass
					Certificate certificadoCliente = aut.getCertificate(user);
					
					PublicKey pubK = certificadoCliente.getPublicKey();
					Signature signature = Signature.getInstance("MD5withRSA");
					signature.initVerify(pubK);
					//signature.update(nonceRecebido.getBytes());
					
					
					//autenticou = catClientes.passCorreta(user, password);
				} else {//adicionar a lista
					autenticou = true;
					String clienteDesconhecido = String.valueOf(nonce);
					clienteDesconhecido += "D"; //flag de desconhecido (tem de se registar)
					outStream.writeObject(clienteDesconhecido);
					
					
					//nonce recebido do cliente
					String nonceRecebido = (String) inStream.readObject();
					if(!clienteDesconhecido.equals(nonceRecebido)) {
						System.out.println("Nonce diferente");
						System.exit(-1);
					}
					
					//assinatura do nonce com chave privada do cliente
					byte assinatura[] = (byte[]) inStream.readObject();
					
					//certificado com chave publica do cliente, ver se recebe o nome do file(acho q sim)
					Certificate certificadoCliente = (Certificate) inStream.readObject();
					
					PublicKey pubK = certificadoCliente.getPublicKey();
					Signature signature = Signature.getInstance("MD5withRSA");
					signature.initVerify(pubK);
					signature.update(nonceRecebido.getBytes());
					if(signature.verify(assinatura)) {
						System.out.println("Msg valida");
					}else {
						System.out.println("msg c assinatura invalida");
					}

					catClientes.addClient(user, pubK, outStream, inStream);
					
				}
				
				System.out.println(autenticou ? "Client '" + user + "' authenticated." :
					"Client '" + user + "' not authenticated.");
				//GET CURRENT CLIENT
				Cliente currentClient = catClientes.getCliente(user);

				while(true) {
					String comando = null;
					try {
						comando = (String) inStream.readObject();
						System.out.println("Client '" + currentClient.getUser() + "' > " + comando);
					} catch (ClassNotFoundException e) {
						System.out.println("[ERROR]: Couldnt read the command sent by the client!");
					}
					String[] splittado = comando.split(" ");
					switch (splittado[0]) {
					case "quit":
					case "exit":
						outStream.writeObject("Server ending for you...");
						outStream.close();
						inStream.close();
						socket.close();
						break;
					case "f":
					case "follow":
						//primeiro de tudo ver se o id dado existe, ver se aparece no ficheiro allUsers, se n existir da logo erro
						//ver ficheiro do cliente, ver se ja tem follow, se ja la tiver diz
						//se n tiver follow, adiciona ao arraylist, adiciona ao txt na parte dos follows.
						if (currentClient.getUser().equals(splittado[1])) {
							outStream.writeObject("You can't follow yourself");
							System.out.println("Client '" + currentClient.getUser() + "' tried to follow himself/herself");
						} else if (catClientes.existeUser(splittado[1])) {
							Cliente seguirClient = catClientes.getCliente(splittado[1]);
							if (currentClient.seguir(seguirClient)) {
								outStream.writeObject("You followed " + splittado[1]);
								System.out.println("The client '" + currentClient.getUser() + "' followed '" + splittado[1] + "'");
							} else {
								outStream.writeObject("You already follow " + splittado[1]);
								System.out.println("The client '" + currentClient.getUser() + "' already follows '" + splittado[1]+ "'");
							}
						} else {
							outStream.writeObject(splittado[1] + " does not exist!");
							System.out.println("The client '" + currentClient.getUser() + "' tried to follow '" + splittado[1] + "' but user doesn't exist");
						}
						break;
					case "u":
					case "unfollow":
						//ver se o id existe, se n existir - avisar
						//ver se tem follow nele, se n tivermos - avisar
						//caso estiver, tirar do info.txt (ficheiro pessoal)
						if (currentClient.getUser().equals(splittado[1])) {
							outStream.writeObject("You can't unfollow yourself");
							System.out.println("Client '" + currentClient.getUser() + "' tried to unfollow himself/herself");
						} else if (catClientes.existeUser(splittado[1])) {
							Cliente unfollowClient = catClientes.getCliente(splittado[1]);
							if (currentClient.deixarDeSeguir(unfollowClient)) {
								outStream.writeObject("You unfollowed '" + splittado[1]+ "'");
								System.out.println("The client '" + currentClient.getUser() + "' unfollowed '" + splittado[1] + "'");
							} else {
								outStream.writeObject("You don't follow " + splittado[1]);
								System.out.println("The client '" + currentClient.getUser() + "' doesn't follow '" + splittado[1]+ "'");
							}
						} else {
							outStream.writeObject(splittado[1] + " does not exist!");
							System.out.println("The client '" + currentClient.getUser() + "' tried to unfollow '" + splittado[1] + "' but user doesn't exist");
						}
						break;
					case "v":
					case "viewfollowers":
						ArrayList<String> followers = currentClient.getFollowers();
						if (followers.isEmpty()) {
							outStream.writeObject("You don't have followers");
							System.out.println("The client '" + currentClient.getUser() + "' doesn't have followers ");
						} else {
							outStream.writeObject("Your followers are " + followers.toString());
							System.out.println("The client '" + currentClient.getUser() + "' is followed by '" + followers.toString() + "'");
						}
						break;
					case "p":
					case "post":
						String path = "data" + File.separator + "Personal User Files" + File.separator + user + File.separator + "Photos" + File.separator + "photo_"
								+ currentClient.getUser() + "_" + currentClient.nrOfPhotos() + ".jpg";
						File fileName = new File(path);
						OutputStream photoRecebida = new BufferedOutputStream(new FileOutputStream(fileName));
						Long dimensao;
						try {
							dimensao = (Long) inStream.readObject();
							byte[] buffer = new byte[dimensao.intValue()];
							buffer = (byte[]) inStream.readObject();
							photoRecebida.write(buffer);
							//adicionar informacao da fotografia (nome) ao ficheiro pessoal info.txt
							currentClient.publishPhoto(path);
							File filePhotos = new File("data" + File.separator + "Server Files" + File.separator + "allPhotos.txt");
							BufferedWriter bW = new BufferedWriter(new FileWriter(filePhotos, true));
							bW.write(currentClient.getUser() + "::" + fileName.getPath());
							bW.newLine();
							bW.close();
							outStream.writeObject("File was submitted with success");
							photoRecebida.close();
							System.out.println("Client '" + currentClient.getUser() + "' posted a photo to the server with success saved in " + fileName.getPath());
						} catch (ClassNotFoundException e) {
							System.out.println("[ERROR]: Couldnt post the photo sent by the client!");
						}
						break;
					case "w":
					case "wall":

						//criar pasta para o cliente
						File wallFolder = new File("wall" + File.separator + splittado[2]);
						if(!wallFolder.mkdirs()) { //se a pasta ja tiver criada
							String[] entries = wallFolder.list();
							for(String s: entries){
								File currentFile = new File(wallFolder.getPath(),s);
								currentFile.delete();
							}
						}

						//devolver os ids das n fotografias mais recentes e o nr de likes destas
						//se houverem menos que o n dado apenas mostrar essas
						//se n exisitirem nenhumas - avisar
						File fileDirectory = new File("data" + File.separator + "Server Files");
						File filePhotos = new File(fileDirectory.getAbsolutePath(), "allPhotos.txt");
						BufferedReader bR = new BufferedReader(new FileReader(filePhotos));

						ArrayList<String> outputAux = new ArrayList<String>();
						String lineO;
						ArrayList<String> allPhotoPaths = new ArrayList<String>();
						while ((lineO = bR.readLine()) != null) {
							String[] splittada = lineO.split("::");
							if (currentClient.follows(splittada[0])) {
								allPhotoPaths.add(splittada[1]);
								outputAux.add(catClientes.getCliente(splittada[0]).getPhoto(splittada[1]));
							}
						}
						ArrayList<String> allPhotoPathsSplitted = new ArrayList<String>();
						ArrayList<String> outputAuxSplitted = new ArrayList<String>();
						//cortar o arraylist allphotopaths
						for(int i = allPhotoPaths.size() - 1; i >= 0; i-- ) {
							if (allPhotoPathsSplitted.size() >= Integer.valueOf(splittado[1])) {
								break;
							}
							allPhotoPathsSplitted.add(allPhotoPaths.get(i));
							outputAuxSplitted.add(outputAux.get(i));
						}
						outStream.writeObject(allPhotoPathsSplitted.size());
						//Caso nao haja fotos
						if (allPhotoPathsSplitted.size() <= 0) {
							outStream.writeObject("You have no photos");
							System.out.println("Client '" + currentClient.getUser() + "' doesnt have any photos from who he follows");
							break;
						}
						for (int i = 0; i < allPhotoPathsSplitted.size(); i++) {
							try {
								File myPhoto = new File(allPhotoPathsSplitted.get(i));
								if (myPhoto.exists()) {
									Long tamanho = (Long) myPhoto.length();
									byte[] buffer = new byte[tamanho.intValue()];
									outStream.writeObject(tamanho);
									InputStream part = new BufferedInputStream(new FileInputStream(myPhoto));
									part.read(buffer);
									outStream.writeObject(buffer);
									part.close();
								}
							} catch (Exception e1) {
								System.out.println("[ERROR]: Couldnt send the photo to the client!");
							}
						}
						String output = "All the " + allPhotoPathsSplitted.size() + " photos from who you follow were sent:\n";
						for(int i = 0; i < outputAuxSplitted.size(); ++i){
							output += "\t" + i + " : " + outputAuxSplitted.get(i);
						}
						outStream.writeObject(output);
						System.out.println("Client '" + currentClient.getUser() + "' received the " + allPhotoPathsSplitted.size() 
											+ "  most recent photos from who he follows");
						break;
					case "l":
					case "like":
						String phId = splittado[1];
						File filePhotos1 = new File("data" + File.separator + "Server Files" + File.separator + "allPhotos.txt");
						BufferedReader bR1 = new BufferedReader(new FileReader(filePhotos1));
						//ver o ficheiro allPhotos a procura do user que publicou a foto que vai levar o like
						String line;
						String pathFoto = null;
						Cliente publisher = new Cliente(null, null, null);
						while ((line = bR1.readLine()) != null) {
							//user::path
							String[] splittada = line.split("::");
							String[] subDirs = splittada[1].split(Pattern.quote(File.separator));
							String nomephoto = subDirs[subDirs.length - 1];
							if (nomephoto.equals(phId)) {
								publisher = catClientes.getCliente(splittada[0]);
								pathFoto = splittada[1];
								break;
							}
						}
						if (pathFoto == null || publisher.getUser() == null) {
							outStream.writeObject("There was an error liking the photo");
							System.out.println("Client '" + currentClient.getUser() + "' couldnt like the photo with the path " + pathFoto);
						} else {
							if (publisher.alreadyLiked(currentClient.getUser(), pathFoto)) {
								outStream.writeObject("You already liked that photo! :)");
								System.out.println("Client '" + currentClient.getUser() + "' already liked the photo with the path " + pathFoto);
							} else {
								publisher.putLike(pathFoto, currentClient.getUser());
								outStream.writeObject("You liked the photo with ID " + splittado[1]);
								System.out.println("Client '" + currentClient.getUser() + "' liked the photo with the path " + pathFoto);
							}
						}
						break;
					case "n":
					case "newgroup":
						if (splittado.length > 2) {
							outStream.writeObject("Invalid GroupID, it cannot have spaces. Please try again");
							System.out.println("Client '" + currentClient.getUser() + "' tried to create a new group with an invalid groupID");
						}
						if (!catGrupos.existeGrupo(splittado[1])) {
							catGrupos.addGrupo(splittado[1], currentClient);
							outStream.writeObject("You created a group with ID " + splittado[1] + " (you are the owner)");
							System.out.println("Client '" + currentClient.getUser() + "' created a new group with groupID '" + splittado[1] + "'");
						} else {
							outStream.writeObject("The group with ID " + splittado[1] + " is invalid");
							System.out.println("Client '" + currentClient.getUser() + "' tried to create a new group that already exists");
						}
						break;
					case "a":
					case "addu":
						if (catClientes.existeUser(splittado[1])) {
							if (!catGrupos.existeGrupo(splittado[2])) {
								outStream.writeObject("The group with ID " + splittado[2] + " is invalid!");
								System.out.println("Client '" + currentClient.getUser() + "' tried to add a user to an invalid group");
							} else {
								if (catGrupos.pertenceAoGrupo(currentClient.getUser(), splittado[2])) {
									if (catGrupos.pertenceAoGrupo(splittado[1], splittado[2])) {
										outStream.writeObject(splittado[1] + " already belongs to the group with ID '" + splittado[2] + "'");
										System.out.println("Client '" + currentClient.getUser() + "' tried to add a user that already is in the group '" + splittado[2] + "'");
									} else if (catGrupos.isDono(currentClient, splittado[2])) {
										catGrupos.addMembro(splittado[1], splittado[2]);
										outStream.writeObject("You added the user with ID '" + splittado[1] + "' to the group with ID '" + splittado[2] + "'");
										System.out.println("Client '" + currentClient.getUser() + "' added the user '" + splittado[1] +"' to the group '" + splittado[2] + "'");
									} else {
										outStream.writeObject("You don't have permissions to add the user with ID '" + splittado[1] + "' to the group with ID '" + splittado[2] + "'");
										System.out.println("Client '" + currentClient.getUser() + "' doesnt have permissions to add users to the group " + splittado[2]);
									}
								} else {
									outStream.writeObject("The group with ID " + splittado[2] + "or the user with id '" + splittado[1] + "' is invalid!");
									System.out.println("Client '" + currentClient.getUser() + "' tried to add an invalid user/group");
								}
							}
						} else {
							outStream.writeObject("The user with ID '" + splittado[1] + "' is invalid!");
							System.out.println("Client '" + currentClient.getUser() + "' tried to add an invalid user to a group");
						}
						break;
					case "r":
					case "removeu":
						if (catClientes.existeUser(splittado[1])) {
							if (!catGrupos.existeGrupo(splittado[2])) {
								outStream.writeObject("The group with ID " + splittado[2] + " is invalid!");
								System.out.println("Client '" + currentClient.getUser() + "' tried to remove a user from an invalid group");
							} else {
								if (catGrupos.pertenceAoGrupo(currentClient.getUser(), splittado[2])) {
									if (!catGrupos.pertenceAoGrupo(splittado[1], splittado[2])) {
										outStream.writeObject(splittado[1] + " doesn't belong to the group with ID '" + splittado[2] + "'");
										System.out.println("Client '" + currentClient.getUser() + "' tried to remove a user that isnt in the group '" + splittado[2] + "'");
									} else if (catGrupos.isDono(currentClient, splittado[2])) {
										if (catGrupos.isDono(catClientes.getCliente(splittado[1]), splittado[2])){
											outStream.writeObject("You cant remove yourself from the group with ID '" + splittado[2] + "'");
											System.out.println("Client '" + currentClient.getUser() + "' tried to remove himself from the group '" + splittado[2] + "'");
										} else {
											catGrupos.removeMembro(splittado[1], splittado[2]);
											outStream.writeObject("You removed the user with ID '" + splittado[1] + "' from the group with ID '" + splittado[2] + "'");
											System.out.println("Client '" + currentClient.getUser() + "' removed the user '" + splittado[1] +"' from the group '" + splittado[2] + "'");
										}
										
									} else {
										outStream.writeObject("You don't have permissions to remove the user with ID '" + splittado[1] + "' from the group with ID '" + splittado[2] + "'");
										System.out.println("Client '" + currentClient.getUser() + "' doesnt have permissions to remove users from the group '" + splittado[2] + "'");
									}
								} else {
									outStream.writeObject("The group with ID '" + splittado[2] + "' or the user with id '" + splittado[1] + "' is invalid!");
									System.out.println("Client '" + currentClient.getUser() + "' tried to remove an invalid user/group");
								}
							}
						} else {
							outStream.writeObject("The user with ID '" + splittado[1] + "' is invalid!");
							System.out.println("Client '" + currentClient.getUser() + "' tried to remove an invalid user from a group");
						}
						break;
					case "g":
					case "ginfo":
						ArrayList<String> currentGrupos = currentClient.getGrupos();
						if (splittado.length <= 1) {
							if (currentGrupos.size() > 0) {
								String outDonos = "You are the owner of the groups:\n";
								String outMembro = "You belong to the groups:\n";
								for (int i = 0; i < currentGrupos.size(); ++i) {
									if (catGrupos.isDono(currentClient, currentGrupos.get(i))) {
										outDonos += currentGrupos.get(i) + "\n";
									}
									//ADICIONA AOS MEMBROS
									outMembro += currentGrupos.get(i) + "\n";
								}
								outStream.writeObject(outDonos + outMembro);
							} else {
								outStream.writeObject("You dont belong to any group");
							}
							System.out.println("Client '" + currentClient.getUser() + "' asked for all his groups info");
						} else {
							//VERIFICAR SE GRUPO EXISTE
							if (!catGrupos.existeGrupo(splittado[1])) {
								outStream.writeObject("The group with ID '" + splittado[1] + "' is invalid!");
								System.out.println("Client '" + currentClient.getUser() + "' asked for info from a group that doesnt exists");
							}
							//VERIFICA SE PERTENCE AO GRUPO
							else if (!catGrupos.pertenceAoGrupo(currentClient.getUser(), splittado[1])) {
								outStream.writeObject("The group with ID '" + splittado[1] + "' is invalid!");
								System.out.println("Client '" + currentClient.getUser() + "' asked for info from a group that he doesnt belong to");
							}
							//SE PERTENCE AO GRUPO
							else {
								ArrayList<String> membros = catGrupos.getMembros(splittado[1]);
								String out = "The owner of the group with ID '" + splittado[1] + "' is: " + membros.get(0)
								+ "\nThe members of that group are:\n";
								for (int i = 0; i < membros.size(); ++i) {
									out += membros.get(i) + "\n";
								}
								outStream.writeObject(out);
								System.out.println("Client '" + currentClient.getUser() + "' asked for info from the group '" + splittado[1] + "'");
							}
						}
						break;
					case "m":
					case "msg":
						if (!catGrupos.existeGrupo(splittado[1])) {
							outStream.writeObject("The group with ID '" + splittado[1] + "' is invalid!");
							System.out.println("Client '" + currentClient.getUser() + "' tried to send a message to a group that doesnt exist");
						}
						//VERIFICA SE PERTENCE AO GRUPO
						else if (!catGrupos.pertenceAoGrupo(currentClient.getUser(), splittado[1])) {
							outStream.writeObject("The group with ID '" + splittado[1] + "' is invalid!");
							System.out.println("Client '" + currentClient.getUser() + "' tried to send a message to a group that he doesnt belong to");
						}
						//SE PERTENCE AO GRUPO
						else {
							//MENSAGEM RECEBIDA
							String msg = "";
							for (int i = 2; i < splittado.length; ++i) {
								msg += splittado[i];
								if (i < splittado.length - 1) {
									msg += " ";
								}
							}
							//GUARDAR MENSAGEM NO FICHEIRO XXX_caixa.txt
							catGrupos.guardarMensagem(splittado[1], msg, currentClient);
							outStream.writeObject("You sent a message to the group with ID '" + splittado[1] + "' with the text: " + msg);
							System.out.println("Client '" + currentClient.getUser() + "' sent a message to the group " + splittado[1]);
						}
						break;
					case "c":
					case "collect":
						if (!catGrupos.existeGrupo(splittado[1])) {
							outStream.writeObject("The group with ID '" + splittado[1] + "' is invalid!");
							System.out.println("Client '" + currentClient.getUser() + "' tried to collect the messages from a group that doesnt exist");
						}
						//VERIFICA SE PERTENCE AO GRUPO
						else if (!catGrupos.pertenceAoGrupo(currentClient.getUser(), splittado[1])) {
							outStream.writeObject("The group with ID " + splittado[1] + " is invalid!");
							System.out.println("Client '" + currentClient.getUser() + "' tried to collect the messages from a group that he doesnt belong to");
						}
						//SE PERTENCE AO GRUPO
						else {
							//LER AS MENSAGEM DE GRUPOID - splittado[1]
							//QUE NAO TENHA LIDO AINDA (AINDA APARECE O NOME POR LER NO FICHEIRO)
							ArrayList<String> mensagens = catGrupos.getMensagensPorLer(splittado[1], currentClient);
							if (mensagens.size() <= 0) {
								outStream.writeObject("You dont have any new messages on the group with ID " + splittado[1]);
								System.out.println("Client '" + currentClient.getUser() + "' doesnt have any messages to collect from the group '" + splittado[1] + "'");
							} else {
								//compilacao DE TODAS AS MENSAGEM POR LER (collected)
								String output2 = "";
								for (int i = 0; i < mensagens.size(); ++i) {
									output2 += mensagens.get(i) + "\n";
								}
								outStream.writeObject(output2);
								System.out.println("Client '" + currentClient.getUser() + "' collected his messages from the group '" + splittado[1] + "'");
							}
						}
						break;
					case "h":
					case "history":
						if (!catGrupos.existeGrupo(splittado[1])) {
							outStream.writeObject("The group with ID '" + splittado[1] + "' is invalid!");
							System.out.println("Client '" + currentClient.getUser() + "' tried to get the message history from a group that doesnt exist");
						}
						//VERIFICA SE PERTENCE AO GRUPO
						else if (!catGrupos.pertenceAoGrupo(currentClient.getUser(), splittado[1])) {
							outStream.writeObject("The group with ID '" + splittado[1] + "' is invalid!");
							System.out.println("Client '" + currentClient.getUser() + "' tried to get the message history from a group that he doesnt belong to");
						}
						//SE PERTENCE AO GRUPO
						else {
							//RETORNAR O HISTORICO
							ArrayList<String> mensagens = catGrupos.getMensagensJaLidas(splittado[1], currentClient);
							if (mensagens.size() <= 0) {
								outStream.writeObject("You dont have any old messages on the group with ID '" + splittado[1] + "'");
								System.out.println("Client '" + currentClient.getUser() + "' doesnt have any message history from the group '" + splittado[1] + "'");
							} else {
								//compilacao de todas as mensagens lidas (historico)
								String output3 = "";
								for (int i = 0; i < mensagens.size(); ++i) {
									output3 += mensagens.get(i) + "\n";
								}
								outStream.writeObject(output3);
								System.out.println("Client '" + currentClient.getUser() + "' got the message history from the group '" + splittado[1] + "'");
							}
						}
						break;
					default:
						outStream.writeObject("Invalid command, please type help to check the available ones");
						System.out.println("Client '" + currentClient.getUser() + "' typed an invalid command");
						break;
					}
				}
			} catch (IOException e) {
				System.out.println("Client '"+user+"' disconnected.");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SignatureException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
