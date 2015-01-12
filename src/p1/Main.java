package p1;

import AESFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import logic.VerifyUser;

public class Main {
	public static void main(String[] args) throws Exception {
		BigInteger q;
		q = new BigInteger("1447145084347575750784711358692802489263175501753");
		final int VALUE_IN_HISTORY = 8;
		int type;

		while (true) {
			type = 0;
			while (true) {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						System.in));
				System.out
						.print("*** Login (1) or Create Account (2) or Exit (3): ");
				try {
					type = Integer.parseInt(br.readLine());
				} catch (NumberFormatException nfe) {
					System.err.println("Invalid Format. Please enter 1 or 2");
				}
				if (type == 1) {
					break;
				} else if (type == 2) {
					break;
				} else if (type == 3) {
					System.out.println("Bye!");
					System.exit(1);
				} else {
					System.out.println("*Invalie. Please enter 1 or 2");
				}
			}

			// init: (1)get random R, (2) generate hpwd
			if (type == 2) {
				String user_name;
				BufferedReader br = new BufferedReader(new InputStreamReader(
						System.in));
				System.out.print("Please Enter User Name: ");
				while (true) {
					user_name = br.readLine();
					if ((user_name.length() != 0)) {
						break;
					} else {
						System.out.print("Please Enter Non-Empty User Name: ");
					}
				}

				String pwd1; // *
				while (true) {
					System.out.print("Please Enter Password: ");
					pwd1 = br.readLine();
					if (pwd1.length() == 8) {
						break;
					} else {
						System.out.println("*Please Enter a 8 bit password.");
					}
				}

				// generate R
				BigInteger random_r = BigInteger.probablePrime(160,
						new Random()); // *

				// init construct table. Get ploy f and get hpwda
				BigInteger table1[][] = new BigInteger[15][2];
				// get a random function
				BigInteger cof[] = new BigInteger[15];
				cof[0] = (BigInteger.probablePrime(52, new Random())); // hwpd
				System.out.println("HPWED " + cof[0].toString());

				Random rand = new Random();
				for (int i = 1; i < 15; i++) {
					cof[i] = BigInteger.valueOf(rand
							.nextInt((999999999 - 0) + 1) + 0);
				}
				// find points (find y)
				BigInteger y0[] = new BigInteger[15];
				BigInteger y1[] = new BigInteger[15];
				for (int i = 0; i < 15; i++) {// all (2i, y0), and all(2i+1, y1)
												// are
												// on the poly
					y0[i] = cal_poly(BigInteger.valueOf(2 * i), cof);
					y1[i] = cal_poly(BigInteger.valueOf(2 * i + 1), cof);
				}

				// cal a* and b*
				String hex;
				BigInteger value;
				for (int i = 0; i < 15; i++) {
					for (int m = 0; m < 2; m++) {
						if (m == 0) { // a*
							hex = Hash.sha1(pwd1 + (Integer.toString(2 * i)));
							value = new BigInteger(hex, 16);
							table1[i][m] = y0[i].add(value.mod(q));
						} else { // b* 
							hex = Hash.sha1(pwd1
									+ (Integer.toString(2 * i + 1)));
							value = new BigInteger(hex, 16);
							table1[i][m] = y1[i].add(value.mod(q));
						}
					}
				}
				// store table in to txt file
				PrintWriter outfile = new PrintWriter("./users/" + user_name
						+ "_table.txt", "UTF-8");
				for (int i = 0; i < 15; i++) {
					outfile.print(table1[i][0]);
					outfile.print(", ");
					outfile.print(table1[i][1]);
					if (i != 14) {
						outfile.println();
					}
				}
				outfile.close();

				// hsitory file creation
				String file = new String();
				PrintWriter outfile2 = new PrintWriter("./users/" + user_name
						+ "_history.txt", "UTF-8");
				file = "THISISGOOD0987654321";
				file = file + "\n" + user_name+"\n0";
				for (int m = 0; m < VALUE_IN_HISTORY; m++) {
					file = file + "\n" + "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0";
				}
				// padding to multiple of 16
				int length_now = file.length();
				int need_to_pad = 16 - length_now % (16);

				for (int i = 0; i < need_to_pad; i++) {
					file = file + "\0";
				}
				// padding to 800 bytes (optional)
				int temp = (800 - file.length());
				for (int i = 0; i < temp; i++) {
					file = file + "\0";
				}
				// process hpwd to 16bytes
				String used_key = cof[0].toString().substring(0, 16);
				// encrypt
				byte[] cipher = AESFile.encrypt(file, used_key);
				for (int i = 0; i < cipher.length; i++) {
					outfile2.print((cipher[i]) + " ");
				}
				outfile2.close();
				System.out.println("Account Created! ");
				System.out.println();
			}

			if (type == 1) { // login
				String user_name;
				BufferedReader br = new BufferedReader(new InputStreamReader(
						System.in));
				System.out.print("User Name: ");
				while (true) {
					user_name = br.readLine();
					if ((user_name.length() != 0)) {
						int check = 0; 
						File f = new File("./users/"+user_name+"_table.txt");
						if(f.exists() && !f.isDirectory()){check =1;}
						if(check == 1){//exist 
							break;
						}else{
							System.out.print("*User Name Do Not Exsit. Please Enter Again: ");
						}
					} else {
						System.out.print("Please Enter Non-Empty User Name: ");
					}
				}
				
				String pwd1; // *
				while (true) {
					System.out.print("Password: ");
					pwd1 = br.readLine();
					if (pwd1.length() == 8) {
						break;
					} else {
						System.out.println("*Please Enter a 8 bit password.");
					}
				}

				System.out.println("Verifying......");

				VerifyUser vu = new VerifyUser();
				boolean login = false;
				login = vu.verify(user_name, pwd1);
				System.out.println("Authentication Result: "+login);
				
				//Update: table and history file
				
			}
		}
	}

	public static BigInteger cal_poly(BigInteger x, BigInteger cof[]) {
		BigInteger result = new BigInteger("0");
		for (int i = 0; i < 15; i++) {
			result = result.add((x.pow(i)).multiply(cof[i]));
		}
		return result;
	}

	public static byte[] xor(byte a[], byte b[]) {
		int size = (a.length > b.length) ? b.length : a.length;
		byte c[] = new byte[size];
		for (int i = 0; i < size; i++) {
			c[i] = (byte) ((int) a[i] ^ (int) b[i]);
		}
		return c;

	}
}
