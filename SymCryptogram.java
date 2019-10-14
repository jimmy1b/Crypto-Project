import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * 
 */

/**
 * @author Jimmy
 *	part 2
 */
public class SymCryptogram {
	public static final byte[] D = ("D").getBytes();
	public static final byte[] T = ("T").getBytes();
	public static final byte[] S = ("S").getBytes();
	public static final byte[] SKE = ("SKE").getBytes();
	public static final byte[] SKA = ("SKA").getBytes();
	public static final byte[] EMPTY = ("").getBytes();
	public final byte[] z; //random int,512 bits
	public final byte[] c; //ciphertext
	public final byte[] t; //authentication?
	
	public SymCryptogram(byte[] theZ, byte[] theC, byte[] theT) {
		z = theZ;
		c = theC;
		t = theT;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Symmetric Cryptogram \n");
		sb.append("z: ");
		sb.append(new BigInteger(z).toString(16));
		sb.append("\n");
		

		sb.append("c: ");
		sb.append(new BigInteger(c).toString(16));
		sb.append("\n");
		

		sb.append("t: ");
		sb.append(new BigInteger(t).toString(16));
//		sb.append("\n");
		return sb.toString();
	}

	public static byte[] macGen(byte[] m, byte[] pw) {
		byte[] t = SHAKE.KMACXOF256(pw, m, 512, T);
		return t;
	}
	
	public static byte[] hashGen(byte[] m) {
		byte[] h = SHAKE.KMACXOF256(EMPTY, m, 512, D);
		return h;
	}
	
	public static SymCryptogram symEncrypt(byte[] m, byte[] pw) {
		SecureRandom rng = new SecureRandom();
		byte[] z = new byte[64];
		for(int i = 0; i < 64; i++) {
			z[i] = (byte) rng.nextInt(256);
		}
		
		//z || pw
		byte[] zpw = getZPW(z, pw);
		byte[] keka = SHAKE.KMACXOF256(zpw, EMPTY, 1024, S);
		byte[] ke = getKe(keka);
		byte[] ka = getKa(keka);
		byte[] c = (new BigInteger(SHAKE.KMACXOF256(ke, EMPTY, m.length * 8, SKE)).xor(new BigInteger(m))).toByteArray();
		byte[] t = SHAKE.KMACXOF256(ka, m, 512, SKA);
		return new SymCryptogram(z, c, t);
	}
	
	public static byte[] symDecrypt(SymCryptogram sym, byte[] pw) throws Exception {
		byte[] zpw = getZPW(sym.z, pw);
		byte[] keka = SHAKE.KMACXOF256(zpw, EMPTY, 1024, S);
		byte[] ke = getKe(keka);
		byte[] ka = getKa(keka);
		byte[] m = (new BigInteger(SHAKE.KMACXOF256(ke, EMPTY, sym.c.length * 8, SKE)).xor(new BigInteger(sym.c))).toByteArray();
		byte[] t = SHAKE.KMACXOF256(ka, m, 512, SKA);
		if (Arrays.equals(t, sym.t)) {
			return m;
		} else {
			throw new Exception("t != t'");
		}
	}
	
	public static byte[] getZPW(byte[] z, byte[] pw) {
		byte[] zpw = new byte[z.length + pw.length];
		for(int i = 0; i < zpw.length; i++) {
			if(i < z.length) {
				zpw[i] = z[i];
			} else {
				zpw[i] = pw[i - z.length];
			}
		}
		return zpw;
	}
	
	public static byte[] getKe(byte[] keka) {
		byte[] ke = new byte[keka.length / 2];
		for(int i = 0; i < keka.length / 2; i++) {
			ke[i] = keka[i];
		}
		
		return ke;
	}
	
	public static byte[] getKa(byte[] keka) {
		byte[] ka = new byte[keka.length / 2];
		for(int i = 0; i < keka.length / 2; i++) {
			ka[i] = keka[i + (keka.length / 2)];
		}
		
		return ka;
	}
}
