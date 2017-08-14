package demo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

import com.google.gson.Gson;
import com.jfinal.core.Controller;
import com.jfinal.json.Json;
import com.jfinal.kit.JsonKit;
import com.jfinal.render.Render;
import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.RequestHeaders;
import com.machinepublishers.jbrowserdriver.Settings;
import com.machinepublishers.jbrowserdriver.Timezone;
import com.machinepublishers.jbrowserdriver.UserAgent;

public class HelloController extends Controller {

	public void getQRCodeImg() {

		String doc = "";
		try {
			JBrowserDriver driver = new JBrowserDriver(Settings.builder().traceConsole(false).loggerLevel(Level.OFF) 
					.connectTimeout(10000).socketTimeout(30000)
					 .connectionReqTimeout(30000).timezone(Timezone.ASIA_SHANGHAI).maxRouteConnections(100).cache(false)
					 .userAgent(UserAgent.CHROME).headless(true).ssl("trustanything").hostnameVerification(false)
					 .quickRender(true).javascript(true)
					 .javaOptions("-Xmx1g", "-XX:+UseG1GC", "-server", "-XX:+AggressiveOpts",
					 "-XX:+UseStringDeduplication")
					 .requestHeaders(getJBrowserDriverRequestHeaders())
					.build());
			// https://login.taobao.com/member/login.jhtml?redirectURL=https%3A%2F%2Fwww.taobao.com%2F
			driver.get("https://login.taobao.com/member/login.jhtml?redirectURL=https%3A%2F%2Fwww.taobao.com%2F");
			System.out.println(driver.getStatusCode());
			doc = driver.getPageSource();

			driver.quit();
		} catch (Exception e) {
			// TODO: handle exception
		}
		Pattern p = Pattern.compile("<div.*?id=\"J_QRCodeImg\".*?><img.*?src=\"(.*?)\">");
		Matcher m = p.matcher(doc);
		String lastUrl = "";
		if (m.find()) {
			lastUrl = "https:" + m.group(1);
			System.out.println(lastUrl);
		}
		//
		List<TestVo> urlParas = new ArrayList<TestVo>();
		Pattern p1 = Pattern.compile(
				"<script.*?src=\"https://qrlogin.taobao.com/qrcodelogin/qrcodeLoginCheck.do(.*?)_ksTS=.*?_(.*?)&amp;callback=jsonp(.*?)\".*?>");
		Matcher m1 = p1.matcher(doc);
		while (m1.find()) {
			String find = m1.group(1);
			// System.out.println("*:" + find);
			// System.out.println("**:" + m1.group(2));
			// System.out.println("***:" + m1.group(3));
			TestVo vo = new TestVo(find, Integer.parseInt(m1.group(2)), Integer.parseInt(m1.group(3)));
			urlParas.add(vo);
		}
		setSessionAttr("urlParas", urlParas);

		// redirect("/hello/checkQRCodeImg");

		// new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		// try {
		// TestVo para0 = urlParas.get(0);
		// TestVo para1 = urlParas.get(1);
		// // TODO Auto-generated method stub
		// for (int i = 1; i < 10000; i++) {
		//
		// int pars0First1 = para0.getFirst1() + i * 28;
		// int pars0First2 = para0.getFirst2() + i * 28;
		// int pars1First1 = para1.getFirst1() + i * 28;
		// int pars1First2 = para1.getFirst2() + i * 28;
		// String url1 = "https://qrlogin.taobao.com/qrcodelogin/qrcodeLoginCheck.do" +
		// para0.getUrl() + "_ksTS="
		// + new Date().getTime() + "_" + pars0First1 + "&amp;callback=jsonp" +
		// pars0First2;
		// String url2 = "https://qrlogin.taobao.com/qrcodelogin/qrcodeLoginCheck.do" +
		// para1.getUrl() + "_ksTS="
		// + new Date().getTime() + "_" + pars1First1 + "&amp;callback=jsonp" +
		// pars1First2;
		// Response orderResp1 = Jsoup.connect(url1).execute();
		// String doc2 = orderResp1.body();
		// System.out.println(doc2);
		// Response orderResp2 = Jsoup.connect(url1).execute();
		// String doc3 = orderResp2.body();
		// System.out.println(doc3);
		//
		// Thread.sleep(2000);
		//
		// }
		//
		// } catch (Exception e) {
		// // TODO: handle exception
		// }

		// }
		// }).start();
		renderText(lastUrl);
	}

	public void chekQRCodeImgBack() throws Exception {
		List<TestVo> urlParas = getSessionAttr("urlParas");
		// if (urlParas == null) {
		// renderNull();
		// return;
		// }
		// try {
		// int i = getParaToInt("index");
		// System.out.println(i);
		TestVo para0 = urlParas.get(0);
		TestVo para1 = urlParas.get(1);
		// // TODO Auto-generated method stub
		for (int i = 1; i < 10000; i++) {
			//
			int pars0First1 = para0.getFirst1() + i * 28;
			int pars0First2 = para0.getFirst2() + i * 28;
			int pars1First1 = para1.getFirst1() + i * 28;
			int pars1First2 = para1.getFirst2() + i * 28;
			String url1 = "https://qrlogin.taobao.com/qrcodelogin/qrcodeLoginCheck.do" + para0.getUrl() + "_ksTS="
					+ new Date().getTime() + "_" + pars0First1 + "&amp;callback=jsonp" + pars0First2;
			// String url2 = "https://qrlogin.taobao.com/qrcodelogin/qrcodeLoginCheck.do" +
			// para1.getUrl() + "_ksTS="
			// + new Date().getTime() + "_" + pars1First1 + "&amp;callback=jsonp" +
			// pars1First2;
			Response orderResp1 = Jsoup.connect(url1).execute();
			String doc2 = orderResp1.body();
			System.out.println(doc2);

			ReturnJsonVo jsonVO = JsonKit.parse(doc2, ReturnJsonVo.class);
			if ("100006".equals(jsonVO.getCode())) {
				// 登录成功的url

			}
			// Response orderResp2 = Jsoup.connect(url2).execute();
			// String doc3 = orderResp2.body();
			// System.out.println(doc3);

			// renderJson(doc2);
			Thread.sleep(2000);

		}

		// } catch (Exception e) {
		// // TODO: handle exception
		// }
		// renderNull();

	}

