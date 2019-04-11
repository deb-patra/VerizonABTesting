package com.incedo.controler;

import java.util.Base64;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import com.incedo.commandVOs.EventSubmitRequestVO;
import com.incedo.commandVOs.ExperimentVariantVo;
import com.incedo.service.EventService;
import com.incedo.service.EventUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by Deb.
 */
@Slf4j
@Controller
public class EventController {

	
	@Value("${checkout.page}")
    private String checkoutPage;
	
	@Value("${gridwall.page}")
    private String griwallPage;
	
	@Value("${layer.id}")
    private int layerId;
	
	@Value("${channel.id}")
    private int channelId;
	
	@Value("${layer.id.ui}")
    private String layerName;
	
	@Value("${channel.id.ui}")
    private String channelName;
	
	@Value("${layer.id.reco}")
    private String layerIdReco;
	
	@Value("${layer.id.reco.ui}")
    private String layerNameReco;
	
    private final EventService eventService;
    private final EventUtil eventUtilService;

	/**
	 * @param eventService
	 * @param eventUtilService
	 */
	public EventController(EventService eventService, EventUtil eventUtilService) {
		this.eventService = eventService;
		this.eventUtilService = eventUtilService;
	}

	@RequestMapping({"/home","/",""})
	public String getHomePage() {
		return "home";
	}
	
	
	@RequestMapping("/getCartPage")
    public String getGridwallPageWithoutParam(HttpServletRequest request, @RequestHeader(value="User-Agent", defaultValue="mobile") String userAgent, Model model) {
    	System.out.println("With in get gridwall details");
    	String userId = request.getParameter("userId");
    	String emailId = request.getParameter("emailId");
    	if(!StringUtils.isEmpty(userId) && !StringUtils.isEmpty(emailId)) {
    		ExperimentVariantVo experimentVariantVo = eventService.getEventJsonFromServiceAPI(userId, emailId, layerId, channelId);
    		if(eventUtilService.incedoGetVariantToken(experimentVariantVo).equalsIgnoreCase("UI_GREEN_EXP")) {
    			showGreenHeader(model, "gridwall");
    		} else if(eventUtilService.incedoGetVariantToken(experimentVariantVo).equalsIgnoreCase("UI_RED_EXP")) {
    			showRedHeader(model, "gridwall");
    		} else {
    			showNormalHeader(model, "gridwall");
    		}
    		setMessageAndRecos(model, experimentVariantVo);
    		eventUtilService.setModelAttribute(model, experimentVariantVo, checkoutPage, "gridwall", "grid_wall", null);
    		EventSubmitRequestVO eventSubmit = eventService.incedoEvent(experimentVariantVo, "grid_wall");
    		System.out.println("eventSubmit::::Gridwal::::"+eventSubmit.toString());
    		//eventService.pushNewEvent(eventSubmit);
    	}else {
    		model.addAttribute("error", "Missing User Id. Please provide User Id to proceed further.");
    		return "home";
    	}
        return "gridwall";
    }
	
	/**
	 * This is the controller method to get the event id based on user id
	 * 
	 * @param model
	 * @return
	 */
    
    @RequestMapping("/cartPage/{userId}/{emailId}")
    public String getGridwallPage(@PathVariable String userId, @PathVariable String emailId, Model model) {
    	if(!StringUtils.isEmpty(userId) && !StringUtils.isEmpty(emailId)) {
    		//String encodedString = Base64.getEncoder().encodeToString(emailId.getBytes());
    		byte[] decodedBytes = Base64.getDecoder().decode(emailId);
    		String decodedString = new String(decodedBytes);
    		System.out.println("decodedString-->"+decodedString);
    		emailId = decodedString;
    		ExperimentVariantVo experimentVariantVo = eventService.getEventJsonFromServiceAPI(userId, emailId, layerId, channelId);
    		if(eventUtilService.incedoGetVariantToken(experimentVariantVo).equalsIgnoreCase("UI_GREEN_EXP")) {
    			showGreenHeader(model, "gridwall");
    		} else if(eventUtilService.incedoGetVariantToken(experimentVariantVo).equalsIgnoreCase("UI_RED_EXP")) {
    			showRedHeader(model, "gridwall");
    		} else {
    			showNormalHeader(model, "gridwall");
    		}
    		setMessageAndRecos(model, experimentVariantVo);
    		eventUtilService.setModelAttribute(model, experimentVariantVo, checkoutPage, "gridwall", "grid_wall", null);
    		EventSubmitRequestVO eventSubmit = eventService.incedoEvent(experimentVariantVo, "grid_wall");
    		System.out.println("eventSubmit::::Gridwal::::"+eventSubmit.toString());
    		//eventService.pushNewEvent(eventSubmit);
    	}else {
    		model.addAttribute("error", "Missing User Id. Please provide User Id to proceed further.");
    		return "home";
    	}
        return "gridwall";
    }
    
