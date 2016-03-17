package sonata.kernel.adaptor.wrapper;

public abstract class AbstractWrapper {

	private String type;
	
	protected void setType(String type){
		this.type=type;
	}

	
	public String getType() {
		return type;
	}
}
