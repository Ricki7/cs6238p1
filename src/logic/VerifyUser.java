package logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Random;

import p1.Hash;
import utility.Node;
import utility.Statistics;

public class VerifyUser {
	private final BigInteger q = new BigInteger(
			"1447145084347575750784711358692802489263175501753");
	private final int position = 1;
	final int VALUE_IN_HISTORY = 8;
	final int k = 2;
	// private HistoryFileReader history_reader;
	// private UserDirNameMapping name_mapper;

	public VerifyUser() {
		// this.history_reader = new HistoryFileReader();
		// this.name_mapper = new UserDirNameMapping();
	}

	public boolean verify(String user_name, String password) throws Exception {
		String tabel_file = "./users/" + user_name + "_table.txt";
		String his_file = "./users/" + user_name + "_history.txt";
		String feature_file_path = "./ress/input";

		// Read feature value from the file
		BufferedReader br = new BufferedReader(new FileReader(new File(
				feature_file_path)));
		String line = null;
		String feature_string = "";
		ArrayList<Integer> feature_value = new ArrayList<Integer>();
		for (int i = 0; (line = br.readLine()) != null; i++) {
			String[] elements = line.split(" ");
			if (elements[1].equals(user_name)) { // *
				for (int j = 2; j < elements.length; j++) {
					feature_value.add(Integer.valueOf(elements[j]));
					if (feature_string.length() == 0) {
						feature_string = elements[j];
					} else {
						feature_string = feature_string + " " + elements[j];
					}
				}
			}
		}

		// begin process to verify
		// String file_name = name_mapper.getMappingName(user_name);
		BigInteger[][] weight_matrix = getWeightMatrix(user_name);

		// for(int i= 0; i<15; i++){
		// for(int m = 0; m<2; m++){
		// System.out.print("* "+weight_matrix[i][m]);
		// }
		// System.out.println();
		// }

		ArrayList<Integer> threshold = getThreshold();

		// get node information for poly
		ArrayList<Node> node_list = new ArrayList<Node>();
		for (int i = 0; i < feature_value.size(); i++) {
			Node temp = new Node();
			// compared with threshold
			if (feature_value.get(i) < threshold.get(i)) {
				temp.setX(2 * i);
				temp.setY(weight_matrix[i][0].subtract(getGpwd(2 * i, password)
						.mod(q)));
				node_list.add(temp);
			} else {
				temp.setX(2 * i + 1);
				temp.setY(weight_matrix[i][1].subtract(getGpwd(2 * i + 1,
						password).mod(q)));
				node_list.add(temp);
			}
		}

		// TODO: from points (A.K.A. ArrayList<Node> node_list) to poly, then
		// let x =0, get hpwd
		ApacheInterpolation interpolation_agent = new ApacheInterpolation();
		ApachePolyNewtonForm polynomial = interpolation_agent
				.interpolate(node_list);
		BigInteger hpwd = polynomial.valueBigInteger(0);
		System.out.println("Calculated Hpwd: " + hpwd);

		// Use this hpwd try to decrypt history file!
		String used_key = hpwd.toString().substring(0, 16);
		String line1;
		try (BufferedReader brr = new BufferedReader(new FileReader("./users/"
				+ user_name + "_history.txt"))) {
			line1 = brr.readLine();
		}

		String[] parts = line1.split(" ");
		int[] ints = new int[parts.length];
		for (int i = 0; i < parts.length; i++) {
			ints[i] = Integer.parseInt(parts[i]);
		}
		byte[] bb = new byte[ints.length];
		for (int i = 0; i < ints.length; i++) {
			bb[i] = ByteBuffer.allocate(4).putInt(ints[i]).array()[3];
		}

		// System.out.println("!!!");
		// for (int i = 0; i < bb.length; i++)
		// System.out.print(new Integer(bb[i]) + " ");
 
		String decrypted = AESFile.decrypt(bb, used_key);
		//System.out.println("decrypted history file: \n" + decrypted);

		if (decrypted.substring(0, 20).equals("THISISGOOD0987654321")) {
			// take in some history file information
			String[] elements = decrypted.split("\n");

			String num_login_string = elements[2];
			int num_login = Integer.parseInt(num_login_string);
			num_login += 1;
			String[] previous_feature_informaiton = new String[7];
			for (int i = 0; i < 7; i++) {
				previous_feature_informaiton[i] = elements[i + 3];
			}

			// for mean and standard div calculation
			int useful_records = 0;
			if ((num_login - 1) >= 7) {
				useful_records = 7;
			} else {
				useful_records = num_login - 1;
			}
			int[] means = new int[15];
			int[][] values = new int[15][useful_records + 1];
			int[] stands = new int[15];

			for (int i = 0; i < 15; i++) { // ith feature
				for (int m = 0; m < useful_records; m++) { // mth time of login
					String[] elements1 = feature_string.split(" ");
					values[i][0] = Integer.parseInt(elements1[i]);
					String[] elements2 = previous_feature_informaiton[m]
							.split(" ");
					values[i][m + 1] = Integer.parseInt(elements2[i]);
				}
			}

			for (int i = 0; i < 15; i++) { // ith feature
				Statistics s = new Statistics(values[i]);
				means[i] = s.getMean();
				stands[i] = s.getStdDev();
			}

			//new poly, and new table.
			BigInteger new_cof[] = new BigInteger[15];
			new_cof[0] = hpwd; 
			Random rand = new Random();
			for (int i = 1; i < 15; i++) {
				new_cof[i] = BigInteger.valueOf(rand
						.nextInt((999999999 - 0) + 1) + 0);
			}
			BigInteger y0[] = new BigInteger[15];
			BigInteger y1[] = new BigInteger[15];
			for (int i = 0; i < 15; i++) {// all (2i, y0), and all(2i+1, y1)
											// are
											// on the poly
				y0[i] = cal_poly(BigInteger.valueOf(2 * i), new_cof);
				y1[i] = cal_poly(BigInteger.valueOf(2 * i + 1), new_cof);
			}
			
			//find distinguishing feature
			for(int i=0; i<15; i++){//check 15feature 
				String[] elements1 = feature_string.split(" "); 
				if(Math.abs(means[i] - threshold.get(i))>k*stands[i]){
					//distinguishing
					if( means[i] < threshold.get(i)){ //faster than avg 
						y1[i] = BigInteger.valueOf(rand
								.nextInt((999999999 - 0) + 1) + 0);//garbegae y1[i]
					}else{
						y0[i] = BigInteger.valueOf(rand
								.nextInt((999999999 - 0) + 1) + 0);//garbegae y0[i]
					}
				}
			}
			
			// cal new a* and b*
			BigInteger table1[][] = new BigInteger[15][2];
			String hex;
			BigInteger value;
			for (int i = 0; i < 15; i++) {
				for (int m = 0; m < 2; m++) {
					if (m == 0) { // a*
						hex = Hash.sha1(password + (Integer.toString(2 * i)));
						value = new BigInteger(hex, 16);
						table1[i][m] = y0[i].add(value.mod(q));
					} else { // b*
						hex = Hash.sha1(password
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
			
			
			// update_history, store feature and login information
			String file = new String();
			PrintWriter outfile2 = new PrintWriter("./users/" + user_name
					+ "_history.txt", "UTF-8");
			file = "THISISGOOD0987654321";
			file = file + "\n" + user_name + "\n" + num_login + "\n";
			file = file + feature_string;
			for (int m = 0; m < VALUE_IN_HISTORY - 1; m++) {
				file = file + "\n" + previous_feature_informaiton[m];
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
			// System.out.println("***: new file: \n"+file);

			// process hpwd to 16bytes
			String used_key1 = hpwd.toString().substring(0, 16);
			// encrypt
			byte[] cipher = AESFile.encrypt(file, used_key1);
			for (int i = 0; i < cipher.length; i++) {
				outfile2.print((cipher[i]) + " ");
			}
			outfile2.close();

			// add feature values for the current login

			return true;
		} else {
			return false;
		}
	}

	private BigInteger getGpwd(int value, String pwd) {
		String hex = null;
		try {
			hex = Hash.sha1(pwd + (Integer.toString(value)));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new BigInteger(hex, 16);
	}

	private BigInteger[][] getWeightMatrix(String user_name) throws IOException {
		BigInteger[][] weight_matrix = new BigInteger[15][2];
		BufferedReader br = new BufferedReader(new FileReader(new File(
				"./users/" + user_name + "_table.txt")));
		String line = null;
		int i = 0;
		while ((line = br.readLine()) != null) {
			String[] elements = line.split(", ");
			weight_matrix[i][0] = new BigInteger(elements[0]);
			weight_matrix[i][1] = new BigInteger(elements[1]);
			i++;
		}
		return weight_matrix;
	}

	private ArrayList<Integer> getThreshold() throws IOException {
		ArrayList<Integer> result = new ArrayList<Integer>();
		BufferedReader br = new BufferedReader(new FileReader(new File(
				"./ress/threshold")));
		String line = null;
		int i = 0;
		while ((line = br.readLine()) != null) {
			result.add(Integer.valueOf(line));
			i++;
		}
		return result;
	}
	public static BigInteger cal_poly(BigInteger x, BigInteger cof[]) {
		BigInteger result = new BigInteger("0");
		for (int i = 0; i < 15; i++) {
			result = result.add((x.pow(i)).multiply(cof[i]));
		}
		return result;
	}
}
