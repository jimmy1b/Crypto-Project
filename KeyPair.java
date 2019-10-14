import java.math.BigInteger;

/**
 * 
 */

/**
 * @author Jimmy
 *	part 3
 */
public class KeyPair {
	public static final byte[] K = ("K").getBytes();
	public static final byte[] EMPTY = ("").getBytes();
	
	public BigInteger s;
	public ECPoint V;
	
	public KeyPair(BigInteger theS, ECPoint theV){
		s = theS;
		V = theV;
	}
	
	public static KeyPair generate(byte[] pw) {
		//the byte array needs to be 1 byte bigger since we are multiplying the int value of it by 4
		//we want the value to be positive
		byte[] a = makeBigger(SHAKE.KMACXOF256(pw, EMPTY, 512, K));
		BigInteger s = (new BigInteger(a)).multiply(BigInteger.valueOf(4));
		ECPoint V = ECPoint.G.ecMultiply(s);
		return new KeyPair(s, V);
	}
	
	public static byte[] makeBigger(byte[] m) {
		byte[] neew = new byte[m.length + 1];
		neew[0] = 0;
		for(int i = 0; i < m.length; i++) {
			neew[i+1] = m[i];
		}
		return neew;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("Edwards Curve Key Pair \n");
		sb.append("s: ");
		sb.append(s.toString());
		sb.append("\nV:\t{\n");
		sb.append(V.toString());
		sb.append("\n\t}");
		
		return sb.toString();
	}
}
