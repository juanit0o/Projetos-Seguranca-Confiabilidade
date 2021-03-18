package Server;

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
			FileInputStream kfile = new FileInputStream("data"+File.separator+"Server Files"+File.separator+keyStoreFile);
			KeyStore kstore = KeyStore.getInstance("JCEKS"); //try
			kstore.load(kfile,keyStorePassword.toCharArray());
			PrivateKey myPrivateKey = (PrivateKey) kstore.getKey(keyStoreFile, keyStorePassword.toCharArray());

			Cipher cDec = Cipher.getInstance("RSA");
			cDec.init(Cipher.DECRYPT_MODE, myPrivateKey);

			FileInputStream fisDec;
			fisDec = new FileInputStream(fileUser);
			//TODO: verificar se escreve por cima ou temos de apagar o conteudo do ficheiro
			CipherInputStream cis;
			cis = new CipherInputStream(fisDec, cDec);

			int index = fileUser.getPath().lastIndexOf(".");
			String fileinfo = fileUser.getPath().substring(0,index) + ".txt";

			System.out.println(fileinfo);

			File fcif = new File(fileinfo);

			//FileOutputStream fosDec = new FileOutputStream(fileUser,false);
			FileOutputStream fosDec = new FileOutputStream(fcif,false);

			try {
				byte[] b1 = new byte[16];
				int j = cis.read(b1);
				while (j != -1) {
					fosDec.write(b1, 0, j);
					j = cis.read(b1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}



			fisDec.close();
			fosDec.close();
			cis.close();
			kfile.close();
		} catch (Exception e1) {
			e1.printStackTrace();
			System.exit(-1);
		} 
	}

	public void encryptFile(File fileUser, String keyStoreFile, String keyStorePassword) {
		try {
			System.out.println("------------ENCRYPT----------");
			FileInputStream kfile = new FileInputStream("data"+File.separator+"Server Files"+File.separator+keyStoreFile);
			KeyStore kstore = KeyStore.getInstance("JCEKS"); //try
			kstore.load(kfile,keyStorePassword.toCharArray());
			Key myPrivateKey = kstore.getKey(keyStoreFile, keyStorePassword.toCharArray());

			Cipher cDec = Cipher.getInstance("RSA");
			cDec.init(Cipher.ENCRYPT_MODE, myPrivateKey);

			FileInputStream fisEnc;
			fisEnc = new FileInputStream(fileUser);
			//TODO: temos de apagar o conteudo do ficheiro
			CipherInputStream cis;
			cis = new CipherInputStream(fisEnc, cDec);

			int index = fileUser.getPath().lastIndexOf(".");
			String fcif = fileUser.getPath().substring(0,index) + ".cif";
			System.out.println(fcif);

			FileOutputStream fosDec = new FileOutputStream(fcif,false);

			byte[] b1 = new byte[16];
			int j = cis.read(b1);

			while (j != -1) {
				fosDec.write(b1, 0, j);
				j = cis.read(b1);
			}
			fisEnc.close();
			fosDec.close();
			cis.close();
			kfile.close();

			//TODO: depois de verificar que funciona fileUser.delete();
		} catch (Exception e1) {
			e1.printStackTrace();
			System.exit(-1);
		} 

	}
}
