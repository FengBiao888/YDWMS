package com.yundao.ydwms.util;

import android.annotation.SuppressLint;
import android.os.Build;

import java.security.Provider;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES 加密工具类
 *
 * @author yyh
 */
public class AESUtil {

	private static final String SEED = "com.yundao.ydwms.SEED";

	/**
	 * 加密
	 * @param clearText 要加密的字串
	 * @return 加密之后的字串
	 * @throws Exception
	 */
	public static String encrypt(String clearText) throws Exception {
		byte[] rawkey = getRawKey(SEED.getBytes());
		byte[] result = encrypt(rawkey, clearText.getBytes());
		return toHex(result);

	}

	/**
	 * 解密
	 *
	 * @param encrypted 加密之后的字串
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String encrypted) throws Exception {
		if (encrypted == null) return null;
		byte[] rawKey = getRawKey(SEED.getBytes());
		byte[] enc = toByte(encrypted);
		byte[] result = decrypt(rawKey, enc);
		return new String(result);
	}

	@SuppressLint("TrulyRandom")
	private static byte[] getRawKey(byte[] seed) throws Exception {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		SecureRandom sr = null;
		if( Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1 ){
		    return InsecureSHA1PRNGKeyDerivator.deriveInsecureKey(seed, 32);
        }else if (Build.VERSION.SDK_INT >= 17) {
			sr = SecureRandom.getInstance("SHA1PRNG", new CryptoProvider());
		} else {
			sr = SecureRandom.getInstance("SHA1PRNG");
		}
		sr.setSeed(seed);
		kgen.init(128, sr); // 192 and 256 bits may not be available
		SecretKey skey = kgen.generateKey();
		byte[] raw = skey.getEncoded();
		return raw;
	}


	private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {

		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] encrypted = cipher.doFinal(clear);
		return encrypted;
	}

	private static byte[] decrypt(byte[] raw, byte[] encrypted)
			throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		byte[] decrypted = cipher.doFinal(encrypted);
		return decrypted;
	}

	private static byte[] toByte(String hexString) {
		int len = hexString.length() / 2;
		byte[] result = new byte[len];
		for (int i = 0; i < len; i++)
			result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2),
					16).byteValue();
		return result;
	}

	private static String toHex(byte[] buf) {
		if (buf == null)
			return "";
		StringBuffer result = new StringBuffer(2 * buf.length);
		for (int i = 0; i < buf.length; i++) {
			appendHex(result, buf[i]);
		}
		return result.toString();
	}

	private static void appendHex(StringBuffer sb, byte b) {
		final String HEX = "0123456789ABCDEF";
		sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
	}

	static final class CryptoProvider extends Provider {
		/**
		 * Creates a Provider and puts parameters
		 */
		public CryptoProvider() {
			super("Crypto", 1.0, "HARMONY (SHA1 digest; SecureRandom; SHA1withDSA signature)");
			put("SecureRandom.SHA1PRNG", "org.apache.harmony.security.provider.crypto.SHA1PRNG_SecureRandomImpl");
			put("SecureRandom.SHA1PRNG ImplementedIn", "Software");
		}
	}

}
