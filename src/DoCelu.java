/*
 * Klasa g³ówna Appletu.
 */
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTree;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;

public class DoCelu extends JApplet implements ActionListener, TreeSelectionListener
{
	//POLA
	private static final long serialVersionUID = 1L;
	private static JPanel sideBar; //lewy pasek boczny
	private static JPanel menu; //menu opcji w lewym pasku
	private static ImageIcon logoI = new ImageIcon("../images/logo.png"); //obrazek logo
	private static JLabel logoL; //logo obiekt
	private static JButton zoomOutB; //przycisk oddalania mapy
	private static JButton startB; //zaznacz punkt startowy
	private static JButton stopB; //zaznacz cel
	public static JComboBox<String> startSpot; //lista przystanków start
	public static JComboBox<String> stopSpot; //lista przystanków cel
	private static JButton showStartB; //przycisk pokazuj¹cy start na mapie
	private static JButton showStopB; //przycisk pokazuj¹cy cel na mapie
	private static JSlider dayS; //wybór dnia tygodnia
	private static JSpinner timeS; //ustawienie godziny
	private static ButtonGroup algoGroup; //grupa radio-przycisków
	private static JRadioButton[] algoRB; //przyciski wyboru algorytmu
	public static JButton searchB; //przycisk SZUKAJ
	private static JScrollPane resultPathMenu; //panel z wynikami
	private static JTree pathT; //drzewo wyników
	public static String selectedPath; //wybrana œcie¿ka
	public static int selectedPathI; //numer œcie¿ki
	private static ImageIcon arrowI = new ImageIcon("../images/arrow.gif"); //ikona pojedynczej strza³ki
	private static ImageIcon dArrowI = new ImageIcon("../images/doubleArrow.gif"); //ikona podwójnej strza³ki
	private static CityMap map; //mapa
	private static JLabel author; //pasek autora
	
	//KONSTRUTKOR
	public static void main(String[] args) {}
	
