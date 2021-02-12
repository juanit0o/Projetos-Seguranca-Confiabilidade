import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner; 

public class SeiTchizServer {

	public static void main(String[] args) {
		System.out.println("Server");
		SeiTchizServer server = new SeiTchizServer(); //o construtor disto vai estar nesta classe ou vai haver outra para isso?

		//loop até acertarem com o tamanho ? ou fechar logo?
		if (args.length != 1) {
			System.out.println("Server is started by typing 'SeiTchizServer (PORT)'!");
			System.exit(-1);
		}
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
			System.out.println("1 Thread por cada cliente");
		}



		public void run(){
			try {
				ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());

				//Método loginUser e autenticar (criar um obj da classe Cliente com esses atributos, e probs mais alguns a ser preenchidos dps lá)
				/*
                String user = null;
                String password = null;


                try {
                    user = (String)inStream.readObject();
                    password = (String)inStream.readObject();
                    System.out.println("Servidor: já recebi a password e o user");
                }catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                }

                //"autenticar" utilizador (vai ser diferente) = ver se existe no ficheiro de users e passwords
                if (user.length() != 0){
                    outStream.writeObject(new Boolean(true));
                    System.out.println("Username ligado: " + user);
                    System.out.println("Com a password: " + password);
                }
                else {
                    outStream.writeObject(new Boolean(false));
                }

                //guardar user e password no ficheiro (criar metodo a parte para isto)
                //ver se o ficheiro existe:  (caso ainda nao exista) criar ficheiro e adicionar o user:username:pass a uma linha
                //caso o ficheiro ja exista, dar append a user:username:pass a uma linha
                //
				 */


				//fechar as streams
				outStream.close();
				inStream.close();

				//fechar socket
				socket.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public boolean loginUser() {
			return true;
		}

		//switch case aqui ou noutra classe com todos os comandos que o cliente suporta para poder mandar para o servidor? (verificar qual foi o comando recebido entrar no switch e fazer o esperado)


	}
}