	public void checkQRCodeImg() throws IOException {
		List<TestVo> urlParas = getSessionAttr("urlParas");
		if (urlParas == null) {
			renderNull();
			return;
		}
		// try {
		int i = getParaToInt("index");
		System.out.println(i);
		TestVo para0 = urlParas.get(0);
		TestVo para1 = urlParas.get(1);
		// // TODO Auto-generated method stub
		//// for (int i = 1; i < 10000; i++) {
		//
		int pars0First1 = para0.getFirst1() + i * 28;
		int pars0First2 = para0.getFirst2() + i * 28;
		int pars1First1 = para1.getFirst1() + i * 28;
		int pars1First2 = para1.getFirst2() + i * 28;
		String url1 = "https://qrlogin.taobao.com/qrcodelogin/qrcodeLoginCheck.do" + para0.getUrl() + "_ksTS="
				+ new Date().getTime() + "_" + pars0First1 + "&amp;callback=jsonp" + pars0First2;
		// String url2 = "https://qrlogin.taobao.com/qrcodelogin/qrcodeLoginCheck.do" +
		// para1.getUrl() + "_ksTS="
		// + new Date().getTime() + "_" + pars1First1 + "&amp;callback=jsonp" +
		// pars1First2;
		Response orderResp1 = Jsoup.connect(url1).execute();
		String doc2 = orderResp1.body();
		System.out.println(doc2);
		// Response orderResp2 = Jsoup.connect(url2).execute();
		// String doc3 = orderResp2.body();
		// System.out.println(doc3);
		Gson gson = new Gson();
		ReturnJsonVo jsonVO = gson.fromJson(doc2, ReturnJsonVo.class);
		// ReturnJsonVo jsonVO = JsonKit.parse(doc2, ReturnJsonVo.class);
		if ("10006".equals(jsonVO.getCode())) {
			// 登录成功的url
			String goLoginUrl = jsonVO.getUrl();
			System.out.println("登录成功的url********:" + goLoginUrl);
			Response req = Jsoup.connect(goLoginUrl).execute();
			StringBuffer cookieStr = new StringBuffer();
			Map<String, String> cookies = req.cookies();
			for (String key : cookies.keySet()) {
				cookieStr.append(key + "=" + cookies.get(key) + "; ");
			}
			setSessionAttr("taoBaoCookie", cookieStr.toString());

		}
		renderJson(doc2);
		// Thread.sleep(2000);

		// }

		// } catch (Exception e) {
		// // TODO: handle exception
		// }
		// renderNull();
	}

	public void getShouChangSource() throws IOException {
		String cookie = getSessionAttr("taoBaoCookie");
		Response orderResp4 = Jsoup.connect(
				"https://shoucang.taobao.com/collectList.htm?spm=a1z0b.3.a2109.d1000374.1103f4fayLnMJd&nekot=1502188689794")
				.header("authority", "shoucang.taobao.com").header("method", "get")
				.header("Accept",
						"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
				.header("upgrade-insecure-requests", "1")
				.userAgent(
						"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36")
				.header("Accept-Encoding", "gzip, deflate, br").header("Accept-Language", "zh-CN,zh;q=0.8")
				.cookie("Cookie", cookie).execute();
		System.out.println("收藏页返回的code: " + orderResp4.statusCode() + ", msg: " + orderResp4.statusMessage());

		String doc4 = orderResp4.body();
		System.out.println("收藏页:" + doc4);
		renderText("收藏页源码:" + doc4);
	}

	public RequestHeaders getJBrowserDriverRequestHeaders() {
		LinkedHashMap<String, String> headersTmp = new LinkedHashMap<>();
		headersTmp.put("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		headersTmp.put("authority", "login.taobao.com");
		headersTmp.put("method", "GET");
		headersTmp.put("path", "/member/login.jhtml?redirectURL=https%3A%2F%2Fwww.taobao.com%2F");
		headersTmp.put("scheme", "https");
		headersTmp.put("Upgrade-Insecure-Requests", "1");
		headersTmp.put("User-Agent",
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36");
		headersTmp.put("Accept-Encoding", "gzip, deflate, br");
		headersTmp.put("Accept-Language", "zh-CN,zh;q=0.8");
		headersTmp.put("cache-control", "no-cache");
		headersTmp.put("pragma", "no-cache");
		return new RequestHeaders(headersTmp);
	}
}

class TestVo {
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getFirst1() {
		return first1;
	}

	public void setFirst1(int first1) {
		this.first1 = first1;
	}

	public int getFirst2() {
		return first2;
	}

	public void setFirst2(int first2) {
		this.first2 = first2;
	}

	public TestVo(String url, int first1, int first2) {
		super();
		this.url = url;
		this.first1 = first1;
		this.first2 = first2;
	}

	private String url;
	private int first1;
	private int first2;

}