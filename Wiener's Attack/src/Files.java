import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;

public class Files {
	  //Read Bytes from a file
	  public static byte[] getBytesFromFile(File paramFile) throws IOException {
	    FileInputStream localFileInputStream = new FileInputStream(paramFile);
	    long l = paramFile.length();
	    if (l > 2147483647L) {
	      localFileInputStream.close();
	      throw new IOException("File too large");
	    }
	    byte[] arrayOfByte = new byte[(int)l];
	    int i = 0;
	    int j = 0;
	
	    while ((i < arrayOfByte.length) && ((j = localFileInputStream.read(arrayOfByte, i, arrayOfByte.length - i)) >= 0)) {
	      i += j;
	    }
	    if (i < arrayOfByte.length) {
	      localFileInputStream .close();
	      throw new IOException("Could not completely read file " + paramFile.getName());
	    }
	    localFileInputStream.close();
	    return arrayOfByte;
	  }
	  
	  //Read the file as a BigInteger for further processing
	  public static BigInteger getKeyFromFile(String paramString) throws IOException {
	    File localFile = new File(paramString);
	    byte[] arrayOfByte = getBytesFromFile(localFile);
	    BigInteger big = new BigInteger(arrayOfByte);
	    return big;
	  }
	
	  //Write a BigInteger into a file named "paramString"
	  public static void writeKeyToFile(BigInteger paramBigInteger, String paramString) throws FileNotFoundException, IOException {
	    File localFile = new File(paramString);
	    FileOutputStream output = new FileOutputStream(localFile);
	    output.write(paramBigInteger.toByteArray());
	    output.close();
	  }
}