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
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.incedo.commandVOs.EventSubmitRequestVO;
import com.incedo.commandVOs.ExperimentVariantVo;
import com.incedo.exception.ServiceException;
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
	
	@Value("${domain.name}")
    private String domainName;
	
	@Value("${layer.UI.id}")
    private int layerIdUIExp;
	
	@Value("${channel.UI.id}")
    private int channelIdUIExp;
	
	@Value("${layer.ML.id}")
    private int layerIdMLExp;
	
	@Value("${channel.ML.id}")
    private int channelIdMLExp;
	
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
    		if(eventUtilService.incedoGetVariantToken(experimentVariantVo).equalsIgnoreCase("VZ_EMAIL_MUSIC")) {
    			showEmailMusicPromo(model, "promo");
    		} else if(eventUtilService.incedoGetVariantToken(experimentVariantVo).equalsIgnoreCase("VZ_EMAIL_MEDIA")) {
    			showEmailMediaPromo(model, "promo");
    		} else {
    			showNormalHeader(model, "gridwall");
    		}
    		eventUtilService.setModelAttribute(model, experimentVariantVo, "/vz"+checkoutPage, "gridwall", "grid_wall", null);
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
    		if(eventUtilService.incedoGetVariantToken(experimentVariantVo).equalsIgnoreCase("VZ_EMAIL_MUSIC")) {
    			showEmailMusicPromo(model, "promo");
    		} else if(eventUtilService.incedoGetVariantToken(experimentVariantVo).equalsIgnoreCase("VZ_EMAIL_MEDIA")) {
    			showEmailMediaPromo(model, "promo");
    		} else {
    			showNormalHeader(model, "promo");
    		}
    		
    		// Setting Attributes for UI
    		eventUtilService.setModelAttribute(model, experimentVariantVo, "/vz"+checkoutPage, "gridwall", "grid_wall", null);
    		
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
    		if(eventUtilService.incedoGetVariantToken(experimentVariantVo).equalsIgnoreCase("VZ_EMAIL_MUSIC")) {
    			showEmailMusicPromo(model, "checkout");
    		} else if(eventUtilService.incedoGetVariantToken(experimentVariantVo).equalsIgnoreCase("VZ_EMAIL_MEDIA")) {
    			showEmailMediaPromo(model, "checkout");
    		} else {
    			showNormalHeader(model, "checkout");
    		}
    		eventUtilService.setModelAttribute(model, experimentVariantVo, null, "checkout", "checkout", "/vz/promoPage/");
    		EventSubmitRequestVO eventSubmit = eventService.incedoEvent(experimentVariantVo, "checkout");
    		System.out.println("eventSubmit::::Checkout::::"+eventSubmit.toString());
    		eventService.pushNewEvent(eventSubmit);
    	}else {
    		model.addAttribute("error", "Missing User Id. Please provide User Id to proceed further.");
    		return "home";
    	}
        return "gridwall";
    }
    
    @RequestMapping("/gridwallUIExpPage/{userId}")
    public String getGridwallPage(@PathVariable String userId, Model model) {
    	if(!StringUtils.isEmpty(userId)) {
    	
    		// Getting event details for the passed in user id and email id
    		ExperimentVariantVo experimentVariantVo = eventService.getEventJsonFromServiceAPI(userId, null, layerIdUIExp, channelIdUIExp);
    		
    		// Showing different Header info based on Experiment or Control
    		if(eventUtilService.incedoGetVariantToken(experimentVariantVo).equalsIgnoreCase("BLUE_BUTTON")) {
    			showBlueHeader(model, "gridwall");
    		} else if(eventUtilService.incedoGetVariantToken(experimentVariantVo).equalsIgnoreCase("GREEN_BUTTON")) {
    			showBlueHeader(model, "gridwall");
    		} else if(eventUtilService.incedoGetVariantToken(experimentVariantVo).equalsIgnoreCase("RED_BUTTON")) {
    			showRedHeader(model, "gridwall");
    		} else {
    			showNormal(model, "gridwall");
    		}
    		
    		// Setting Attributes for UI
    		eventUtilService.setModelAttribute(model, experimentVariantVo, "/vz/checkoutUIExpPage", "gridwall", "grid_wall", null);
    		
    		// Generating new event
    		EventSubmitRequestVO eventSubmit = eventService.incedoEvent(experimentVariantVo, "promoEmail");
    		System.out.println("eventSubmit::::Gridwal::::"+eventSubmit.toString());
    		
    		// Pushing new Event
    		eventService.pushNewEvent(eventSubmit);
    	}else {
    		model.addAttribute("error", "Missing User Id. Please provide User Id to proceed further.");
    		return "home";
    	}
        return "gridwall";
    }
    
    @RequestMapping("/checkoutUIExpPage/{userId}")
    public String getCheckoutNewExpPage(@PathVariable String userId, Model model) {
    	if(!StringUtils.isEmpty(userId)) {
    		
    		// Getting event details for the passed in user id and email id
    		ExperimentVariantVo experimentVariantVo = eventService.getEventJsonFromServiceAPI(userId, null, layerIdUIExp, channelIdUIExp);
    		
    		// Showing different Header info based on Experiment or Control
    		if(eventUtilService.incedoGetVariantToken(experimentVariantVo).equalsIgnoreCase("BLUE_BUTTON")) {
    			showBlueHeader(model, "checkout");
    		} else if(eventUtilService.incedoGetVariantToken(experimentVariantVo).equalsIgnoreCase("RED_BUTTON")) {
    			showRedHeader(model, "checkout");
    		} else {
    			showNormal(model, "checkout");
    		}
    		
    		// Setting Attributes for UI
    		eventUtilService.setModelAttribute(model, experimentVariantVo, null, "checkout", "checkout", "/vz/gridwallUIExpPage/");
    		EventSubmitRequestVO eventSubmit = eventService.incedoEvent(experimentVariantVo, "checkout");
    		System.out.println("eventSubmit::::Gridwal::::"+eventSubmit.toString());
    		
    		// Pushing new Event
    		eventService.pushNewEvent(eventSubmit);
    	}else {
    		model.addAttribute("error", "Missing User Id. Please provide User Id to proceed further.");
    		return "home";
    	}
        return "gridwall";
    }
    
    public void showEmailMusicPromo(Model model, String pageHeading) {
		if("promo".equalsIgnoreCase(pageHeading)) {
			model.addAttribute("eventColor", "Variation1");
		} else {
			model.addAttribute("eventColor", "PlanUpgradeSuccess");
		}
		
    }
    
    public void showEmailMediaPromo(Model model, String pageHeading) {
		if("promo".equalsIgnoreCase(pageHeading)) {
			model.addAttribute("eventColor", "Variation2");
		} else {
			model.addAttribute("eventColor", "PlanUpgradeSuccess");
		}
		
    }
    
    public void showNormalHeader(Model model, String pageHeading) {
    	if("promo".equalsIgnoreCase(pageHeading)) {
    		model.addAttribute("eventColor", "Variation2");
		} else {
			model.addAttribute("eventColor", "PlanUpgradeSuccess");
		}
		
    }
    
    public void showBlueHeader(Model model, String pageHeading) {
		if("gridwall".equalsIgnoreCase(pageHeading)) {
			model.addAttribute("eventColor", "green_button");
		} else {
			model.addAttribute("eventColor", "checkout");
		}
		
    }
    
    public void showRedHeader(Model model, String pageHeading) {
		if("gridwall".equalsIgnoreCase(pageHeading)) {
			model.addAttribute("eventColor", "red_button");
		} else {
			model.addAttribute("eventColor", "checkout");
		}
		
    }
    
    public void showNormal(Model model, String pageHeading) {
		if("gridwall".equalsIgnoreCase(pageHeading)) {
			model.addAttribute("eventColor", "control_button");
		} else {
			model.addAttribute("eventColor", "checkout");
		}
		
    }
    
    @RequestMapping("/sendEmails/{userId}/{emailId}")
    public String triggerEmails(@PathVariable String userId, @PathVariable String emailId, Model model) {
    	try {
    		sendEmail(emailId, userId);    		
    	}catch (Exception e) {
			e.printStackTrace();
		}
        model.addAttribute("emailList",emailId);
        return "emailSuccess";
    }
    
    public void sendEmail(String emailId, String userId) throws Exception {
    	System.out.println("--------Triggering email-------");
    	ExperimentVariantVo experimentVariantVo = eventService.getEventJsonFromServiceAPI(userId, emailId, layerId, channelId);
    	String imageName = null;
    	String subject = null;
    	boolean isExperiment = false;
    	if(eventUtilService.incedoGetVariantToken(experimentVariantVo).equalsIgnoreCase("VZ_EMAIL_MUSIC")) {
			imageName = "email-music.png";
    		subject = "VZW AB Email Testing - Music Variant";
    		isExperiment = true;
		} else if(eventUtilService.incedoGetVariantToken(experimentVariantVo).equalsIgnoreCase("VZ_EMAIL_MEDIA")) {
			imageName = "email-video.png";
    		subject = "VZW AB Email Testing - Media Variant";
    		isExperiment = true;
		} 
    	if(isExperiment) {
    		EventSubmitRequestVO eventSubmit = eventService.incedoEvent(experimentVariantVo, "promoEmail");
    		System.out.println("eventSubmit::::email::::"+eventSubmit.toString());
    		eventService.pushNewEvent(eventSubmit);
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            String encodedString = Base64.getEncoder().encodeToString(emailId.getBytes());
            String url = domainName+"/promoPage/"+userId+"/"+encodedString;
            String openMailUrl = domainName+"/openEmailsEvent/"+userId+"/"+encodedString;
            System.out.println("--------encodedString-------"+encodedString+", -----------url---------"+url+"-------openMailUrl--------"+openMailUrl);
            helper.setTo(emailId);
            helper.setText(
                    "<html>"
                    + "<body>"
                     + "<div>"
                        + "<div></div>"
                        + "<div>"
                        + "<a href=\""+url+"\"><img src='http://ec2-18-211-84-216.compute-1.amazonaws.com/images/"+imageName+"' style='float:left;width:450px;height:750px;'/></a>"
                        + "<div></div>"  + "<div>"
                        + "<img src=\""+openMailUrl+"\" style='float:left;width:1px;height:1px;'/>"
                        + "</div>"
                        + "</br>"
                      + "</div>"+""
                      	+ "</br></br></br></br></br> </br></br></br></br></br> </br></br></br></br></br> </br></br></br></br></br> </br></br></br></br></br></br></br></br></br> </br></br></br></br></br></br></br></br>"
                        +"<a href=\""+url+"\">Click Promo</a></body>"
                    + "</html>", true);
            helper.setSubject(subject);
            sender.send(message);
    	}
    }
    
    @ExceptionHandler(ServiceException.class)
    public ModelAndView handleServiceException() {
    	ModelAndView model = new ModelAndView();
    	model.setViewName("serviceError");
    	return model;
    }
    
    @RequestMapping("/promoMLExpPage/{userId}")
    public String getPromoMDPage(@PathVariable String userId, Model model) {
    	if(!StringUtils.isEmpty(userId)) {
    		
    		// Getting event details for the passed in user id and email id
    		ExperimentVariantVo experimentVariantVo = eventService.getEventJsonFromServiceAPI(userId, null, layerIdMLExp, channelIdMLExp);
    		
    		// Showing different Header info based on Experiment or Control
    		if(eventUtilService.incedoGetVariantTokenMLExp(experimentVariantVo).equalsIgnoreCase("ML_Model_Experiments")) {
    			showEmailMusicPromo(model, "promo");
    		} else {
    			showNormalHeader(model, "promo");
    		}
    		
    		// Setting Attributes for UI
    		eventUtilService.setModelAttribute(model, experimentVariantVo, "/vz/checkoutMLExpPage", "gridwall", "grid_wall", null);
    		
    		// Generating new event
    		EventSubmitRequestVO eventSubmit = eventService.incedoEvent(experimentVariantVo, "promoEmail");
    		System.out.println("eventSubmit::::Gridwal::::"+eventSubmit.toString());
    		
    		// Pushing new Event
    		eventService.pushNewEvent(eventSubmit);
    	}else {
    		model.addAttribute("error", "Missing User Id. Please provide User Id to proceed further.");
    		return "home";
    	}
        return "gridwall";
    }
    
    @RequestMapping("/checkoutMLExpPage/{userId}")
    public String getCheckoutMDPage(@PathVariable String userId, Model model) {
    	System.out.println("With in get checkout details");
    	if(!StringUtils.isEmpty(userId)) {
    		ExperimentVariantVo experimentVariantVo = eventService.getEventJsonFromServiceAPI(userId, null, layerIdMLExp, channelIdMLExp);
    		if(eventUtilService.incedoGetVariantTokenMLExp(experimentVariantVo).equalsIgnoreCase("ML_Model_Experiments")) {
    			showEmailMusicPromo(model, "checkout");
    		} else {
    			showNormalHeader(model, "checkout");
    		}
    		eventUtilService.setModelAttribute(model, experimentVariantVo, null, "checkout", "checkout", "/vz/promoMLExpPage/");
    		EventSubmitRequestVO eventSubmit = eventService.incedoEvent(experimentVariantVo, "checkout");
    		System.out.println("eventSubmit::::Checkout::::"+eventSubmit.toString());
    		eventService.pushNewEvent(eventSubmit);
    	}else {
    		model.addAttribute("error", "Missing User Id. Please provide User Id to proceed further.");
    		return "home";
    	}
        return "gridwall";
    }
}
