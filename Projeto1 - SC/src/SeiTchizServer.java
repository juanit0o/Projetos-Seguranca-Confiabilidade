import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class SeiTchizServer {

	private CatalogoClientes catClientes;

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
					catClientes.addClient(user, password);
				}

				//"autenticar" utilizador (vai ser diferente) = ver se existe no ficheiro
				outStream.writeObject(autenticou);
				System.out.println(autenticou ? "Client '" + user + "' authenticated." :
					"Client '" + user + "' not authenticated.");

				//GET CURRENT CLIENT
				Cliente currentClient = catClientes.getCliente(user);


				//guardar user e password no ficheiro (criar metodo a parte para isto)
				//ver se o ficheiro existe:  (caso ainda nao exista) criar ficheiro e adicionar o user:username:pass a uma linha
				//caso o ficheiro ja exista, dar append a user:username:pass a uma linha
				//
				while(true) {
					String comando = "";
					try {
						comando = (String) inStream.readObject();
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
						
						if(currentClient.getID() == Integer.parseInt(splittado[1])) {
							outStream.writeObject("You can't follow yourself");
							System.out.println("Client " + currentClient.getID() + " tried to follow himself/herself");
						} else if(catClientes.existeID(Integer.parseInt(splittado[1]))){
							Cliente seguirClient = catClientes.getCliente(Integer.parseInt(splittado[1]));
							if(currentClient.seguir(seguirClient)){
								outStream.writeObject("You followed " + splittado[1]);
								System.out.println("The client "+currentClient.getID() + " followed " + splittado[1]);
							} else {
								outStream.writeObject("You already follow " + splittado[1]);
								System.out.println("The client "+currentClient.getID() + " already follows " + splittado[1]);
							}
						} else {
							outStream.writeObject(splittado[1] + " does not exist!");
							System.out.println("The client "+currentClient.getID() + " tried to follow " + splittado[1] + " but user doesn't exist");
						}
						break;
						
					case "u":
					case "unfollow":
						//ver se o id existe, se n exisitir diz isso
						//ver se tamos com follow nele, se n tivermos diz isso
						//caso estivermos, tirar do manel.txt (ficheiro pessoal)
						if(currentClient.getID() == Integer.parseInt(splittado[1])) {
							outStream.writeObject("You can't unfollow yourself");
							System.out.println("Client " + currentClient.getID() + " tried to unfollow himself/herself");
						}else if(catClientes.existeID(Integer.parseInt(splittado[1]))){
							Cliente unfollowClient = catClientes.getCliente(Integer.parseInt(splittado[1]));
							if(currentClient.deixarDeSeguir(unfollowClient)){
								outStream.writeObject("You unfollowed " + splittado[1]);
								System.out.println("The client "+currentClient.getID() + " unfollowed " + splittado[1]);
							} else {
								outStream.writeObject("You don't follow " + splittado[1]);
								System.out.println("The client "+currentClient.getID() + " doesn't follow " + splittado[1]);
							}
						} else {
							outStream.writeObject(splittado[1] + " does not exist!");
							System.out.println("The client "+currentClient.getID() + " tried to unfollow " + splittado[1] + " but user doesn't exist");
						}
						break;
						
					case "v":
					case "viewfollowers":
						ArrayList<Integer> followers = currentClient.getFollowers();
						if(followers.isEmpty()) {
							outStream.writeObject("You don't have followers");
							System.out.println("The client "+currentClient.getID() + " doesn't have followers ");
						}else {
							outStream.writeObject("Your followers are " + followers.toString());
						}
						break;
						
					case "p":
					case "post":
						//confirmar a parte de (envia foto para perfil do cliente armazenado no sv)
						//sera apenas copiar uma foto de uma diretoria qq para o txt pessoal? como se poe uma foto num txt sequer? fica so na mm pasta (manel1.jpeg?)
						
						//cliente vai ter de ter um atributo com as fotos postadas (wall)
						outStream.writeObject("You posted the photo " + splittado[1]); //tem de ir a diretoria da foto e copiar para o mural
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
						outStream.writeObject("You created a group with ID " + splittado[1] + " (you are the owner)");
						break;
					
					case "a":
					case "addu":
						outStream.writeObject("You added the user with ID " + splittado[1] + " to the group with ID "+ splittado[2]);
						break;
					
					case "r":
					case "removeu":
						outStream.writeObject("You removed the user with ID " + splittado[1] + " from the group with ID "+ splittado[2]);
						break;
					
					case "g":
					case "ginfo":
						//se nao for dado groupID, mostra os grupos que o user eh dono e os grupos a q pertence (caso n pertenca a nada nem seja dono, dar essa msg)
						outStream.writeObject("You are the owner of the groups ..."); //grupos de que eh dono
						outStream.writeObject("You belong to the groups ..."); //grupos a que pertence
						//se estes dois forem vazios, dar uma msg a dizer que n ha nada
						
						//se for dado groupID, mostra o dono desse grupo e os membros do grupo, caso ele pertenca (dono ou nao)
						outStream.writeObject("You belong to the groups ..."); //membros do grupo e dono
						//se n pertencer ao grupo do id, diz que eh privado e n tem acesso
						outStream.writeObject("This group is private and you aren't in it"); //membros do grupo e dono
						
						break;
					
					case "m":
					case "msg":
						//se nao for dado groupID, mostra os grupos que o user eh dono e os grupos a q pertence (caso n pertenca a nada nem seja dono, dar essa msg)
						outStream.writeObject("You sent a message to the group with ID " + splittado[1] + " with the text: " + splittado[2]);	
						break;	
					
					case "c":
					case "collect":
						//se nao for dado groupID, mostra os grupos que o user eh dono e os grupos a q pertence (caso n pertenca a nada nem seja dono, dar essa msg)
						outStream.writeObject("You received all the messages that were pending on the group with ID " + splittado[1]); 	
						//mostrar as msgs que recebeu da caixa
						break;
						
					case "h":
					case "history":
						outStream.writeObject("The messages from thr group with ID " + splittado[1] + " you have already read and are in your history are " + "msgshistorico"); 	
						//mostrar as msgs que ja leu
						break;
						
					default:
						outStream.writeObject("Invalid command, please type help to check the available ones");
						break;
					}
				}
				//loop para comandos sincronizado com client



				//fechar as streams


			} catch (IOException e) {
				//e.printStackTrace();
				System.out.println("Client '"+user+"' disconnected.");
			}
		}
	}
}
