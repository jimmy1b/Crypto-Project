import java.math.BigInteger;

/**
 * 
 */

/**
 * @author Jimmy
 *	part 5
 */
public class ECSignature {
	public static final byte[] K = ("K").getBytes();
	public static final byte[] N = ("N").getBytes();
	public static final byte[] T = ("T").getBytes();
	public static final byte[] EMPTY = ("").getBytes();
	
	public BigInteger h;
	public BigInteger z;
	
	public ECSignature(BigInteger theH, BigInteger theZ) {
		h = theH;
		z = theZ;
	}

	public static ECSignature sigGen(byte[] m, byte[] pw) {
//		BigInteger s = (new BigInteger(SHAKE.KMACXOF256(pw, EMPTY, 512, K))).multiply(BigInteger.valueOf(4));
		byte[] a = KeyPair.makeBigger(SHAKE.KMACXOF256(pw, EMPTY, 512, K));
		BigInteger s = (new BigInteger(a)).multiply(BigInteger.valueOf(4));
		return privSigGen(m, s);
	}
	
	public static ECSignature privSigGen(byte[] m, BigInteger s) {
//		BigInteger k = (new BigInteger(SHAKE.KMACXOF256(s.toByteArray(), m, 512, N))).multiply(BigInteger.valueOf(4));
		byte[] b = KeyPair.makeBigger(SHAKE.KMACXOF256(s.toByteArray(), m, 512, N));
		BigInteger k = (new BigInteger(b)).multiply(BigInteger.valueOf(4));
		ECPoint U = ECPoint.G.ecMultiply(k);
//		System.out.println(U.toString());
//		BigInteger h = new BigInteger(SHAKE.KMACXOF256(U.x.toByteArray(), m, 512, T));
		byte[] c = KeyPair.makeBigger(SHAKE.KMACXOF256(U.x.toByteArray(), m, 512, T));
		BigInteger h = (new BigInteger(c));
		BigInteger hs = h.multiply(s);
		BigInteger z = (k.subtract(hs)).mod(ECPoint.r);
		return new ECSignature(h, z);
	}
	
	public static ECPoint sigVerify(ECSignature sig, byte[] m, ECPoint V) throws Exception {
		ECPoint a = (ECPoint.G.ecMultiply(sig.z));
//				.ecAdd((V.ecMultiply(sig.h)));
		ECPoint b = (V.ecMultiply(sig.h));
		ECPoint U = a.ecAdd(b);
//		System.out.println(U.toString());
//		System.out.println(new BigInteger(KeyPair.makeBigger(SHAKE.KMACXOF256(U.x.toByteArray(), m, 512, T))).toString());
//		System.out.println(sig.h.toString());
		if (sig.h.equals(new BigInteger(KeyPair.makeBigger(SHAKE.KMACXOF256(U.x.toByteArray(), m, 512, T))))) {
			return U;
		} else {
			throw new Exception("KMACXOF256(U_x, m, 512, \"T\") != h");
		}
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("Edwards Curve Schnorr Signature \n");
		sb.append("s: ");
		sb.append(h.toString());
		sb.append("\nV: ");
		sb.append(z.toString());
		
		return sb.toString();
	}
}
