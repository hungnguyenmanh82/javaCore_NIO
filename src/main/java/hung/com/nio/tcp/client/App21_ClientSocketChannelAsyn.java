package hung.com.nio.tcp.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * + Phần này dùng Selector để quản lý các event of ServerSocketChannel và ClientSocketChannel.
 * + step1: run ServerSocketSelector app first (it waits for Client connect) 
 * + step2: run ClientSocketSelector app after that (run from eclipse if you want to debug)
 * + cần xem vd FileChannelTest để hiểu rõ về cách dùng NIO Buffer (vd: ByteBuffer, charBuffer...)
 * 
 * cách 1: xem cách dùng Maven để build Jar file include thư viện và run commandline dễ hơn nhiều để nhìn console
 * cach 2: Mỗi app Eclipse sẽ open tren 1 console rieng đc 


 */
public class App21_ClientSocketChannelAsyn {
	public static void main (String [] args)  throws IOException, InterruptedException {  
		InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost", 8080);  //lưu ý port 8080 on Server


		SocketChannel socketChannel = SocketChannel.open();  
		//in case: there are many Local addresses on the computer (more than one network card)
		//socketChannel.bind(local_address); // if not use, automatically allocate by OS

		//configure for non-blocking here
		socketChannel.configureBlocking(false); // socketChannel.isBlocking() = false
		boolean isBlock = socketChannel.isBlocking();
		System.out.println("is channel blocking = "+ isBlock);  //= false (là non-blocking ở trên)

		//asynchronous connect will return immediately 
		boolean connected = socketChannel.connect(inetSocketAddress); // not yet connect here		
		System.out.println("connect state = " + connected);
		System.out.println("pending state = " + socketChannel.isConnectionPending());

		//synchronous finishConnect(): sẽ đáp trả lại connect từ server, nếu chưa có response nó sẽ bị block
		while(!socketChannel.finishConnect()){
			//never run here because, finishConnect() is synchronous
			Thread.sleep(100);//10ms
			System.out.println("--connect state = " + socketChannel.isConnected());
			System.out.println("--pending state = " + socketChannel.isConnectionPending());
		}

		System.out.println("connect state = " + socketChannel.isConnected());
		System.out.println("pending state = " + socketChannel.isConnectionPending());

		// Sending messages to the server		
		String [] msg = new String [] {"Time goes fast.", "What next?", "Bye Bye"};  
		for (int j = 0; j < msg.length; j++) {  
			byte [] message = new String(msg [j]).getBytes(); 
			ByteBuffer buffer = ByteBuffer.wrap(message);  
			socketChannel.write(buffer);  
			System.out.println(msg [j]);  
			buffer.clear();  
			Thread.sleep(3000);  
		}

		socketChannel.close();               
	}  
}
