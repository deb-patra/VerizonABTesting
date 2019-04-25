package com.incedo.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.UUID;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.uuid.Generators;
import com.incedo.commandVOs.EventSubmitRequestVO;
import com.incedo.commandVOs.ExperimentVariantVo;
import com.incedo.exception.ServiceException;

/**
 * Created by Deb.
 */
@Service
public class EventServiceImpl implements EventService {
	
	 @Value("${service.api.url}")
     private String serviceApi;
	 
	 @Value("${postevent.api.url}")
     private String postEventserviceApi;

	@Override
	public ExperimentVariantVo getEventJsonFromServiceAPI(String userId, String emailId, int layerId, int channelId) {
		
		URL url;
		String jsonString = null;
		UriComponentsBuilder componentsBuilder = UriComponentsBuilder.fromUriString(serviceApi)
				.queryParam("channel_id", channelId).queryParam("layer_id", layerId).queryParam("user_id", userId);
		System.out.println("componentsBuilder::"+componentsBuilder.toUriString());
		
		try {
			url = new URL(componentsBuilder.toUriString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			if (conn.getResponseCode() != 200) {
				throw new ServiceException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			String output;
			while ((output = br.readLine()) != null) {
				System.out.println(output);
				jsonString = output;
			}
			conn.disconnect();
		} catch (IOException e) {
			System.out.println("IO Exception : "+e.getMessage());
		}	
		String variantToken = null;
		String exptName = null;
		String bucket = null;
		String layerName = null;
		String channelName = null;
		int expId = 1;
		int variantId = 0;
		JSONObject obj = new JSONObject(jsonString);
		if(obj.has("variant_token")) {
			variantToken = obj.get("variant_token").toString();
		}
		if(obj.has("bucket")) {
			bucket = obj.get("bucket").toString();
		}
		if(obj.has("exp_id")) {
			expId = (Integer) obj.get("exp_id");
		}
		if(obj.has("variant_id")) {
			variantId = (Integer) obj.get("variant_id");
		}
		if(obj.has("expt_name")) {
			exptName = (String) obj.get("expt_name");
		}
		if(obj.has("layer_name")) {
			layerName = (String) obj.get("layer_name");
		}
		if(obj.has("channel_name")) {
			channelName = (String) obj.get("channel_name");
		}
		ExperimentVariantVo experimentVariantVo = new ExperimentVariantVo();
		experimentVariantVo.setBucket(bucket);
		experimentVariantVo.setVariantToken(variantToken);
		experimentVariantVo.setExpId(expId);
		experimentVariantVo.setVariantId(variantId);
		experimentVariantVo.setExptName(exptName);
		experimentVariantVo.setLayerName(layerName);
		experimentVariantVo.setChannelName(channelName);
		experimentVariantVo.setUserId(userId);
		if(!StringUtils.isEmpty(emailId)) {
			experimentVariantVo.setEmailId(emailId);
		}
		experimentVariantVo.setLayerId(layerId);
		experimentVariantVo.setChannelId(channelId);
		return experimentVariantVo;
	}

	@Override
	public void pushNewEvent(EventSubmitRequestVO eventSubmit ) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			String requestJSON = mapper.writeValueAsString(eventSubmit);
			System.out.println("requestJSON ::"+requestJSON);
			System.out.println("postEventserviceApi ::"+postEventserviceApi);
			URL url = new URL(postEventserviceApi);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			OutputStream os = conn.getOutputStream();
			os.write(requestJSON.getBytes());
			os.flush();
			if (conn.getResponseCode() != 200) {
				throw new ServiceException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));
			String output;
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}
			conn.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	@Override
	public EventSubmitRequestVO incedoEvent(ExperimentVariantVo experimentVariantVo, String stage) {
		UUID uuid = Generators.timeBasedGenerator().generate();
		EventSubmitRequestVO eventSubmit = new EventSubmitRequestVO();
		eventSubmit.setUser_id(experimentVariantVo.getUserId());
		eventSubmit.setEvt_id(uuid.toString());
		eventSubmit.setVariant_id(experimentVariantVo.getVariantId());
		int expId = experimentVariantVo.getExpId();
		String variantToken = experimentVariantVo.getVariantToken();
		if(!StringUtils.isEmpty(variantToken) && (variantToken.contains("control") || variantToken.contains("Control"))) {
			expId = -1;
		} 
		eventSubmit.setExp_id(expId);
		eventSubmit.setLayer_id(experimentVariantVo.getLayerId());
		eventSubmit.setChannel_id(experimentVariantVo.getChannelId());
		eventSubmit.setStage(stage);
		eventSubmit.setEmail(experimentVariantVo.getEmailId());
		Instant instant = Instant.now();
		Long timeStampSeconds = instant.getEpochSecond();
		if(null != timeStampSeconds) {
			eventSubmit.setTime(timeStampSeconds.intValue());
		}
		System.out.println("eventSubmit:::"+eventSubmit);
		return eventSubmit;
	}
}