	//INICJALIZACJA APPLETU
	@Override
	public void init()
	{
		//Okno g³ówne
		super.init(); //wywo³anie metody init klasy JApplet
		this.setVisible(true);
		Time.initTime(); //aktualizacja daty i godziny
		
		//Wczytanie plików
		Spot.load();
		//Spot.save();
		
		//Pasek boczny
		sideBar = new JPanel();
		sideBar.setBackground(Color.LIGHT_GRAY);
		sideBar.setLayout(new BorderLayout());
		this.add(sideBar, BorderLayout.WEST);
		
		//Panel menu
		menu = new JPanel();
		sideBar.add(menu,BorderLayout.CENTER);
		menu.setBackground(sideBar.getBackground());
		menu.setPreferredSize(new Dimension(250,600));
		menu.setLayout(new FlowLayout(FlowLayout.RIGHT,10,10));
		
		//Logo
		logoL = new JLabel(logoI);
		menu.add(logoL);
		zoomOutB = new JButton("M");
		zoomOutB.setToolTipText("Oddal mapê");
		zoomOutB.addActionListener(this);
		menu.add(zoomOutB);
		
		//Start
			//Przycisk do ustawienia punktu startowego na mapie
		startB = new JButton("Z"); 
		startB.setPreferredSize(new Dimension(50,24));
		startB.addActionListener(this);
		startB.setToolTipText("Zaznacz na mapie");
		menu.add(startB);
			//Lista przystanków startowych
		startSpot = new JComboBox<String>(Spot.listOfSpots.keySet().toArray(new String[Spot.listOfSpots.keySet().size()]));
		startSpot.insertItemAt("Start", 0);
		startSpot.setSelectedIndex(0);
		startSpot.setPreferredSize(new Dimension(120,24));
		startSpot.addActionListener(this);
		startSpot.setToolTipText("Punkt startowy");
		menu.add(startSpot);
			//Przycisk wyœwietlaj¹cy punkt startowy na mapie
		showStartB = new JButton("S");
		showStartB.setPreferredSize(new Dimension(42,24));
		showStartB.addActionListener(this);
		showStartB.setToolTipText("Poka¿ punkt startowy");
		menu.add(showStartB);
		
		//Stop
			//Przycisk do ustawienia punktu docelowego na mapie
		stopB = new JButton("Do");
		stopB.setPreferredSize(startB.getPreferredSize());
		stopB.addActionListener(this);
		stopB.setToolTipText(startB.getToolTipText());
		menu.add(stopB);
			//Lista przystanków docelowych
		stopSpot = new JComboBox<String>(Spot.listOfSpots.keySet().toArray(new String[Spot.listOfSpots.keySet().size()]));
		stopSpot.insertItemAt("Stop", 0);
		stopSpot.setSelectedIndex(0);
		stopSpot.setPreferredSize(new Dimension(120,24));
		stopSpot.addActionListener(this);
		stopSpot.setToolTipText("Cel podó¿y");
		menu.add(stopSpot);
			//Przycisk wyœwietlaj¹cy punkt docelowy na mapie
		showStopB = new JButton("S");
		showStopB.setPreferredSize(showStartB.getPreferredSize());
		showStopB.addActionListener(this);
		showStopB.setToolTipText("Poka¿ cel podró¿y");
		menu.add(showStopB);
		
		//Dzieñ tygodnia
		dayS = new JSlider(JSlider.HORIZONTAL, 0, 6, 1);
		dayS.setBackground(menu.getBackground());
		dayS.setPreferredSize(new Dimension(170,34));
		dayS.setValue(LocalDateTime.now().getDayOfWeek().getValue()-1);
		dayS.setToolTipText("Dzieñ tygodnia");
		dayS.setLabelTable(Time.week);
		dayS.setPaintLabels(true);
		menu.add(dayS);
		
		//Godzina
		timeS = new JSpinner();
        timeS.setModel(new SpinnerDateModel());
        timeS.setEditor(new JSpinner.DateEditor(timeS, "HH:mm"));
        timeS.setToolTipText("Godzina odjazdu");
        menu.add(timeS);
		
        //Wybór algorytmu
        algoGroup = new ButtonGroup();
        algoRB = new JRadioButton[3];
        for(int i=0; i<algoRB.length; i++) {
        	algoRB[i] = new JRadioButton();
        	algoRB[i].addActionListener(this);
        	algoRB[i].setBackground(menu.getBackground());
        	algoGroup.add(algoRB[i]);
        	menu.add(algoRB[i]);
        }
        algoRB[0].setSelected(true);
        algoRB[0].setToolTipText("Bez przesiadek");
        algoRB[1].setToolTipText("Szybko");
        algoRB[2].setToolTipText("Optymalnie");
        algoRB[2].setEnabled(false);
        
        //Szukaj
		searchB = new JButton("Szukaj");
		searchB.setToolTipText(searchB.getText());
		searchB.addActionListener(this);
		menu.add(searchB);
		searchB.setEnabled(false);
		
		//Mapa
		map = new CityMap();
		this.add(map, BorderLayout.EAST);
		this.setSize(new Dimension(1200,620));
		selectedPath = "";
		selectedPathI = 0;
		
		//Pasek autora
		author = new JLabel("<html>Autor: Szyd³owski Artur<br> Nr albumu: 285351<br> AiR Gr 7 - WIMiR AGH - 2017</html>", SwingConstants.CENTER);
		sideBar.add(author,BorderLayout.SOUTH);
	}
	@Override
	public void paint(Graphics g) {
		super.paint(g); //wywo³anie metody paint klasy JApplet
	}
	
