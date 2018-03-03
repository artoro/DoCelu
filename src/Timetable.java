/*
 * Klasa opisuj¹ca rozk³ad jazdy.
 */
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Scanner;

public class Timetable
{
	//POLA
	private Map<String,Short[][]> table; //tablica rozk³adu jazdy[nazwa przystanku][dzieñ 0-roboczy, 1-sobota, 2-niedziela][nr kursu]
	
	//KONSTRUKTORY
	public Timetable() {
		table = new LinkedHashMap<String,Short[][]>();
	}
	
	//METODY
	public void add(String name, int day, Scanner in) { //dodaje do danego przystanku kursy danego dnia z pliku
		if(!table.containsKey(name)) table.put(name, new Short[3][]);
		HashSet<Short> time = new LinkedHashSet<Short>();
		while(in.hasNextShort()) time.add(in.nextShort());
		table.get(name)[day] = time.toArray(new Short[time.size()]);
	}
	public int isLinked(Spot start, Spot stop) { //zwraca odleg³oœæ miêdzy podanymi przystankami (-1 jeœli nie s¹ po³¹czone)
		int i = -1;
		for(String key : table.keySet()) {
			if(i > -1) {
				if(key.equals(stop.getName())) return i;
				else i++;
			}
			else if(key.equals(start.getName())) i = 0;
		}
		return -1;
	}
	public String getNextSpot(Spot start) { //zwraca nastêpny przystanek
		Boolean b = false;
		for(String key : table.keySet()) {
			if(b == true) return key;
			else if(key.equals(start.getName())) b = true;
		}
		return "";
	}
	public Link getNextLink(short nr, Spot start, Time tStart) { //zwraca nastêpne po³¹czenie
		Spot stop = Spot.listOfSpots.get(getNextSpot(start));
		if(stop == null) return null;
		Time tStop;
		Short[] tab;
		try { tab = table.get(start.getName())[tStart.when()]; if(tab==null) throw new ArrayIndexOutOfBoundsException(); }
		catch (ArrayIndexOutOfBoundsException e) {
			Travel.load(nr, tStart.when());
			tab = table.get(start.getName())[tStart.when()];
		}
		for(int i=0; i<tab.length; i++) if(tab[i] >= tStart.getTime()) {
			tStop = new Time(table.get(stop.getName())[tStart.when()][i]%1440 + tStart.getDay()*1440);
			return new Link(nr, start, stop, new Time(tStart.getDay()*1440+tab[i]%1440), tStop); 
		}
		return getNextLink(nr, start, tStart.nextDay());
	}
	public Path[] getPath(Link link, int nr, int amount) { //zwraca œcie¿ki dojazdu
		Spot start = link.getStart();
		Time t0 = link.getStartTime();
		Path[] P = new Path[amount];
		int length = isLinked(start,link.getStop())+1;
		if(length>0) for(int j=0; j<P.length; j++) {
			P[j] = new Path(length);
			for (int i=0; i<P[j].path.size(); i++) {
				P[j].path.set(i, getNextLink((short)nr, start, t0));
				start = P[j].path.get(i).getStop();
				t0 = P[j].path.get(i).getStopTime();
			}
			start = link.getStart();
			t0 = P[j].getStartTime().add();
		}
		return P;
	}
}