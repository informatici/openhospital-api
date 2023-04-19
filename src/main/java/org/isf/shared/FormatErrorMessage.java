package org.isf.shared;

public class FormatErrorMessage {

	public static String format(String message) {
		message = message.replace("angal.", "");
		message = message.replace(".msg", "");
		return message;
	}
	public static String format(String message, String label, String label2) {
		message = message.replace("angal.", "");
		message = message.replace(".msg", "");
		message = message.replace(label, label2);
		return message;
	}
}