    @RequestMapping("/checkoutPage/{userId}/{emailId}")
    public String getCheckoutPage(@PathVariable String userId, @PathVariable String emailId, Model model) {
    	System.out.println("With in get checkout details");
    	if(!StringUtils.isEmpty(userId)) {
    		//String encodedString = Base64.getEncoder().encodeToString(emailId.getBytes());
    		byte[] decodedBytes = Base64.getDecoder().decode(emailId);
    		String decodedString = new String(decodedBytes);
    		System.out.println("decodedString-->"+decodedString);
    		emailId = decodedString;
    		ExperimentVariantVo experimentVariantVo = eventService.getEventJsonFromServiceAPI(userId, emailId, layerId, channelId);
    		if(eventUtilService.incedoGetVariantToken(experimentVariantVo).equalsIgnoreCase("UI_GREEN_EXP")) {
    			showGreenHeader(model, "checkout");
    		} else if(eventUtilService.incedoGetVariantToken(experimentVariantVo).equalsIgnoreCase("UI_RED_EXP")) {
    			showRedHeader(model, "checkout");
    		} else {
    			showNormalHeader(model, "checkout");
    		}
    		setMessageAndRecos(model, experimentVariantVo);
    		eventUtilService.setModelAttribute(model, experimentVariantVo, null, "checkout", "checkout", "/cartPage/");
    		EventSubmitRequestVO eventSubmit = eventService.incedoEvent(experimentVariantVo, "checkout");
    		System.out.println("eventSubmit::::Checkout::::"+eventSubmit.toString());
    		//eventService.pushNewEvent(eventSubmit);
    	}else {
    		model.addAttribute("error", "Missing User Id. Please provide User Id to proceed further.");
    		return "home";
    	}
        return "gridwall";
    }
    public void showBlueHeader(Model model, String pageHeading) {
		String eventColor = null;
		String color = "blue";
		if(!StringUtils.isEmpty(color) && "default".equals(color)) {
			eventColor = "default";
		} else {
			eventColor = pageHeading + "_" + color;
		}
		model.addAttribute("eventColor", eventColor);
    }
    public void showRedHeader(Model model, String pageHeading) {
		String eventColor = null;
		String color = "red";
		if(!StringUtils.isEmpty(color) && "default".equals(color)) {
			eventColor = "default";
		} else {
			eventColor = pageHeading + "_" + color;
		}
		model.addAttribute("eventColor", eventColor);
    }
    public void showGreenHeader(Model model, String pageHeading) {
		String eventColor = "green";
		String color = "green";
		if(!StringUtils.isEmpty(color) && "default".equals(color)) {
			eventColor = "default";
		} else {
			eventColor = pageHeading + "_" + color;
		}
		model.addAttribute("eventColor", eventColor);
    }
    
    public void showLifeStyleModel(Model model) {
    	model.addAttribute("eventColor", "recos3");
    }
    
    public void showControlModel(Model model) {
    	model.addAttribute("eventColor", "recos2");
    }
    
    public void showNormalHeader(Model model, String pageHeading) {
    	showBlueHeader(model, pageHeading);
    }
    
    public void setMessageAndRecos(Model model, ExperimentVariantVo experimentVariantVo) {
    	String variantToken = experimentVariantVo.getVariantToken();
    	if(!StringUtils.isEmpty(variantToken)) {
    		if("message_var1".equalsIgnoreCase(variantToken)) {
    			model.addAttribute("eventColor", "Message_123");
    		} else if("message_var2".equalsIgnoreCase(variantToken)) {
    			model.addAttribute("eventColor", "Message_456");
    		} else if("message_var3".equalsIgnoreCase(variantToken)) {
    			model.addAttribute("eventColor", "Message_789");
    		} else if("recos_2".equalsIgnoreCase(variantToken)) {
    			model.addAttribute("eventColor", "recos2");
    		} else if("recos_3".equalsIgnoreCase(variantToken)) {
    			model.addAttribute("eventColor", "recos3");
    		}
    	}
    }
    
}
