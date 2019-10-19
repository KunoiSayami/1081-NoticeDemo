package org.example.u.noticedemo.listSupport;

public class NotificationType {
	private String title;
	private String body;
	public NotificationType(String _title, String _body){
		this.title = _title;
		this.body = _body;
	}

	String getTitle() {
		return this.title;
	}

	String getBodyShort() {
		if (getBody().length() > 50) {
			return getBody().substring(0, 50);
		}
		return getBody();
	}

	String getBody() {
		return this.body;
	}

	@Override
	public String toString() {
		return this.title;
	}
}
