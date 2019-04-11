/**
 * 
 */
package com.incedo.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

import com.incedo.commandVOs.ExperimentVariantVo;

import net.sf.uadetector.ReadableDeviceCategory;
import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;

/**
 * @author Deb
 *
 */
@Service
public class EventUtil {
	@Value("${desktop.channel}")
    private String desktopChannel;
	@Value("${mobile.channel}")
    private String mobileChannel;
	@Value("${getexperiment.api.url}")
	private String getExperimentAPI;
	@Value("${layer.id}")
    private int configLayerId;
	@Value("${channel.id}")
    private int configChannelId;
	public static UserAgentStringParser parser = UADetectorServiceFactory.getResourceModuleParser();
	public String getChannelId(String userAgent) {
		ReadableUserAgent agent = parser.parse(userAgent);
		ReadableDeviceCategory device = agent.getDeviceCategory();
		System.out.println("\nDevice: " + device.getName());
		if("Personal computer".equals(device.getName())) {
			//return desktopChannel;
			return mobileChannel;
		} else {
			return mobileChannel;
		}
	}

	public String getExperimentToken(String jsonString) {
		JSONObject obj = new JSONObject(jsonString);
		if(obj.has("variant_token")) {
			String experimentToken = obj.get("variant_token").toString();
			return experimentToken;
		}
		if(obj.has("experimentToken")) {
			String experimentToken = obj.get("experimentToken").toString();
			return experimentToken;
		}
		return null;
	}
	
	public String getEventId(String jsonString) {
		String eventColor = "null";
		JSONObject obj = new JSONObject(jsonString);
		if(obj.has("experimentToken")) {
			String experimentToken = obj.get("experimentToken").toString();
			if(!StringUtils.isEmpty(experimentToken) && experimentToken.contains("_")) {
				eventColor = experimentToken.substring(experimentToken.lastIndexOf("_")+1);
			}
			return eventColor;
		}
		return "default";
	}
	
	public String getEventColor(String jsonString) {
		JSONObject obj = new JSONObject(jsonString);
		if(obj.has("variant_token")) {
			String experimentToken = obj.get("variant_token").toString();
			//experimentToken = "default";
			if(!StringUtils.isEmpty(experimentToken)) {
				if(experimentToken.toLowerCase().contains("red")) {
					return "red";
				} else if(experimentToken.toLowerCase().contains("green")) {
					return "green";
				}else if(experimentToken.toLowerCase().contains("blue")) {
					return "blue";
				}else if(experimentToken.toLowerCase().contains("control")) {
					return "blue";
				}
			}
		}
		return "blue";
	}
	
	public int getExperimentId(String jsonString) {
		try {
			JSONObject obj = new JSONObject(jsonString);
			if(obj.has("exp_id")) {
				int expId = (Integer) obj.get("exp_id");
				return expId;
			}
		} catch(Exception e) {
			return 1;
		}
		return 1;
	}
	
	public String getBucket(String jsonString) {
		JSONObject obj = new JSONObject(jsonString);
		if(obj.has("bucket")) {
			String bucket = obj.get("bucket").toString();
			return bucket;
		}
		return null;
	}
	
	public int getLayerIdFromAPI(String jsonString) {
		try {
			JSONObject obj = new JSONObject(jsonString);
			if(obj.has("layer_id")) {
				int layerId = (Integer) obj.get("layer_id");
				return layerId;
			}
		} catch(Exception e) {
			return configLayerId;
		}
		return configLayerId;
	}
	
	public int getChannelIdFromAPI(String jsonString) {
		try {
			JSONObject obj = new JSONObject(jsonString);
			if(obj.has("channel_id")) {
				int channelId = (Integer) obj.get("channel_id");
				return channelId;
			}
		} catch(Exception e) {
			return configChannelId;
		}
		return configChannelId;
	}
	
	public int getVariantIdFromAPI(String jsonString) {
		try {
			JSONObject obj = new JSONObject(jsonString);
			if(obj.has("variant_id")) {
				int variantId = (Integer) obj.get("variant_id");
				return variantId;
			}
		} catch(Exception e) {
			return 0;
		}
		return 0;
	}
	
	public Map<String, String> getEventJsonFromServiceAPI() {
		URL url;
		Map<String, String> idMap = new HashMap<String, String>();
		String jsonString = null;
		try {
			url = new URL(getExperimentAPI);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			String output;
			while ((output = br.readLine()) != null) {
				System.out.println(output);
				jsonString = output;
			}
			JSONArray jsonarray = new JSONArray(jsonString);
			for (int i = 0; i < jsonarray.length(); i++) {
			    JSONObject jsonobject = jsonarray.getJSONObject(i);
			    String layerName = jsonobject.getString("layer_name");
			    String channelName = jsonobject.getString("channel_name");
			    String layerChannel = layerName+"@"+channelName;
			    JSONArray jsonVarientarray = jsonobject.getJSONArray("variant");
			    for (int j = 0; j < jsonVarientarray.length(); j++) {
			    	JSONObject jsonVariantobject = jsonVarientarray.getJSONObject(j);
			    	String id = jsonVariantobject.get("id").toString();
			    	if(!StringUtils.isEmpty(id)) {
			    		idMap.put(id, layerChannel);
			    	}
			    }
			}
			System.out.println("idMap from service ::"+idMap);
			conn.disconnect();
		} catch (IOException e) {
			System.out.println("IOException ::"+e.getMessage());
		}	
		return idMap;
	}
	
	public String incedoGetVariantToken(ExperimentVariantVo experimentVariantVo) {
		String variantToken = experimentVariantVo.getVariantToken();
		if(!StringUtils.isEmpty(variantToken)) {
			if(variantToken.toLowerCase().contains("red")) {
				return "UI_RED_EXP";
			} else if(variantToken.toLowerCase().contains("green")) {
				return "UI_GREEN_EXP";
			}else if(variantToken.toLowerCase().contains("blue")) {
				return "UI_BLUE_EXP";
			}else if(variantToken.toLowerCase().contains("control")) {
				return "UI_BLUE_EXP";
			}else if(variantToken.toLowerCase().contains("life_style")) {
				return "life_style_model1";
			}
		}
		return "UI_BLUE_EXP";
	}
	
	public void setModelAttribute(Model model, ExperimentVariantVo experimentVariantVo, String nextPage, String pageHeading, String stage, String previousPage) {
    	if(!StringUtils.isEmpty(experimentVariantVo.getVariantToken())) {
        	model.addAttribute("userId", experimentVariantVo.getUserId());
        	model.addAttribute("emailId", experimentVariantVo.getEmailId());
        	model.addAttribute("expToken", experimentVariantVo.getVariantToken());
        	model.addAttribute("expId", experimentVariantVo.getExpId());
        	model.addAttribute("expName", experimentVariantVo.getExptName());
        	model.addAttribute("channelName", experimentVariantVo.getChannelName());
        	model.addAttribute("layerName", experimentVariantVo.getLayerName());
        	model.addAttribute("pageHeading", pageHeading);
        	String encodedString = Base64.getEncoder().encodeToString(experimentVariantVo.getEmailId().getBytes());
        	if(!StringUtils.isEmpty(nextPage)) {
        		model.addAttribute("nextPage", nextPage+"/"+experimentVariantVo.getUserId()+"/"+encodedString);
        	}
        	if(!StringUtils.isEmpty(previousPage)) {
        		model.addAttribute("previousPage", previousPage+experimentVariantVo.getUserId()+"/"+encodedString);
        	}
        }
    }
	
}
