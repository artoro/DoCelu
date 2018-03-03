/*
 * Klasa opisuj¹ca godzinê.
 */
import java.time.LocalDateTime;
import java.util.Hashtable;
import javax.swing.JLabel;

public class Time
{
	//POLA STATYCZNE
	public static Hashtable<Integer,JLabel> week = new Hashtable<Integer,JLabel>(); //dni tygodnia
	public static Time now = new Time((LocalDateTime.now().getDayOfWeek().getValue()-1)*1440 + 
								LocalDateTime.now().getHour()*60 + LocalDateTime.now().getMinute()); //aktualny czas
	//POLA OBIEKTU
	public short min; //czas wyra¿ony w minutach dnia tygodnia
	
	//KONSTRUKTORY
	public static void initTime() { //inicjalizacja danych statycznych
		week.put(new Integer(0), new JLabel("Pon"));
		week.put(new Integer(1), new JLabel("Wt"));
		week.put(new Integer(2), new JLabel("Œr"));
		week.put(new Integer(3), new JLabel("Czw"));
		week.put(new Integer(4), new JLabel("Pi¹"));
		week.put(new Integer(5), new JLabel("Sob"));
		week.put(new Integer(6), new JLabel("Nie"));
	}
	
	public Time() { min = 0; }
	public Time(int MINUTES) { min = (MINUTES>10080)? (short)(MINUTES-10080) : (short)MINUTES; }
	public Time(int DAY, int HOURS, int MINUTES) { min = (short) (DAY*1440 + HOURS*60 + MINUTES); }
	public Time(int DAY, String HM) {
		min = (short) (DAY*1440 +
				Short.parseShort(HM.substring(HM.indexOf(':')-2, HM.indexOf(':'))) * 60 +
				Short.parseShort(HM.substring(HM.indexOf(':')+1, HM.indexOf(':')+3)));
	}
	
	//METODY
	public Boolean isAfter(Time t0) { //zwraca czy t0 jest wczeœniej ni¿ this
		return t0.min <= min;
	}
	public Time diff(Time t0) { //zwraca ró¿nicê w czasie
		if(this.isAfter(t0)) return new Time(min-t0.min);
		else return new Time(min-t0.min+10080);
	}
	public Time add() { //dodaje 1 minutê
		return new Time(min+1);
	}
	public Time add(Time t0) { //dodaje okreœlony czas
		if (t0.min+min < 100079) return new Time(t0.min+min);
		else return new Time(t0.min+min-10080);
	}
	public short getDay() { //zwraca numer dnia tygodnia, przy czym pon=0, niedz=6
		return (short)(min/1440);
	}
	public String getStringDay() { //zwraca nazwê dnia tygodnia
		return week.get(getDay()).getText();
	}
	public short getTime() { //zwraca godzinê w formacie dla rozk³adu jazdy
		return (min<7200)? (short)(min%1440) : min;
	}
	public short getHour() { //zwraca godzinê
		return (short)((min%1440)/60);
	}
	public short getMin() { //zwraca minuty w godzinie
		return (short)(min%60);
	}
	public int when() { //zwraca rodzaj dnia: roboczy=0, sobota=1, niedziela=2
		return (min<7200)? 0 : min/1440-4;
	}
	public Time nextDay() { //zwraca czas pocz¹tku nastêpnego dnia
		return (getDay()==6)? new Time() : new Time(1440*(getDay()+1));
	}
	@Override
	public String toString() {
		return getHour() + ((min%60<10)? ":0" : ":") + min%60;
	}
}
