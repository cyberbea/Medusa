package utilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class infoFileCreator {
	
	
public static void main(String[] args) throws IOException{
	//for a fake net where the id are integers from 0 to k-1. 
	String fileName = "";
	int k = 100;//number of nodes
	File file = new File(fileName);
	FileWriter writer = new FileWriter(file);
	ArrayList<String> v = new ArrayList<String>();
	for(int i=0;i<k;++i){
		v.add(String.valueOf(i));
	}
	//Collections.shuffle(v);// random order.
	for(int i=0;i<v.size();++i){
		writer.write(v.get(i)+"\t"+"1"+"\n");
	}
	writer.flush();
}

}
