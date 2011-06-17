package test.objects;
import java.security.Principal;

public class TestRolePrincipal 
	implements Principal {
	String roleName = null;
	public TestRolePrincipal(String roleName){
		this.roleName = roleName;
	}
	public String getName(){
		return roleName;
	}
	public String toString() {
		return roleName;
	}

}
