import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;


public class RSAsignature {
	public static void main(String[] args){
		try {
			//SHA-1(m)
			byte[] message = Files.getBytesFromFile(new File("data.any"));
			java.security.MessageDigest  alga = java.security.MessageDigest.getInstance("SHA-1");
			alga.update(message); 
			byte[] digest = alga.digest(); 
			System.out.println("Digest:" + byte2hex(digest)); 
		
			//RSA Signature
			BigInteger d = Files.getKeyFromFile("P1_PrivateKey1.bin");
			BigInteger N = Files.getKeyFromFile("N1.bin");
			BigInteger content = new BigInteger(digest);
			
			BigInteger signature = content.modPow(d,N );
			Files.writeKeyToFile(signature, "P2_Signature.bin");
			System.out.println(signature.toString());
			System.out.println(byte2hex(signature.toByteArray()));
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	} 
	
	public static String byte2hex(byte[] b) 
	{ 
		String hs = ""; 
		String stmp = ""; 
		for (int n = 0; n < b.length; n++) 
		{ 
		stmp = (java.lang.Integer.toHexString(b[n] & 0XFF)); 
		if (stmp.length() == 1) hs = hs + "0" + stmp; 
		else 
		hs = hs + stmp; 
		if (n < b.length - 1) hs = hs + ":"; 
		} 
		return hs.toUpperCase(); 
	} 
	
}

