/*
 * Miejsce na mapie, np.: przystanek autobusowy, tramwajowy lub PKP.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class Spot
{
	//POLA STATYCZNE
	public static Map<String,Spot> listOfSpots = new TreeMap<String,Spot>(); //lista wczytanych przystank�w
	public static Spot edit; //obiekt w pami�ci podr�cznej, kt�ry b�dzie podlega� edycji
	public static MySpot myStart; //start ustawiony r�cznie przez u�ytkownika
	public static MySpot myStop; //cel ustawiony r�cznie przez u�ytkownika
	
	//POLA OBIEKTU
	private Vector2D pos; //pozycja na mapie
	private String name; //nazwa przystanku
	public ArrayList<Short> links; //lista numer�w linii kursuj�cych przez przystanek
	
	//KONSTRUKTORY
	public Spot(String NAME) { //podstawowy
		name = NAME;
		pos = null;
		links = new ArrayList<Short>();
	}
	public Spot(String NAME, Vector2D POS, ArrayList<Short> LINKS) { //standardowy
		name = NAME;
		pos = POS;
		links = LINKS;
	}
	public Spot(Spot S) { //kopiuj�cy
		pos = new Vector2D(S.pos.x, S.pos.y);
		name = new String(S.name);
		links = S.links;
	}
	
	//METODY
	public static Boolean load() { //wczytywania listy przystank�w z pliku
		myStart = new MySpot("Start");
		myStop = new MySpot("Stop");
		Scanner in;
		try { in = new Scanner(new File("../map/spots.txt")); }
		catch(FileNotFoundException e) { System.out.println("B��d odczytu pliku spots.txt"); return false; }
		while(in.hasNext()) {
			String name = new String();
			int x=0, y=0;
			ArrayList<Short> nr = new ArrayList<Short>();
			
			if(!in.hasNextShort()) name = new String(in.next());
			while(!in.hasNextShort()) name += " " + in.next();
			if(in.hasNextShort()) x=in.nextInt();
			if(in.hasNextShort()) y=in.nextInt();
			while(in.hasNextShort()) nr.add(in.nextShort());
			nr.trimToSize();
			if(name.length()>0) {
				if (x==0 || y==0) listOfSpots.put(name, new Spot(name, null, nr));
				else listOfSpots.put(name, new Spot(name, new Vector2D(x,y), nr));
			}
		}
		in.close();
		return true;
	}
	public static Boolean save() { //zapis listy do pliku
		PrintWriter out;
		try { out = new PrintWriter("../map/spots.txt"); }
		catch (FileNotFoundException e) { System.out.println("B��d zapisu pliku spots.txt"); return false; }
		for(Spot entry : listOfSpots.values()){
			  out.println(entry.toString());
		}
	    out.close();
		return true;
	}
	public static void addSpot(Spot S) { //dodaje nowy przystanek do listy
		listOfSpots.put(S.name, S);
	}
	public void addLink(int nr) { //dodaje nowe numery linii do rozk�adu przystanku
		if(!links.contains((short)nr)) links.add((short)nr);
	}
	public static Spot getSpot(String name) { //zwraca przystanek o podanej nazwie (je�li istnieje)
		return listOfSpots.get(name);
	}
	public String getName() { //zwraca nazw� przystanku
		return name;
	}
	public Vector2D getPosition() { //zwraca wektor po�o�enia przystanku na mapie
		if(pos == null) {//brak oznaczenia na mapie, trzeba zaznaczy�
			pos = new Vector2D();
		}
		return pos;
	}
	public void setPosition(Vector2D pos) { //ustawia pozycj� przystanku
		this.pos = new Vector2D(pos);
	}
	@Override
	public String toString() { //nadpisana metoda toString
		StringBuilder b = new StringBuilder(); //zoptymalizowane tworzenie stringa
		b.append(name);
		b.append(" " + getPosition());
		for(int i : links) if(i!=0) b.append(" " + i);
		return b.toString();
	}
}