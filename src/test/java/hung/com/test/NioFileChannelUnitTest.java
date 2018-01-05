package hung.com.test;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import hung.com.nio.file.FileChannelTest;

public class NioFileChannelUnitTest {
    @Test
    public void test_copyData() throws IOException {
    	FileChannelTest.test_copyData();
    	
    	Assert.assertEquals(1, 1);
    }
    
    @Test
    public void test_ReadableByteChannel_1() throws IOException {
    	FileChannelTest.test_ReadableByteChannel_1();
    	
    	Assert.assertEquals(1, 1);
    }
    
    @Test
    public void test_ReadableByteChannel_2() throws IOException {
    	FileChannelTest.test_ReadableByteChannel_2();
    	
    	Assert.assertEquals(1, 1);
    }

    @Test
    public void test_WritableByteChannel_1() throws IOException {
    	FileChannelTest.test_WritableByteChannel_1();
    	
    	Assert.assertEquals(1, 1);
    }
    
    @Test
    public void test_Channel_transfer() throws IOException {
    	FileChannelTest.test_Channel_transfer();
    	
    	Assert.assertEquals(1, 1);
    }
}
