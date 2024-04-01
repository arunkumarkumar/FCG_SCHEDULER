package com.schedular.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.schedular.Dao.HttpClient;

public class ApiHit {

	Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger(this.getClass());

	public List<String> readFile(String filePath) {
		List<String> requests = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = br.readLine()) != null) {
				requests.add(line);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return requests;
		}
		return requests;
	}

	public void sendRequests(List<String> requests, String starNik_File, int readTimeout, String apiurl,
			int connectionTimeout, int fetchTimeout) {
		try {
			for (int chance = 0; chance < 4; chance++) {

				if (!requests.isEmpty()) {
					List<String> failedrequests = new ArrayList<>();
					for (int requestId = 0; requestId < requests.size(); requestId++) {
						if (!apiHit(requests.get(requestId), readTimeout, apiurl, connectionTimeout, fetchTimeout)) {
							failedrequests.add(requests.get(requestId));
						}
					}
					requests.clear();
					requests.addAll(failedrequests);
				}

			}
			writeFile(requests, starNik_File);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	private boolean deleteFile(List<String> requests, String starNik_File) {
		try {
			if (requests.size() == 0) {
			
				File file = new File(starNik_File);
				file.delete();
				LOGGER.info("STARNIK UPDATE FLAT FILE DELETED SUCCESSFULLY");
				return true;
			} else {
				LOGGER.info("STARNIK UPDATE FLAT FILE NOT  DELETED");
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return false;
	}

	private boolean writeFile(List<String> requests, String starNik_File) {
		try {
			if (requests.size() > 0) {
				try (BufferedWriter bufferWriter = new BufferedWriter(new FileWriter(starNik_File))) {
					for (String line : requests) {
						bufferWriter.write(line);
						bufferWriter.newLine();

					}
					bufferWriter.close();
					return true;
				} catch (Exception e) {
					LOGGER.error(e.getMessage());
				}
			} else if (requests.size() == 0) {

				 deleteFile(requests, starNik_File);

			}

		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return true;
	}

	private boolean apiHit(String requestBody, int readTimeout, String apiurl, int connectionTimeout,
			int fetchTimeout) {
		try {
			JSONObject jsonObject = (JSONObject) new JSONParser().parse(requestBody);
			HttpClient httpClient = new HttpClient();

			org.json.simple.JSONObject receviedpostCallHistoryResponse = httpClient.httpResponse(
					jsonObject.get("ucid").toString(), apiurl + "external/updateNotes", "Put",
					(JSONObject) jsonObject.get("StarnikUpdateRequest"), readTimeout, connectionTimeout, fetchTimeout);
			if (receviedpostCallHistoryResponse == null) {
				LOGGER.info("RESPONSE ----> CONNECTION TIMEOUT"+"  ,"+"ucid"+"="+jsonObject.get("ucid").toString());
			} else {
				if (receviedpostCallHistoryResponse.get("responseCode").toString() != null
						&& receviedpostCallHistoryResponse.get("responseCode").toString().equalsIgnoreCase("000")) {
					LOGGER.info("SUCCESS RESPONSE  AND RESPONSE CODE IS ----> "
							+ receviedpostCallHistoryResponse.get("responseCode").toString()+"  ,"+"ucid"+"="+jsonObject.get("ucid").toString());
					LOGGER.info(receviedpostCallHistoryResponse.get("responseBody").toString());
					return true;

				} else if (receviedpostCallHistoryResponse.get("responseCode").toString() != "000") {
					LOGGER.info("FAILURE RESPONSE  AND RESPONSE CODE IS ----> "
							+ receviedpostCallHistoryResponse.get("responseCode").toString()+"  ,"+"ucid"+"="+jsonObject.get("ucid").toString());

					LOGGER.info(receviedpostCallHistoryResponse.get("message").toString() + "FOR THIS REQUEST"
							+ (JSONObject) jsonObject.get("StarnikUpdateRequest"));
					return false;
				} 
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return false;
	}

	public void setApiNeedValues(String apiurl, int readTimeout, int connectionTimeout, int fetchTimeout,
			String starNik_File) {
		try {

			List<String> Allrequests = readFile(starNik_File);
			sendRequests(Allrequests, starNik_File, readTimeout, apiurl, connectionTimeout, fetchTimeout);

		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}

	}
}
