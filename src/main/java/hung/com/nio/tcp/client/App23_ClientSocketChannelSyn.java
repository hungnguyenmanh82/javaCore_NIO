package hung.com.nio.tcp.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * + Phần này dùng Selector để quản lý các event of ServerSocketChannel và ClientSocketChannel.
 * + step1: run ServerSocketSelector app first
 * + step2: run ClientSocketSelector app after that
 * + cần xem vd FileChannelTest để hiểu rõ về cách dùng NIO Buffer (vd: ByteBuffer, charBuffer...)
 * 
 * cách 1: xem cách dùng Maven để build Jar file include thư viện và run commandline dễ hơn nhiều để nhìn console
 * cach 2: Mỗi app Eclipse sẽ open tren 1 console rieng đc (để ý góc trên bên trái Console có các nút bấm)
 */
public class App23_ClientSocketChannelSyn {
	public static void main (String [] args)  throws IOException, InterruptedException {  
		InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost", 8080);  //lưu ý port 8080 on Server
		
		//
		long startTime = System.currentTimeMillis();
		System.out.println("open() connect = " + startTime);
		SocketChannel client = SocketChannel.open(inetSocketAddress); //connect synchronous 
		System.out.println("time connect = " + (System.currentTimeMillis() - startTime));
		
		boolean isBlock = client.isBlocking();  //default = true
		System.out.println("is channel blocking = "+ isBlock);  //= default = true
		System.out.println("The Client was connected to server:");  
		// Sending messages to the server
		
		
		String [] msg = new String [] {"Time goes fast.", "What next?", "Bye Bye"};  
		for (int j = 0; j < msg.length; j++) {  
			byte [] message = new String(msg [j]).getBytes(); 
			ByteBuffer buffer = ByteBuffer.wrap(message);  
			client.write(buffer);  
			System.out.println(msg [j]);  
			buffer.clear();  
			Thread.sleep(9000);  
		}  
		client.close();               
	}  
}
