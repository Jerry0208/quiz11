package quiz11.constants;

public enum QuesType {

	SINGLE("S"), //
	MULTI("M"), //
	TEXT("T");

	private String type;

	private QuesType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public static boolean checkType(String type) {
//		if (type.equalsIgnoreCase(QuesType.SINGLE.toString())//
//		 || type.equalsIgnoreCase(QuesType.MULTI.toString())//
//		 || type.equalsIgnoreCase(QuesType.TEXT.toString())) {
//			return true;
//
//		}
		
		// 上面那一段邏輯可以用以下來替代:
		// QuesType.values() 可以取得在 QuesType 此 enum 中所有的 type
		for(QuesType item : QuesType.values()) {
			if(item.getType().equalsIgnoreCase(type)) {
				return true;
			}
		}
		return false;
	}

}
