/***
 * Simple command line tool to encrypt/decrypt the given space seperated words. 
 * Useful poorman tool to store the encrypted strings in plain text files or in database. 
 ***/
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
 
/**
 * @author Vijay Reddiar
 *
 */

public class EncryptDecryptTool {
 
    private static SecretKeySpec secretKeySpec = null;
//    private static byte[] key
    private static final String USAGE_MSG = "Usage: secret=mysecret cmd={encrypt|decrypt} abc def ....";
	private static final String PADDING_KEY = "!@#$%^&**&^%$#@!";
 
    
    public static void main(String[] args)
    {
    	String cmd = null;
    	String secret = null;

    	System.out.print("Using Parameters: ");
        for (String arg: args){
        	System.out.print(arg + " ");
        }
        System.out.println();
    	
    	if (args.length < 3){
        	System.out.println(USAGE_MSG);
        	System.exit(1);
        }
    	
    	String[] tokens;
    	tokens = args[0].split("=");
    	if (tokens[0].equals("secret")){
    		secret = tokens[1];
    		if (secret.length() < 16){
    			//Pad it with special characters to avoid bad key errors
    			secret = secret + PADDING_KEY.substring(0, 16-secret.length());
    		}
    	}
    	else {
        	System.out.println("No <secret> parameter");
        	System.out.println(USAGE_MSG);
        	System.exit(1);
    	}
    	
    	tokens = args[1].split("=");
    	if (tokens[0].equals("cmd")){
    		cmd = tokens[1];
    		if (!cmd.equals("encrypt") && !cmd.equals("decrypt")){
	        	System.out.println("Invalid <cmd> parameter");
	        	System.out.println(USAGE_MSG);
	        	System.exit(1);
    		}
    	}
    	else {
        	System.out.println("No <cmd> parameter");
        	System.out.println(USAGE_MSG);
        	System.exit(1);
    	}
 

    	for (int i=2; i < args.length; i++){
    		if (cmd.equals("encrypt")){
    			String encryptedValue = EncryptDecryptTool.encrypt(args[i], secret);
    			System.out.println(args[i] + ", " + encryptedValue);
    			}
    		else {
    			String decryptedValue = EncryptDecryptTool.decrypt(args[i], secret);
    			System.out.println(args[i] + ", " + decryptedValue);
    		}
        }
    }    
    
    
    
    public static void setKey(String myKey)
    {
        byte[] key = Arrays.copyOf(myKey.getBytes(), 16);
        secretKeySpec = new SecretKeySpec(key, "AES");
    }
 
    public static String encrypt(String strToEncrypt, String secret)
    {
        try
        {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        }
        catch (Exception e)
        {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }
 
    public static String decrypt(String strToDecrypt, String secret)
    {
        try
        {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        }
        catch (Exception e)
        {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }
}