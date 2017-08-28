import java.io.Serializable;

public class Prices implements Serializable{
	
    private double allDay;
    private double morning;
    private double lunch;
    private double afternoon;
    private double preSchool;
    private String ageGroup;
    
	public Prices(double allDay, double morning, double lunch, double afternoon, double preSchool,
			String ageGroup) {
		super();
		this.allDay = allDay;
		this.morning = morning;
		this.lunch = lunch;
		this.afternoon = afternoon;
		this.preSchool = preSchool;
		this.ageGroup = ageGroup;
	}

	public String getAgeGroup() {
		return ageGroup;
	}

	public void setAgeGroup(String ageGroup) {
		this.ageGroup = ageGroup;
	}

	
	public double getAllDay() {
		return allDay;
	}

	public void setAllDay(double allDay) {
		this.allDay = allDay;
	}

	public double getMorning() {
		return morning;
	}

	public void setMorning(double morning) {
		this.morning = morning;
	}

	public double getLunch() {
		return lunch;
	}

	public void setLunch(double lunch) {
		this.lunch = lunch;
	}

	public double getAfternoon() {
		return afternoon;
	}

	public void setAfternoon(double afternoon) {
		this.afternoon = afternoon;
	}

	public double getPreSchool() {
		return preSchool;
	}

	public void setPreSchool(double preSchool) {
		this.preSchool = preSchool;
	}

    
   
    
   
    
}
