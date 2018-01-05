package hung.com.nio.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * + Phần này ko dùng Selector. Chỉ dùng Channel để xử lý Non-blocking.
 * + cần xem vd FileChannelTest để hiểu rõ về cách dùng NIO Buffer (vd: ByteBuffer, charBuffer...)
 * 
 * Phải run bằng commandline mới đc.
 * + step1: run ServerSocketSelector app first (it waits for Client connect)
 *    >>java hung.com.nio.socket.ServerSocketSelector
 *    
 * + step2: run ClientSocketSelector app after that
 * 
 * Commandline từ: "D:\JEE_Workpace\NioTest\target\classes"
 */
public class ServerSocketSelector {
	public static void main(String args[]) throws IOException {
		//=============Selector is used to manage Channels
		Selector selector = Selector.open();
		System.out.println("Selector is open for making connection: " + selector.isOpen());  
		
		//=================== create ServerSocket channel
        ServerSocketChannel srvSocketChannel = ServerSocketChannel.open();  
        InetSocketAddress hostAddress = new InetSocketAddress("localhost", 8080);  
        srvSocketChannel.bind(hostAddress);  
        srvSocketChannel.configureBlocking(false);  //false = non-blocking = asynchronous
        
        //============ register ServerSocketChannel to Selector
        /**
         * SelectionKey.OP_ACCEPT    => only for Server
         * SelectionKey.OP_CONNECT   => only for client  (server OP_ACCEPT mean Connected)
         * SelectionKey.OP_READ      
         * SelectionKey.OP_WRITE
         * 
         */
        int ops = srvSocketChannel.validOps();  //SelectionKey.OP_ACCEPT
        SelectionKey selectKy = srvSocketChannel.register(selector, ops, null);  
        
        for (;;) {  
            System.out.println("Waiting for the select operation...");  
            /**
             	int select(): blocking (synchronous)
			 	int select(long TS): blocking but have timeout
				int selectNow(): non-blocking (asynchronous) => return immediately
             */
            //Key: chính là Event. 
            int numberOfEvent = selector.select();  // blocking 
            System.out.println("The Number of selected keys are: " + numberOfEvent);  
            
            //===================== type of event is called SelectedKeys
            Set<SelectionKey> selectedKeys = selector.selectedKeys();  
            Iterator<SelectionKey> itr = selectedKeys.iterator();  //iterator.next() points to first item of the list.
            
            //browser all items of Set
            while (itr.hasNext()) {  
                SelectionKey ky = (SelectionKey) itr.next();  
                if (ky.isAcceptable()) {  //SelectionKey.OP_ACCEPT 
                    // The new client connection is accepted  
                    SocketChannel clientChannel = srvSocketChannel.accept();  
                    clientChannel.configureBlocking(false);  
                    /**
                     * SelectionKey.OP_ACCEPT    => only for Server
                     * SelectionKey.OP_CONNECT   => only for client  (server OP_ACCEPT mean Connected)
                     * SelectionKey.OP_READ      
                     * SelectionKey.OP_WRITE
                     * 
                     */
                    //clientChannel.validOps();  // đăng ký tất cả các Operation
                    clientChannel.register(selector, SelectionKey.OP_READ);  
//                    clientChannel.register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE); 
                    System.out.println("The new connection is accepted from the client: " + clientChannel);  
                }  
                else if (ky.isReadable()) {  //SelectionKey.OP_READ 
                    // Data is read from the client  
                    SocketChannel client = (SocketChannel) ky.channel();  
                    ByteBuffer buffer = ByteBuffer.allocate(256);  
                    client.read(buffer);  
                    String output = new String(buffer.array()).trim();  
                    System.out.println("Message read from client: " + output);  
                    if (output.equals("Bye Bye")) {  
                        client.close();  
                        System.out.println("The Client messages are complete; close the session.");  
                    }  
                }
/*                else if (ky.isWritable()){
                	
                }*/
                
                itr.remove();  //remove event from Set
            } // end of while loop  
        } // end of for loop  
	} 
}