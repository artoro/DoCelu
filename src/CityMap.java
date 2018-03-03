/*
 * Panel interaktywnej mapy.
 */
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;

public class CityMap extends JPanel implements MouseListener
{
	//POLA
	private static final long serialVersionUID = 1L;
	public static final double SCALE = 0.15767; //Skala mapy oddalonej wzglêdem mapy w powiêkszeniu
	public static final Vector2D BMAP_START = new Vector2D(97,17); //Przesuniêcie miêdzy mapami
	public static final Vector2D BMAP_FIELD = new Vector2D(134,82); //Siatka du¿ej mapy
	public static final Vector2D MAP_CENTER = new Vector2D(2600,1610); //Umowny œrodek mapy - centrum Krakowa
	public static final Vector2D MAP_FIELD = new Vector2D(850,520); //Siatka mapy w powiêkszeniu
	private static BufferedImage image; //Grafika mapy
	private static BufferedImage startI, spotI, spot2I, stopI; //Ikony przystanków
	private static Color pathC; //Kolor œcie¿ki
	private static Boolean zoom = false; //Powiêkszenie
	private static JButton[] field = new JButton[9]; //Przyciski steruj¹ce
	private static Vector2D mousePos = new Vector2D(); //Ostatnia pozycja myszki w momencie klikniêcia
	public static byte mouseState = 0; //Stan myszki (0 nic, 1 setSpotPos)
	public static Vector2D mapPos = new Vector2D(MAP_CENTER); //Pozycja mapy

	//KONSTRUKTOR
	public CityMap() {
		//Stworzenie panelu
		super();
		this.setPreferredSize(new Dimension(MAP_FIELD.x+50*2, MAP_FIELD.y+50*2));
		this.setLayout(new FlowLayout(FlowLayout.CENTER,0,0));
		this.fieldInit();
		this.setVisible(true);
		
		//Wczytanie obrazków ikon
		try {
			File imageFile = new File("../images/z.png");
			startI = ImageIO.read(imageFile);
			imageFile = new File("../images/przez.png");
			spotI = ImageIO.read(imageFile);
			imageFile = new File("../images/przesiadka.png");
			spot2I = ImageIO.read(imageFile);
			imageFile = new File("../images/do.png");
			stopI = ImageIO.read(imageFile);
		}
		catch (IOException e) {
			System.err.println("Blad wczytywania ikon");
		}
	}
	
	private void fieldInit() { //Inicjalizacja przycisków steruj¹cych
		for (int i=0; i<9; i++) {
			field[i] = new JButton();
			field[i].setContentAreaFilled(false);
			field[i].addMouseListener(this);
		}
		this.add(field[1]);
			field[1].setPreferredSize(new Dimension(50,50));
		this.add(field[2]);
			field[2].setPreferredSize(new Dimension(MAP_FIELD.x,50));
		this.add(field[3]);
			field[3].setPreferredSize(new Dimension(50,50));
			
		this.add(field[4]);
			field[4].setPreferredSize(new Dimension(50,MAP_FIELD.y));
		this.add(field[0]);
			field[0].setPreferredSize(new Dimension(MAP_FIELD.x,MAP_FIELD.y));
		this.add(field[5]);
			field[5].setPreferredSize(new Dimension(50,520));
			
		this.add(field[6]);
			field[6].setPreferredSize(new Dimension(50,50));
		this.add(field[7]);
			field[7].setPreferredSize(new Dimension(MAP_FIELD.x,50));
		this.add(field[8]);
			field[8].setPreferredSize(new Dimension(50,50));
		
		this.zoomOut();
		this.moveMap();
	}
	
