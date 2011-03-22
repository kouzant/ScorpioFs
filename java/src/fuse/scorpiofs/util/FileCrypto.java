package fuse.scorpiofs.util;

import java.io.*;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.util.concurrent.TimeUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class FileCrypto {
	
	private static byte[] buffer=new byte[64];
	private static int bytesRead;
	private static  char[] password;

	public FileCrypto(){
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		java.io.Console cons;
		if(((cons=System.console())!=null) && (Constants.password==null)){
			try{
				password=cons.readPassword("[%s]", "Password:");
				Constants.setPassword(password);
			}catch(IOError e0){
				e0.printStackTrace();
			}
		}
	}
	
	public void encrypt(FileInputStream fis, FileOutputStream fos){
		SecureRandom sr=new SecureRandom();
		byte[] salt=new byte[8];
		sr.nextBytes(salt);

		try{
			SecretKeyFactory factory=SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			KeySpec spec=new PBEKeySpec(Constants.password,salt,1024,128);
			SecretKey tmp=factory.generateSecret(spec);
			SecretKey secret = new SecretKeySpec(tmp.getEncoded(),"AES");

			Cipher cipher=Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");
			cipher.init(Cipher.ENCRYPT_MODE,secret);
			AlgorithmParameters params=cipher.getParameters();
			byte[] iv=params.getParameterSpec(IvParameterSpec.class).getIV();

			File saltFile=new File(Constants.personalDir+"/.salt");
			FileOutputStream saFos=new FileOutputStream(saltFile);
			saFos.write(salt);
			saFos.flush();
			saFos.close();
			
			File ivFile=new File(Constants.personalDir+"/.iv");
			FileOutputStream ivFos=new FileOutputStream(ivFile);
			ivFos.write(iv);
			ivFos.flush();
			ivFos.close();

			while((bytesRead=fis.read(buffer))!=-1){
				byte[] output=cipher.update(buffer,0,bytesRead);
				if(output!=null)
					fos.write(output);
			}
			byte[] output=cipher.doFinal();
			if(output!=null)
				fos.write(output);
			fos.flush();
		}catch(NullPointerException e0){
			e0.printStackTrace();
		}catch(NoSuchAlgorithmException e1){
			e1.printStackTrace();
		}catch(IllegalArgumentException e2){
			e2.printStackTrace();
		}catch(InvalidKeySpecException e3){
			e3.printStackTrace();
		}catch(ArrayIndexOutOfBoundsException e4){
			e4.printStackTrace();
		}catch(NoSuchProviderException e5){
			e5.printStackTrace();
		}catch(NoSuchPaddingException e6){
			e6.printStackTrace();
		}catch(InvalidKeyException e7){
			e7.printStackTrace();
		}catch(InvalidParameterSpecException e8){
			e8.printStackTrace();
		}catch(IllegalStateException e9){
			e9.printStackTrace();
		}catch(IllegalBlockSizeException e10){
			e10.printStackTrace();
		}catch(BadPaddingException e11){
			e11.printStackTrace();
		}catch(IOException e12){
			e12.printStackTrace();
		}
	}

	public void decrypt(FileInputStream fis, FileOutputStream fos){
		byte[] salt=new byte[8];
		byte[] iv=new byte[16];
		
		try{
			File saltFile=new File(Constants.personalDir+"/.salt");
			FileInputStream saFis=new FileInputStream(saltFile);
			saFis.read(salt);
			saFis.close();

			File ivFile=new File(Constants.personalDir+"/.iv");
			FileInputStream ivFis=new FileInputStream(ivFile);
			ivFis.read(iv);
			ivFis.close();

			System.out.println("Initialization Vector: "+new String(iv)+"size: "+iv.length);
			System.out.println("Salt: "+new String(salt));

			SecretKeyFactory factory=SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			KeySpec spec=new PBEKeySpec(Constants.password,salt,1024,128);
			SecretKey tmp=factory.generateSecret(spec);
			SecretKey secret = new SecretKeySpec(tmp.getEncoded(),"AES");

			Cipher cipher=Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");
			cipher.init(Cipher.DECRYPT_MODE,secret,new IvParameterSpec(iv));

			while((bytesRead=fis.read(buffer))!=-1){
				byte[] output=cipher.update(buffer,0,bytesRead);
				if(output!=null)
					fos.write(output);
			}
			byte[] output=cipher.doFinal();
			if(output!=null)
				fos.write(output);
			fos.flush();
			fis.close();
			fos.close();
		}catch(NullPointerException e0){
			e0.printStackTrace();
		}catch(NoSuchAlgorithmException e1){
			e1.printStackTrace();
		}catch(IllegalArgumentException e2){
			e2.printStackTrace();
		}catch(InvalidKeySpecException e3){
			e3.printStackTrace();
		}catch(ArrayIndexOutOfBoundsException e4){
			e4.printStackTrace();
		}catch(NoSuchProviderException e5){
			e5.printStackTrace();
		}catch(NoSuchPaddingException e6){
			e6.printStackTrace();
		}catch(InvalidKeyException e7){
			e7.printStackTrace();
		}catch(IllegalStateException e8){
			e8.printStackTrace();
		}catch(IllegalBlockSizeException e9){
			e9.printStackTrace();
		}catch(BadPaddingException e10){
			e10.printStackTrace();
		}catch(InvalidAlgorithmParameterException e11){
			e11.printStackTrace();
		}catch(IOException e12){
			e12.printStackTrace();
		}
	}
}
