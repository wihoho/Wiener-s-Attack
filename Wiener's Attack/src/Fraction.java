import java.math.BigDecimal;
import java.math.BigInteger;

public class Fraction
{
  public BigInteger numerator;
  public BigInteger denominator;

  //Constructor of the Fraction class which initializes the numerator and denominator
  public Fraction(BigInteger paramBigInteger1, BigInteger paramBigInteger2){
	//find out the gcd of paramBigInteger1 and paramBigInteger2 which is used for ensuring the numerator and denominator are relatively prime
    BigInteger localBigInteger = gcd(paramBigInteger1, paramBigInteger2);
    
    this.numerator = paramBigInteger1.divide(localBigInteger);
    this.denominator = paramBigInteger2.divide(localBigInteger);
  }
  
  //Constructor for the case when calculating (paramBigInteger1 /(paramFraction.numerator / paramFraction.denominator))
  public Fraction(BigInteger paramBigInteger, Fraction paramFraction) {
	    this.numerator = paramBigInteger.multiply(paramFraction.denominator);
	    this.denominator = paramFraction.numerator;
	    BigInteger localBigInteger = gcd(this.numerator, this.denominator);
	    this.numerator = this.numerator.divide(localBigInteger);
	    this.denominator = this.denominator.divide(localBigInteger);
  }
  
  //Calculate the quotient of this Fraction
  public BigInteger floor() {
    BigDecimal localBigDecimal1 = new BigDecimal(this.numerator);
    BigDecimal localBigDecimal2 = new BigDecimal(this.denominator);
    return localBigDecimal1.divide(localBigDecimal2, 3).toBigInteger();
  }
  
  //Calculate the remainder of this Fraction and assign the result to form a new Fraction
  public Fraction remainder(){
	  BigInteger floor = this.floor();
	  BigInteger numeratorNew = this.numerator.subtract(floor.multiply(this.denominator));
	  BigInteger denominatorNew = this.denominator;
	  return new Fraction(numeratorNew, denominatorNew);
  }

  public static BigInteger gcd(BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    return paramBigInteger1.gcd(paramBigInteger2);
  }
}