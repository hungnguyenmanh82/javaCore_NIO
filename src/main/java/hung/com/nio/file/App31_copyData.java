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
public class App31_copyData {
	public static void main(String args[]) throws IOException {  
		//xem chạy ví dụ ở phần JUnit test
		//lưu y khi chạy ở test thì FileChannelTest.class.getResource("/") = target/test-classes/
		test_copyData();

	} 

	public static void test_copyData()  throws IOException{
		// ==========================input file ==========================
		// folder: "target/classes/testin.txt"   => test run app on Eclipse as Java app
		URL urlIn = App31_copyData.class.getResource("/testin.txt");
		System.out.println("urlIn="+urlIn.getPath());

		FileInputStream input = new FileInputStream (urlIn.getPath()); // Path of Input text file  
		ReadableByteChannel source = input.getChannel();  

		//=========================== output file =========================
		URL urlOut = App31_copyData.class.getResource("/"); // folder = "target/classes" or target/test-classes
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

}
