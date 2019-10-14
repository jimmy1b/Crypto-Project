import java.math.BigInteger;

/**
 * 
 */

/**
 * @author Jimmy
 *	part 3
 */
public class ECPoint {
	//class to make objects for points on edwards curve E_521
	//Globals:
		//p := 2^521 - 1
		//E_521 : x^2 + y^2 = 1 376012x^2y^2, edwards curve over F_p
		//G := (x,y), a point on E_521 where x = 18 and y is even
	//number of points is always a multiple of 4
		//for E_521: n_521 := 4r
			//r = 2^519 - 3375547632585017057891076304187825350719049512140512266186335150085779108655765
	public static final BigInteger p = BigInteger.ONE.shiftLeft(521).subtract(BigInteger.ONE);
	public static final BigInteger d = BigInteger.valueOf(-376014);
	public static final BigInteger r = BigInteger.ONE.shiftLeft(519)
			.subtract(new BigInteger("337554763258501705789107630418782636071904961214051226618635150085779108655765"));
	                                //337554763258501705789107630418782636071904961214051226618635150085779108655765
	public static final BigInteger n = r.multiply(BigInteger.valueOf(4));
	public static final ECPoint G = new ECPoint(BigInteger.valueOf(18), false);
	public static final ECPoint NEUTRAL = new ECPoint();
	
	public BigInteger x;
	public BigInteger y;
	
	//contructor for neutral element: 
		//O:=(0,1)
	private ECPoint(){
		x = BigInteger.ZERO;
		y = BigInteger.ONE;
	}
	
	//constructor for a curve point given x and y (BigIntegers)
	//private so that the points created are only ones that are on E251
	public ECPoint(BigInteger theX, BigInteger theY){
		x = theX;
		y = theY;
	}
	
	//constructor for a curve point given x and least significant bit of y
		//find y by computing y = sqrt((1 - x^2)/(1 + 376014x^2)) mod p
		//uhhhhhhhhhhhhhhhhhhh in the project spec
	private ECPoint(BigInteger theX, boolean ylsb) {
		x = theX;
		
		BigInteger top = BigInteger.ONE.subtract(x.pow(2));
        BigInteger bot = x.pow(2).multiply(BigInteger.valueOf(376014)).add(BigInteger.ONE).modInverse(p);

        y = sqrt(top.multiply(bot), p, ylsb).mod(p);
	}
		
	//method for equality comparison
		//instance method of a point given another point
	
		
	//method for opposite point
		//instance method of a point?
	public ECPoint getOpposite() {
		return new ECPoint(x.multiply(BigInteger.valueOf(-1)).mod(p), y);
	}
		
	//method for the sum of two points
		//instance method of a point given another point
	public ECPoint ecAdd(ECPoint other) {
		//(x_1y_2 + y_1x_2)/(1 + dx_1x_2y_1y_2)
		BigInteger nx = ((x.multiply(other.y)).add(y.multiply(other.x))).multiply
				((BigInteger.ONE.add(d.multiply(x).multiply(y).multiply(other.x).multiply(other.y)).modInverse(p)))
				.mod(p);

		//(y_1y_2 + x_1x_2)/(1 - dx_1x_2y_1y_2)
		BigInteger ny = ((y.multiply(other.y)).subtract(x.multiply(other.x)))
				.multiply((BigInteger.ONE.subtract(d.multiply(x).multiply(y).multiply(other.x).multiply(other.y)).modInverse(p)))
				.mod(p);
		
		return new ECPoint(nx, ny);
	}
	
	public ECPoint ecDouble() {
		BigInteger nx = (x.multiply(y).multiply(BigInteger.valueOf(2))).multiply((x.pow(2).add(y.pow(2))).modInverse(p)).mod(p);
		BigInteger ny = (y.pow(2).subtract(x.pow(2))).multiply((BigInteger.valueOf(2).subtract(x.pow(2)).subtract(y.pow(2))).modInverse(p)).mod(p);
		return new ECPoint(nx, ny);
	}

	//method for multiplication by a scaler s
		//uses the sum method s times
	public ECPoint ecMultiply(BigInteger mult) {
//		ECPoint tmp = this;
		ECPoint sum = NEUTRAL;
//		System.out.println(mult.bitLength());
//		int len = mult.bitLength();
//		if(len % 8 != 0) len += 8 - (len % 8);
		
//		if(mult.signum() < 0) {
//			System.out.println(mult.bitLength());
//	        System.out.println(mult.toString(2));
//			mult = mult.multiply(BigInteger.valueOf(-1));
//			System.out.println(mult.bitLength());
//	        System.out.println(" " +mult.toString(2));
//		}
		
		for(int i = mult.bitLength(); i >= 0; i--) {
			sum = sum.ecDouble();
			if(mult.testBit(i)) {
				sum = sum.ecAdd(this);
			}
		}
		
//		for(int i = 0; i < len + 1; i++) {
////			System.out.print(mult.testBit(i));
//			if(mult.testBit(i)) {
//				System.out.print(1);
//				sum = sum.ecAdd(tmp);
////				System.out.println(i);
////				System.out.println(tmp.toString());
//			} else {
//				System.out.print(0);
//				
//			}
//			tmp = tmp.ecDouble();
////			tmp = tmp.ecAdd(tmp);
//		}

//		System.out.println();
		return sum;
	}
	
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
//		sb.append("Edwards Curve Point \n");
		sb.append("x: ");
		sb.append(x.toString());
		sb.append("\ny: ");
		sb.append(y.toString());
		return sb.toString();
	}
	
	/**
	* Compute a square root of v mod p with a specified
	* least significant bit, if such a root exists.
	*
	* @param v the radicand.
	* @param p the modulus (must satisfy p mod 4 = 3).
	* @param lsb desired least significant bit (true: 1, false: 0).
	* @return a square root r of v mod p with r mod 2 = 1 iff lsb = true
	* if such a root exists, otherwise null.
	*/
	public static BigInteger sqrt(BigInteger v, BigInteger p, boolean lsb) {
		assert (p.testBit(0) && p.testBit(1)); // p = 3 (mod 4)
		if (v.signum() == 0) {
			return BigInteger.ZERO;
		}
		BigInteger r = v.modPow(p.shiftRight(2).add(BigInteger.ONE), p);
		if (r.testBit(0) != lsb) {
			r = p.subtract(r); // correct the lsb
		}
		return (r.multiply(r).subtract(v).mod(p).signum() == 0) ? r : null;
	}
}
