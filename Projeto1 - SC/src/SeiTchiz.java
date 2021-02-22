import java.net.Socket;
import java.util.Scanner;
import java.io.*;

public class SeiTchiz {
	private static Socket cSoc = null;
	private static final int PORT_DEFAULT = 45678;

	private static ObjectInputStream inObj = null;
	private static ObjectOutputStream outObj = null;

	private static final Scanner inSc = new Scanner(System.in);

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
			case "p":
			case "post":
			
				//pegar no path, ir ao path, converter foto para bytes, enviar bytes para o server
				try {
					//enviar comando
                	 outObj.writeObject("post");
					 String photoPath = comando[1]; //path para onde se encontra a fotografia
					 for(int i = 2; i < comando.length; ++i){
						photoPath += " " + comando[i];
					 }
					 
					 File myPhoto = new File(photoPath);
					 if(myPhoto.exists()) {
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
						 
					 }else {
						 System.out.println("The file with the path " + photoPath +  " doesn't exist");
						 System.out.println("\nInsert a command or type help to see commands: ");
					 }
					  
			  
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		        
				break;
			
			case "w":
			case "wall":
				
				File wallFolder = new File("..\\Wall\\"+ user);
				if(!wallFolder.mkdirs()) { //se a pasta ja tiver criada
					String[] entries = wallFolder.list();
					for(String s: entries){
					    File currentFile = new File(wallFolder.getPath(),s);
					    currentFile.delete();
					}
				}
				
				
				
				try {
					outObj.writeObject("wall "+comando[1]);
					int nrFotos = (int) inObj.readObject();
					
					for(int i = 0; i < nrFotos; i++) {
						File fileName = new File(wallFolder.getAbsolutePath(),"wall_"+user
						+ "_"+ i + ".jpg");
						
						OutputStream photoRecebida = new BufferedOutputStream(new FileOutputStream(fileName));
						Long dimensao; //ADICIONADO
						try {
							dimensao = (Long) inObj.readObject();
							byte[] buffer = new byte[dimensao.intValue()];
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

				break;
				
			default: //QUANDO O SERVIDOR SE DESLIGA E VOLTA A LIGAR, O CLIENTE JA NAO CONSEGUE COMUNICAR C ELE, TENTAR LIGA-LOS OUTRA X
				try {
					outObj.writeObject(output);
					System.out.println((String) inObj.readObject());
					System.out.println("\nInsert a command or type help to see commands: ");
				} catch (IOException | ClassNotFoundException e) {
					System.out.println("The server is now offline :(");
					//e.printStackTrace();
				}
				break;
			}
		}
	}

	private static void autenticacao(String user, String pass) {
		try {
			outObj.writeObject(user);
			outObj.writeObject(pass);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}

		System.out.println("Cliente enviou nome e pass");

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

	private static void conectToServer(String ip, int port) {
		try {
			cSoc = new Socket(ip,port);
			outObj = new ObjectOutputStream(cSoc.getOutputStream());
			inObj = new ObjectInputStream(cSoc.getInputStream());
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
