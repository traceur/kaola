package com.kaola.crawlers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.wanghaomiao.seimi.annotation.Crawler;
import cn.wanghaomiao.seimi.def.BaseSeimiCrawler;
import cn.wanghaomiao.seimi.http.HttpMethod;
import cn.wanghaomiao.seimi.struct.Request;
import cn.wanghaomiao.seimi.struct.Response;
import cn.wanghaomiao.xpath.model.JXDocument;
import net.sf.json.JSONObject;

@Crawler(name = "creditcompany", delay = 0,useCookie = true)
public class CreditCompanySpider extends BaseSeimiCrawler {

	/**
	 * http://credit.ndrc.gov.cn/XYXX/admin_client/form_designer/ttt/index.html 
	 * http://credit.ndrc.gov.cn/XYXX/doPost;JSESSIONID=wKghggBQWHRAR3ubZ-pEyEZLte6-a0GzvkgA
	 * 中华人民共和国国家发展和改革委员会 信用企业
	 */
	private BufferedWriter file = null;
	
	@Override
	public String[] startUrls() {
		try {
			file = new BufferedWriter(new FileWriter("d://data/creditcompany.txt", true));
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 初始化页面
		return new String[] { "http://www.baidu.com" };
	}

	@Override
	public void start(Response response) {
		try {
		 
		int pageNum = 1;
		 
		for(int p=1;p<=pageNum;p++){
			Map<String, String> params = new HashMap<String, String>();
			params.put("data", "PFJlcXVlc3QgIGFjdGlvbj0iYWRtaW5fY2xpZW50L2Zvcm1fZGVzaWduZXIvdHR0L2FjdGlvbi9nZXRpbmZvLnhtbCIgcmVxdWVzdD0iSlNPTiIgcmVzcG9uc2U9IkpTT04iID48RGF0YT57Ik1JTk5VTSI6ODEsIk1BWE5VTSI6OTAsInBhZ2VTaXplIjoxMCwiTUxMWCI6IiIsIlNYIjoiIiwiWk4iOiIiLCJYWVhYSkwiOiIiLCJYWVpUTUMiOiIiLCJYWURNIjoiIiwiWFlaVElEIjoiIiwiU0oiOiIifTwvRGF0YT48L1JlcXVlc3Q+");
			push(new Request("http://credit.ndrc.gov.cn/XYXX/doPost;JSESSIONID=wKghggBQWHRAR3ubZ-pEyEZLte6-a0GzvkgA", "callback").setHttpMethod(HttpMethod.POST).setParams(params).setSkipDuplicateFilter(true));
		  }
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
    
	
	/*
	 * 获取列表页面每行记录的URL地址
	 */
	public void callback(Response response) {
		System.out.println(response.getRealUrl());
		try {
			 String content = response.getContent();
			 
			 JSONObject tb = JSONObject.fromObject(content);
			 String tbstr= (String) tb.get("tb");
			 
			 String head = "<html><head>22</head><body><table>";
			 
			 String head2 = "</table></body></html>";
			 Element e =Jsoup.parse(head+tbstr+head2);
			 Elements trs= e.getElementsByTag("tr");
			 
			 //序号	企业名称	所在省份	资质证书号	有效期至	资质范围
			 for (int i = 0; i < trs.size(); i++) {
				Elements tds =  trs.get(i).children();
				String rowString = "";
				for (int j = 0; j < tds.size(); j++) {
					String tdStr = tds.get(j).text().trim();
					
					if(j == tds.size()-1){
						tdStr  = tds.get(j).attr("title").trim();
						rowString = rowString +(tdStr.length()==0?"-":tdStr)+"\n" ;
					}else if(j ==1){
						tdStr  = tds.get(j).children().get(0).attr("title").trim();
						rowString = rowString +(tdStr.length()==0?"-":tdStr)+"\001" ;
					}else{
						rowString = rowString + tdStr +"\001";
					}
				}
				System.out.println(rowString);
				file.write(rowString);
			 }
			file.flush();
		} catch (Exception e) {
			//e.printStackTrace();
			
		}
	}
	
    /**
     * UA库可以自行查找整理，这里仅为样例
     * @return
     */
    @Override
    public String getUserAgent(){
        return "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36";
    }
    
}
