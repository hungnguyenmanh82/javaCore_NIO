package hung.com.nio.file;

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
public class FileChannelTest {
	public static void main(String args[]) throws IOException {  
		//xem chạy ví dụ ở phần JUnit test
		//lưu y khi chạy ở test thì FileChannelTest.class.getResource("/") = target/test-classes/
		test_copyData();
		test_Channel_transfer();
	} 

	public static void test_copyData()  throws IOException{
		// ==========================input file ==========================
		// folder: "target/classes/testin.txt"   => test run app on Eclipse as Java app
		URL urlIn = FileChannelTest.class.getResource("/testin.txt");
		System.out.println("urlIn="+urlIn.getPath());

		FileInputStream input = new FileInputStream (urlIn.getPath()); // Path of Input text file  
		ReadableByteChannel source = input.getChannel();  

		//=========================== output file =========================
		URL urlOut = FileChannelTest.class.getResource("/"); // folder = "target/classes" or target/test-classes
		System.out.println("urlOut="+urlOut.getPath()+"testOut.txt");

		FileOutputStream output = new FileOutputStream (urlOut.getPath()+"testOut.txt"); // Path of Output text file  
		WritableByteChannel destination = output.getChannel();

		//=========================copy from input file to output file =================
		copyData(source, destination);  
		source.close();  
		destination.close();  
	}

	private static void copyData(ReadableByteChannel src, WritableByteChannel dest) throws IOException   
	{  
		ByteBuffer buffer = ByteBuffer.allocateDirect(20 * 1024);
		//read asynchronous
		while (src.read(buffer) != -1)   
		{  
			//limit = current position, position = 0
			buffer.flip();  
			// keep sure that buffer was fully drained  
			while (buffer.hasRemaining())   //tính từ limit to capacity
			{  
				dest.write(buffer);  //get data from postion to limit then write to Dest channel.
			}  
			buffer.clear(); // position = 0, limit = capacity. don't erase data in buffer
		}  
	}

	public static void test_WritableByteChannel_1() throws IOException{
		//=========================== output file =========================
		URL urlOut = FileChannelTest.class.getResource("/");
		System.out.println("urlOut="+urlOut.getPath()+"testWrite.txt");

		FileOutputStream output = new FileOutputStream (urlOut.getPath()+"testWrite.txt"); // Path of Output text file  
		WritableByteChannel destination = output.getChannel();

		byte[] bytes = new String("you are beautiful to me. I wana be next to you all time").getBytes();

		//buffer handle array directly (dont copy)
		ByteBuffer buffer = ByteBuffer.wrap(bytes);

		while (buffer.hasRemaining())   //tính từ limit to capacity
		{  
			destination.write(buffer);  //get data from postion to limit then write to Dest channel.
		}  

		//Here: position = limit
		buffer.clear(); // position = 0, limit = capacity. don't erase data in buffer

		bytes = new String("\r\nnever give up, be strong, com on, go").getBytes();

		buffer.put(bytes);  //position is change
		buffer.flip();     //limit = position, position = 0
		destination.write(buffer);

		destination.close();

	}


	/**
	 * Ko nên dùng cách này:  ByteBuffer buffer = ByteBuffer.allocateDirect(20 * 1024);
	 */
	public static void test_ReadableByteChannel_1() throws IOException{

		// folder: "target/classes/testin.txt"   => test run app on Eclipse as Java app
		URL url = FileChannelTest.class.getResource("/testin.txt");
		System.out.println("url="+url.getPath());

		FileInputStream input = new FileInputStream (url.getPath()); 
		ReadableByteChannel src = input.getChannel(); 

		//allocate memory for buffer in bytes => cách này gọi buffer.hasArray() = false
		//Ko nên dùng cách này
		ByteBuffer buffer = ByteBuffer.allocateDirect(20 * 1024);
		//dùng cách này hay hơn:
		//		ByteBuffer buffer = ByteBuffer.wrap(new byte[20 * 1024]);

		if( buffer.hasArray() == false){
			System.out.println("Error: buffer does support byte array");
		}
		StringBuilder stringBuilder = new StringBuilder();

		//read asynchronous
		while (src.read(buffer) != -1)   
		{  
			if(buffer.hasRemaining() == false){ //buffer full => position = limit = capacity
				stringBuilder.append(new String(buffer.array()));
				buffer.clear(); //it not erase data. position = 0. limit = capacity.
			}
		}

		if(buffer.position() == 0){
			//have no data in buffer
			return;
		}else{ //get data from buffer
			//data from 0 to position
			stringBuilder.append(new String(buffer.array(),0,buffer.position()));
		}

		System.out.println(stringBuilder.toString());

		src.close();
	}

	/**
	 * nên dùng cách này sẽ tối ưu hóa memory hơn:
	 * 
	 * ByteBuffer buffer = ByteBuffer.wrap(new byte[20 * 1024]);
	 */
	public static void test_ReadableByteChannel_2() throws IOException{

		// folder: "target/classes/testin.txt"   => test run app on Eclipse as Java app
		URL url = FileChannelTest.class.getResource("/testin.txt");
		System.out.println("url="+url.getPath());

		FileInputStream input = new FileInputStream (url.getPath()); 
		ReadableByteChannel src = input.getChannel(); 

		//allocate memory for buffer in bytes
		ByteBuffer buffer = ByteBuffer.wrap(new byte[20 * 1024]);

		if( buffer.hasArray() == false){
			System.out.println("Error: buffer does support byte array");
		}else{
			System.out.println("Ok: buffer does support byte array");
		}
		StringBuilder stringBuilder = new StringBuilder();

		//read asynchronous
		while (src.read(buffer) != -1)   
		{  
			if(buffer.hasRemaining() == false){ //buffer full => position = limit = capacity
				stringBuilder.append(new String(buffer.array()));
				buffer.clear(); //it not erase data. position = 0. limit = capacity.
			}
		}

		if(buffer.position() == 0){
			//have no data in buffer
			return;
		}else{ //get data from buffer
			//data from 0 to position
			stringBuilder.append(new String(buffer.array(),0,buffer.position()));
		}

		src.close();
		System.out.println(stringBuilder.toString());
	}

	/**
	 * Cách này tối ưu hóa bộ nhớ, truyền data trực tiếp giữa các channel
	 */
	public static void test_Channel_transfer() throws IOException{
		
		URL url = FileChannelTest.class.getResource("/");
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
