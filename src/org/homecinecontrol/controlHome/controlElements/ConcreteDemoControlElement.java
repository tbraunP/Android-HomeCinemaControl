package org.homecinecontrol.controlHome.controlElements;

/**
 * 
 * @author pyro
 *
 */
public class ConcreteDemoControlElement implements ControlElement{

	private String title;
	
	public ConcreteDemoControlElement(String title){
		this.title = title;
	}
	
	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getStateString() {
		return "On";
	}

	@Override
	public void onClick() {
		// TODO Auto-generated method stub
		
	}

}
