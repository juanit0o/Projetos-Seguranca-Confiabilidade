package Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;

/**
 * Classe representativa de um grupo, que e composto por um id, lista de
 * clientes membros, lista de mensagens, lista de mensagens em historico,
 * cliente dono do grupo, ficheiro de grupo, log de mensagens, ficheiro de
 * membros e ficheiro de historico mensagens.
 * 
 * @author Diogo Pinto 52763
 * @author Francisco Ramalho 53472
 * @author Joao Funenga 53504
 *
 */
public class Grupo {

	private String grupoID;
	private ArrayList<Cliente> membros;
	private ArrayList<Mensagem> msgs;
	private ArrayList<Mensagem> historicoMsgs;
	private Cliente dono; // quem cria o grupo
	private File groupFolder;
	private File msgLog;
	private File membrosGrupo;
	private File msgHistorico;
	private File grupoChaves;
	private File groupKeysFolder;

	private String keyStoreFile;
	private String keyStorePassword;

	/**
	 * Construtor da classe que inicia um grupo recebendo um id de grupo e cliente
	 * dono.
	 * 
	 * @param grupoID - id de grupo.
	 * @param dono    - cliente dono do grupo.
	 */
	public Grupo(String grupoID, Cliente dono, String keyStoreFile, String keyStorePassword) {
		this.grupoID = grupoID;
		this.dono = dono;
		this.membros = new ArrayList<Cliente>();
		membros.add(dono);
		this.msgs = new ArrayList<Mensagem>();
		this.historicoMsgs = new ArrayList<Mensagem>();
		this.groupFolder = new File("data" + File.separator + "Group Folder" + File.separator + this.grupoID);
		this.msgLog = new File(groupFolder.getAbsolutePath(), this.grupoID + "_" + "caixa" + ".cif");
		this.membrosGrupo = new File(groupFolder.getAbsolutePath(), this.grupoID + "_" + "membros" + ".cif");
		this.msgHistorico = new File(groupFolder.getAbsolutePath(), this.grupoID + "_" + "historico" + ".cif");

		this.keyStoreFile = keyStoreFile;
		this.keyStorePassword = keyStorePassword;

		this.groupKeysFolder = new File("GroupKeys");
		this.grupoChaves = new File(groupKeysFolder.getAbsolutePath(), this.grupoID + "_" + "chaves" + ".txt");
	}

	/**
	 * Construtor da classe que inicia um grupo recebendo um id de grupo e cliente
	 * dono. Recebendo tambem listas de membros, mensagens e historico.
	 * 
	 * @param grupoID   - id de grupo.
	 * @param dono      - cliente dono do grupo.
	 * @param membros
	 * @param msgs
	 * @param historico
	 */
	public Grupo(String grupoID, Cliente dono, ArrayList<Cliente> membros, ArrayList<Mensagem> msgs,
			ArrayList<Mensagem> historico, String keyStoreFile, String keyStorePassword) {
		this.grupoID = grupoID;
		this.dono = dono;
		this.membros = membros;
		this.msgs = msgs;
		this.historicoMsgs = historico;
		this.groupFolder = new File("data" + File.separator + "Group Folder" + File.separator + this.grupoID);
		this.msgLog = new File(groupFolder.getAbsolutePath(), this.grupoID + "_" + "caixa" + ".cif");
		this.membrosGrupo = new File(groupFolder.getAbsolutePath(), this.grupoID + "_" + "membros" + ".cif");
		this.msgHistorico = new File(groupFolder.getAbsolutePath(), this.grupoID + "_" + "historico" + ".cif");

		this.keyStoreFile = keyStoreFile;
		this.keyStorePassword = keyStorePassword;

		this.groupKeysFolder = new File("GroupKeys");
		this.grupoChaves = new File(groupKeysFolder.getAbsolutePath(), this.grupoID + "_" + "chaves" + ".txt");

	}

