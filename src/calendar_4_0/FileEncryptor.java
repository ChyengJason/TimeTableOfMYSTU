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
	private String fileInputName; // fileinput:��Ҫ���ܵ��ļ�����
	private String fileOutputName;// fileOutput:���ܺ���ļ�����
	private String filePlainText;// plaintext�ǽ��ܺ�����ķ�����ļ���
	private String loginPwd;

	public FileEncryptor(String loginPwd,String fileInputName,String fileOutputName,String filePlainText){
		this.loginPwd = loginPwd;
		this.fileInputName = fileInputName;
		this.fileOutputName = fileOutputName;
		this.filePlainText = filePlainText;
	}
	
	public void FileLock() {
		int length = 10;// ���볤��
		char[] password = new char[length];
		// passwordҪ�޸ģ�fileInput��fileOutput��plaintextҪ��
		password = loginPwd.toCharArray();
		try {// ������Կ
			createKey(password);
			// �����ļ�
			encrypt(password, fileInputName, fileOutputName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void FileUnLock() {
		int length = 10;// ���볤��
		char[] password = new char[length];
		// passwordҪ�޸ģ�fileInput��fileOutput��plaintextҪ��
		password = loginPwd.toCharArray();

		try {
			// �����ļ�
			decrypt(password, fileOutputName, filePlainText);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String pwd = "123";
		String loginInfoPath = "./LoginInfo.txt"; // δ����ǰ���ļ���
		String loginInfoEnPath = "./LoginInfoEn.txt"; // ���ܺ�������ļ���
		String filePlainText = "./LoginInfoPlain.txt";
		FileEncryptor fileEncryptor =new FileEncryptor(pwd, loginInfoPath, loginInfoEnPath, filePlainText);
		fileEncryptor.FileLock();
		fileEncryptor.FileUnLock();

	}

	/**
	 * Creates a 256-bit Rijndael key and stores it to the filesystem as a
	 * KeyStore.
	 */
	// ����createKey����������Կ����Rijndael�㷨����һ����Կ������
	private void createKey(char[] password) throws Exception {
		System.out.println("Generating a Rijndael key...");

		// Create a Rijndael�㷨����Կ��������
		KeyGenerator keyGenerator = KeyGenerator.getInstance("Rijndael");
		keyGenerator.init(128);
		// ��Կ��������������Կ��
		Key key = keyGenerator.generateKey();

		System.out.println("Done generating the key.");

		// Now we need to create the file with the key,
		// encrypting it with a password.
		// ����8���ֽڵ���
		byte[] salt = new byte[8];
		SecureRandom random = new SecureRandom();
		// ���������
		random.nextBytes(salt);
		PBEKeySpec pbeKeySpec = new PBEKeySpec(password);
		// ��ʼ����Կ��������ʹ�õ�����java8�ṩ���㷨PBEWithSHA1AndDESede

		SecretKeyFactory keyFactory = SecretKeyFactory
				.getInstance("PBEWithSHA1AndDESede");

		// ��������Կ˵������Կ
		SecretKey pbeKey = keyFactory.generateSecret(pbeKeySpec);
		// ���κ��ظ�������ֵ��װ��pbeParamSpec��
		PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, ITERATIONS);
		// ������Կ˵������Կ���Σ��ظ�������ֵ��PBEWithSHA1AndDESede���м���

		Cipher cipher = Cipher.getInstance("PBEWithSHA1AndDESede");

		cipher.init(Cipher.ENCRYPT_MODE, pbeKey, pbeParamSpec);

		// Encrypt the key
		// ��PBE���ܶ���Կ���м���
		byte[] encryptedKeyBytes = cipher.doFinal(key.getEncoded());
		// �Ƚ���д���ļ��У�Ȼ���ٽ����ܵ���Կд��
		// Write out the salt, and then the encrypted key bytes
		FileOutputStream fos = new FileOutputStream(KEY_FILENAME);
		fos.write(salt);
		fos.write(encryptedKeyBytes);
		// �ر��ļ�
		fos.close();

	}

	/**
	 * Loads a key from the filesystem
	 */
	// ���ļ����ܺͽ���֮ǰ����Ҫ��Կ���мӽ��ܣ�loadKey()��������createkey()�κ����Ӵպ���ܺ����Կ���з������
	private Key loadKey(char[] password) throws Exception {
		// Load the bytes from the encrypted key file.
		// �����ܺ����Կ�����ļ�
		FileInputStream fis = new FileInputStream(KEY_FILENAME);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int i = 0;
		// ���ļ�������ݶ�ȡ����
		while ((i = fis.read()) != -1) {
			baos.write(i);
		}
		fis.close();
		// ������ת��Ϊ�ֽ�
		byte[] saltAndKeyBytes = baos.toByteArray();
		baos.close();

		// get the salt, which is the first 8 bytes
		// ȡ����8���ֽڵ��Σ���salt�洢
		byte[] salt = new byte[8];
		System.arraycopy(saltAndKeyBytes, 0, salt, 0, 8);

		// get the encrypted key bytes
		// ��ü��ܺ����Կ�ֽڣ���encryptedKeyBytes�洢
		int length = saltAndKeyBytes.length - 8;
		byte[] encryptedKeyBytes = new byte[length];
		System.arraycopy(saltAndKeyBytes, 8, encryptedKeyBytes, 0, length);

		// Create the PBE cipher
		// ����PBE�Ľ��ܹ�����
		PBEKeySpec pbeKeySpec = new PBEKeySpec(password);
		// ��ʼ����Կ��������ʹ�õ�����java8�ṩ���㷨PBEWithSHA1AndDESede
		SecretKeyFactory keyFactory = SecretKeyFactory
				.getInstance("PBEWithSHA1AndDESede");
		// ��������Կ˵������Կ
		SecretKey pbeKey = keyFactory.generateSecret(pbeKeySpec);
		// ���κ��ظ�������ֵ��װ��pbeParamSpec��
		PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, ITERATIONS);
		Cipher cipher = Cipher.getInstance("PBEWithSHA1AndDESede");
		// ���ܲ���
		cipher.init(Cipher.DECRYPT_MODE, pbeKey, pbeParamSpec);

		// Decrypt the key bytes
		// ��ý��ܺ���ֽ�
		byte[] decryptedKeyBytes = cipher.doFinal(encryptedKeyBytes);

		// Create the key from the key bytes
		SecretKeySpec key = new SecretKeySpec(decryptedKeyBytes, "Rijndael");
		// �ɹ����Կ��
		return key;

	}

	/**
	 * Encrypt a file using Rijndael. Load the key from the filesystem, given a
	 * password.
	 */
	// �����Կ֮�󣬽�fileInput�����Ľ��м��ܣ����뵽fileOutput���ļ���
	private void encrypt(char[] password, String fileInput, String fileOutput)
			throws Exception {
		System.out.println("Loading the key.");
		// ��ȡ��Կ
		Key key = loadKey(password);
		System.out.println("Loaded the key.");

		// Create a cipher using that key to initialize it
		// ����ʵ��Rijndael�㷨��CBCģ�ͣ���䷽ʽΪPKCS5Padding
		Cipher cipher = Cipher.getInstance("Rijndael/CBC/PKCS5Padding");

		System.out.println("Initializing SecureRandom...");

		// Now we need an Initialization Vector for the cipher in CBC mode.
		// We use 16 bytes, because the block size of Rijndael is 256 bits.
		// �������16λ�ֽڵĳ�ʼ������������������Rijndael�鳤����ͬ
		SecureRandom random = new SecureRandom();
		byte[] iv = new byte[16];
		random.nextBytes(iv);
		// �����ַ����ļ�����
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
		// ����ʼ������д�뵽�����ļ���
		fos.write(iv);
		// ����ʼ��������װ��spec��
		IvParameterSpec spec = new IvParameterSpec(iv);

		System.out.println("Initializing the cipher.");
		// ���м��ܲ���
		cipher.init(Cipher.ENCRYPT_MODE, key, spec);
		// ����cos�������Ĵ��������ļ�
		CipherOutputStream cos = new CipherOutputStream(fos, cipher);

		System.out.println("Encrypting the file...done");

		int theByte = 0;
		// ��fileInput�ļ����м��ܣ��������ܺ�����Ĵ��뵽fileOutput�ļ���
		while ((theByte = fis.read()) != -1) {
			cos.write(theByte);
		}
		// �ر��ļ�
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
		// ��ȡ��Կ
		Key key = loadKey(password);
		System.out.println("Loaded the key.");
		// ����Rijndael�㷨��CBCģ�ͣ���䷽ʽΪPKCS5Padding
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
				System.out.println("�����ļ�");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		FileInputStream fis = new FileInputStream(fileInput);
		FileOutputStream fos = new FileOutputStream(fileOutput);

		// Read the IV from the file. It's the first 16 bytes.
		// ��֮ǰ���ܵ��ļ��е�������ȡ����
		byte[] iv = new byte[16];
		fis.read(iv);

		IvParameterSpec spec = new IvParameterSpec(iv);

		System.out.println("Initializing the cipher.");
		// �Լ����ļ����н���
		cipher.init(Cipher.DECRYPT_MODE, key, spec);
		// ����cis�������Ĵ��������ļ�
		CipherInputStream cis = new CipherInputStream(fis, cipher);

		System.out.println("Decrypting the file...done");

		int theByte = 0;
		// ��ȡ�����ļ��е����ģ��������ܺ������д�뵽���
		while ((theByte = cis.read()) != -1) {
			fos.write(theByte);
		}
		cis.close();
		fos.close();
	}
}