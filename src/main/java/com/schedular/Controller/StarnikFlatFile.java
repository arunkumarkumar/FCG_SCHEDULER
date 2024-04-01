package com.schedular.Controller;

import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.schedular.Service.ApiHit;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class StarnikFlatFile {
	Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger(this.getClass());
	@Value("${externalProperties}")
	private String externalProperties;

	@Schedules({ @Scheduled(cron = "${cronExpression}"), @Scheduled(cron = "${cronExpression1}") })
	public void starNik_File() {
		try {
			JSONParser jsonParser = new JSONParser();
			Object obj = jsonParser.parse(new FileReader(externalProperties));
			JSONObject jsonObject = (JSONObject) obj;
			Object flatfileLocation = jsonObject.get("FlatFileLocation");
			JSONObject json = (JSONObject) flatfileLocation;
			Object fileLocation = json.get("fileLocation");
			Object apiurl = json.get("apiurl");
			Object readTimeout = json.get("readTimeOut");
			int readTime = Integer.parseInt((String) readTimeout);
			Object connectionTimeout = json.get("connectionTimeOut");
			int connectionTime = Integer.parseInt((String) connectionTimeout);
			Object fetchTimeout = json.get("fetchTimeOut");
			int fetchTime = Integer.parseInt((String) fetchTimeout);
			File fileLocations = new File(fileLocation.toString());
			if (fileLocations.canRead()) {
				File[] ListOfFiles = fileLocations.listFiles();
				for (int y = 0; y < ListOfFiles.length; y++) {
					if (ListOfFiles[y].getName().contains("StarnikUpdate.json")) {
						LOGGER.info("SCHEDULAR APPLICATION STARTED\nSTARNIK UPDATE FLATFILE EXISTS");
						LOGGER.info(ListOfFiles[y]);
						fileExists(ListOfFiles[y].toString(), apiurl.toString(), readTime, connectionTime, fetchTime);
					}
				}
			}

		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	public void fileExists(String starNikFile, String apiurl, int readTimeout, int connectionTimeout,
			int fetchTimeout) {
		try {
			ApiHit api = new ApiHit();
			api.setApiNeedValues(apiurl, readTimeout, connectionTimeout, fetchTimeout, starNikFile.toString());
			LOGGER.info("SCHEDULAR APPLICATION ENDED");

		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}

	}
}
