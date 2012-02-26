package ru.spbstu.telematics.mirolyubov.lab1;
import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class lab1Md5
{
	public static void main(String args[])
	{
		Scanner in = new Scanner(System.in);
		System.out.println("Write a string");
		String source = in.nextLine();
		String output = new String();				
		try 
		{
			MessageDigest md5 = MessageDigest.getInstance("md5");	
			md5.update(source.getBytes());			
			byte[] hash = md5.digest();				
			for (int i = 0; i < hash.length; i++) 	
			{
                if ((hash[i] & 0xFF) < 0x10)
                output+="0";
                output+=(Integer.toHexString(0xFF & hash[i])); 
            }
		}
		catch (NoSuchAlgorithmException e) 
		{                        
        }
        System.out.println(output);					
	}
}
