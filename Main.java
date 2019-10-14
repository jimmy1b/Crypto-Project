import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Scanner;

/**
 * 
 */

/**
 * @author Jimmy
 *
 */
public class Main {
	//constants for file names
	public static String INPUT = "input.txt";
	public static String SYM_CRYPTOGRAM = "symcryptogram.txt";
	public static String PUB_KEY = "publickey.txt";
	public static String OTHER_PUB_KEY = "otherpublickey.txt";
	public static String ENCRYPTED_PRIV_KEY = "encryptedprivatekey.txt";
	public static String EC_CRYPTOGRAM = "eccryptogram.txt";
	public static String SIGNATURE = "signature.txt";
	static Scanner keyboard = new Scanner(System.in);

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		//command line stuff
		for(;;) {
//			Scanner sc = new Scanner(System.in);
//			System.out.println("Cryptography Practical Project\nSelect an option:\n1) Compute Hash\n2)Symmetric Encryption/Decryption\n3) Elliptic Key Pair Generation\n4) Elliptic Key Pair Encryption/Decryption\n5) Elliptic Schnorr Signature/Verification\n");
//			int s = sc.nextInt();
			boolean exit = false;
			System.out.println("Cryptography Practical Project\nSelect an option:\n1) Compute Hash\n2) Symmetric Encryption/Decryption\n3) Elliptic Key Pair Generation\n4) Elliptic Key Pair Encryption/Decryption\n5) Elliptic Schnorr Signature/Verification\n6) Exit");
			int choice = intOptionFromScanner(1, 6);
//			int choice = mainMenu();
			switch(choice) {
				//pt 1
				//hash of file input
				//bonus: user input
				case 1: //pt1, pt1bonus
					System.out.println("Compute Hash\nSelect Option:\n1) File Input\n2) Keyboard Input\n3) Go Back");
					int a = intOptionFromScanner(1, 3);
					switch(a) {
						case 1:
							String hash1 = getMessageFromFile();
							System.out.println("Message:\t" + hash1);
							byte[] hash11 = SymCryptogram.hashGen(hash1.getBytes());
							System.out.println("Hash:");
							System.out.println(new BigInteger(hash11).toString());
							System.out.println("Enter to continue..");
							keyboard.nextLine();
							break;
						case 2:
							System.out.println("Enter your message:");
							String hash2 = keyboard.nextLine();
							writeToFile(INPUT, hash2);
							System.out.println("Message written to \"input.txt\":\t" + hash2);
							byte[] hash22 = SymCryptogram.hashGen(hash2.getBytes());
							System.out.println("Hash:");
							System.out.println(new BigInteger(hash22).toString());
							System.out.println("Enter to continue..");
							keyboard.nextLine();
							break;
						case 3:
							break;
					}
					
					break;
					
				//pt 2
				//encrypt file under pw
					//write cryptogram to file?
					//symcryptogram.txt
				//decrypt cryptogram under pw
				//bonus: compute mac of file with pw
				case 2: //pt2, pt2bonus, pt4bonus
					System.out.println("Symmetric Encryption/Decryption\nSelect Option:\n1) Encrypt: File Input\n2) Encrypt: Keyboard Input\n3) Decrypt\n4) Compute MAC\n5) Go Back");
					int b = intOptionFromScanner(1, 5);
					switch(b) {
						case 1:
							String m1 = getMessageFromFile();
							System.out.println("Enter your passphrase:");
							String pw1 = keyboard.nextLine();
							System.out.println("Message:\t" + m1);
							SymCryptogram sc1 = SymCryptogram.symEncrypt(m1.getBytes(), pw1.getBytes());
							System.out.println(sc1.toString());
							writeToFile(SYM_CRYPTOGRAM, new BigInteger(sc1.z).toString() + "\n" + new BigInteger(sc1.c).toString() + "\n" + new BigInteger(sc1.t).toString());
							System.out.println("Enter to continue..");
							keyboard.nextLine();
							break;
						case 2:
							System.out.println("Enter your message:");
							String m2 = keyboard.nextLine();
							writeToFile(INPUT, m2);
							System.out.println("Message written to \"" + INPUT +"\":\t" + m2);
							System.out.println("Enter your passphrase:");
							String pw2 = keyboard.nextLine();
							System.out.println("Message:\t" + m2);
							SymCryptogram sc2 = SymCryptogram.symEncrypt(m2.getBytes(), pw2.getBytes());
							System.out.println(sc2.toString());
							writeToFile(SYM_CRYPTOGRAM, new BigInteger(sc2.z).toString() + "\n" + new BigInteger(sc2.c).toString() + "\n" + new BigInteger(sc2.t).toString());
							System.out.println("Enter to continue..");
							keyboard.nextLine();
							break;
						case 3:
							SymCryptogram sc3 = getSCFromFile(SYM_CRYPTOGRAM);
							System.out.println("Enter your passphrase:");
							String pw3 = keyboard.nextLine();
							System.out.println(sc3);
							byte[] m3;
							try {
								m3 = SymCryptogram.symDecrypt(sc3, pw3.getBytes());
								System.out.println("Decrypting...");
								System.out.println("Decrypted Message:\t" + new String(m3));
							} catch (Exception e1) {
								System.out.println("Problem with decrypting:\n" + e1.getMessage());
							}
							System.out.println("Enter to continue..");
							keyboard.nextLine();
							break;
						case 4:
							String m = getMessageFromFile();
							System.out.println("Enter your passphrase:");
							String pw = keyboard.nextLine();
							System.out.println("Message:\t" + m);
							byte[] mac = SymCryptogram.macGen(m.getBytes(), pw.getBytes());
							System.out.println("MAC:");
							System.out.println(new BigInteger(mac).toString());
							System.out.println("Enter to continue..");
							keyboard.nextLine();
							break;
						case 5:
							break;
					}
					break;
					
				//pt3
				//elliptic key pair gen under pw
				//write public key to a file
					//publickey.txt
				//bonus: encrypt private key under other pw, write it to a file
					//encryptedprivatekey.txt
				case 3: //pt3, pt3bonus, pt5bonus
					System.out.println("Elliptic Key Pair Generation");
					System.out.println("Enter your passphrase:");
					String pw = keyboard.nextLine();
					KeyPair kp = KeyPair.generate(pw.getBytes());
					System.out.println(kp.toString());
					writeToFile(PUB_KEY, kp.V.x.toString() + "\n" + kp.V.y.toString());
					
					System.out.println("Select Option:\n1) Encrypt Private Key\n2) Generate Other Key Pair\n3) Go Back");
					int c = intOptionFromScanner(1, 3);
					switch(c) {
						case 1:
							System.out.println("Enter your password:");
							String pw1 = keyboard.nextLine();
							SymCryptogram sc1 = SymCryptogram.symEncrypt(kp.s.toByteArray(), pw1.getBytes());
							System.out.println(sc1.toString());
							writeToFile(ENCRYPTED_PRIV_KEY, new BigInteger(sc1.z).toString() + "\n" + new BigInteger(sc1.c).toString() + "\n" + new BigInteger(sc1.t).toString());
							System.out.println("Enter to continue..");
							keyboard.nextLine();
							break;
						case 2:
							System.out.println("Enter other passphrase:");
							String pw2 = keyboard.nextLine();
							KeyPair kp2 = KeyPair.generate(pw2.getBytes());
							System.out.println(kp2.toString());
							writeToFile(OTHER_PUB_KEY, kp2.V.x.toString() + "\n" + kp2.V.y.toString());
							System.out.println("Enter to continue..");
							keyboard.nextLine();
							break;
						case 3:
							break;
					}
					break;
					
				//pt4
				//encrypt file under public key file, write to file
					//eccryptogram.txt
				//decrypt file given pw
				//bonus: encrypt/decrypt without reading from files
				case 4://pt4, pt4bonus, pt5bonus
					System.out.println("Elliptic Key Pair Encryption/Decryption\nSelect Option:\n1) Encrypt Under Own Public Key: File Input\n2) Encrypt Under Own Public Key: Keyboard Input\n3) Encrypt Under Other Public Key: File Input\n4) Encrypt Under Other Public Key: Keyboard Input\n5) Decrypt With Passphrase\n6) Decrypt With Encrypted Private Key(need password)\n7) Go Back");
					int d = intOptionFromScanner(1, 7);
					switch(d) {
						case 1:
							String m1 = getMessageFromFile();
							System.out.println("Message:\t" + m1);
							ECPoint pk1 = getPubKeyFromFile(PUB_KEY);
							System.out.println("Public Key\n" + pk1);
							System.out.println("Encrypting..\n");
							ECCryptogram ecc1 = ECCryptogram.ECEncrypt(m1.getBytes(), pk1);
							System.out.println(ecc1);
							writeToFile(EC_CRYPTOGRAM, ecc1.Z.x + "\n" + ecc1.Z.y + "\n" + new BigInteger(ecc1.c).toString() + "\n" + new BigInteger(ecc1.t).toString());
							System.out.println("Enter to continue..");
							keyboard.nextLine();
							break;
						case 2:
							System.out.println("Enter your message:");
							String m2 = keyboard.nextLine();
							writeToFile(INPUT, m2);
							System.out.println("Message written to \"" + INPUT +"\":\t" + m2);
							ECPoint pk2 = getPubKeyFromFile(PUB_KEY);
							System.out.println("Public Key\n" + pk2);
							System.out.println("Encrypting..\n");
							ECCryptogram ecc2 = ECCryptogram.ECEncrypt(m2.getBytes(), pk2);
							System.out.println(ecc2);
							writeToFile(EC_CRYPTOGRAM, ecc2.Z.x + "\n" + ecc2.Z.y + "\n" + new BigInteger(ecc2.c).toString() + "\n" + new BigInteger(ecc2.t).toString());
							System.out.println("Enter to continue..");
							keyboard.nextLine();
							break;
						case 3:
							String m3 = getMessageFromFile();
							System.out.println("Message:\t" + m3);
							ECPoint pk3 = getPubKeyFromFile(OTHER_PUB_KEY);
							System.out.println("Public Key\n" + pk3);
							System.out.println("Encrypting..\n");
							ECCryptogram ecc3 = ECCryptogram.ECEncrypt(m3.getBytes(), pk3);
							System.out.println(ecc3);
							writeToFile(EC_CRYPTOGRAM, ecc3.Z.x + "\n" + ecc3.Z.y + "\n" + new BigInteger(ecc3.c).toString() + "\n" + new BigInteger(ecc3.t).toString());
							System.out.println("Enter to continue..");
							keyboard.nextLine();
							break;
						case 4:
							System.out.println("Enter your message:");
							String m4 = keyboard.nextLine();
							writeToFile(INPUT, m4);
							System.out.println("Message written to \"" + INPUT +"\":\t" + m4);
							ECPoint pk4 = getPubKeyFromFile(OTHER_PUB_KEY);
							System.out.println("Public Key\n" + pk4);
							System.out.println("Encrypting..\n");
							ECCryptogram ecc4 = ECCryptogram.ECEncrypt(m4.getBytes(), pk4);
							System.out.println(ecc4);
							writeToFile(EC_CRYPTOGRAM, ecc4.Z.x + "\n" + ecc4.Z.y + "\n" + new BigInteger(ecc4.c).toString() + "\n" + new BigInteger(ecc4.t).toString());
							System.out.println("Enter to continue..");
							keyboard.nextLine();
							break;
						case 5:
							System.out.println("Enter your passphrase:");
							String pw1 = keyboard.nextLine();
							ECCryptogram ecc = getECCFromFile(EC_CRYPTOGRAM);
							try {
								byte[] m5 = ECCryptogram.ECDecrypt(ecc, pw1.getBytes());
								System.out.println(ecc);
								System.out.println("Decrypting...");
								System.out.println("DecryptedMessage:\t" + new String(m5));
							} catch (Exception e1) {
								System.out.println("Problem with decrypting:\n" + e1.getMessage());
							}
							System.out.println("Enter to continue..");
							keyboard.nextLine();
							
							break;
						case 6:
							System.out.println("Enter your password:");
							String pw2 = keyboard.nextLine();
							SymCryptogram sc = getSCFromFile(ENCRYPTED_PRIV_KEY);
							ECCryptogram eccc = getECCFromFile(EC_CRYPTOGRAM);
							try {
								System.out.println("Decrypting Private Key...");
								System.out.println(sc);
								BigInteger priv = new BigInteger(SymCryptogram.symDecrypt(sc, pw2.getBytes()));
								System.out.println("Private Key Decrypted");
								byte[] m6 = ECCryptogram.privECDecrypt(eccc, priv);
								System.out.println(eccc);
								System.out.println("Decrypting...");
								System.out.println("Decrypted Message:\t" + new String(m6));
							} catch (Exception e1) {
								System.out.println("Problem with decrypting:\n" + e1.getMessage());
							}
							System.out.println("Enter to continue..");
							keyboard.nextLine();
							
							break;
						case 7:
							break;
					}
					break;
					
				//pt5
				//sign a file under pw and write signature to a file
				//verify the signature and file under the public key file
				//bonus: allow encrypting under recipient's public key and sign with the user's private key
				case 5: //pt5, pt5bonus
					System.out.println("Elliptic Schnorr Signature/Verification\nSelect Option:\n1) Sign With Passphrase\n2) Sign With Encrypted Private Key (need password)\n3) Verify Signature With Own Public Key\n4) Verify Signature With Other Public Key\n5) Go Back");
					int e = intOptionFromScanner(1, 5);
					switch(e) {
						case 1:
							String m1 = getMessageFromFile();
							System.out.println("Message:\t" + m1);
							System.out.println("Enter your passphrase:");
							String pw1 = keyboard.nextLine();
							ECSignature sig1 = ECSignature.sigGen(m1.getBytes(), pw1.getBytes());
							System.out.println(sig1);
							writeToFile(SIGNATURE, sig1.h.toString() + "\n" + sig1.z.toString());
							System.out.println("Enter to continue..");
							keyboard.nextLine();
							break;
						case 2:
							String m2 = getMessageFromFile();
							System.out.println("Message:\t" + m2);
							System.out.println("Enter your password:");
							String pw2 = keyboard.nextLine();
							SymCryptogram sc = getSCFromFile(ENCRYPTED_PRIV_KEY);
							try {
								System.out.println("Decrypting Private Key...");
								System.out.println(sc);
								BigInteger priv = new BigInteger(SymCryptogram.symDecrypt(sc, pw2.getBytes()));
								System.out.println("Private Key Decrypted");
								ECSignature sig2 = ECSignature.privSigGen(m2.getBytes(), priv);
								System.out.println(sig2);
								writeToFile(SIGNATURE, sig2.h.toString() + "\n" + sig2.z.toString());
							} catch (Exception e1) {
								System.out.println("Problem with decrypting:\n" + e1.getMessage());
							}
							System.out.println("Enter to continue..");
							keyboard.nextLine();
							break;
						case 3:
							String m3 = getMessageFromFile();
							System.out.println("Message:\t" + m3);
							ECPoint pk1 = getPubKeyFromFile(PUB_KEY);
							System.out.println("Public Key\n" + pk1);
							ECSignature sig3 = getSigFromFile(SIGNATURE);
							System.out.println(sig3);
							try {
								System.out.println("Verifying...");
								ECSignature.sigVerify(sig3, m3.getBytes(), pk1);
						        System.out.println("Signature Verified");
							} catch (Exception e1) {
								System.out.println("Problem with Verifying:\n" + e1.getMessage());
							}
							System.out.println("Enter to continue..");
							keyboard.nextLine();
							break;
						case 4:
							String m4 = getMessageFromFile();
							System.out.println("Message:\t" + m4);
							ECPoint pk2 = getPubKeyFromFile(OTHER_PUB_KEY);
							System.out.println("Public Key\n" + pk2);
							ECSignature sig4 = getSigFromFile(SIGNATURE);
							System.out.println(sig4);
							try {
								System.out.println("Verifying...");
								ECSignature.sigVerify(sig4, m4.getBytes(), pk2);
						        System.out.println("Signature Verified");
							} catch (Exception e1) {
								System.out.println("Problem with Verifying:\n" + e1.getMessage());
							}
							System.out.println("Enter to continue..");
							keyboard.nextLine();
							break;
						case 5:
							break;
					}
					break;
					
				case 6:
					exit = true;
					break;
			}
			if (exit) break;
			
		}
		keyboard.close();
	}
	
	private static int intOptionFromScanner(int first, int last) {
		for(;;) {
			String s = keyboard.nextLine();
//			if(sc.hasNext()) sc.nextLine();
//			System.out.println(s.length());
			if(s.length() == 1) {
				if(Character.isDigit(s.charAt(0))) {
					int ss = Integer.parseInt(s);
					if(!(ss < first || ss > last)) return ss;
				}
			}
		}
	}
	
	private static ECSignature getSigFromFile(String name) {
		BigInteger h;
		BigInteger z;
		try {
			Scanner in = new Scanner(new FileReader(name));
			h = new BigInteger(in.nextLine());
			z = new BigInteger(in.nextLine());
			in.close();
			return new ECSignature(h, z);
		} catch (Exception e) {
			System.out.println("File Not Formatted Correctly");
		}
		return null;
	}
	
	private static ECPoint getPubKeyFromFile(String name) {
		BigInteger x;
		BigInteger y;
		try {
			Scanner in = new Scanner(new FileReader(name));
			x = new BigInteger(in.nextLine());
			y = new BigInteger(in.nextLine());
			in.close();
			return new ECPoint(x, y);
		} catch (Exception e) {
			System.out.println("File Not Formatted Correctly");
		}
		return null;
	}
	
	private static ECCryptogram getECCFromFile(String name) {
		BigInteger z1;
		BigInteger z2;
		BigInteger c;
		BigInteger t;
		try {
			Scanner in = new Scanner(new FileReader(name));
			z1 = new BigInteger(in.nextLine());
			z2 = new BigInteger(in.nextLine());
			c = new BigInteger(in.nextLine());
			t = new BigInteger(in.nextLine());
			in.close();
			return new ECCryptogram(new ECPoint(z1, z2), c.toByteArray(), t.toByteArray());
		} catch (Exception e) {
			System.out.println("File Not Formatted Correctly");
		}
		return null;
	}
	
	private static SymCryptogram getSCFromFile(String name) {
		BigInteger z;
		BigInteger c;
		BigInteger t;
		try {
			Scanner in = new Scanner(new FileReader(name));
			z = new BigInteger(in.nextLine());
			c = new BigInteger(in.nextLine());
			t = new BigInteger(in.nextLine());
			in.close();
			return new SymCryptogram(z.toByteArray(), c.toByteArray(), t.toByteArray());
		} catch (Exception e) {
			System.out.println("File Not Formatted Correctly");
		}
		return null;
	}
	
	private static String getMessageFromFile() {
		String s = "";
		try {
			Scanner in = new Scanner(new FileReader(INPUT));
			while(in.hasNext()) {
				s += in.nextLine();
			}
			in.close();
			return s;
		} catch (Exception e) {
			System.out.println("Input file doesn't exist");
		}
		return null;
	}
	
	private static void writeToFile(String name, String thing) throws FileNotFoundException {
		PrintWriter pr = new PrintWriter(name);
		pr.print(thing);
		pr.close();
	}

}
