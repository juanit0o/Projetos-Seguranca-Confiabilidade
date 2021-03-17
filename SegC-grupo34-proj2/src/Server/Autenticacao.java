package Server;

import java.io.File;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.util.Random;

public class Autenticacao {

	public long generateNonce() {
		Random rnd = new Random();
		long nonce = (long) (Math.floor(Math.random() * (99999999 - 10000000 + 1)) + 10000000);
        return nonce;
	}
	
	public Certificate getCertificate(String user) {
		//pasta do lado do sv
		File cert = new File("PubKeys" + File.separator + user + ".cer");
		//como dar load do certificado do utilizador
		return cert;
		//return null;
	}
}
