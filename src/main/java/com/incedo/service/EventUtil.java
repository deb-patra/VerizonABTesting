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
