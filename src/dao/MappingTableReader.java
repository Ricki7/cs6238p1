package dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;

public class MappingTableReader {
	private String base_position = "";
	public ArrayList<Integer> getThreshold() throws IOException{
		ArrayList<Integer> result = new ArrayList<Integer>();
		BufferedReader br = new BufferedReader(new FileReader(new File(base_position+"/threshold")));
		String line = null;
		int i = 0;
		while((line=br.readLine())!=null){
			result.add(Integer.valueOf(line));
			i++;
		}
		return result;
	}
//	public BigInteger[][] getWeightMatrix(String file_name) throws IOException{
//		BigInteger[][] weight_matrix = new BigInteger[15][2];
//		BufferedReader br = new BufferedReader(new FileReader(new File(base_position+"/"+file_name)));
//		String line = null;
//		int i = 0;
//		while((line=br.readLine())!=null){
//			String[] elements = line.split(",");
//			weight_matrix[i][0]=new BigInteger(elements[0]);
//			weight_matrix[i][1]=new BigInteger(elements[1]);
//			i++;
//		}
//		return weight_matrix;
//	}
	
	
}
