import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class WienerAttack {
	
	//Four ArrayList for finding proper n/d which later on for guessing k/dg
	List<BigInteger> q = new ArrayList<BigInteger>();
	List<Fraction> r = new ArrayList<Fraction>();
	List<BigInteger> n = new ArrayList<BigInteger>();
	List<BigInteger> d = new ArrayList<BigInteger>();
	
	BigInteger e = new BigInteger("6792605526025");
	BigInteger N = new BigInteger("9449868410449");
	
	Fraction kDdg = new Fraction(BigInteger.ZERO, BigInteger.ONE); // k/dg, D means "divide"
	
	//Default constructor
	public WienerAttack(){
		
	}
	
	//Constructor for the case using files as inputs for generating e and N
	public WienerAttack(String eFile, String NFile) throws IOException{
		this.e = Files.getKeyFromFile(eFile);
		this.N = Files.getKeyFromFile(NFile);
	}
	
	public static void main(String[] args){
		Wiener("e1.bin","n1.bin");
		Wiener("e2.bin","n2.bin");
		Wiener("e3.bin","n3.bin");
		Wiener("e4.bin","n4.bin");
		Wiener("e5.bin","n5.bin");
		Wiener("e6.bin","n6.bin");
		Wiener("e7.bin","n7.bin");
		Wiener("e8.bin","n8.bin");
		Wiener("e9.bin","n9.bin");
		Wiener("e10.bin","n10.bin");
		Wiener("e11.bin","n11.bin");
		Wiener("e12.bin","n12.bin");
		Wiener("e13.bin","n13.bin");
	}
	
	public static void Wiener(String fileE, String fileN){
		WienerAttack wiener;
		String caseNum = null;
		
		if(fileE.length() == 6)
			caseNum = fileE.substring(1,2);
		else if(fileE.length() == 7)
			caseNum = fileE.substring(1,3);
		
		try {
			wiener = new WienerAttack(fileE,fileN); //e comes from e1.bin, and N comes from n1.bin, this is simply a test case. All cases on edventure could be used to replace the current files
			BigInteger privateKey = wiener.attack(); //Start to attack
			
			if(privateKey.equals(BigInteger.ONE.negate())){
				System.out.print("Case "+caseNum+": ");
				System.out.println("This attack is unsuccessful bencause there are no continuted fractions fulfilling the requirements of private key.");
				System.out.println();
			}
			else{
				Files.writeKeyToFile(privateKey, "P1_PrivateKey"+caseNum+".bin"); //Write the private key into a proper file
				System.out.println("Case "+caseNum+": "+privateKey.toString());
				System.out.println("The above private key has been successfully saved into P1_PrivateKey"+caseNum+".bin");
				System.out.println();
			}
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public BigInteger attack(){
		int i= 0;
		BigInteger temp1;
		
		//This loop keeps going unless the privateKey is calculated or no privateKey is generated
		//When no privateKey is generated, temp1 == -1
		while((temp1 = step(i)) == null){
			i++;
		}
		
		return temp1;
	}
	
	//Steps follow the paper called "Cryptanalysis of Short RSA Secret Exponents by Michael J. Wiener"
	public BigInteger step(int iteration){
		if(iteration == 0){
			//initialization for iteration 0
			Fraction ini = new Fraction(e,N);
			q.add(ini.floor());
			r.add(ini.remainder());
			n.add(q.get(0));
			d.add(BigInteger.ONE);		
		}
		else if (iteration == 1){
			//iteration 1
			Fraction temp2 = new Fraction(r.get(0).denominator, r.get(0).numerator);
			q.add(temp2.floor());
			r.add(temp2.remainder());
			n.add((q.get(0).multiply(q.get(1))).add(BigInteger.ONE));
			d.add(q.get(1));
		}
		else{
			if(r.get(iteration-1).numerator.equals(BigInteger.ZERO)){
				return BigInteger.ONE.negate(); //Finite continued fraction. and no proper privateKey could be generated. Return -1
			}
			
			//go on calculating n and d for iteration i by using formulas stating on the paper
			Fraction temp3 = new Fraction(r.get(iteration-1).denominator, r.get(iteration-1).numerator);
			q.add(temp3.floor());
			r.add(temp3.remainder());
			n.add((q.get(iteration).multiply(n.get(iteration-1)).add(n.get(iteration-2))));
			d.add((q.get(iteration).multiply(d.get(iteration-1)).add(d.get(iteration-2))));
		}
		
		//if iteration is even, assign <q0, q1, q2,...,qi+1> to kDdg
		if(iteration % 2 == 0){
			if(iteration == 0){
				kDdg = new Fraction(q.get(0).add(BigInteger.ONE), BigInteger.ONE);
			}
			else{
				kDdg = new Fraction((q.get(iteration).add(BigInteger.ONE)).multiply(n.get(iteration-1)).add(n.get(iteration-2)), (q.get(iteration).add(BigInteger.ONE)).multiply(d.get(iteration-1)).add(d.get(iteration-2)));
			}
		}
		
		//if iteration is odd, assign <q0, q1, q2,...,qi> to kDdg
		else{
			kDdg = new Fraction(n.get(iteration), d.get(iteration));
		}
		
		//System.out.println("k: "+kDdg.numerator+" dg:"+kDdg.denominator);
		
		BigInteger edg = this.e.multiply(kDdg.denominator); //get edg from e * dg
		
		//dividing edg by k yields a quotient of (p-1)(q-1) and a remainder of g 
		BigInteger fy = (new Fraction(this.e, kDdg)).floor(); 
		BigInteger g = edg.mod(kDdg.numerator);
		
		//get (p+q)/2 and check whether (p+q)/2 is integer or not
		BigDecimal pAqD2 = (new BigDecimal(this.N.subtract(fy))).add(BigDecimal.ONE).divide(new BigDecimal("2"));
		if(!pAqD2.remainder(BigDecimal.ONE).equals(BigDecimal.ZERO))
			return null;
		
		//get [(p-q)/2]^2 and check [(p-q)/2]^2 is a perfect square or not
		BigInteger pMqD2s = pAqD2.toBigInteger().pow(2).subtract(N);
		BigInteger pMqD2 = sqrt(pMqD2s);
		if(!pMqD2.pow(2).equals(pMqD2s))
			return null;
		
		//get private key d from edg/eg
		BigInteger privateKey = edg.divide(e.multiply(g));
		return privateKey;
		
	}
	
	//get the root of BigInteger paramBigInteger
	public static BigInteger sqrt(BigInteger paramBigInteger){
	    BigInteger localBigInteger1 = BigInteger.valueOf(0L);
	    BigInteger localBigInteger2 = localBigInteger1.setBit(2 * paramBigInteger.bitLength());
	    do
	    {
	      BigInteger localBigInteger3 = localBigInteger1.add(localBigInteger2);
	      if (localBigInteger3.compareTo(paramBigInteger) != 1) {
	        paramBigInteger = paramBigInteger.subtract(localBigInteger3);
	        localBigInteger1 = localBigInteger3.add(localBigInteger2);
	      }
	      localBigInteger1 = localBigInteger1.shiftRight(1);
	      localBigInteger2 = localBigInteger2.shiftRight(2);
	    }while (localBigInteger2.bitCount() != 0);
	    return localBigInteger1;
	}
}
