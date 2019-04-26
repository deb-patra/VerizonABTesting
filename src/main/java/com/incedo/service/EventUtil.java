/**
 * 
 */
package com.incedo.service;

import java.util.Base64;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

import com.incedo.commandVOs.ExperimentVariantVo;

/**
 * @author Deb
 *
 */
@Service
public class EventUtil {
	
	public String incedoGetVariantToken(ExperimentVariantVo experimentVariantVo) {
		return experimentVariantVo.getVariantToken();
	}
	
	public String incedoGetVariantTokenMLExp(ExperimentVariantVo experimentVariantVo) {
		if(experimentVariantVo.getVariantToken().toLowerCase().contains("ml_music")) {
			return "ML_Model_Experiments";
		}
		return "ML_Model_Control";
	}
	
	public void setModelAttribute(Model model, ExperimentVariantVo experimentVariantVo, String nextPage, String pageHeading, String stage, String previousPage) {
    	if(!StringUtils.isEmpty(experimentVariantVo.getVariantToken())) {
        	model.addAttribute("userId", experimentVariantVo.getUserId());
        	String encodedString = null;
        	if(!StringUtils.isEmpty(experimentVariantVo.getEmailId())) {
        		model.addAttribute("emailId", experimentVariantVo.getEmailId());
        		encodedString = Base64.getEncoder().encodeToString(experimentVariantVo.getEmailId().getBytes());
        	}
        	model.addAttribute("expToken", experimentVariantVo.getVariantToken());
        	model.addAttribute("expId", experimentVariantVo.getExpId());
        	model.addAttribute("expName", experimentVariantVo.getExptName());
        	model.addAttribute("channelName", experimentVariantVo.getChannelName());
        	model.addAttribute("layerName", experimentVariantVo.getLayerName());
        	model.addAttribute("pageHeading", pageHeading);
        	
        	if(!StringUtils.isEmpty(nextPage)) {
        		if(!StringUtils.isEmpty(experimentVariantVo.getEmailId())) {
        			model.addAttribute("nextPage", nextPage+"/"+experimentVariantVo.getUserId()+"/"+encodedString);
        		} else {
        			model.addAttribute("nextPage", nextPage+"/"+experimentVariantVo.getUserId());
        		}
        	}
        	if(!StringUtils.isEmpty(previousPage)) {
        		if(!StringUtils.isEmpty(experimentVariantVo.getEmailId())) {
        			model.addAttribute("previousPage", previousPage+experimentVariantVo.getUserId()+"/"+encodedString);
        		} else {
        			model.addAttribute("previousPage", previousPage+experimentVariantVo.getUserId());
        		}
        		
        	}
        }
    }
	
}
