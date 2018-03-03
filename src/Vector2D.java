/*
 * Klasa opisuj¹ca dwuwymiarowy wektor po³o¿enia.
 */
import java.awt.Point;

public class Vector2D
{
	//POLA
	public int x;
	public int y;
	
	//KONSTRUKTORY
	Vector2D() { x=0; y=0; }
	Vector2D(int X, int Y) { x=X; y=Y; }
	Vector2D(Vector2D V) { x=V.x; y=V.y; }
	Vector2D(Point P) { x=P.x; y=P.y; }
	
	//METODY
	public float distance(Vector2D V) { return (float)Math.sqrt((x-V.x)*(x-V.x) + (y-V.y)*(y-V.y)); } //d³ugoœæ wektora
	public int distanceXY(Vector2D V) { return Math.abs(x-V.x) + Math.abs(y-V.y); } //odleg³oœæ w przestrzeni metrycznej Manhattan
	public Vector2D move(Vector2D V) { x+=V.x; y+=V.y; return this; } //przemieszczenie wektora
	public Vector2D move(int X, int Y) { x+=X; y+=Y; return this; }
	public Vector2D getField() { //zwraca koordynaty po³o¿enia wed³ug siatki mapy
		Vector2D f = new Vector2D(this);
		f.move(CityMap.BMAP_START);
		f.x = CityMap.MAP_FIELD.x * (f.x / CityMap.BMAP_FIELD.x) + 50;
		f.y = CityMap.MAP_FIELD.y * (f.y / CityMap.BMAP_FIELD.y) + 50;
		return f;
	}
	public boolean isContainedIn(Vector2D START, Vector2D SIZE) { //sprawdza czy punkt jest zawarty w widoku
		return (START.x-50<x && START.y-50<y && START.x+SIZE.x+50>x && START.y+SIZE.y+50>y);
	}
	@Override
	public String toString() {
		return x + " " + y;
	}
}
