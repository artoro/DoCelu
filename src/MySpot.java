/*
 * Opisuje dowolny punkt na mapie.
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MySpot extends Spot {
	//POLA
	public Spot[] walk;
	
	//KONSTRUKTORY
	public MySpot(String NAME) { //podstawowy
		super(NAME);
	}
	public MySpot(Spot S) { //kopiuj¹cy
		super(S);
		Spot.edit = this;
		findLinks();
	}
	
	//METODY
	@Override
	public void setPosition(Vector2D pos) { //ustawia pozycjê
		if(pos != null) {
			super.setPosition(pos);
			findLinks();
		}
		else {
			Spot.edit = this;
			CityMap.mouseState = 1;
		}
	}
	private void findLinks() { //znajduje po³¹czenia z najbli¿szymi przystankami
		ArrayList<Spot> S = new ArrayList<Spot>(Spot.listOfSpots.size());
		for(Spot lofs : Spot.listOfSpots.values()) if(lofs.links.size()>0) S.add(lofs);
		Collections.sort(S, new KomparatorDistance());
		walk = new Spot[(4>S.size())? S.size() : 4];
		for(int i=0; i<walk.length; i++) walk[i] = S.get(i);
	}
	private class KomparatorDistance implements Comparator<Spot> { //porównuje odleg³oœci przystanków
        @Override
        public int compare(Spot s1, Spot s2) {
        	if(Spot.edit.getPosition().x == 0) return 0;
        	else if(s1.getPosition().x == 0)  return 1;
            else if(s2.getPosition().x == 0) return -1;
            else return (int)(s1.getPosition().distance(Spot.edit.getPosition()) - s2.getPosition().distance(Spot.edit.getPosition()));
        }
    }
}