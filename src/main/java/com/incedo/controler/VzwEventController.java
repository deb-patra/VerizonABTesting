package com.incedo.controler;

import java.util.Base64;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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

/**
 * Created by Deb.
 */
@Controller
public class VzwEventController {
	
	@Value("${checkout.page}")
    private String checkoutPage;
	
	@Value("${layer.id}")
    private int layerId;
	
	@Value("${channel.id}")
    private int channelId;
	
	@Value("${layer.id.reco}")
    private String layerIdReco;
	
	@Value("${trigger.email.list}")
	private String emailList;
	
	@Value("${domain.name}")
    private String domainName;
	
	
    private final EventService eventService;
    private final EventUtil eventUtilService;
    private JavaMailSender sender;

	/**
	 * @param eventService
	 * @param eventUtilService
	 * @param sender
	 */
	public VzwEventController(EventService eventService, EventUtil eventUtilService, JavaMailSender sender) {
		super();
		this.eventService = eventService;
		this.eventUtilService = eventUtilService;
		this.sender = sender;
	}


	@RequestMapping({"/home","/",""})
	public String getHomePage() {
		return "home";
	}
	
	
	@RequestMapping("/getPromoPage")
    public String getGridwallPageWithoutParam(HttpServletRequest request, @RequestHeader(value="User-Agent", defaultValue="mobile") String userAgent, Model model) {
    	System.out.println("With in get gridwall details");
    	String userId = request.getParameter("userId");
    	String emailId = request.getParameter("emailId");
    	if(!StringUtils.isEmpty(userId) && !StringUtils.isEmpty(emailId)) {
    		ExperimentVariantVo experimentVariantVo = eventService.getEventJsonFromServiceAPI(userId, emailId, layerId, channelId);
    		if(eventUtilService.incedoGetVariantToken(experimentVariantVo).equalsIgnoreCase("EMAIL_PROMO_EXP")) {
    			showEmailPromo(model, "gridwall");
    		} else {
    			showNormalHeader(model, "gridwall");
    		}
    		eventUtilService.setModelAttribute(model, experimentVariantVo, checkoutPage, "gridwall", "grid_wall", null);
    		EventSubmitRequestVO eventSubmit = eventService.incedoEvent(experimentVariantVo, "promo");
    		System.out.println("eventSubmit::::Gridwal::::"+eventSubmit.toString());
    		eventService.pushNewEvent(eventSubmit);
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
    
    @RequestMapping("/promoPage/{userId}/{emailId}")
    public String getPromoPage(@PathVariable String userId, @PathVariable String emailId, Model model) {
    	if(!StringUtils.isEmpty(userId) && !StringUtils.isEmpty(emailId)) {
    		
    		// Decoding Email id
    		byte[] decodedBytes = Base64.getDecoder().decode(emailId);
    		String decodedString = new String(decodedBytes);
    		System.out.println("decodedString-->"+decodedString);
    		emailId = decodedString;
    		
    		// Getting event details for the passed in user id and email id
    		ExperimentVariantVo experimentVariantVo = eventService.getEventJsonFromServiceAPI(userId, emailId, layerId, channelId);
    		
    		// Showing different Header info based on Experiment or Control
    		if(eventUtilService.incedoGetVariantToken(experimentVariantVo).equalsIgnoreCase("EMAIL_PROMO_EXP")) {
    			showEmailPromo(model, "gridwall");
    		} else {
    			showNormalHeader(model, "gridwall");
    		}
    		
    		// Setting Attributes for UI
    		eventUtilService.setModelAttribute(model, experimentVariantVo, checkoutPage, "gridwall", "grid_wall", null);
    		
    		// Generating new event
    		EventSubmitRequestVO eventSubmit = eventService.incedoEvent(experimentVariantVo, "promo");
    		System.out.println("eventSubmit::::Gridwal::::"+eventSubmit.toString());
    		
    		// Pushing new Event
    		eventService.pushNewEvent(eventSubmit);
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
    		if(eventUtilService.incedoGetVariantToken(experimentVariantVo).equalsIgnoreCase("EMAIL_PROMO_EXP")) {
    			showEmailPromo(model, "checkout");
    		} else {
    			showNormalHeader(model, "checkout");
    		}
    		eventUtilService.setModelAttribute(model, experimentVariantVo, null, "checkout", "checkout", "/promoPage/");
    		EventSubmitRequestVO eventSubmit = eventService.incedoEvent(experimentVariantVo, "checkout");
    		System.out.println("eventSubmit::::Checkout::::"+eventSubmit.toString());
    		eventService.pushNewEvent(eventSubmit);
    	}else {
    		model.addAttribute("error", "Missing User Id. Please provide User Id to proceed further.");
    		return "home";
    	}
        return "gridwall";
    }
    public void showEmailPromo(Model model, String pageHeading) {
		String eventColor = "green";
		String color = "green";
		if(!StringUtils.isEmpty(color) && "default".equals(color)) {
			eventColor = "default";
		} else {
			eventColor = pageHeading + "_" + color;
		}
		model.addAttribute("eventColor", eventColor);
    }
    
    public void showNormalHeader(Model model, String pageHeading) {
    	String eventColor = null;
		String color = "blue";
		if(!StringUtils.isEmpty(color) && "default".equals(color)) {
			eventColor = "default";
		} else {
			eventColor = pageHeading + "_" + color;
		}
		model.addAttribute("eventColor", eventColor);
    }
    
    @RequestMapping("/sendEmails/{userId}")
    public String triggerEmails(@PathVariable String userId, Model model) {
    	String[] emails = emailList.split(",");
    	try {
    		for(int i=0; i < emails.length; i++) {
            	System.out.println("emails[i]----------->"+emails[i]);
				sendEmail(emails[i], userId);
			} 
    	}catch (Exception e) {
			e.printStackTrace();
		}
        model.addAttribute("emailList",emailList);
        return "emailSuccess";
    }
    
    public void sendEmail(String emailId, String userId) throws Exception {
    	System.out.println("--------Triggering email-------");
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        String encodedString = Base64.getEncoder().encodeToString(emailId.getBytes());
        String url = domainName+userId+"/"+encodedString;
        System.out.println("--------encodedString-------"+encodedString+", -----------url---------"+url);
        helper.setTo(emailId);
        helper.setText("<html><body>Hi There! <a href=\""+url+"\">click here</a><body></html>", true);
        helper.setSubject("VZW AB Testing - Email event Testing");
        sender.send(message);
    }
}
