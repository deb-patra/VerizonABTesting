/**
 * 
 */
package com.incedo.commandVOs;

/**
 * @author Deb
 *
 */
public class EventSubmitRequestVO {
	private String user_id;
	private String email;
	private String evt_id;
	private int variant_id;
	private int exp_id;
	private int layer_id;
	private int channel_id;
	private int time;
	private String stage;
	
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return the user_id
	 */
	public String getUser_id() {
		return user_id;
	}
	/**
	 * @param user_id the user_id to set
	 */
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	/**
	 * @return the evt_id
	 */
	public String getEvt_id() {
		return evt_id;
	}
	/**
	 * @param evt_id the evt_id to set
	 */
	public void setEvt_id(String evt_id) {
		this.evt_id = evt_id;
	}
	/**
	 * @return the variant_id
	 */
	public int getVariant_id() {
		return variant_id;
	}
	/**
	 * @param variant_id the variant_id to set
	 */
	public void setVariant_id(int variant_id) {
		this.variant_id = variant_id;
	}
	/**
	 * @return the exp_id
	 */
	public int getExp_id() {
		return exp_id;
	}
	/**
	 * @param exp_id the exp_id to set
	 */
	public void setExp_id(int exp_id) {
		this.exp_id = exp_id;
	}
	/**
	 * @return the layer_id
	 */
	public int getLayer_id() {
		return layer_id;
	}
	/**
	 * @param layer_id the layer_id to set
	 */
	public void setLayer_id(int layer_id) {
		this.layer_id = layer_id;
	}
	/**
	 * @return the channel_id
	 */
	public int getChannel_id() {
		return channel_id;
	}
	/**
	 * @param channel_id the channel_id to set
	 */
	public void setChannel_id(int channel_id) {
		this.channel_id = channel_id;
	}
	/**
	 * @return the time
	 */
	public int getTime() {
		return time;
	}
	/**
	 * @param time the time to set
	 */
	public void setTime(int time) {
		this.time = time;
	}
	/**
	 * @return the stage
	 */
	public String getStage() {
		return stage;
	}
	/**
	 * @param stage the stage to set
	 */
	public void setStage(String stage) {
		this.stage = stage;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EventSubmitRequestVO [user_id=" + user_id + ", email=" + email + ", evt_id=" + evt_id + ", variant_id="
				+ variant_id + ", exp_id=" + exp_id + ", layer_id=" + layer_id + ", channel_id=" + channel_id
				+ ", time=" + time + ", stage=" + stage + "]";
	}
	
}
