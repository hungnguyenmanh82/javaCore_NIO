package hung.com.nio.file;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * examples of Channel class and NIO Buffer class (not involve Selector class)
 * + phần này cần học đầu tiên để hiểu về Channel và NIO Buffer
 * + sau khi học xong phần này thì học tiếp phần Selector với ServerSocketSelector và ClientSocketSelector
 */
public class App36_Channel_transfer {
	public static void main(String args[]) throws IOException {  

		test_Channel_transfer();
	} 


	/**
	 * Cách này tối ưu hóa bộ nhớ, truyền data trực tiếp giữa các channel
	 */
	public static void test_Channel_transfer() throws IOException{
		
		URL url = App36_Channel_transfer.class.getResource("/");
		// url=/D:/JEE_Workpace/NioTest/target/test-classes/  => nếu chạy từ JUnit
		// tức là sr/test/resource
		System.out.println("url="+url.getPath());
		

		String[] iF = new String[]{"input1.txt","input2.txt","input3.txt","input4.txt"};  
		//Path of Output file and contents will be written in this file
		//D:/JEE_Workpace/NioTest/target/test-classes/
		String oF = "combine_output.txt";  
		//Acquired the channel for output file  
		FileOutputStream output = new FileOutputStream(new File(url.getPath()+oF));  
		WritableByteChannel targetChannel = output.getChannel();  
		for (int j = 0; j < iF.length; j++)  
		{  
			//Get the channel for input files  
			FileInputStream input = new FileInputStream(url.getPath()+ iF[j]);  
			FileChannel inputChannel = input.getChannel();  

			//The data is tranfer from input channel to output channel  
			inputChannel.transferTo(0, inputChannel.size(), targetChannel);  

			//close an input channel  
			inputChannel.close();  
			input.close();  
		}  
		//close the target channel  
		targetChannel.close();  
		output.close();  
	}
}