	//METODY
	public void zoomIn() { //Przybli¿enie mapy
		zoom = true;
		field[0].setPreferredSize(new Dimension(MAP_FIELD.x,MAP_FIELD.y));
		for(int i=1; i<9; i++) {
			field[i].setVisible(true);
			field[i].setEnabled(true);
		}
	}
	public void zoomOut() { //Oddalenie mapy
		zoom = false;
		field[0].setPreferredSize(this.getPreferredSize());
		for(int i=1; i<9; i++) {
			field[i].setVisible(false);
			field[i].setEnabled(false);
		}
	}
	public void moveMap() { //Przesuniêcie / odœwie¿enie mapy
		String file;
		if(zoom) file = "../map/"+mapPos+".jpg";
		else file = "../map/bigMap.jpg";
		File imageFile = new File(file);
		try {
			image = ImageIO.read(imageFile);
		} catch (IOException e) {
			System.err.println("Blad wczytywania mapy");
			System.err.println(imageFile);
			zoomOut();
			moveMap();
		}
		if(Path.pathsL!=null && Path.pathsL.size()>0) drawPath(Path.pathsL.get(DoCelu.selectedPathI).path);
		drawSpot(Link.walk.getStart());
		drawSpot(Link.walk.getStop());
		this.repaint();
	}
	public void drawPath(List<Link> path) { //Rysuje œcie¿kê na mapie
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		if(path==null || path.size()<2) {
			if(!zoom && path.size()==1) drawRoute(path.get(0),g2d);
		}
		else for(int i=1; i<path.size(); i++) {
			Vector2D v = new Vector2D(path.get(i).getStart().getPosition());
			if(v.x == 0) continue;
			else if(zoom && path.get(i).getStart().getPosition().isContainedIn(mapPos, MAP_FIELD)) {
				v.move(-mapPos.x+50, -mapPos.y+50);
				
				Vector2D dv = new Vector2D(path.get(i).getStop().getPosition());
				dv.move(-path.get(i).getStart().getPosition().x, -path.get(i).getStart().getPosition().y);
				double rotation = Math.atan(dv.y*1.0/dv.x);
				if(dv.x<0) rotation += Math.PI;
				AffineTransform tx = AffineTransform.getRotateInstance(rotation, spotI.getWidth()/2, spotI.getHeight()/2);
				AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
				if(path.get(i-1).getNum() == path.get(i).getNum())
					g2d.drawImage(op.filter(spotI, null), v.x-spotI.getWidth()/2, v.y-spotI.getHeight()/2, null);
				else g2d.drawImage(op.filter(spot2I, null), v.x-spotI.getWidth()/2, v.y-spotI.getHeight()/2, null);
			}
			else if(!zoom) {
				drawRoute(path.get(i),g2d);
			}
		}
	}
	public void drawRoute(Link L, Graphics2D g2d) { //Rysuje drogi
		Vector2D v = new Vector2D(L.getStart().getPosition());
		v.x = (int)(v.x*SCALE-BMAP_START.x);
		v.y = (int)(v.y*SCALE-BMAP_START.y);
		Vector2D v2 = new Vector2D(L.getStop().getPosition());
		v2.x = (int)(v2.x*SCALE-BMAP_START.x);
		v2.y = (int)(v2.y*SCALE-BMAP_START.y);
		g2d.setColor(new Color(9,20,50));
		g2d.setStroke(new BasicStroke(8));
        g2d.draw(new Line2D.Float(v.x, v.y, v2.x, v2.y));
        if(L.getNum()==0) pathC = Color.WHITE;
        else if(L.getNum()<100 && L.getNum()>-100) pathC = Color.RED;
        else pathC = Color.BLUE;
        g2d.setColor(pathC);
		g2d.setStroke(new BasicStroke(2));
        g2d.draw(new Line2D.Float(v.x, v.y, v2.x, v2.y));
	}
	public void drawSpot(Spot S) { //Rysuje przystanki
		if(S == null) return;
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		Vector2D v = new Vector2D(S.getPosition());
		if(v.x == 0) return;
		else if(zoom && S.getPosition().isContainedIn(mapPos, MAP_FIELD)) {
			v.move(-mapPos.x+50, -mapPos.y+50);
		}
		else if(!zoom) {
			v.x = (int)(v.x*SCALE-BMAP_START.x);
			v.y = (int)(v.y*SCALE-BMAP_START.y);
		}
		else return;
		if(S==Link.walk.getStart()) g2d.drawImage(startI, v.x-startI.getWidth()/2, v.y-startI.getHeight()/2, null);
		else if(S==Link.walk.getStop()) g2d.drawImage(stopI, v.x-stopI.getWidth()/2, v.y-stopI.getHeight()/2, null);
	}
	@Override
	public void paintComponent(Graphics g) { //Rysuje komponent
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(image, 0, 0, this);
	}
	
	//OBS£UGA MYSZKI
	@Override
	public void mousePressed(MouseEvent e) { //Zapisuje pozycjê myszki w momencie klikniêcia
		if(e.getSource() == field[0]) {
			mousePos = new Vector2D(e.getPoint());
			if(zoom == false) mapPos = mousePos.getField();
		}
		e.consume();
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getSource() == field[0]) {
			if(zoom) {//Zaznacza na mapie punkt zgodny z pozycj¹ myszki
				if(mouseState == 1) {
					mouseState = 0;
					Spot.edit.setPosition(new Vector2D(mousePos.move(mapPos)));
					Spot.edit = null;
					if(Link.isSetUp()) DoCelu.searchB.setEnabled(true);
					moveMap();
//ADMIN
					System.out.println(Spot.edit);
					Spot.save();
				}
			}
			else { //Przybli¿a mapê w wybranym miejscu
				zoomIn();
				moveMap();
			}
		}
		else if(zoom) { //Przyciski steruj¹ce map¹
			if(e.getSource() == field[1]) {
				mapPos.move(-MAP_FIELD.x,-MAP_FIELD.y);// N-W
				moveMap();
			}
			else if(e.getSource() == field[2]) {
				mapPos.move(0,-MAP_FIELD.y);// N
				moveMap();
			}
			else if(e.getSource() == field[3]) {
				mapPos.move(MAP_FIELD.x,-MAP_FIELD.y);// N-E
				moveMap();
			}
			else if(e.getSource() == field[4]) {
				mapPos.move(-MAP_FIELD.x,0);// W
				moveMap();
			}
			else if(e.getSource() == field[5]) {
				mapPos.move(MAP_FIELD.x,0);// E
				moveMap();
			}
			else if(e.getSource() == field[6]) {
				mapPos.move(-MAP_FIELD.x,MAP_FIELD.y);// S-W
				moveMap();
			}
			else if(e.getSource() == field[7]) {
				mapPos.move(0,MAP_FIELD.y);// S
				moveMap();
			}
			else if(e.getSource() == field[8]) {
				mapPos.move(MAP_FIELD.x,MAP_FIELD.y);// S-E
				moveMap();
			}
		}
		e.consume();
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
}