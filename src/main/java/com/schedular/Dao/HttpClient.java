package com.schedular.Dao;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.parser.JSONParser;


public class HttpClient {
	org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger(this.getClass());

	public org.json.simple.JSONObject httpResponse(String ivrRequestID, String url, String requestType,
			org.json.simple.JSONObject requestBody, int readTimeout, int connectionTimeout, int fetchTimeout) {
		org.json.simple.JSONObject finalJson = null;
		HttpUriRequest request = null;

		try {

			RequestConfig config = RequestConfig.custom().setConnectTimeout(connectionTimeout * 1000)
					.setSocketTimeout(fetchTimeout * 1000).setConnectionRequestTimeout(readTimeout * 1000).build();

			HttpClientBuilder clientbuilder = HttpClients.custom();
			if ("Get".equalsIgnoreCase(requestType)) {
				request = new HttpGet(url);
				request.setHeader("Content-Type", "application/json; charset=utf-8");
				request.setHeader("apirequestId", ivrRequestID);
			} else if ("Post".equalsIgnoreCase(requestType)) {
				request = new HttpPost(url);
				request.setHeader("Content-Type", "application/json; charset=utf-8");
				request.setHeader("apirequestId", ivrRequestID);
				((HttpPost) request).setEntity(new StringEntity(requestBody.toString(), "UTF-8"));
			} else {
				request = new HttpPut(url);
				request.setHeader("Content-Type", "application/json; charset=utf-8");
				request.setHeader("apirequestId", ivrRequestID);
				((HttpPut) request).setEntity(new StringEntity(requestBody.toString(), "UTF-8"));
			}

			try (CloseableHttpClient httpClient = clientbuilder.setDefaultRequestConfig(config).build();
					CloseableHttpResponse response = httpClient.execute(request)) {

				finalJson = (org.json.simple.JSONObject) new JSONParser()
						.parse(EntityUtils.toString(response.getEntity()));

			} catch (Exception e) {
				LOGGER.error(e.getMessage());
			}

		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}

		return finalJson;

	}
}
