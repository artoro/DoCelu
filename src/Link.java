/*
 * Klasa zawieraj¹ca informacjê na temat po³¹czenia miêdzy dwoma miejscami.
 */
public class Link
{
	//POLA STATYCZNE
	public static final float pedestrianVel = 0.07f; // min/px
	public static Link walk = new Link(); //ustawienia startu i celu podró¿y
	
	//POLA OBIEKTÓW
	private short nr; //numer po³¹czenia
	private Spot start; //punkt startowy
	private Spot stop; //punkt koñcowy
	private Time t0; //godzina odjazdu
	private Time t1; //godzina przyjazdu
	private float cost; //³¹czny czas dotarcia
	
	//KONSTRUKTORY
	public Link() { //domyœlny
		nr = 0; 
		start = null;
		stop = null;
		t0 = Time.now;
		t1 = null;
		cost = Float.MAX_VALUE;
	}
	public Link(Spot START, Spot STOP, Time T0) { //jeœli idziemy pieszo
		nr = 0;
		start = START;
		stop = STOP;
		t0 = T0;
		cost = start.getPosition().distance(walk.stop.getPosition())*pedestrianVel;
		t1 = T0.add(new Time((int)cost));
		this.addCost(t1.diff(walk.getStartTime()).min);
	}
	public Link(short NUM, Spot START, Spot STOP, Time T0, Time T1) { //standardowy
		nr = NUM;
		start = START;
		stop = STOP;
		t0 = T0;
		t1 = T1;
		cost = calcCost();
	}
	public Link(Link L) { //kopiuj¹cy
		nr = L.nr;
		start = new Spot(L.start);
		stop = new Spot(L.stop);
		t0 = new Time(L.t0.min);
		t1 = new Time(L.t1.min);
		cost = L.cost;
	}
	
	//METODY, SET'ery i GET'ery
	public float calcCost() { //oblicza koszt dotychczasowy plus czas dojœcia pieszego do celu podó¿y
		if(walk.stop == null || t1 == null) return Float.MAX_VALUE;
		return t1.diff(walk.getStartTime()).min + stop.getPosition().distance(walk.stop.getPosition())*pedestrianVel;
	}
	public static void setWStart(Spot START) { //ustawia start obiektu walk
		walk.start = START;
		if(isSetUp()) {
			walk.cost = walk.calcCost();
			DoCelu.searchB.setEnabled(true);
		}
	}
	public static void setWStart() { //w³¹cza tryb ustawiania myszk¹ startu obiektu walk
		DoCelu.startSpot.setSelectedItem(0);
		walk.start = Spot.myStart;
		Spot.edit = Spot.myStart;
		walk.cost = Float.MAX_VALUE;
		CityMap.mouseState = 1;
	}
	public Spot getStart() { //zwraca start Spot po³¹czenia
		return start;
	}
	public static void setWStop(Spot STOP) { //ustawia cel podró¿y obiektu walk
		walk.stop = STOP;
		DoCelu.stopSpot.setSelectedItem(0);
		if(walk.start != null) {
			walk.cost = walk.calcCost();
			if(isSetUp()) DoCelu.searchB.setEnabled(true);
		}
	}
	public static void setWStop() { //w³¹cza tryb ustawienia myszk¹ celu obiektu walk
		DoCelu.startSpot.setSelectedItem(0);
		walk.stop = Spot.myStop;
		Spot.edit = Spot.myStop;
		walk.cost = Float.MAX_VALUE;
		CityMap.mouseState = 1;
	}
	public void setStop(Spot STOP) { //ustawia stop Spot po³¹czenia
		stop = STOP;
		if(isSetUp()) cost = calcCost();
	}
	public Spot getStop() { //zwraca stop Spot po³¹czenia
		return stop;
	}
	public static void setWTimeStart(Time tStart) { //ustawia czas startu obiektu walk
		if(isSetUp()) walk = new Link(walk.start, walk.stop, tStart);
		else walk.t0 = tStart;
	}
	public Time getStartTime() { //zwraca czas startu
		return t0;
	}
	public void setStopTime(Time T1) { //ustawia godzinê momentu dotarcia do celu po³¹czenia
		this.t1 = T1;
	}
	public Time getStopTime() { //zwraca godzinê dojœcia do celu
		return t1;
	}
	public void addCost(double d) { //dodaje dodatkowy koszt podó¿y
		cost += d;
	}
	public float getCost() { //zwraca koszt czasowy podró¿y
		return cost;
	}
	public void setNum(short NUM) { //ustawia numer po³¹czenia
		nr = NUM;
	}
	public short getNum() { //zwraca numer po³¹czenia
		return nr;
	}
	public static boolean isSetUp() { //zwraca informacjê czy start i cel podó¿y zosta³y ju¿ okreœlone
		if(walk.start != null && walk.stop != null) return true;
		else return false;
	}
	@Override
	public String toString() {
		try { return t0 + " " + start.getName() + " ["+nr+"]> " + stop.getName() + " " + t1; }
		catch (NullPointerException e) {
			System.out.println("Start: " + start + " Stop: " + stop);
			return "?";
		}
	}
}