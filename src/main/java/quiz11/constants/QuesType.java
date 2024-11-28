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
		
		// �W�����@�q�޿�i�H�ΥH�U�Ӵ��N:
		// QuesType.values() �i�H���o�b QuesType �� enum ���Ҧ��� type
		for(QuesType item : QuesType.values()) {
			if(item.getType().equalsIgnoreCase(type)) {
				return true;
			}
		}
		return false;
	}

}
