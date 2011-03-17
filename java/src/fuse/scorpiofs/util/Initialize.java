package fuse.scorpiofs.util;
import java.io.File;

public class Initialize {
	public Initialize(){
		String userHome=System.getProperty("user.home");
		System.out.println("User Home:"+userHome);
		new File(userHome+"/.scorpiofs").mkdir();
	}
}
