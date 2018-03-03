/*
 * Klasa opisuj�ca �rodek komunikacji miejskiej - tramwaj, autobus, poci�g, itp.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Travel
{
	//POLA STATYCZNE
	public static Map<Short,Travel> listOfTravels = new HashMap<Short,Travel>(); //lista wczytanych �rodk�w komunikacji
	
	//POLA OBIEKTU
	private short num; //numer linii
	private Timetable timetable; //rozk�ad jazdy
	
	//KONSTRUKTORY
	public Travel(int NUM) { num=(short)NUM; timetable = new Timetable(); }
	
	//METODY
	public static Boolean load(int nr, int day) { //wczytuje dane (0 dzie� roboczy, 1 sobota, 2 niedziela)
		Scanner in;
		try { in = new Scanner(new File("../data/"+nr+"_"+day+".txt")); }
		catch(FileNotFoundException e) { System.out.println("B��d odczytu pliku "+nr+"_"+day+".txt"); return false; }
		while(in.hasNext()) {
			String name = new String();
			if(!in.hasNextShort()) name = new String(in.next());
			while(!in.hasNextShort()) name += " " + in.next();
			if(name.length()<1) continue;
			if(Spot.listOfSpots.containsKey(name)) name=Spot.listOfSpots.get(name).getName();
			else Spot.addSpot(new Spot(name));
			Spot.listOfSpots.get(name).addLink(nr);
			addTravel(new Travel(nr));
			Travel.get(nr,day).timetable.add(name,day,in);
		}
		in.close();
		return true;
	}
	public static Travel get(int number, int day) { //zwraca dane po��czenie (je�li nie zosta�o jeszcze wczytane, to pr�buje to zrobi�)
		if(!listOfTravels.containsKey((short)number)) load(number, day);
		return listOfTravels.get((short)number);
	}
	public static Boolean addTravel(Travel t) { //dodaje nowe po��czenie do listy
		if (listOfTravels.containsKey(t.num)) return false;
		else listOfTravels.put(t.num, t);
		return true;
	}
	public int isLinked(Spot start, Spot stop) { //zwraca odleg�o�� mi�dzy dwoma przystankami (-1 je�li nie ma po��czenia)
		return timetable.isLinked(start, stop);
	}
	public String getNextSpot(Spot start) { //zwraca nast�pny przystanek
		return timetable.getNextSpot(start);
	}
	public Link getNextLink(Spot start, Time tStart) { //zwraca najbli�sze po��czenie z przystanku start po godzinie tStart
		return timetable.getNextLink(num, start, tStart);
	}
	public Path[] getPath(Link link, int amount) { //zwraca �cie�ki doj�cia mi�dzy przystankami
		return timetable.getPath(link, num, amount);
	}
}