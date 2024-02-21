package com.steepgraph.ta.framework;

import com.steepgraph.ta.framework.utils.pages.EncryptionUtil;

public class encryptUtil {

	public static void main(String[] args) throws Exception {
		try {
			String key = args[0];
			System.out.println("---Original Password---\n" + key);
			if (!key.equalsIgnoreCase("")) {
				System.out.println("---Encrypted Password---");
				System.out.println(EncryptionUtil.encrypt(key));
			} else {
				System.out.println("Invalid Password");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
