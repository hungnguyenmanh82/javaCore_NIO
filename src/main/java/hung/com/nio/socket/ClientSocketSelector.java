package hung.com.nio.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * + Phần này dùng Selector để quản lý các event of  ClientSocketChannel.
 * + cần hiểu về Channel trc khi hiểu về Selector
 * 
 * Phải run bằng commandline mới đc.
 * + step1: run ServerSocketSelector app first (it waits for Client connect)
 *    >>java hung.com.nio.socket.ServerSocketSelector
 *    
 * + step2: run ClientSocketSelector app after that (run from eclipse if you want to debug)
 *   >>java hung.com.nio.socket.ClientSocketSelector
 */
public class ClientSocketSelector {
	public static void main (String [] args)  throws IOException, InterruptedException {  
		InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost", 8080);  //lưu ý port 8080 on Server

		
		//======================================================================
		SocketChannel socketChannel1 = SocketChannel.open();
		//in case: there are many Local addresses on the computer (more than one network card)
		//socketChannel.bind(local_address); // if not use, automatically allocate by OS
		socketChannel1.configureBlocking(false); // socketChannel.isBlocking() = false

		SocketChannel socketChannel2 = SocketChannel.open();  
		socketChannel2.configureBlocking(false); // socketChannel.isBlocking() = false
		
		//=======================================================================
        /**
         * SelectionKey.OP_ACCEPT    => only for Server
         * SelectionKey.OP_CONNECT   => only for client  (server OP_ACCEPT mean Connected)
         * SelectionKey.OP_READ      
         * SelectionKey.OP_WRITE
         * 
         */
		//register to receive Channel's Event from OS via Selector
		Selector selector = Selector.open();
		socketChannel1.register(selector, SelectionKey.OP_CONNECT|SelectionKey.OP_WRITE);
		socketChannel2.register(selector, SelectionKey.OP_CONNECT|SelectionKey.OP_WRITE);
		
		//asynchronous connect will return immediately 
		socketChannel1.connect(inetSocketAddress); // not yet connect here		
		socketChannel2.connect(inetSocketAddress); // not yet connect here	
		
		//===================== catch callback Event from OS via Selector here =========
		//===================== catch callback Event from OS via Selector here =========
		int count = 0;
        for (;;) {
        	count++;
            System.out.println("Waiting for the select operation...");  
            /**
             	int select(): blocking (synchronous) => may return 0
			 	int select(long TS): blocking but have timeout
				int selectNow(): non-blocking (asynchronous) => return immediately
             */
            //Key: chính là Event. 
            int numberOfEvent = selector.select();  // blocking (có trường hợp trả về 0 luôn, đã test).
            System.out.println("The Number of selected keys are: " + numberOfEvent);  
            
            //===================== type of event is called SelectedKeys
            Set<SelectionKey> selectedKeys = selector.selectedKeys();  
            Iterator<SelectionKey> itr = selectedKeys.iterator();  //iterator.next() points to first item of the list.
                  
            //browser all Event of Set
            while (itr.hasNext()) {  
                SelectionKey key = (SelectionKey) itr.next();  
                if (key.isConnectable()) {  //SelectionKey.OP_ACCEPT 
                	SocketChannel client = (SocketChannel) key.channel();  
                	//client.getLocalAddress() = null point exception here
                	
                	client.finishConnect();  //must call this function 
                	//after call finishConnect() => client.getLocalAddress() != null here
                	System.out.println("***********isConnectable: "+ new String(client.getLocalAddress().toString()));
                	
                } else if (key.isWritable()) {  //SelectionKey.OP_READ 
                    // Data is read from the client  
                	System.out.println("***********isWritable");
                    // Data is read from the client  
                    SocketChannel client = (SocketChannel) key.channel();  
                    
                    ByteBuffer buffer = ByteBuffer.wrap(new String(client.getLocalAddress().toString()).getBytes());
                    int numByte = client.write(buffer);
                    
                    System.out.println("numByte = "+ numByte);
                    
                    if(count ==2){
                    	System.out.println("socket close");
                    	client.close();  //
                    }
                   
                }
                
                itr.remove();  //remove event from Set
            } // end of while loop  
        } // end of for loop  
             
	}  
}
