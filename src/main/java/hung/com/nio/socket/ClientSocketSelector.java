package hung.com.nio.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * + Phần này dùng Selector để quản lý các event of ServerSocketChannel và ClientSocketChannel.
 * + step1: run ServerSocketSelector app first
 * + step2: run ClientSocketSelector app after that
 * + cần xem vd FileChannelTest để hiểu rõ về cách dùng NIO Buffer (vd: ByteBuffer, charBuffer...)
 */
public class ClientSocketSelector {
	public static void main (String [] args)  throws IOException, InterruptedException {  
		InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost", 8080);  //lưu ý port 8080 on Server
		
		//channel để dùng non-blocking (or asynchronous)
		SocketChannel client = SocketChannel.open(inetSocketAddress);  
		System.out.println("The Client is sending messages to server...");  
		// Sending messages to the server
		
		
		String [] msg = new String [] {"Time goes fast.", "What next?", "Bye Bye"};  
		for (int j = 0; j < msg.length; j++) {  
			byte [] message = new String(msg [j]).getBytes(); 
			ByteBuffer buffer = ByteBuffer.wrap(message);  
			client.write(buffer);  
			System.out.println(msg [j]);  
			buffer.clear();  
			Thread.sleep(3000);  
		}  
		client.close();               
	}  
}
