package p1;

import java.nio.ByteBuffer;

public class test {
	public static void main(String[] args) throws Exception {
		String a = "55 46 55 -79 -128";
		String[] parts = a.split(" ");
		int [] ints = new int[parts.length];
		for(int i =0; i<parts.length; i++){
			ints[i] = Integer.parseInt(parts[i]); 
		}
		
		byte[] bb = new byte[ints.length];
		for(int i=0; i<ints.length; i++){
			bb[i] = ByteBuffer.allocate(4).putInt(ints[i]).array()[3];
		}
	
		System.out.println("!!!");
		for (int i = 0; i < bb.length; i++)
			System.out.print(new Integer(bb[i]) + " ");
	}
}
