import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpGet;

import com.chenxin.core.util.HttpClientUtil;
import com.chenxin.proxy.ProxyHttpClient;
import com.chenxin.spider.entity.Page;


public class HttpClientTest {
	
	public static void main(String[] args) throws IOException {
		//Proxy{timeInterval=5987877449442, ip='27.122.12.45', port=3128, availableFlag=false, anonymousFlag=false, 
			//	lastSuccessfulTime=0, successfulTotalTime=0, failureTimes=0, 
				//successfulTimes=0, successfulAverageTime=0.0}
		HttpGet request  = new HttpGet("https://www.baidu.com");
		request.setConfig(HttpClientUtil.getRequestConfigBuilder().setProxy(new HttpHost("27.122.12.45", 3128)).build());
		HttpClientUtil.getWebPage(request);
	}

}
