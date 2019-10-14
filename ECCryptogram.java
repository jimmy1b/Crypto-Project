import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * 
 */

/**
 * @author Jimmy
 *	part 4
 */
public class ECCryptogram {
	public static final byte[] P = ("P").getBytes();
	public static final byte[] K = ("K").getBytes();
	public static final byte[] PKE = ("PKE").getBytes();
	public static final byte[] PKA = ("PKA").getBytes();
	public static final byte[] EMPTY = ("").getBytes();
	public final ECPoint Z; 
	public final byte[] c; //ciphertext
	public final byte[] t; //authentication?

	public ECCryptogram(ECPoint theZ, byte[] theC, byte[] theT) {
		Z = theZ;
		c = theC;
		t = theT;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Edwards Curve Cryptogram \n");
		sb.append("Z:\t{\n");
		sb.append(Z.toString());
		sb.append("\n\t}\n");
		

		sb.append("c: ");
		sb.append(new BigInteger(c).toString(16));
		sb.append("\n");
		

		sb.append("t: ");
		sb.append(new BigInteger(t).toString(16));
//		sb.append("\n");
		return sb.toString();
	}
	
	public static ECCryptogram ECEncrypt(byte[] m, ECPoint V) {
		SecureRandom rng = new SecureRandom();
		byte[] z = new byte[64];
		for(int i = 0; i < 64; i++) {
			z[i] = (byte) rng.nextInt(256);
		}
		BigInteger k = (new BigInteger(z)).multiply(BigInteger.valueOf(4));
		ECPoint W = V.ecMultiply(k);
		ECPoint Z = ECPoint.G.ecMultiply(k);
		byte[] keka = SHAKE.KMACXOF256(W.x.toByteArray(), EMPTY, 1024, P);
		byte[] ke = SymCryptogram.getKe(keka);
		byte[] ka = SymCryptogram.getKa(keka);
		byte[] c = (new BigInteger(SHAKE.KMACXOF256(ke, EMPTY, m.length * 8, PKE)).xor(new BigInteger(m))).toByteArray();
		byte[] t = SHAKE.KMACXOF256(ka, m, 512, PKA);
		return new ECCryptogram(Z, c, t);
	}
	
	public static byte[] ECDecrypt(ECCryptogram ec, byte[] pw) throws Exception {
		byte[] a = KeyPair.makeBigger(SHAKE.KMACXOF256(pw, EMPTY, 512, K));
		BigInteger s = (new BigInteger(a)).multiply(BigInteger.valueOf(4));
//		BigInteger s = (new BigInteger(SHAKE.KMACXOF256(pw, EMPTY, 512, K))).multiply(BigInteger.valueOf(4));
//		.mod(ECPoint.p);
		return privECDecrypt(ec, s);
	}
		
	public static byte[] privECDecrypt(ECCryptogram ec, BigInteger s)throws Exception {
		ECPoint W = ec.Z.ecMultiply(s);
		byte[] keka = SHAKE.KMACXOF256(W.x.toByteArray(), EMPTY, 1024, P);
		byte[] ke = SymCryptogram.getKe(keka);
		byte[] ka = SymCryptogram.getKa(keka);
		byte[] m = (new BigInteger(SHAKE.KMACXOF256(ke, EMPTY, ec.c.length * 8, PKE)).xor(new BigInteger(ec.c))).toByteArray();
		byte[] t = SHAKE.KMACXOF256(ka, m, 512, PKA);
//		System.out.println("EC Decrypted? \nt': "+ new BigInteger(t).toString(16) + "\n");
		if (Arrays.equals(t, ec.t)) {
			return m;
		} else {
			throw new Exception("t != t'");
		}
	}
}
