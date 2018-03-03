/*
 * Klasa zawieraj¹ca algorytmy wyszukuj¹ce trasy dojazdu.
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

public class Path implements Comparable<Path>
{
	//POLA STATYCZNE
	public static List<Path> pathsL; //przetworzone dane
	private static DefaultMutableTreeNode TOP = new DefaultMutableTreeNode("Znalezione po³¹czenia");
	private static DefaultMutableTreeNode nullTop = new DefaultMutableTreeNode("Brak po³¹czeñ o podanych kryteriach");
	
	//POLA OBIEKTU
	public List<Link> path;
	
	//KONSTRUKTORY
	public Path(int size) { //tworzy œcie¿kê o danej d³ugoœci
		path = new ArrayList<Link>(size);
		for(int i=0; i<size; i++) path.add(new Link());
	}
	
	//METODY
	public Link getPathLink() {
		return new Link(path.get(path.size()-1).getNum(),getStart(),getStop(),getStartTime(),getStopTime());
	}
	public Spot getStart() {
		return path.get(0).getStart();
	}
	public Time getStartTime() {
		return path.get(0).getStartTime();
	}
	public Spot getStop() {
		return path.get(path.size()-1).getStop();
	}
	public Time getStopTime() {
		return path.get(path.size()-1).getStopTime();
	}
	public short getNum() {
		return path.get(path.size()-1).getNum();
	}
	public int isFaster(Path P) {
		if(P.getStart()==this.getStart() && P.getStop()==this.getStop()) {
			if(P.path.get(P.path.size()-1).getCost()>=this.path.get(this.path.size()-1).getCost()) return 1; // P wolniejsze od this
			return -1; // P szybsze od this
		}
		return 0; // nie mo¿na porównaæ, P != this
	}
	
	//METODY STATYCZNE
	public static JTree makeTree() { //tworzy drzewo wyników na podstawie wyznaczonych œcie¿ek
		if(pathsL.size() == 0) return new JTree(nullTop);
		else {
			short amount = 0;
			DefaultMutableTreeNode top = new DefaultMutableTreeNode(TOP);
			JTree pathsT = new JTree(top);
			for(Path P : pathsL) {
				//dodaje dojœcia do przystanków i usuwa zbêdne po³¹czenia
				Link walk;
				if(P.getStart() != Link.walk.getStart()) {
					Time walkTime = P.getStartTime().diff(new Time((int)(P.getStart().getPosition().distance(Link.walk.getStart().getPosition())*Link.pedestrianVel)));
					walk = new Link((short)0,Link.walk.getStart(),P.getStart(),walkTime,P.getStartTime());
					P.path.add(0,walk);
				}
				if(P.getStop() != Link.walk.getStop()) {
					walk = new Link(P.getStop(),Link.walk.getStop(),P.getStopTime());
					Time cost = P.getStopTime().diff(P.getStartTime());
					walk.addCost(cost.min);
					P.path.add(P.path.size(),walk);
				}
				if(P.getStart() == P.path.get(0).getStop() && P.getStart()!=Link.walk.getStop())
					P.path.remove(0);
			}
			Collections.sort(pathsL); //sortuje wed³ug godziny dotarcia do celu
			for(Path P : pathsL) {
				if(amount > 5) break;
				else amount++;
				Link travelL;
				String link;
				DefaultMutableTreeNode pathNode, travelNode;
				
				//nazwa œcie¿ki
				link = P.getStartTime() + " " + P.getStart().getName() + " -> " + P.getStop().getName() + " " + P.getStopTime();
				pathNode = new DefaultMutableTreeNode(link);
				top.add(pathNode);
				
				//przystanki poœrednie
				int i = 0;
				if(i<P.path.size()) {
					travelL = new Link(P.path.get(i));
					travelNode = new DefaultMutableTreeNode(travelL);
					pathNode.add(travelNode);
					for(; i<P.path.size(); i++) {
						if(i>0 && P.path.get(i).getNum() != travelL.getNum()) {
							travelNode.add(new DefaultMutableTreeNode(P.path.get(i-1).getStopTime() + " " + P.path.get(i-1).getStop().getName()));
							travelL.setStop(P.path.get(i).getStart());
							travelL.setStopTime(P.path.get(i-1).getStopTime());
							travelNode.setUserObject(travelL);
							travelL = new Link(P.path.get(i));
							travelNode = new DefaultMutableTreeNode(travelL);
							pathNode.add(travelNode);
						}
						travelNode.add(new DefaultMutableTreeNode(P.path.get(i).getStartTime() + " " + P.path.get(i).getStart().getName()));
					}
					travelNode.add(new DefaultMutableTreeNode(P.getStopTime() + " " + P.getStop().getName()));
					travelL.setStop(P.getStop());
					travelL.setStopTime(P.getStopTime());
					travelNode.setUserObject(travelL);
				}
			}
			return pathsT;
		}
	}
	public static void quickPath(Spot start, Spot stop, Time startTime) { //algorytm szybki
		directPath(start,stop,startTime);
		ArrayList<Path> linkL = new ArrayList<Path>();
		ArrayList<Spot> checked = new ArrayList<Spot>();
		Path P;
		if(start.links.size()==0) { //dojœcie z MySpot do przystanków startowych
			MySpot MS = new MySpot(start);
			for(Spot S : MS.walk) {
				P = new Path(1);
				P.path.set(0, new Link(start,S,startTime));
				linkL.add(P);
			}
		}
		//Przystanek startowy
		P = new Path(1);
		P.path.set(0, new Link((short)0,start,start,startTime,startTime));
		linkL.add(P);
		checked.add(stop);
		pathsL.add(P);
		
		//Szukaj œcie¿ek (algorytm A*)
		do {
			Path top = linkL.get(0);
			if(checked.contains(top.getStop())) {
				pathsL.add(top);
				linkL.remove(0);
				continue;
			}
			checked.add(top.getStop());
			for(short nr : top.getStop().links) if(nr!=-top.getNum()) {
				P = new Path(top.path.size()+1);
				try {
					P.path.set(P.path.size()-1, Travel.get(nr,top.getStopTime().when()).getNextLink(top.getStop(), top.getStopTime()));
					if(P.path.get(P.path.size()-1)==null) throw new NullPointerException();
				}
				catch (NullPointerException e) { continue; }
				for(int i=0; i<top.path.size(); i++) {
					P.path.set(i,top.path.get(i));
				}
				P.path.get(P.path.size()-1).addCost(top.getStopTime().diff(top.getStartTime()).min);
				/*for(int i=0; i<linkL.size() && P!=null; i++) switch(linkL.get(i).isFaster(P)) {
					case 1: P = null; i = linkL.size(); break; // s¹ szybsze dojazdy w to miejsce ni¿ P
					case -1: linkL.remove(i); i = linkL.size(); break; // P jest szybsze ni¿ wczeœniej znalezione po³¹czenie
				}
				if(P!=null) {
					linkL.add(P); 
					checked.remove(P.getStop());
				}*/
				linkL.add(P); 
				Collections.sort(linkL, new Comparator<Path>(){
					public int compare(Path P1,Path P2){
                    	return (int) (P1.getStop().getPosition().distance(Link.walk.getStop().getPosition()) -
                        		P2.getStop().getPosition().distance(Link.walk.getStop().getPosition()));
                }});
			}
		} while(linkL.size()>0 && pathsL.size()<30 && checked.size()<80);
		/*Collections.sort(pathsL);
		for(int i=0; i<pathsL.size(); i++) for(int j=i+1; j<pathsL.size(); j++) {
			Path P1 = pathsL.get(i);
			Path P2 = pathsL.get(j);
			if(P1.getNum() == P2.getNum()) {// || P1.path.get(2).getStartTime() == P2.path.get(2).getStartTime()) {
				if(P1.getStopTime().min==P2.getStopTime().min) {
					System.out.println("REMOVE " + P1);
					pathsL.remove(P1);
				}
				else if(P1.getStop().getPosition().distance(Link.walk.getStop().getPosition()) <
                		P2.getStop().getPosition().distance(Link.walk.getStop().getPosition())) {
					System.out.println("REMOVE " + P2);
					pathsL.remove(P2);
				}
			} 
		}*/
	}
	public static void directPath(Spot start, Spot stop, Time startTime) { //algorytm bezpoœredni
		if(start.links.size()==0) { //dojœcie z MySpot do startu
			MySpot MS = new MySpot(start);
			for(Spot S : MS.walk) {
				directPath(S,stop,startTime);
			}
			return;
		}
		ArrayList<Short> directLinks = new ArrayList<Short>();
		for(short s0 : start.links) for(short s1 : stop.links){ //linie ³¹cz¹ce bezpoœrednio
			if(s0==s1 && Travel.get(s0,startTime.when()).isLinked(start, stop)>-1) directLinks.add(s0);
		}
		if(directLinks.size() > 0) {
			for(int i=0; i<directLinks.size(); i++) { //po³¹czenia bezpoœrednie
				Path[] P = Travel.get(directLinks.get(i),startTime.when()).getPath(new Link(start,stop,startTime), 4);
				for(int k=0; k<P.length; k++) if(P[k]!=null) pathsL.add(P[k]);
				System.out.println(pathsL.toString());
			}
		}
	}
/*	public static JTree optimalPath() {
		return pathsT();
	}*/
	@Override
	public int compareTo(Path P2) { //komparator ró¿nicy czasowej godziny dojœcia
		int diff = getStopTime().min-P2.getStopTime().min;
		return (diff>5000)? -diff : diff;
	}
	@Override
	public String toString() {
		return getStartTime() + " " + getStart().getName() + " -> " + getStop().getName() + " " + getStopTime();
	}
}
