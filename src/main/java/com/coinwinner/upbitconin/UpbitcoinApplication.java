package com.coinwinner.upbitconin;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class UpbitcoinApplication {


	public void checkHotTicker(String ticker) throws IOException, ParseException {
		OkHttpClient client = new OkHttpClient();

		Request request = new Request.Builder()
			.url("https://api.upbit.com/v1/candles/days?market="+ticker+"&count=10")
			.get()
			.addHeader("accept", "application/json")
			.build();

		Response response = client.newCall(request).execute();
		ResponseBody responseBody = response.body();
		String result = responseBody.string();

		JSONParser jsonParser = new JSONParser();
		Object obj = jsonParser.parse(result);

		JSONArray jsonArray = (JSONArray) obj;

		String marketName ="";
		double price1 = 0;
		double price5 = 0;
		double price10 = 0;
		double highPrice = 0;
		double lowPrice = 0;
		double tradePrice = 0;
		double openingPrice = 0;
		double increasePercent = 0;
		double changeRate5days = 0;
		double changeRate10days = 0;

		ArrayList priceList = new ArrayList<>();

		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject = (JSONObject) jsonArray.get(i);

			marketName = (String)jsonObject.get("market");
			highPrice = (double)jsonObject.get("high_price");
			lowPrice = (double)jsonObject.get("low_price");
			tradePrice = (double)jsonObject.get("trade_price");
			openingPrice = (double)jsonObject.get("opening_price");
			increasePercent = Math.floor(((tradePrice-openingPrice)/(openingPrice))*10000) / 100;

			if(i == 0) {
				price1 = (double)jsonObject.get("trade_price");
			}else if(i == 4) {
				price5 = (double)jsonObject.get("trade_price");
			}else if(i == 9) {
				price10 = (double)jsonObject.get("trade_price");
			}

			priceList.add(tradePrice);
			log.info(i+1+"일 코인명 : {}",marketName);
			log.info(i+1+"고가 : {}",highPrice);
			log.info(i+1+"저가 : {}",lowPrice);
			log.info(i+1+"시가 : {}",openingPrice);
			log.info(i+1+"종가 : {}",tradePrice);
			log.info(i+1+"상승률 : {}",increasePercent);

		}
		changeRate5days = Math.floor(((price1-price5)/(price5))*10000) / 100;
		changeRate10days = Math.floor(((price1-price10)/(price10))*10000) / 100;

		log.info("코인명 : {}",marketName);
		log.info("5일 변동률 : {}",changeRate5days);
		log.info("10일 변동률 : {}",changeRate10days);

	}

	public List<String> getTickers() throws ParseException {
		URL url = null;
		String readLine = null;
		StringBuilder buffer = null;
		BufferedReader bufferedReader = null;
		BufferedWriter bufferedWriter = null;
		HttpURLConnection urlConnection = null;

		int connTimeout = 5000;
		int readTimeout = 3000;
		String btc5 ="https://api-testnet.bybit.com/v5/market/kline?category=linear&symbol=BTCUSDT&interval=5";
		String tickers ="https://api-testnet.bybit.com/v5/market/tickers?category=linear";
		String symbol = "";
		//https://api-testnet.bybit.com/v5/market/funding/history?category=linear&symbol=10000LADYSUSDT&startTime=1692086400000&endTime=1692086400000

		String apiUrl = tickers;    // 각자 상황에 맞는 IP & url 사용

		try
		{
			url = new URL(apiUrl);
			urlConnection = (HttpURLConnection)url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setConnectTimeout(connTimeout);
			urlConnection.setReadTimeout(readTimeout);
			urlConnection.setRequestProperty("Accept", "application/json;");

			buffer = new StringBuilder();
			if(urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK)
			{
				bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));
				while((readLine = bufferedReader.readLine()) != null)
				{
					buffer.append(readLine).append("\n");
				}
			}
			else
			{
				buffer.append("code : ");
				buffer.append(urlConnection.getResponseCode()).append("\n");
				buffer.append("message : ");
				buffer.append(urlConnection.getResponseMessage()).append("\n");
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			try
			{
				if (bufferedWriter != null) { bufferedWriter.close(); }
				if (bufferedReader != null) { bufferedReader.close(); }
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		JSONParser jsonParser = new JSONParser();
		Object obj = jsonParser.parse(buffer.toString());
		JSONObject jsonObject = (JSONObject) obj;
		JSONObject jsonObject1 = (JSONObject) jsonObject.get("result");
		JSONArray jsonArray = (JSONArray) jsonObject1.get("list");
		List<String> tickerList = new ArrayList<>();

		for (int i = 0; i < jsonArray.size(); i++) {
			//jsonArray.get(i);
			JSONObject j = (JSONObject) jsonArray.get(i);
			String tickerName = (String)j.get("symbol");
			String fundingRate = (String)j.get("fundingRate");
			tickerList.add(tickerName);
			System.out.println(tickerName + " : " + fundingRate);
			//System.out.println(tickerName.toString());
		}
		return tickerList;
	}

	public void getFundingRate(String symbol) throws Exception {
		URL url = null;
		String readLine = null;
		StringBuilder buffer = null;
		BufferedReader bufferedReader = null;
		BufferedWriter bufferedWriter = null;
		HttpURLConnection urlConnection = null;

		int connTimeout = 5000;
		int readTimeout = 3000;
		String btc5 ="https://api-testnet.bybit.com/v5/market/kline?category=linear&symbol=BTCUSDT&interval=5";
		String tickers ="https://api-testnet.bybit.com/v5/market/tickers?category=linear";

		String fundingRate = "https://api-testnet.bybit.com/v5/market/funding/history?category=linear&symbol="+symbol+"&startTime=1692086400000&endTime=1692086400000";
		String apiUrl = fundingRate;    // 각자 상황에 맞는 IP & url 사용

		try
		{
			url = new URL(apiUrl);
			urlConnection = (HttpURLConnection)url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setConnectTimeout(connTimeout);
			urlConnection.setReadTimeout(readTimeout);
			urlConnection.setRequestProperty("Accept", "application/json;");

			buffer = new StringBuilder();
			if(urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK)
			{
				bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));
				while((readLine = bufferedReader.readLine()) != null)
				{
					buffer.append(readLine).append("\n");
				}
			}
			else
			{
				buffer.append("code : ");
				buffer.append(urlConnection.getResponseCode()).append("\n");
				buffer.append("message : ");
				buffer.append(urlConnection.getResponseMessage()).append("\n");
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			try
			{
				if (bufferedWriter != null) { bufferedWriter.close(); }
				if (bufferedReader != null) { bufferedReader.close(); }
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		JSONParser jsonParser = new JSONParser();
		Object obj = jsonParser.parse(buffer.toString());
		JSONObject jsonObject = (JSONObject) obj;
		JSONObject jsonObject1 = (JSONObject) jsonObject.get("result");
		JSONArray jsonArray = (JSONArray) jsonObject1.get("list");
		List<String> tickerList = new ArrayList<>();

		for (int i = 0; i < jsonArray.size(); i++) {
			//jsonArray.get(i);
			JSONObject j = (JSONObject) jsonArray.get(i);
			String rate1 = (String)j.get("fundingRate");
			double rate = Double.parseDouble(rate1);
			if(rate <= -0.001){
				String fundRate = (String)j.get("symbol") + ":  " + (String)j.get("fundingRate");
				System.out.println(fundRate);
			}
			//String fundRate = (String)j.get("symbol") + ":  " + (String)j.get("fundingRate");
			//System.out.println(fundRate);
		}
		//System.out.println(jsonObject1);

	}

	public void getInfo() throws Exception {
		URL url = null;
		String readLine = null;
		StringBuilder buffer = null;
		BufferedReader bufferedReader = null;
		BufferedWriter bufferedWriter = null;
		HttpURLConnection urlConnection = null;

		int connTimeout = 5000;
		int readTimeout = 3000;
		String btc5 ="https://api-testnet.bybit.com/v5/market/kline?category=linear&symbol=BTCUSDT&interval=5";
		String tickers ="https://api-testnet.bybit.com/v5/market/tickers?category=linear";

		//String fundingRate = "https://api-testnet.bybit.com/v5/market/funding/history?category=linear&symbol="+symbol+"&startTime=1692086400000&endTime=1692086400000";
		String apiUrl = btc5;    // 각자 상황에 맞는 IP & url 사용

		try
		{
			url = new URL(apiUrl);
			urlConnection = (HttpURLConnection)url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setConnectTimeout(connTimeout);
			urlConnection.setReadTimeout(readTimeout);
			urlConnection.setRequestProperty("Accept", "application/json;");

			buffer = new StringBuilder();
			if(urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK)
			{
				bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));
				while((readLine = bufferedReader.readLine()) != null)
				{
					buffer.append(readLine).append("\n");
				}
			}
			else
			{
				buffer.append("code : ");
				buffer.append(urlConnection.getResponseCode()).append("\n");
				buffer.append("message : ");
				buffer.append(urlConnection.getResponseMessage()).append("\n");
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			try
			{
				if (bufferedWriter != null) { bufferedWriter.close(); }
				if (bufferedReader != null) { bufferedReader.close(); }
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		JSONParser jsonParser = new JSONParser();
		Object obj = jsonParser.parse(buffer.toString());
		JSONObject jsonObject = (JSONObject) obj;
		JSONObject jsonObject1 = (JSONObject) jsonObject.get("result");

		System.out.println(jsonObject1);

	}


	public static void main(String[] args) throws Exception {

		UpbitcoinApplication upbitcoinApplication = new UpbitcoinApplication();


//		String[] tickers = {"BTC-HIFI","BTC-ASTR","BTC-LPT"};
//
//		for(String ticker : tickers) {
//			upbitcoinApplication.checkHotTicker(ticker);
//		}

//		List<String> tickers = upbitcoinApplication.getTickers();
//		for (String ticker : tickers) {
//			upbitcoinApplication.getFundingRate(ticker);
//		}

		upbitcoinApplication.getTickers();

	}

}
