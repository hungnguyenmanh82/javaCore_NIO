package hung.com.nio.socket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * đây chính là attached Object gắn với 1 Socket khi đăng ký với OS.
 * Mục đính khi có sự kiện asynchronous event => thì cần xử lý event này với attched Object
 *
 */
public class MyContext {
	private SocketChannel clientSocket;

	public MyContext(SocketChannel clientSocket) {
		this.clientSocket = clientSocket;
	}
	
	public void readSocket(){
        ByteBuffer buffer = ByteBuffer.allocate(256);  // new buffer
        
        int numberByteRead = 0;
        
		try {
			//chỗ này là vòng lặp đọc nhiều lần vì buffer size < Socket reading Buffer nhiều lần.
			//Mỗi lần buffer đầy lại tạo ra 1 event
			//buffer size nên lấy lớn hơn kích thước gói tin có kích thước lớn nhất (như thế sẽ tối ưu về Memory)
			numberByteRead = clientSocket.read(buffer);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
        System.out.println("buffer.position() = "+ buffer.position());
        System.out.println("numberByteRead = "+ numberByteRead);
        
        if(numberByteRead== -1){ // socket was close
        	System.out.println("***********socketClient was close");
        	try {
				clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }else{
        	String output = new String(buffer.array()).trim();  
            System.out.println("Message read from client: " + output); 
        }  
	}
	
	public void writeSocket(){
		// làm tương tự với trường hợp Read
	}
	
}
