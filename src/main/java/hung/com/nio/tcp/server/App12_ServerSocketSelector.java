package hung.com.nio.tcp.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import hung.com.nio.tcp.client.MyContext;

/**
 * + Phần này ko dùng Selector. Chỉ dùng Channel để xử lý Non-blocking.
 * + cần xem vd FileChannelTest để hiểu rõ về cách dùng NIO Buffer (vd: ByteBuffer, charBuffer...)
 * 
 * Phải run bằng commandline mới đc.
 * + step1: run ServerSocketSelector app first (it waits for Client connect)
 *    >>java hung.com.nio.socket.ServerSocketSelector
 *    
 * + step2: run ClientSocketSelector app after that (run from eclipse if you want to debug)
 *   >>java hung.com.nio.socket.ClientSocketSelector
 * 
 * Commandline từ: "D:\JEE_Workpace\NioTest\target\classes"
 */
public class App12_ServerSocketSelector {
	public static void main(String args[]) throws IOException, InterruptedException {
		//=============Selector is used to manage Channels
		Selector selector = Selector.open();
		System.out.println("Selector is open for making connection: " + selector.isOpen());  
		
		//=================== create ServerSocket channel
        ServerSocketChannel srvSocketChannel = ServerSocketChannel.open();  
        InetSocketAddress hostAddress = new InetSocketAddress("localhost", 8080);  
        srvSocketChannel.bind(hostAddress);  
        srvSocketChannel.configureBlocking(false);  //false = non-blocking = asynchronous
        
        //============ register ServerSocketChannel to Selector
        //đây là 4 sự kiện của Socket mà OS thông báo với selector => phải đăng ký với OS
        /**
         * SelectionKey.OP_ACCEPT    => only for Server
         * SelectionKey.OP_CONNECT   => only for client  (server OP_ACCEPT mean Connected)
         * SelectionKey.OP_READ      
         * SelectionKey.OP_WRITE
         * 
         */
        
        //serversocket chỉ có duy nhất event SelectionKey.OP_ACCEPT 
        // serversocket ko dùng 3 event còn lại ở trên => 3 event còn lại dùng cho ClientSocket
        int ops = srvSocketChannel.validOps();  //SelectionKey.OP_ACCEPT
       //đăng ký với OS bắt event của serverSocket: SelectionKey.OP_ACCEPT và gửi cho Selector
        SelectionKey selectKy = srvSocketChannel.register(selector, ops, null);    // selectKy.cancel() to unregister this Channel
//        SelectionKey register(Selector sel, int ops, Object att) => att = attached Object của user
        
      //===================== catch callback Event from OS via Selector here =========
        for (;;) {  
            System.out.println("Waiting for the select operation...");  
            /**
             	int select(): blocking (synchronous)
			 	int select(long TS): blocking but have timeout
				int selectNow(): non-blocking (asynchronous) => return immediately
             */
            //Key: chính là Event. 
            int numberOfEvent = selector.select();  // blocking 
            System.out.println("The Number of selected keys are: numberOfEvent =" + numberOfEvent);  
            
            //===================== type of event is called SelectedKeys
            Set<SelectionKey> selectedKeys = selector.selectedKeys();  
            Iterator<SelectionKey> itr = selectedKeys.iterator();  //iterator.next() points to first item of the list.
            
            //browser all Event of Set
            while (itr.hasNext()) {  
                SelectionKey ky = (SelectionKey) itr.next();
                
                //ky.attachment()
                if (ky.isAcceptable()) {  //SelectionKey.OP_ACCEPT 
                    // The new client connection is accepted
                	//here Buffer send/receiver of socket will be allocate (default 64k/64k)
                	//if not process here, connect timeout will be control by OS to close the connection
                	//khi lấy đc client bắt buộc phải Map với 1 Context bằng HashMap 
                	long startTime = System.currentTimeMillis();
                	System.out.println("Connect Event from Client => not yet accepted: wait 2s " );
                	         	
                	Thread.sleep(2000); //check with ClientSocketSelector2 để test connect đã kết nối bởi OS chưa.
                	System.out.println(" sleep = "+ (System.currentTimeMillis() - startTime) );
                	
                	//connect đã đc thiết lập bởi OS từ trc khi accept (đã test với client đã có connect trc khi accept).
                    SocketChannel clientChannel = srvSocketChannel.accept();  
                    clientChannel.configureBlocking(false);  
                    
                    MyContext myContext = new MyContext(clientChannel);
                    /**
                     * SelectionKey.OP_ACCEPT    => only for Server
                     * SelectionKey.OP_CONNECT   => only for client  (server OP_ACCEPT mean Connected)
                     * SelectionKey.OP_READ      
                     * SelectionKey.OP_WRITE
                     * 
                     */
//                    clientChannel.validOps();  // = SelectionKey.OP_READ | SelectionKey.OP_WRITE | SelectionKey.OP_CONNECT
                    
                    clientChannel.register(selector, SelectionKey.OP_READ|SelectionKey.OP_CONNECT, myContext);  
//                    clientChannel.register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE); 
                    System.out.println("The new connection is accepted from the client: " + clientChannel);  
                }  
                else if (ky.isReadable()) {  //SelectionKey.OP_READ 
                	System.out.println("***********readable");
                    // Data is read from the client  
                	//tìm lại Context của client trong HashMap để xử lý
                	//Đọc và ghi là 2 quá trình độc lập có thể thực thi trên 2 thread riêng
                	//quá trình đọc asynchronous có thể cố định buffer size, đọc nhiều lần (ByteBuf size < socket buffer size)
                	//VertX và Netty cũng làm vậy
                    SocketChannel clientSocket = (SocketChannel) ky.channel();                 
                    MyContext att = (MyContext)ky.attachment();
                    
                    att.readSocket();
          
                }else if (ky.isConnectable()){ //SelectionKey.OP_CONNECT
                	 //never call here
                	 //this option only use for Client side with SocketChannel
                	 System.out.println("***********connectable");
                } else if (ky.isWritable() ){ //SelectionKey.OP_WRITE
                	MyContext att = (MyContext)ky.attachment();
                	att.writeSocket();
                }
                
                System.out.println(itr.toString() );
                itr.remove();  //remove event from Set
            } // end of while loop  
        } // end of for loop  
	} 
}
