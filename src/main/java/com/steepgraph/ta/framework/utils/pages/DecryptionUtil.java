package com.steepgraph.ta.framework.utils.pages;

import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import com.steepgraph.ta.framework.Constants;

public class DecryptionUtil {

	// private static final String SALT = "SALT";

	// private static final String AES_KEY;

	public static String decrypt(String strToDecrypt) throws Exception {
		String SALT = "SALT";
		PropertyUtil propUtil = PropertyUtil.newInstance();
		String AES_KEY = propUtil.getProperty(Constants.PROPERTY_KEY_EXECUTION_PRIVATE_KEY);
		if (AES_KEY == null || "".equalsIgnoreCase(AES_KEY))
			throw new Exception("private key is not defined in config files");
		try {
			byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			IvParameterSpec ivspec = new IvParameterSpec(iv);

			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			KeySpec spec = new PBEKeySpec(AES_KEY.toCharArray(), SALT.getBytes(), 65536, 256);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
			if (strToDecrypt != null) {
				return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
			}
		} catch (Exception e) {
			System.out.println("Error while decrypting: " + e.toString());
		}
		return null;
	}
}
