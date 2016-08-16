package calendar_4_0;

import java.security.*;

import javax.crypto.*;
import javax.crypto.spec.*;

import java.io.*;

/**
 * This class encrypts and decrypts a file using CipherStreams and a 256-bit
 * Rijndael key stored in the filesystem.
 */

public class FileEncryptor {
	private String KEY_FILENAME = "rijndaelkey.bin";
	private int ITERATIONS = 1000;
	private String fileInputName; // fileinput:需要加密的文件名，
	private String fileOutputName;// fileOutput:加密后的文件名；
	private String filePlainText;// plaintext是解密后的明文放入该文件中
	private String loginPwd;

	public FileEncryptor(String loginPwd,String fileInputName,String fileOutputName,String filePlainText){
		this.loginPwd = loginPwd;
		this.fileInputName = fileInputName;
		this.fileOutputName = fileOutputName;
		this.filePlainText = filePlainText;
	}
	
	public void FileLock() {
		int length = 10;// 密码长度
		char[] password = new char[length];
		// password要修改，fileInput，fileOutput，plaintext要改
		password = loginPwd.toCharArray();
		try {// 创建密钥
			createKey(password);
			// 加密文件
			encrypt(password, fileInputName, fileOutputName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void FileUnLock() {
		int length = 10;// 密码长度
		char[] password = new char[length];
		// password要修改，fileInput，fileOutput，plaintext要改
		password = loginPwd.toCharArray();

		try {
			// 解密文件
			decrypt(password, fileOutputName, filePlainText);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String pwd = "123";
		String loginInfoPath = "./LoginInfo.txt"; // 未加密前的文件名
		String loginInfoEnPath = "./LoginInfoEn.txt"; // 加密后输出的文件名
		String filePlainText = "./LoginInfoPlain.txt";
		FileEncryptor fileEncryptor =new FileEncryptor(pwd, loginInfoPath, loginInfoEnPath, filePlainText);
		fileEncryptor.FileLock();
		fileEncryptor.FileUnLock();

	}

	/**
	 * Creates a 256-bit Rijndael key and stores it to the filesystem as a
	 * KeyStore.
	 */
	// 创建createKey方法产生密钥，用Rijndael算法创建一个密钥发生器
	private void createKey(char[] password) throws Exception {
		System.out.println("Generating a Rijndael key...");

		// Create a Rijndael算法的密钥流生成器
		KeyGenerator keyGenerator = KeyGenerator.getInstance("Rijndael");
		keyGenerator.init(128);
		// 密钥流产生器生成密钥流
		Key key = keyGenerator.generateKey();

		System.out.println("Done generating the key.");

		// Now we need to create the file with the key,
		// encrypting it with a password.
		// 创建8个字节的盐
		byte[] salt = new byte[8];
		SecureRandom random = new SecureRandom();
		// 随机产生盐
		random.nextBytes(salt);
		PBEKeySpec pbeKeySpec = new PBEKeySpec(password);
		// 初始化密钥构造器，使用的是由java8提供的算法PBEWithSHA1AndDESede

		SecretKeyFactory keyFactory = SecretKeyFactory
				.getInstance("PBEWithSHA1AndDESede");

		// 创建带密钥说明的密钥
		SecretKey pbeKey = keyFactory.generateSecret(pbeKeySpec);
		// 将盐和重复计算数值封装到pbeParamSpec中
		PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, ITERATIONS);
		// 将带密钥说明的密钥和盐，重复计算数值用PBEWithSHA1AndDESede进行加密

		Cipher cipher = Cipher.getInstance("PBEWithSHA1AndDESede");

		cipher.init(Cipher.ENCRYPT_MODE, pbeKey, pbeParamSpec);

		// Encrypt the key
		// 用PBE加密对密钥进行加密
		byte[] encryptedKeyBytes = cipher.doFinal(key.getEncoded());
		// 先将盐写入文件中，然后再讲加密的密钥写入
		// Write out the salt, and then the encrypted key bytes
		FileOutputStream fos = new FileOutputStream(KEY_FILENAME);
		fos.write(salt);
		fos.write(encryptedKeyBytes);
		// 关闭文件
		fos.close();

	}

	/**
	 * Loads a key from the filesystem
	 */
	// 对文件加密和解密之前都需要密钥进行加解密，loadKey()方法，将createkey()盐和盐杂凑后加密后的密钥进行反向解密
	private Key loadKey(char[] password) throws Exception {
		// Load the bytes from the encrypted key file.
		// 将加密后的密钥所在文件
		FileInputStream fis = new FileInputStream(KEY_FILENAME);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int i = 0;
		// 将文件里的内容读取出来
		while ((i = fis.read()) != -1) {
			baos.write(i);
		}
		fis.close();
		// 将内容转化为字节
		byte[] saltAndKeyBytes = baos.toByteArray();
		baos.close();

		// get the salt, which is the first 8 bytes
		// 取出那8个字节的盐，用salt存储
		byte[] salt = new byte[8];
		System.arraycopy(saltAndKeyBytes, 0, salt, 0, 8);

		// get the encrypted key bytes
		// 获得加密后的密钥字节，用encryptedKeyBytes存储
		int length = saltAndKeyBytes.length - 8;
		byte[] encryptedKeyBytes = new byte[length];
		System.arraycopy(saltAndKeyBytes, 8, encryptedKeyBytes, 0, length);

		// Create the PBE cipher
		// 创建PBE的解密构造器
		PBEKeySpec pbeKeySpec = new PBEKeySpec(password);
		// 初始化密钥构造器，使用的是由java8提供的算法PBEWithSHA1AndDESede
		SecretKeyFactory keyFactory = SecretKeyFactory
				.getInstance("PBEWithSHA1AndDESede");
		// 创建带密钥说明的密钥
		SecretKey pbeKey = keyFactory.generateSecret(pbeKeySpec);
		// 将盐和重复计算数值封装到pbeParamSpec中
		PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, ITERATIONS);
		Cipher cipher = Cipher.getInstance("PBEWithSHA1AndDESede");
		// 解密操作
		cipher.init(Cipher.DECRYPT_MODE, pbeKey, pbeParamSpec);

		// Decrypt the key bytes
		// 获得解密后的字节
		byte[] decryptedKeyBytes = cipher.doFinal(encryptedKeyBytes);

		// Create the key from the key bytes
		SecretKeySpec key = new SecretKeySpec(decryptedKeyBytes, "Rijndael");
		// 成功获得钥匙
		return key;

	}

	/**
	 * Encrypt a file using Rijndael. Load the key from the filesystem, given a
	 * password.
	 */
	// 获得密钥之后，将fileInput的明文进行加密，存入到fileOutput的文件中
	private void encrypt(char[] password, String fileInput, String fileOutput)
			throws Exception {
		System.out.println("Loading the key.");
		// 获取密钥
		Key key = loadKey(password);
		System.out.println("Loaded the key.");

		// Create a cipher using that key to initialize it
		// 加密实验Rijndael算法，CBC模型，填充方式为PKCS5Padding
		Cipher cipher = Cipher.getInstance("Rijndael/CBC/PKCS5Padding");

		System.out.println("Initializing SecureRandom...");

		// Now we need an Initialization Vector for the cipher in CBC mode.
		// We use 16 bytes, because the block size of Rijndael is 256 bits.
		// 随机产生16位字节的初始化向量，向量长度与Rijndael块长度相同
		SecureRandom random = new SecureRandom();
		byte[] iv = new byte[16];
		random.nextBytes(iv);
		// 创建字符流文件对象
		File fis_1, fos_1;
		fis_1 = new File(fileInput);
		if (!fis_1.exists()) {
			try {
				fis_1.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		fos_1 = new File(fileOutput);

		if (!fos_1.exists()) {
			try {
				fos_1.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		FileInputStream fis = new FileInputStream(fileInput);
		FileOutputStream fos = new FileOutputStream(fileOutput);

		// Write the IV as the first 16 bytes in the file
		// 将初始化向量写入到加密文件中
		fos.write(iv);
		// 将初始化向量封装到spec中
		IvParameterSpec spec = new IvParameterSpec(iv);

		System.out.println("Initializing the cipher.");
		// 进行加密操作
		cipher.init(Cipher.ENCRYPT_MODE, key, spec);
		// 创建cos，将密文打包进输出文件
		CipherOutputStream cos = new CipherOutputStream(fos, cipher);

		System.out.println("Encrypting the file...done");

		int theByte = 0;
		// 对fileInput文件进行加密，并将加密后的密文存入到fileOutput文件中
		while ((theByte = fis.read()) != -1) {
			cos.write(theByte);
		}
		// 关闭文件
		fis.close();
		cos.close();
	}

	/**
	 * Decrypt a file using Rijndael. Load the key from the filesystem, given a
	 * password.
	 */
	private void decrypt(char[] password, String fileInput, String fileOutput)
			throws Exception {
		System.out.println("Loading the key.");
		// 获取密钥
		Key key = loadKey(password);
		System.out.println("Loaded the key.");
		// 解密Rijndael算法，CBC模型，填充方式为PKCS5Padding
		// Create a cipher using that key to initialize it
		Cipher cipher = Cipher.getInstance("Rijndael/CBC/PKCS5Padding");

		File fis_1, fos_1;
		fis_1 = new File(fileInput);
		if (!fis_1.exists()) {
			try {
				fis_1.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		fos_1 = new File(fileOutput);

		if (!fos_1.exists()) {
			try {
				fos_1.createNewFile();
				System.out.println("创建文件");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		FileInputStream fis = new FileInputStream(fileInput);
		FileOutputStream fos = new FileOutputStream(fileOutput);

		// Read the IV from the file. It's the first 16 bytes.
		// 将之前加密的文件中的向量读取出来
		byte[] iv = new byte[16];
		fis.read(iv);

		IvParameterSpec spec = new IvParameterSpec(iv);

		System.out.println("Initializing the cipher.");
		// 对加密文件进行解密
		cipher.init(Cipher.DECRYPT_MODE, key, spec);
		// 创建cis，将明文打包进输出文件
		CipherInputStream cis = new CipherInputStream(fis, cipher);

		System.out.println("Decrypting the file...done");

		int theByte = 0;
		// 读取加密文件中的密文，并将解密后的内容写入到输出
		while ((theByte = cis.read()) != -1) {
			fos.write(theByte);
		}
		cis.close();
		fos.close();
	}
}