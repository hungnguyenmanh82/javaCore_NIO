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
public class App32_ReadableByteChannel3 {
	public static void main(String args[]) throws IOException {  
		//xem chạy ví dụ ở phần JUnit test
		//lưu y khi chạy ở test thì FileChannelTest.class.getResource("/") = target/test-classes/
		test_ReadableByteChannel_3();
	} 


	/**
	 * Ko nên dùng cách này:  ByteBuffer buffer = ByteBuffer.allocateDirect(20 * 1024);
	 */
	public static void test_ReadableByteChannel_3() throws IOException{

		// folder: "target/classes/testin.txt"   => test run app on Eclipse as Java app
		URL url = App32_ReadableByteChannel3.class.getResource("/testin.txt");
		System.out.println("url="+url.getPath());

		FileInputStream input = new FileInputStream (url.getPath()); 
		ReadableByteChannel src = input.getChannel(); 

		//allocate memory for buffer in bytes => cách này gọi buffer.hasArray() = false
		//Ko nên dùng cách này (xem cách 2)
		ByteBuffer buffer = ByteBuffer.allocateDirect(10); 
		//dùng cách này hay hơn:
		//		ByteBuffer buffer = ByteBuffer.wrap(new byte[20 * 1024]);

		if( buffer.hasArray() == false){
			System.out.println("Error: buffer does support byte array: buffer.array() = null");
		}
		
		
		byte[] byteArray = new byte[20]; // = buffer.capacity()

		//read asynchronous
		while (src.read(buffer) != -1)   
		{  //0 <= mark <= position <= limit <= capacity
			if(buffer.hasRemaining() == false){ //buffer full => position = limit = capacity
				int capacity = buffer.capacity();
				System.out.println("capacity = "+ capacity);
				int position = buffer.position();
				System.out.println("position = "+ position);
				int remain = buffer.remaining();
				System.out.println("remain = "+ remain);
				//muốn đọc ra phải thay đổi position = 0 đã.
				buffer.clear();  // this function doesnt erase buffer 
				
				//jump to this function => bản chất là copy byte to byte
				buffer.get(byteArray, 0, position);			
				System.out.println("full buffer:" + new String(byteArray));
				buffer.clear(); //it not erase data. position = 0. limit = capacity.
			}
		}

		if(buffer.position() == 0){
			//have no data in buffer
			return;
		}else{ //get data from buffer
			//data from 0 to position
			buffer.get(byteArray, 0, buffer.position());
			System.out.println("not full buffer" + new String(byteArray,0,buffer.position()));
		}

		src.close();
	}

}
