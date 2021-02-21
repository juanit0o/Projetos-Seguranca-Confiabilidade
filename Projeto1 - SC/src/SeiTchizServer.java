import java.awt.image.BufferedImage;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;

import javax.imageio.ImageIO;

public class SeiTchizServer {

	private CatalogoClientes catClientes;
	private CatalogoGrupos catGrupos;

	public static void main(String[] args) {
		System.out.println("Server");
		SeiTchizServer server = new SeiTchizServer(); //o construtor disto vai estar nesta classe ou vai haver outra para isso?

		//loop at� acertarem com o tamanho ? ou fechar logo?
		if (args.length != 1) {
			System.out.println("Server is started by typing 'SeiTchizServer (PORT)'!");
			System.exit(-1);
		}
		System.out.println("- - - - - - - - - - -");
		server.startServer(Integer.parseInt(args[0])); //nice
		//
	}

	//metodo para iniciar o servidor
	public void startServer (int port){
		ServerSocket sSoc = null;
		try {
			sSoc = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}
		catClientes = new CatalogoClientes();
		catGrupos = new CatalogoGrupos(catClientes);
		//servidor vai estar em loop a receber comandos dos clientes sem se desligar
		while(true) {
			try {
				Socket inSoc = sSoc.accept();
				ServerThread newServerThread = new ServerThread(inSoc);
				newServerThread.start();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		//deveremos fechar aqui? ter um metodo para fechar maybe?
		//sSoc.close();
	}


	//Threads utilizadas para comunicacao com os clientes
	class ServerThread extends Thread {

		private Socket socket = null;

		ServerThread(Socket inSoc) {
			socket = inSoc;
			//System.out.println("1 Thread por cada cliente");
		}


		public void run(){
			String user = null;
			try {
				ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());

				//M�todo loginUser e autenticar (criar um obj da classe Cliente com esses atributos,
				//e probs mais alguns a ser preenchidos dps l�)
				
				String password = null;
				Boolean autenticou = false;

				try {
					user = (String) inStream.readObject();
					password = (String) inStream.readObject();
					//System.out.println("Servidor: j� recebi a password e o user");
					//System.out.println("Info client: "+user+":"+password);
				}catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}

				//aqui comparar se ja existe user
				if (catClientes.existeUser(user)) {
					autenticou = catClientes.passCorreta(user, password);
				} else {//adicionar � lista
					autenticou = true;
					catClientes.addClient(user, password, outStream, inStream);
				}

				//"autenticar" utilizador (vai ser diferente) = ver se existe no ficheiro
				outStream.writeObject(String.valueOf(autenticou));
				System.out.println(autenticou ? "Client '" + user + "' authenticated." :
					"Client '" + user + "' not authenticated.");

				//GET CURRENT CLIENT
				Cliente currentClient = catClientes.getCliente(user);


				//guardar user e password no ficheiro (criar metodo a parte para isto)
				//ver se o ficheiro existe:  (caso ainda nao exista) criar ficheiro e adicionar o user:username:pass a uma linha
				//caso o ficheiro ja exista, dar append a user:username:pass a uma linha
				//
				while(true) {
					String comando = null;
					try {
						//System.out.println(inStream.readObject());
						comando = (String) inStream.readObject(); //TA A RECEBER MAL PARA O COMANDO DA PHOTO (PHOTO + PATH)
						System.out.println(comando);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					
					//TRATAR DISTO, PARA QUANDO SO MANDAMOS FOLLOW (SEM ARGS DEPOIS) D� OUT OF BOUNDS
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
						//primeiro de tudo ver se o id dado existe, ver se aparece no ficheiro allUsers, se n exisitir da logo erro
						//ver ficheiro do cliente, ver se ja tem follow do gajo, se ja la tiver diz
						//se n tiver follow, adiciona ao arraylist (ver se � preciso ou apenas trabalhamos com os txts), adiciona ao txt na parte dos follows.
						
						if(currentClient.getUser().equals(splittado[1])) {
							outStream.writeObject("You can't follow yourself");
							System.out.println("Client " + currentClient.getUser() + " tried to follow himself/herself");
						} else if(catClientes.existeUser(splittado[1])){ 
							Cliente seguirClient = catClientes.getCliente(splittado[1]);
							if(currentClient.seguir(seguirClient)){
								outStream.writeObject("You followed " + splittado[1]);
								System.out.println("The client "+currentClient.getUser() + " followed " + splittado[1]);
							} else {
								outStream.writeObject("You already follow " + splittado[1]);
								System.out.println("The client "+currentClient.getUser() + " already follows " + splittado[1]);
							}
						} else {
							outStream.writeObject(splittado[1] + " does not exist!");
							System.out.println("The client "+currentClient.getUser() + " tried to follow " + splittado[1] + " but user doesn't exist");
						}
						break;
						
					case "u":
					case "unfollow":
						//ver se o id existe, se n exisitir diz isso
						//ver se tamos com follow nele, se n tivermos diz isso
						//caso estivermos, tirar do manel.txt (ficheiro pessoal)
						if(currentClient.getUser().equals(splittado[1])) {
							outStream.writeObject("You can't unfollow yourself");
							System.out.println("Client " + currentClient.getUser() + " tried to unfollow himself/herself");
						}else if(catClientes.existeUser(splittado[1])){
							Cliente unfollowClient = catClientes.getCliente(splittado[1]);
							if(currentClient.deixarDeSeguir(unfollowClient)){
								outStream.writeObject("You unfollowed " + splittado[1]);
								System.out.println("The client "+currentClient.getUser() + " unfollowed " + splittado[1]);
							} else {
								outStream.writeObject("You don't follow " + splittado[1]);
								System.out.println("The client "+currentClient.getUser() + " doesn't follow " + splittado[1]);
							}
						} else {
							outStream.writeObject(splittado[1] + " does not exist!");
							System.out.println("The client "+currentClient.getUser() + " tried to unfollow " + splittado[1] + " but user doesn't exist");
						}
						break;
						
					case "v":
					case "viewfollowers":
						ArrayList<String> followers = currentClient.getFollowers();
						if(followers.isEmpty()) {
							outStream.writeObject("You don't have followers");
							System.out.println("The client "+currentClient.getUser() + " doesn't have followers ");
						}else {
							outStream.writeObject("Your followers are " + followers.toString());
						}
						break;
						
					case "p":
					case "post":
						//confirmar a parte de (envia foto para perfil do cliente armazenado no sv)
						//sera apenas copiar uma foto de uma diretoria qq para o txt pessoal? como se poe uma foto num txt sequer? fica so na mm pasta (manel1.jpeg?)
						
						//limitar o tipo de ficheiros a jpg, png, photomanel0.png


						File photoFolder = new File("..\\data\\Personal User Files\\"+ user + "\\Photos");
						File fileName = new File(photoFolder.getAbsolutePath(),"photo_"+currentClient.getUser()
								+ "_"+currentClient.nrOfPhotos() + ".jpg");

						OutputStream photoRecebida = new BufferedOutputStream(new FileOutputStream(fileName));

						//byte[] buffer = new byte[2048];
						Long dimensao = new Long(0); //ADICIONADO
						try {
							dimensao = (Long) inStream.readObject();

							int temp = dimensao.intValue();

							while(inStream.available() > 0) {
								byte[] buffer = new byte[temp >= 1024 ? 1024 : temp];

								int x = inStream.read(buffer, 0, temp >= 1024 ? 1024 : temp);
								photoRecebida.write(buffer, 0, x);
								temp -= x;
							}

							//adicionar informacao da fotografia (nome) ao ficheiro pessoal info.txt
							
							currentClient.publishPhoto(fileName);
							
							File fileDirectory = new File("..\\data\\Server Files");
							File filePhotos = new File(fileDirectory.getAbsolutePath(), "allPhotos.txt");
							//allPhotos
							BufferedWriter bW = new BufferedWriter(new FileWriter(filePhotos,true)); 
						
							bW.write(currentClient.getUser() + "::" + filePhotos.getAbsolutePath()); //userID, 2*dois pontos, photoPath
							bW.newLine();
							bW.close();
							
							//por isto antes do publish foto caso n funcione 
							outStream.writeObject("File was submitted with success in " + photoFolder.getAbsolutePath());							
							photoRecebida.close();
							
							System.out.println("Fim da foto");
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					
					case "w" :
					case "wall":
						//devolver os ids das n fotografias mais recentes (tem de tar ordenadas) e o nr de likes destas
						//se houverem menos que o n dado apenas mostrar essas
						//se n exisitirem nns dizer isso
						outStream.writeObject("Your recent " + splittado[1] + " photos are " + "mostrarfotos"); //tem de ir a diretoria da foto e copiar para o mural
						break;
						
					case "l":
					case "like":
						//primeiro fazemos wall x para ter os ids das fotos
						//ir ao txt pessoal e adicionar likeFoto1 (p exemplo??), ter mais uma seccao para as fotos que gosta, entre os $$?
						outStream.writeObject("You liked the photo with ID " + splittado[1]);
						break;
						
					case "n":
					case "newgroup":
						//como crl vamos guardar os grupos+membros nos txts, este prob � o mm pas outras merdas p baixo glhf gg
						if(splittado.length > 2) {
							outStream.writeObject("Invalid GroupID, it cannot have spaces. Please try again");
						}

						if(!catGrupos.existeGrupo(splittado[1])){
							catGrupos.addGrupo(splittado[1], currentClient);
							outStream.writeObject("You created a group with ID " + splittado[1] + " (you are the owner)");
						}else {
							outStream.writeObject("The group with ID " + splittado[1] + " is invalid");
						}

						break;
					
					case "a":
					case "addu":

						//userID - splittado[1]
						//groupID - splittado[2]
						if(catClientes.existeUser(splittado[1])){
							if(!catGrupos.existeGrupo(splittado[2])){
								outStream.writeObject("The group with ID " + splittado[2] + " is invalid!");
							} else {
								if(catGrupos.pertenceAoGrupo(currentClient.getUser(), splittado[2])) {
									if(catGrupos.pertenceAoGrupo(splittado[1], splittado[2])){
										outStream.writeObject(splittado[1] + " already belongs to the group with ID " + splittado[2]);
									}else if (catGrupos.isDono(currentClient, splittado[2])) {
										catGrupos.addMembro(splittado[1], splittado[2]);
										outStream.writeObject("You added the user with ID " + splittado[1] + " to the group with ID "+ splittado[2]);
									}else {
										outStream.writeObject("You don't have permissions to add the user with ID " + splittado[1] + " to the group with ID "+ splittado[2]);
									}
								}else {
									outStream.writeObject("The group with ID " + splittado[2] + "or the user with id "+ splittado[1] + " is invalid!");
								}
							}
						} else {
							outStream.writeObject("The user with ID " + splittado[1] + " is invalid!");
						}

						break;
					
					case "r":
					case "removeu":
						
						if(catClientes.existeUser(splittado[1])){
							if(!catGrupos.existeGrupo(splittado[2])){
								outStream.writeObject("The group with ID " + splittado[2] + " is invalid!");
							} else {
								if(catGrupos.pertenceAoGrupo(currentClient.getUser(), splittado[2])) {
									if(!catGrupos.pertenceAoGrupo(splittado[1], splittado[2])){
										outStream.writeObject(splittado[1] + " doesn't belong to the group with ID " + splittado[2]);
									}else if (catGrupos.isDono(currentClient, splittado[2])) {
										catGrupos.removeMembro(splittado[1], splittado[2]);
										outStream.writeObject("You removed the user with ID " + splittado[1] + " from the group with ID "+ splittado[2]);
									}else {
										outStream.writeObject("You don't have permissions to remove the user with ID " + splittado[1] + " from the group with ID "+ splittado[2]);
									}
								}else {
									outStream.writeObject("The group with ID " + splittado[2] + "or the user with id "+ splittado[1] + " is invalid!");
								}
							}
						} else {
							outStream.writeObject("The user with ID " + splittado[1] + " is invalid!");
						}	
						
						break;
					
					case "g":
					case "ginfo":

						//groupID - splittado[1]
						ArrayList<String> currentGrupos = currentClient.getGrupos();

						if(splittado.length <= 1){
							//ASSINALAR SE NAO TEM GRUPOS


							if(currentGrupos.size() > 0){
								String outDonos = "You are the owner of the groups:\n";
								String outMembro = "You belong to the groups:\n";

								for(int i = 0; i < currentGrupos.size(); ++i){
									//SE FOR DONO ADICIONA AO OUTDONO
									if(catGrupos.isDono(currentClient, currentGrupos.get(i))){
										outDonos += currentGrupos.get(i) + "\n";
									}
									//ADICIONA AOS MEMBROS
									outMembro += currentGrupos.get(i) + "\n";
								}
								outStream.writeObject(outDonos + outMembro);


							} else {
								outStream.writeObject("You dont belong to any group");
							}

						} else {
							//VERIFICAR SE GRUPO EXISTE
							if(!catGrupos.existeGrupo(splittado[1])){
								outStream.writeObject("The group with ID " + splittado[1] + " is invalid!");
							}
							//VERIFICA SE PERTENCE AO GRUPO
							else if(!catGrupos.pertenceAoGrupo(currentClient.getUser(), splittado[1])){
								outStream.writeObject("The group with ID " + splittado[1] + " is invalid!");
							}
							//SE PERTENCE AO GRUPO
							else {
								ArrayList<String> membros =  catGrupos.getMembros(splittado[1]);
								String output = "The owner of the group with ID " + splittado[1] + " is: " + membros.get(0)
										+ "\nThe members of that group are:\n";
								for(int i = 0; i < membros.size(); ++i){
									output += membros.get(i) + "\n";
								}
								outStream.writeObject(output);
							}
						}

						break;
					
					case "m":
					case "msg":
						if(!catGrupos.existeGrupo(splittado[1])){
							outStream.writeObject("The group with ID " + splittado[1] + " is invalid!");
						}
						//VERIFICA SE PERTENCE AO GRUPO
						else if(!catGrupos.pertenceAoGrupo(currentClient.getUser(), splittado[1])){
							outStream.writeObject("The group with ID " + splittado[1] + " is invalid!");
						}
						//SE PERTENCE AO GRUPO
						else {

							//MENSAGEM RECEBIDA
							String msg = "";
							for(int i = 2; i < splittado.length; ++i){
								msg += splittado[i];
								if(i < splittado.length - 1){
									msg += " ";
								}
							}
							//GUARDAR MENSAGEM NO FICHEIRO XXX_caixa.txt
							catGrupos.guardarMensagem(splittado[1], msg, currentClient);
							outStream.writeObject("You sent a message to the group with ID " + splittado[1] + " with the text: " + msg);
						}
						break;
					
					case "c":
					case "collect":

						if(!catGrupos.existeGrupo(splittado[1])){
							outStream.writeObject("The group with ID " + splittado[1] + " is invalid!");
						}
						//VERIFICA SE PERTENCE AO GRUPO
						else if(!catGrupos.pertenceAoGrupo(currentClient.getUser(), splittado[1])){
							outStream.writeObject("The group with ID " + splittado[1] + " is invalid!");
						}
						//SE PERTENCE AO GRUPO
						else {

							//LER AS MENSAGEM DE GRUPOID - splittado[1]
							//QUE NAO TENHA LIDO AINDA (AINDA APARECE O NOME POR LER NO FICHEIRO)

							//ARRAYLIST - SE VAZIO
							//USER: mensagens
							ArrayList<String> mensagens = catGrupos.getMensagensPorLer(splittado[1], currentClient);

							if(mensagens.size() <= 0){
								outStream.writeObject("You dont have any new messages on the group with ID " + splittado[1]);

							} else {
								//COMPILAcao DE TODAS AS MENSAGEM POR LER (collected
								String output = "";
								for(int i = 0; i < mensagens.size(); ++i){
									output += mensagens.get(i) + "\n";
								}
								outStream.writeObject(output);
							}

						}
						break;
						
					case "h":
					case "history":
						if(!catGrupos.existeGrupo(splittado[1])){
							outStream.writeObject("The group with ID " + splittado[1] + " is invalid!");
						}
						//VERIFICA SE PERTENCE AO GRUPO
						else if(!catGrupos.pertenceAoGrupo(currentClient.getUser(), splittado[1])){
							outStream.writeObject("The group with ID " + splittado[1] + " is invalid!");
						}
						//SE PERTENCE AO GRUPO
						else {
							//RETORNAR O HISTORICO

							ArrayList<String> mensagens = catGrupos.getMensagensJaLidas(splittado[1], currentClient);

							if(mensagens.size() <= 0){
								outStream.writeObject("You dont have any old messages on the group with ID " + splittado[1]);

							} else {
								//COMPILAÇÃO DE TODAS AS MENSAGEM POR LER (collected
								String output = "";
								for(int i = 0; i < mensagens.size(); ++i){
									output += mensagens.get(i) + "\n";
								}
								outStream.writeObject(output);
							}
						}
						break;
						
					default:
						outStream.writeObject("Invalid command, please type help to check the available ones");
						break;
					}
				}
				//fechar as streams
				//TODO


			} catch (IOException e) {
				//e.printStackTrace();
				System.out.println("Client '"+user+"' disconnected.");
			}
		}
	}
}
