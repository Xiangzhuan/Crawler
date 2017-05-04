import java.io.IOException;

import com.chenxin.proxy.ProxyHttpClient;
import com.chenxin.spider.entity.Page;


public class HttpClientTest {
	
	public static void main(String[] args) throws IOException {
		ProxyHttpClient client = ProxyHttpClient.getInstance();
		Page page = client.getWebPage("http://www.baidu.com");
		System.out.println(page.toString());
	}

}
