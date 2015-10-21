package org.springfield.euscreen.domain;

public class Video {
	private String width;
	private String height;
	private boolean controls = false;
	private String poster;
	private boolean autoplay = false;
	private boolean loop = false;
	private boolean muted = false;
	private String src;
	private boolean preload = false;
	private String ticket;
	
	public Video(){
		
	}
	
	public String getWidth() {
		return width;
	}
	public void setWidth(String width) {
		this.width = width;
	}
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	public boolean isControls() {
		return controls;
	}
	public void setControls(boolean controls) {
		this.controls = controls;
	}
	public String getPoster() {
		return poster;
	}
	public void setPoster(String poster) {
		this.poster = poster;
	}
	public boolean isAutoplay() {
		return autoplay;
	}
	public void setAutoplay(boolean autoplay) {
		this.autoplay = autoplay;
	}
	public boolean isLoop() {
		return loop;
	}
	public void setLoop(boolean loop) {
		this.loop = loop;
	}
	public boolean isMuted() {
		return muted;
	}
	public void setMuted(boolean muted) {
		this.muted = muted;
	}
	public String getSrc() {
		return src;
	}
	public void setSrc(String src) {	
		this.src = src;
	}
	public boolean isPreload() {
		return preload;
	}
	public void setPreload(boolean preload) {
		this.preload = preload;
	}

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}
}
