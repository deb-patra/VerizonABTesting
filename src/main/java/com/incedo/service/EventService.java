package com.incedo.service;

import com.incedo.commandVOs.EventSubmitRequestVO;
import com.incedo.commandVOs.ExperimentVariantVo;

/**
 * Created by Deb
 */
public interface EventService {
	ExperimentVariantVo getEventJsonFromServiceAPI(String userId, String emailId, int layerId, int channelId);
	
	public EventSubmitRequestVO incedoEvent(ExperimentVariantVo experimentVariantVo, String stage);

	void pushNewEvent(EventSubmitRequestVO eventSubmit);
}
