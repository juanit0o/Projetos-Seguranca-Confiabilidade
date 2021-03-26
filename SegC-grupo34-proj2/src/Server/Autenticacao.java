package Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;

public class Autenticacao {

	/**
	 * Gera um Nonce
	 * @return Nonce Long
	 */
	public long generateNonce() {
		long nonce = (long) (Math.floor(Math.random() * (99999999 - 10000000 + 1)) + 10000000);
		return nonce;
	}

	/**
	 * Devolve o certificado de um user
	 * @param user - cliente
	 * @return certificado
	 */
	public Certificate getCertificate(String user) {

		try {
			FileInputStream fis = new FileInputStream("PubKeys" + File.separator + user + ".cer");
			CertificateFactory cf = CertificateFactory.getInstance("X509");
			Certificate cert = cf.generateCertificate(fis);
			return cert;

		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * Desencripta um ficheiro .cif (para um ficheiro .txt)
	 * @param fileUser - Ficheiro a desencriptar
	 * @param keyStoreFile Path para o ficheiro keystore
	 * @param keyStorePassword Password para o ficheiro keystore
	 */
	public void decryptFile(File fileUser, String keyStoreFile, String keyStorePassword) {
		try {
			System.out.println("File to decrypt: "+ fileUser.getName());
			//ir buscar a chave
			FileInputStream kfile = new FileInputStream("data" + File.separator + keyStoreFile);
			KeyStore kstore = KeyStore.getInstance("JKS"); //try
			kstore.load(kfile,keyStorePassword.toCharArray());
			PrivateKey myPrivateKey = (PrivateKey) kstore.getKey(keyStoreFile, keyStorePassword.toCharArray());
			
			//fazer unwrap com chave privada 
			Cipher c = Cipher.getInstance("RSA");
			c.init(Cipher.UNWRAP_MODE, myPrivateKey);
			
			File grupChav = new File(fileUser.getPath().substring(0,fileUser.getPath().length() - 4) + "_chave" + ".txt");
			BufferedReader br = new BufferedReader(new FileReader(grupChav));
			
			String linha = br.readLine();
			byte[] stringToByte = DatatypeConverter.parseHexBinary(linha);	
			
			Key unwrappedKey = c.unwrap(stringToByte, "AES", Cipher.SECRET_KEY);
			
			//com a chave simetrica usar para decifrar o fileUser
			Cipher cDec = Cipher.getInstance("AES");
			cDec.init(Cipher.DECRYPT_MODE, unwrappedKey);
			
			FileInputStream fis = new FileInputStream(fileUser.getPath());
			CipherInputStream cis = new CipherInputStream(fis, cDec);

			//tratamento do path novo para o .txt
			int index = fileUser.getPath().lastIndexOf(".");
			String fileinfo = fileUser.getPath().substring(0,index) + ".txt";

			//stream para escrit no .txt
			FileOutputStream fos = new FileOutputStream(fileinfo,false);

			byte[] b1 = new byte[16];
			int read = -1;
			while((read = cis.read(b1))!= -1) {
				fos.write(b1, 0, read);
			}

			fis.close();
			fos.close();
			kfile.close();
			cis.close();
			br.close();

			grupChav.delete();

		} catch (Exception e1) {
			System.out.println("Error fetching Server keystore");
			e1.printStackTrace();
			System.exit(-1);
		} 
	}

	/**
	 * Encripta um ficheiro .txt (para um ficheiro .cif)
	 * @param fileUser Ficheiro a encriptar
	 * @param keyStoreFile Path para o ficheiro keystore
	 * @param keyStorePassword Password para o ficheiro keystore
	 */
	public void encryptFile(File fileUser, String keyStoreFile, String keyStorePassword) {
		try { 
			System.out.println("File to encrypt: "+fileUser.getName());
			FileInputStream kfile = new FileInputStream("data"+ File.separator + keyStoreFile);
			KeyStore kstore = KeyStore.getInstance("JKS"); //try
			kstore.load(kfile,keyStorePassword.toCharArray());

			Certificate cert = kstore.getCertificate(keyStoreFile);
			PublicKey pubKey = cert.getPublicKey();
			
			File grupChav = new File(fileUser.getPath().substring(0,fileUser.getPath().length() - 4) + "_chave" + ".txt");
			grupChav.createNewFile();

			KeyGenerator kg = KeyGenerator.getInstance("AES");
			kg.init(128);
			SecretKey sharedKey = kg.generateKey();

			//cifrar ficheiro com simetrica
			Cipher cEnc = Cipher.getInstance("AES");
			cEnc.init(Cipher.ENCRYPT_MODE, sharedKey);
			FileInputStream fis = new FileInputStream(fileUser);

			int index = fileUser.getPath().lastIndexOf(".");
			String fcif = fileUser.getPath().substring(0,index) + ".cif";
		
			FileOutputStream fos = new FileOutputStream(fcif,false);
			CipherOutputStream cos = new CipherOutputStream(fos, cEnc);
			
			byte[] b = new byte[16];
			int i = fis.read(b);
			while(i!= -1) {
				cos.write(b,0,i);
				i = fis.read(b);
			}
			cos.close();
			fis.close();
			kfile.close();

			//fazer wrap a chave simetrica e guardar num ficheiro
			Cipher c = Cipher.getInstance("RSA");
			c.init(Cipher.WRAP_MODE, pubKey);
			byte[] wrappedKey = c.wrap(sharedKey);
			BufferedWriter bW = new BufferedWriter(new FileWriter(grupChav));
			bW.write(DatatypeConverter.printHexBinary(wrappedKey));
			bW.newLine();
			bW.close();
			
			fileUser.delete();
		} catch (Exception e1) {
			e1.printStackTrace();
			System.exit(-1);
		} 

	}
}
