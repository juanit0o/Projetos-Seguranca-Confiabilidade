package Server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Random;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;

public class Autenticacao {

	public long generateNonce() {
		Random rnd = new Random();
		long nonce = (long) (Math.floor(Math.random() * (99999999 - 10000000 + 1)) + 10000000);
		return nonce;
	}

	public Certificate getCertificate(String user) {
		//pasta do lado do sv
		try {
			FileInputStream fis = new FileInputStream("PubKeys" + File.separator + user + ".cer");
			//como dar load do certificado do utilizador
			CertificateFactory cf = CertificateFactory.getInstance("X509");
			Certificate cert = cf.generateCertificate(fis);
			return cert;

		} catch (CertificateException e) {

			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	public void decryptFile(File fileUser ,String keyStoreFile, String keyStorePassword) {
		try {
			System.out.println("------------DECRYPT----------");
			System.out.println("File to decrypt: "+fileUser.getPath());
			//ir buscar a chave
			FileInputStream kfile = new FileInputStream("data"+File.separator+"Server Files"+File.separator+keyStoreFile);
			KeyStore kstore = KeyStore.getInstance("JCEKS"); //try
			kstore.load(kfile,keyStorePassword.toCharArray());
			PrivateKey myPrivateKey = (PrivateKey) kstore.getKey(keyStoreFile, keyStorePassword.toCharArray());
			//iniciar cifra desencriptacao
			Cipher cDec = Cipher.getInstance("RSA");
			cDec.init(Cipher.DECRYPT_MODE, myPrivateKey);

			FileInputStream fis = new FileInputStream(fileUser.getPath());
			CipherInputStream cis = new CipherInputStream(fis, cDec);

			//tratamento do path novo para o .txt
			int index = fileUser.getPath().lastIndexOf(".");
			String fileinfo = fileUser.getPath().substring(0,index) + ".txt";
			System.out.println("File to put data decrypted: "+fileinfo);

			//stream para escrit no .txt
			FileOutputStream fos = new FileOutputStream(fileinfo,false);

			byte[] b1 = new byte[16];
			int available = (int) fileUser.length(); //TODO tirar o if, so entra aqui com o ficheiro .cif com merdas
			System.out.println("cis.available(): "+available);
			if(available == 0) {
				System.out.println("sala vazia?");
			}else {
				try {
					int j = cis.read(b1);
					while (j != -1) {
						fos.write(b1, 0, j);
						j = cis.read(b1);
					}
					System.out.println("sala cheia");
					cis.close();
				} catch (Exception e) {
					System.out.println("nada para ler");
					e.printStackTrace();
				}
			}
			System.out.println("acabado decriptacao");

			fis.close();
			fos.close();
			
			kfile.close();
		} catch (Exception e1) {
			e1.printStackTrace();
			System.exit(-1);
		} 
	}

	public void encryptFile(File fileUser, String keyStoreFile, String keyStorePassword) {
		try {
			System.out.println("------------ENCRYPT----------");
			System.out.println("File to encrypt: "+fileUser.getPath());
			FileInputStream kfile = new FileInputStream("data"+File.separator+"Server Files"+File.separator+keyStoreFile);
			KeyStore kstore = KeyStore.getInstance("JCEKS"); //try
			kstore.load(kfile,keyStorePassword.toCharArray());
			Key myPrivateKey = kstore.getKey(keyStoreFile, keyStorePassword.toCharArray());

			Cipher cDec = Cipher.getInstance("RSA");
			cDec.init(Cipher.ENCRYPT_MODE, myPrivateKey);

			FileInputStream fis = new FileInputStream(fileUser);

			int index = fileUser.getPath().lastIndexOf(".");
			String fcif = fileUser.getPath().substring(0,index) + ".cif";
			System.out.println(fcif);

			FileOutputStream fos = new FileOutputStream(fcif,false);
			CipherOutputStream cos = new CipherOutputStream(fos, cDec);
			
			
			byte[] b1 = new byte[16];
			int j = fis.read(b1);
			System.out.println("ENCRYPTED WRITTEN");

			while (j != -1) {
				cos.write(b1, 0, j);
				j = fis.read(b1);
			}
			fis.close();
			fos.close();
			cos.close();
			kfile.close();

			//TODO: depois de verificar que funciona fileUser.delete();
			//fileUser.delete();
		} catch (Exception e1) {
			e1.printStackTrace();
			System.exit(-1);
		} 

	}
}
