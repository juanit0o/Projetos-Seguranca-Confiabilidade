import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class SeiTchizServer {

	private CatalogoClientes catClientes;

	public static void main(String[] args) {
		System.out.println("Server");
		SeiTchizServer server = new SeiTchizServer(); //o construtor disto vai estar nesta classe ou vai haver outra para isso?

		//loop até acertarem com o tamanho ? ou fechar logo?
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

				//Método loginUser e autenticar (criar um obj da classe Cliente com esses atributos,
				//e probs mais alguns a ser preenchidos dps lá)

				
				String password = null;
				Boolean autenticou = false;

				try {
					user = (String) inStream.readObject();
					password = (String) inStream.readObject();
					//System.out.println("Servidor: já recebi a password e o user");
					//System.out.println("Info client: "+user+":"+password);
				}catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}

				//aqui comparar se ja existe user
				if (catClientes.existeUser(user)) {
					autenticou = catClientes.passCorreta(user, password);
				} else {//adicionar à lista
					autenticou = true;
					catClientes.addClient(user, password);
				}

				//"autenticar" utilizador (vai ser diferente) = ver se existe no ficheiro
				outStream.writeObject(autenticou);
				System.out.println(autenticou ? "Client '"+user+"' authenticated.":
					"Client '"+user+"' not authenticated.");


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
					switch (comando) {
					case "quit":
					case "exit":
						outStream.writeObject("Server ending for you...");
						outStream.close();
						inStream.close();
						socket.close();
						break;

					default:
						outStream.writeObject("toma la um croquete...");
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
