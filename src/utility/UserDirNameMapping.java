package utility;

import interfaces.FileNameMapping;

public class UserDirNameMapping implements FileNameMapping{

	@Override
	/**
	 * May apply encryption on dir name later for safty reasons
	 */
	public String getMappingName(String name) {
		return name+"_dir";
	}

}
