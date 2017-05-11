package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.scene.control.TextField;

public class MainLayoutController {
	private static Main mainApp;
	
	@FXML private TextField path;
	@FXML private TextField key;
	
	int[] prime = {2,3,5,7,11,13,17,19,23,
			29,31,37,41,43,47,53,59,61,67,
			71,73,79,83,89,97,101,103,107,109,
			113,127,131,137,139,149,151,157,163};
	
	public static int randomRange(int n1, int n2) {
	    return (int)(Math.random() * (n2 - n1 + 1)) + n1;
	}
	
	@FXML private void initialize() {
		String tmpKey = "";
		for (int i = 0; i < 10; i++) {
			int c;
			if (randomRange(0,1)==0) c = randomRange(65,90);
			else c = randomRange(97, 122);
			tmpKey += (char)c;
		}
		
		System.out.println("Auto Generated Key : " + tmpKey);
		key.setText(tmpKey);
	}
	
	@FXML public void onEncrypt() {
		String filePath = path.getText();
		String originalText = "";
		boolean limit = false;
		byte[] b = new byte[10485760]; //Max 10MB
		
		if (filePath.equals("")) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Path Error");
			alert.setHeaderText("Invalid Path");
			alert.setContentText("Path can't be blank.");
			alert.showAndWait();
			return;
		}
		
		String newFilePath = filePath.substring(0, filePath.length()-4);
		String extension = filePath.substring(filePath.length()-3);
		newFilePath += "_Encrypted." + extension;
		if (!extension.equals("txt")) limit = true;
		
		String plainKey = key.getText();
		String hashedKey = getMD5(plainKey);
		
		System.out.println("filePath : " + filePath);
		
		if (!isKeyValid(plainKey)) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Key Validation Error");
			alert.setHeaderText("Invalid Key");
			alert.setContentText("The key you provided is invalid.\nKey should be composed of A~Z and a~z.");
			alert.showAndWait();
			return;
		}
		
		System.out.println("New File Created : " + newFilePath);
		System.out.println("Original File Text : " + originalText);
		System.out.println("Hash generated by key : " + hashedKey);
		
		try {			
			FileInputStream fIn = new FileInputStream(filePath);
			FileOutputStream fOut = new FileOutputStream(newFilePath, false);
			long fileSize = getFileSize(filePath);
			if (limit) {
				try {
					fIn.read(b, 0, (int)fileSize);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				while (true) {
					try {
						int c = fIn.read();
						if (c == -1) break;
						else originalText += (char)c;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
			System.out.println("READ OK!");
			
			try {
				if (limit) {
					for (int i=0; i<1000; i++) {
						System.out.print(b[i]);
						b[i] += hashedKey.charAt(i%32);
						System.out.println(" --> " + b[i]);
						fOut.write(b[i]);
					}
					fOut.write(b, 1000, (int)fileSize-1000);
				} else {
					for (int i=0; i<originalText.length(); i++) {
						int c = originalText.charAt(i);
						c += hashedKey.charAt(i%32);
						fOut.write(c);
					}
				}
				
			} catch (IOException e) {
					e.printStackTrace();
			}
			
			System.out.println("ENCRYPT OK!");
			try {
				fIn.close();
				fOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			System.out.println("File Path : " + filePath);
			e.printStackTrace();
		}
		
		printResult("encrypt");
	}
		
	
	@FXML public void onDecrypt() {
		String filePath = path.getText();
		String originalText = "";
		boolean limit = false;
		byte[] b = new byte[10485760]; //Max 10MB
		
		if (filePath.equals("")) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Path Error");
			alert.setHeaderText("Invalid Path");
			alert.setContentText("Path can't be blank.");
			alert.showAndWait();
			return;
		}
		String newFilePath = filePath.substring(0, filePath.length()-4);
		String extension = filePath.substring(filePath.length()-3);
		newFilePath += "_Decrypted." + extension;
		if (!extension.equals("txt")) limit = true;

		String plainKey = key.getText();
		String hashedKey = getMD5(plainKey);
		if (!isKeyValid(plainKey)) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Key Validation Error");
			alert.setHeaderText("Invalid Key");
			alert.setContentText("The key you provided is invalid.\nKey should be composed of A~Z and a~z.");
			alert.showAndWait();
			return;
		}
		
		System.out.println("New File Created : " + newFilePath);
		System.out.println("Original File Text : " + originalText);
		System.out.println("Hash generated by key : " + hashedKey);
		
		try {			
			FileInputStream fIn = new FileInputStream(filePath);
			FileOutputStream fOut = new FileOutputStream(newFilePath, false);
			long fileSize = getFileSize(filePath);
			if (limit) {
				try {
					fIn.read(b, 0, (int)fileSize);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				while (true) {
					try {
						int c = fIn.read();
						if (c == -1) break;
						else originalText += (char)c;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
			System.out.println("READ OK!");
						
			try {
				if (limit) {
					for (int i=0; i<1000; i++) {
						System.out.print(b[i]);
						b[i] -= hashedKey.charAt(i%32);
						System.out.println(" --> " + b[i]);
						fOut.write(b[i]);
					}
					fOut.write(b, 1000, (int)fileSize-1000);
				} else {
					for (int i=0; i<originalText.length(); i++) {
						int c = originalText.charAt(i);
						c -= hashedKey.charAt(i%32);
						fOut.write(c);
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			System.out.println("DECRYPT OK!");
			try {
				fIn.close();
				fOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			System.out.println("File Path : " + filePath);
			e.printStackTrace();
		}
		
		printResult("decrypt");
	}
	
	public long getFileSize(String path) {
		File f = new File(path);
		return f.length();
	}
	
	public int getIntegerKey(String s) {
		int result = 0;
		for (int i = 0; i < s.length(); i++) {
			result += (int)s.charAt(i);
			
		}
		return result;
	}
	
	public static void setMainApp(Main mainAppGiven) {
		mainApp = mainAppGiven;
	}
	
	@FXML private void onBrowse() {
		FileChooser fileChooser = new FileChooser();
	
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("All files (*.*)", "*.*");
		fileChooser.getExtensionFilters().add(extFilter);
		
		File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
		path.setText(file.getPath());
	}
	
	public void printResult(String s) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Result");
		alert.setHeaderText("Success");
		alert.setContentText("The file was successfully " + s + "ed.");
		alert.showAndWait();
		return;
	}
	
	public MainLayoutController() {
		
	}

	public String getMD5(String plaintext) {
		MessageDigest m;
		try {
			m = MessageDigest.getInstance("MD5");
			m.reset();
			m.update(plaintext.getBytes());
			byte[] digest = m.digest();
			
			BigInteger bigInt = new BigInteger(1,digest);
			String hashtext = bigInt.toString(16);
			while (hashtext.length() < 32) {
			  hashtext = "0"+hashtext;
			}
			return hashtext;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "ERROR";
	}
	
	public boolean isKeyValid(String key) {
		boolean valid = true;
		for (int i = 0; i<key.length(); i++) {
			int c = key.charAt(i);
			if (!((c>=65 && c<=90) || (c>=97 && c<= 122))) {
				valid = false;
				break;
			}
		}
		return valid;
	}
}