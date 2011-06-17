package test.objects;

import java.security.Permission;

public class TestResourcePermission extends Permission {

	private static final long serialVersionUID = 0;
	private String actions = null;
	public TestResourcePermission(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	public TestResourcePermission(String name, String actions) {
		super(name);
		this.actions = actions;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getActions() {
		// TODO Auto-generated method stub
		return actions;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean implies(Permission permission) {
		// TODO Auto-generated method stub
		return false;
	}

}
