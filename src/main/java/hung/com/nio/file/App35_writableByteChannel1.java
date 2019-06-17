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
public class App35_writableByteChannel1 {
	public static void main(String args[]) throws IOException {  
		//xem chạy ví dụ ở phần JUnit test
		//lưu y khi chạy ở test thì FileChannelTest.class.getResource("/") = target/test-classes/
		test_WritableByteChannel_1();
	} 

	public static void test_WritableByteChannel_1() throws IOException{
		//=========================== output file =========================
		URL urlOut = App35_writableByteChannel1.class.getResource("/");
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


}
