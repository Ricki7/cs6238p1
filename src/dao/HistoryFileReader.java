package dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class HistoryFileReader {
	private String history_file_path = "";
	private Map<String,String> history;
	public HistoryFileReader(){
		try {
			getHistory();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void getHistory() throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(history_file_path)));
		String line=null;
		while((line = br.readLine())!=null){
			String[] elements = line.split(",");
			history.put(elements[0], elements[1]);
		}
	}
}
