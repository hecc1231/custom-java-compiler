package lexicalscanner.preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import lexicalscanner.LexicalScanner;

public class FileFliter {
	public static final int ANO_START_STATE = 0;
	public static int currentState = ANO_START_STATE;
	public FileFliter(String pathName){
		// TODO Auto-generated constructor stub
		File infile = new File(pathName);
		if(!infile.exists()){
			System.out.println("input file not exist!\n");
		}
		File filterFile = new File(LexicalScanner.filterPathName);
		if(!filterFile.exists()){
			try {
				filterFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		BufferedReader bfReader;
		BufferedWriter bfWriter;
		try {
			bfReader = new BufferedReader(
					new InputStreamReader(new FileInputStream(infile)));
			bfWriter = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(filterFile)));
			String data = null;
			int annoFlag = 0;
			while((data=bfReader.readLine())!=null){
				int strFlag = 0;
				for(int i=0;i<data.length();i++){
					if(data.charAt(i)=='"'){
						strFlag = 1-strFlag;
					}
					if(strFlag==0&&i<data.length()-1){
						if(annoFlag==0&&data.charAt(i)=='/'&&data.charAt(i+1)=='/'){
							annoFlag = 1;
							bfWriter.append("\r\n");
							annoFlag = 0;
							break;
						}
						else if(annoFlag==0&&data.charAt(i)=='/'&&data.charAt(i+1)=='*'){
							annoFlag = 2;
						}
						else if(annoFlag==2&&data.charAt(i)=='*'&&data.charAt(i+1)=='/'){
							annoFlag = 0;
							if(i+1==data.length()-1){
								bfWriter.append("\r\n");
								break;
							}
							else {
								i+=2;//跳过*/
							}
						}	
					}
					if(annoFlag==0){
						bfWriter.append(data.charAt(i));
					}

				//对于普通一行字符串
				if(i==data.length()-1){
					bfWriter.write("\r\n");
				}
			}
			//针对空行要保留该行
			if(data.length()==0){
				bfWriter.write("\r\n");
			}
		}
		bfReader.close();
		bfWriter.flush();
		bfWriter.close();
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}catch (IOException e){
		e.printStackTrace();
	}
	System.out.println("Filter File sucessfully!\n");
}
}