	//ZDARZENIA
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == zoomOutB) { //oddal mapê
			map.zoomOut();
			map.moveMap();
		}
		else if (e.getSource() == startB) { //ustaw start myszk¹
			startSpot.setSelectedItem(0);
			Spot.edit = Spot.myStart;
			Link.setWStart();
			if(resultPathMenu != null) hideResults();
		}
		else if (e.getSource() == startSpot) { //ustaw start z listy
			Spot start = Spot.listOfSpots.get(startSpot.getSelectedItem());
			if(start != null) Link.setWStart(start);
			else Link.setWStart();
			if(resultPathMenu != null) hideResults();
		}
		else if (e.getSource() == showStartB) { //wyœwietl start na mapie
			if(Link.walk.getStart() != null) {
				Spot S = Link.walk.getStart();
				if(S.getPosition().x != 0 && S.getPosition().y != 0) {
					CityMap.mapPos = new Vector2D ((int) ((S.getPosition().x - 50) / CityMap.MAP_FIELD.x) * CityMap.MAP_FIELD.x + 50,
												(int) ((S.getPosition().y - 50) / CityMap.MAP_FIELD.y) * CityMap.MAP_FIELD.y + 50);
					map.zoomIn();
					map.moveMap();
					map.drawSpot(S);
				}
//ADMIN MODE
				else {
					System.out.println(S.getName() + " nie ma ustawionej pozycji!");
					Spot.edit = S;
					CityMap.mouseState = 1;
				}
//
			}
		}
		else if (e.getSource() == stopB) { //ustaw myszk¹ cel
			stopSpot.setSelectedItem(0);
			Spot.edit = Spot.myStop;
			Link.setWStop();
			if(resultPathMenu != null) hideResults();
		}
		else if (e.getSource() == stopSpot) { //ustaw cel z listy
			Spot stop = Spot.listOfSpots.get(stopSpot.getSelectedItem());
			if(stop != Link.walk.getStop() && resultPathMenu != null) hideResults();
			if(stop != null) Link.setWStop(stop);
			else Link.setWStop();
			if(resultPathMenu != null) hideResults();
		}
		else if (e.getSource() == showStopB) { //wyœwietl cel na mapie
			if(Link.walk.getStop() != null) {
				Spot S = Link.walk.getStop();
				if(S.getPosition().x != 0 && S.getPosition().y != 0) {
					CityMap.mapPos = new Vector2D ((int) ((S.getPosition().x - 50) / CityMap.MAP_FIELD.x) * CityMap.MAP_FIELD.x + 50,
												(int) ((S.getPosition().y - 50) / CityMap.MAP_FIELD.y) * CityMap.MAP_FIELD.y + 50);
					map.zoomIn();
					map.moveMap();
					map.drawSpot(S);
				}
				else {
					map.zoomOut();
					map.moveMap();
				}
			}
		}
		else if (e.getSource() == searchB) { //szukaj
			Link.setWTimeStart(new Time(dayS.getValue(),timeS.getValue().toString()));
			hideResults();
			if(algoRB[0].isSelected()) Path.directPath(Link.walk.getStart(), Link.walk.getStop(), Link.walk.getStartTime());
			else if(algoRB[1].isSelected()) Path.quickPath(Link.walk.getStart(), Link.walk.getStop(), Link.walk.getStartTime());
			pathT = Path.makeTree();
			showResults();
		}
	}
	
	//UKRYJ POPRZEDNIE WYSZUKIWANIE
	private void hideResults() {
		if(resultPathMenu != null) menu.remove(resultPathMenu);
		resultPathMenu = null;
		Path.pathsL = new ArrayList<Path>();
		selectedPath = "";
		selectedPathI = 0;
		menu.revalidate();
		menu.repaint();
		map.moveMap();
	}
	
	//WYSZUKAJ I WYŒWIETL WYNIKI
	private void showResults() {
		pathT.putClientProperty("JTree.lineStyle", "Horizontal");
		UIManager.put("Tree.closedIcon", arrowI);
		UIManager.put("Tree.openIcon", dArrowI);
		if (arrowI != null) {
		    DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		    renderer.setLeafIcon(arrowI);
		    pathT.setCellRenderer(renderer);
		}
		resultPathMenu = new JScrollPane(pathT);
		resultPathMenu.setPreferredSize(new Dimension(230,330));
		menu.add(resultPathMenu);
		pathT.addTreeSelectionListener(this);
		menu.revalidate();
		menu.repaint();
		map.moveMap();
	}
	
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		//Wybór wyœwietlanego po³¹czenia na mapie
		try { 
			if (selectedPath != pathT.getSelectionPath().getPathComponent(1).toString()) {
				String t = pathT.getSelectionPath().getPathComponent(1).toString();
				for(int i=0; i<6 && i<Path.pathsL.size(); i++)
					if (t.contains(Path.pathsL.get(i).getStopTime().toString()) &&
							t.contains(Path.pathsL.get(i).getStartTime().toString())) {
						selectedPath = t;
						selectedPathI = i;
						map.moveMap();
						return;
				}
			}
		}
		catch (IllegalArgumentException ei) { return; }
	}
}