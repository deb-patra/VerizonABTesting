package com.incedo.controler;

import java.io.IOException;
import java.util.Base64;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.incedo.commandVOs.EventSubmitRequestVO;
import com.incedo.commandVOs.ExperimentVariantVo;
import com.incedo.service.EventService;

@RestController
public class VzwEventRestController {
	private final EventService eventService;
    @Value("${layer.id}")
    private int layerId;
	
	@Value("${channel.id}")
    private int channelId;
	
    public VzwEventRestController(EventService eventService) {
		super();
		this.eventService = eventService;
	}

	@RequestMapping(value = "/openEmailsEvent/{userId}/{emailId}", method = RequestMethod.GET,
            produces = MediaType.IMAGE_PNG_VALUE)
    public void getImage(@PathVariable String userId, @PathVariable String emailId, HttpServletResponse response) throws IOException {
    	byte[] decodedBytes = Base64.getDecoder().decode(emailId);
		String decodedString = new String(decodedBytes);
		System.out.println("decodedString getImage-->"+decodedString);
		emailId = decodedString;
    	ExperimentVariantVo experimentVariantVo = eventService.getEventJsonFromServiceAPI(userId, emailId, layerId, channelId);
    	EventSubmitRequestVO eventSubmit = eventService.incedoEvent(experimentVariantVo, "openEmail");
		System.out.println("eventSubmit::::email::::getImage:::"+eventSubmit.toString());
		eventService.pushNewEvent(eventSubmit);
    	Resource imgFile = new ClassPathResource("/static/images/Variation1.png");

        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        StreamUtils.copy(imgFile.getInputStream(), response.getOutputStream());
    }
}
