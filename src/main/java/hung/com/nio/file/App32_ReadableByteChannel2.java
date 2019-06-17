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
public class App32_ReadableByteChannel2 {
	public static void main(String args[]) throws IOException {  

		test_ReadableByteChannel_2();
	} 


	/**
	 * nên dùng cách này sẽ tối ưu hóa memory hơn:
	 * 
	 * ByteBuffer buffer = ByteBuffer.wrap(new byte[20 * 1024]);
	 */
	public static void test_ReadableByteChannel_2() throws IOException{

		// folder: "target/classes/testin.txt"   => test run app on Eclipse as Java app
		URL url = App32_ReadableByteChannel2.class.getResource("/testin.txt");
		System.out.println("url="+url.getPath());

		FileInputStream input = new FileInputStream (url.getPath()); 
		ReadableByteChannel src = input.getChannel(); 

		//allocate memory for buffer in bytes
		ByteBuffer buffer = ByteBuffer.wrap(new byte[20 * 1024]); //= ByteBuffer.allocate(256)

		if( buffer.hasArray() == false){
			System.out.println("Error: buffer does support byte array");
		}else{
			System.out.println("Ok: buffer does support byte array");
		}
		StringBuilder stringBuilder = new StringBuilder();

		//read asynchronous
		while (src.read(buffer) != -1)   
		{   //0 <= mark <= position <= limit <= capacity
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
	


}