	/**
	 * Metodo que regista um grupo em disco.
	 */
	public void registaGrupo() {
		try {
			groupFolder.mkdirs();
			msgLog.createNewFile();
			membrosGrupo.createNewFile();
			msgHistorico.createNewFile();

			groupKeysFolder.mkdirs();
			grupoChaves.createNewFile();
			// Autenticacao aut = new Autenticacao();
			try {
				File grupChav = new File(groupKeysFolder.getAbsolutePath(), this.grupoID + "_" + "chaves" + ".txt");
				grupChav.createNewFile();

				KeyGenerator kg = KeyGenerator.getInstance("AES");
				kg.init(128);
				SecretKey sharedKey = kg.generateKey();

				Cipher c = Cipher.getInstance("RSA");
				// ir buscar a public key do dono
				Key ku = dono.getPublicKey();
				c.init(Cipher.WRAP_MODE, ku);
				// cifrar a chave secreta que queremos enviar
				byte[] wrappedKey = c.wrap(sharedKey);

				BufferedWriter bW = new BufferedWriter(new FileWriter(grupChav));
				bW.write("0:<" + dono.getUser() + "," + DatatypeConverter.printHexBinary(wrappedKey) + ">");
				bW.newLine();

				bW.close();


			} catch (IOException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			groupContentsToFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Metodo que recebe um cliente e verifica se este e dono do grupo.
	 * 
	 * @param cliente - cliente
	 * @return true se cliente e dono do grupo, senao false.
	 */
	public boolean isDono(Cliente cliente) {
		return cliente.getUser().equals(dono.getUser());
	}

	/**
	 * Metodo que identifica se um cliente recebido pertence ao grupo.
	 * 
	 * @param cliente - cliente
	 * @return true se cliente pertence ao grupo, senao false.
	 */
	public boolean pertenceGrupo(String cliente) {
		for (int i = 0; i < membros.size(); ++i) {
			if (membros.get(i).getUser().equals(cliente)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Metodo que adiciona um cliente ao grupo atual.
	 * 
	 * @param cliente - cliente
	 */
	public void addMembro(Cliente cliente) {
		membros.add(cliente);
		// gerar uma nova chave com o novo cliente
		// fazer cenas magicas com a chave
		// adicionar ao ficheiro das chaves com 1: nova chave
		atualizarGrupoChaves();
		groupContentsToFile();
		cliente.entrarEmGrupo(grupoID, this.keyStoreFile, this.keyStorePassword);
	}

	private void atualizarGrupoChaves() {
		KeyGenerator kg;
		try {
			File grupChav = new File(groupKeysFolder.getAbsolutePath(), this.grupoID + "_" + "chaves" + ".txt");
			// grupChav.createNewFile();

			kg = KeyGenerator.getInstance("AES");
			kg.init(128);
			SecretKey sharedKey = kg.generateKey();

			// cifrar essa chave de grupo com a chave pública de cada um dos membros do
			// grupo (incluindo o novo membro)
			BufferedWriter bW = new BufferedWriter(new FileWriter(grupChav, true));
			bW.write(getLastKeyIndex() + 1 + ":");

			for (int i = 0; i < membros.size(); i++) {
				Cipher c = Cipher.getInstance("RSA");

				Key ku = membros.get(i).getPublicKey();
				// ir buscar a public key do dono
				c.init(Cipher.WRAP_MODE, ku);

				// cifrar a chave secreta que queremos enviar
				byte[] wrappedKey = c.wrap(sharedKey);

				bW.write("<" + membros.get(i).getUser() + "," + DatatypeConverter.printHexBinary(wrappedKey) + ">");
				if (i + 1 < membros.size()) {
					bW.write(";");
				}

			}
			bW.newLine();
			bW.close();

			// enviar para o servidor uma lista com a nova chave de grupo cifrada com as
			// chaves públicas dos membros do grupo.
			// Cada elemento da lista é um par <userID, userID-NewGroupKey>

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Metodo que remove um cliente do grupo atual.
	 * 
	 * @param cliente - cliente
	 */
	public void removeMembro(Cliente cliente) {
		membros.remove(cliente);
		atualizarGrupoChaves();
		groupContentsToFile();
		cliente.sairDeGrupo(grupoID, this.keyStoreFile, this.keyStorePassword);
	}

	/**
	 * Metodo que guarda mensagem enviada de um cliente em listas e em disco.
	 * 
	 * @param msg     - mensagem enviada
	 * @param cliente - cliente
	 */
	public void guardarMensagem(String msg, Cliente cliente) {
		ArrayList<Cliente> membAux = new ArrayList<Cliente>();
		for (int i = 0; i < membros.size(); ++i) {
			membAux.add(membros.get(i));
		}
		msgs.add(new Mensagem(this.grupoID, cliente, msg, membAux, getLastKeyIndex()));
		groupContentsToFile();
	}

	private int getLastKeyIndex() {
		File groupKeys = new File("GroupKeys" + File.separator + this.grupoID + "_chaves.txt");
		BufferedReader br;
		int lastIndex = 0;
		try {
			br = new BufferedReader(new FileReader(groupKeys));
			String thislinha = "";
			while ((thislinha = br.readLine()) != null) {
				lastIndex++;
			}
			return lastIndex - 1;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return lastIndex;
	}

	/**
	 * Metodo que devolve o Id do grupo atual.
	 * 
	 * @return groupID
	 */
	public String getGrupoID() {
		return this.grupoID;
	}

	/**
	 * Metodo devolve lista de id's dos membros do grupo.
	 * 
	 * @return lista de id's de membros.
	 */
	public ArrayList<String> getMembros() {
		ArrayList<String> output = new ArrayList<String>();
		for (int i = 0; i < membros.size(); ++i) {
			output.add(membros.get(i).getUser());
		}
		return output;
	}

	/**
	 * Metodo devolve as mensagens por ler de um determinado cliente dentro do
	 * grupo.
	 * 
	 * @param cliente - cliente a verificar
	 * @return lista de mensagens por ler
	 */
	public ArrayList<String> getMensagensPorLer(String cliente) {
		ArrayList<String> output = new ArrayList<String>();
		for (int i = 0; i < msgs.size(); ++i) {
			if (msgs.get(i).porLerMensagem(cliente)) {
				output.add(msgs.get(i).toString());

				// Verificar se todos ja leram, caso sim remover do msgs
				if (msgs.get(i).jaLeramTodos()) {
					historicoMsgs.add(msgs.get(i));
					msgs.remove(msgs.get(i));
					--i;
				}

			}
		}
		groupContentsToFile();
		return output;
	}

	/**
	 * Metodo devolve as mensagens lidas de um determinado cliente dentro do grupo.
	 * 
	 * @param cliente - cliente a verificar
	 * @return lista de mensagens lidas
	 */
	public ArrayList<String> getMensagensJaLidas(String cliente) {
		ArrayList<String> output = new ArrayList<String>();
		// FICHEIRO HISTORICO
		for (int i = 0; i < historicoMsgs.size(); ++i) {
			if (historicoMsgs.get(i).jaLeuMensagem(cliente)) {
				output.add(historicoMsgs.get(i).toString());
			}
		}
		// FICHEIRO CAIXA
		for (int i = 0; i < msgs.size(); ++i) {
			if (msgs.get(i).jaLeuMensagem(cliente)) {
				output.add(msgs.get(i).toString());
			}
		}
		return output;
	}

	/**
	 * Metodo que escreve os dados de um grupo em disco.
	 */
	public void groupContentsToFile() {
		// File membrosGrupo = new File(groupFolder.getAbsolutePath(), this.grupoID +
		// "_" + "membros" + ".cif");
		Autenticacao aut = new Autenticacao();
		try {
			File membrosFich;
			if (membrosGrupo.length() > 0) {
				aut.decryptFile(membrosGrupo, this.keyStoreFile, this.keyStorePassword);
				membrosFich = new File(groupFolder.getAbsolutePath(), this.grupoID + "_" + "membros" + ".txt");

			} else {
				membrosFich = new File(groupFolder.getAbsolutePath(), this.grupoID + "_" + "membros" + ".txt");
				membrosFich.createNewFile();
			}

			BufferedWriter bW = new BufferedWriter(new FileWriter(membrosFich));
			// membros
			for (int i = 0; i < membros.size(); i++) {
				bW.write(membros.get(i).getUser());
				bW.newLine();
			}
			bW.close();
			aut.encryptFile(membrosFich, this.keyStoreFile, this.keyStorePassword);

		} catch (IOException e) {
			e.printStackTrace();
		}
		// File caixaMsgGrupo = new File(groupFolder.getAbsolutePath(), this.grupoID +
		// "_" + "caixa" + ".cif");
		try {
			File caixaFich;
			if (msgLog.length() > 0) {
				aut.decryptFile(msgLog, keyStoreFile, keyStorePassword);
				caixaFich = new File(groupFolder.getAbsolutePath(), this.grupoID + "_" + "caixa" + ".txt");
			} else {
				caixaFich = new File(groupFolder.getAbsolutePath(), this.grupoID + "_" + "caixa" + ".txt");
				caixaFich.createNewFile();
			}

			BufferedWriter bW = new BufferedWriter(new FileWriter(caixaFich));
			// MENSAGEM COM ELEMENTOS
			for (int i = 0; i < msgs.size(); i++) {
				bW.write(msgs.get(i).msgContentToFile());
				bW.newLine();
			}
			bW.close();

			aut.encryptFile(caixaFich, keyStoreFile, keyStorePassword);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
