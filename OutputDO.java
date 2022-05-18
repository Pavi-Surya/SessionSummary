package com.wipro.bt.beanpackage;

public class OutputDO {
	private String userName;
	private int sessionCount;
	private Long sessionTime;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getSessionCount() {
		return sessionCount;
	}

	public void setSessionCount(int sessionCount) {
		this.sessionCount = sessionCount;
	}

	public Long getSessionTime() {
		return sessionTime;
	}

	public void setSessionTime(Long sessionTime) {
		this.sessionTime = sessionTime;
	}
	
	public OutputDO(String userName, int sessionCount, Long sessionTime) {
		this.userName = userName;
		this.sessionCount = sessionCount;
		this.sessionTime = sessionTime;
	}

}
