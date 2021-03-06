package view;

import java.util.ArrayList;

import javafx.scene.Group;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import molecules.Molecule;
import utils.Couple;

public class MoleculeGroup extends Group {

	private double width;
	private double height;

	private Molecule molecule;
	private Couple<Integer, Integer>[] hexagonsCoords;
	private Couple<Double, Double>[] centersCoords;

	protected Hexagon2[] hexagons;

	private double xShift;
	private double yShift;

	public MoleculeGroup(Molecule molecule) {
		this.molecule = molecule;
		buildHexagons();
		drawHexagons();
	}

	@SuppressWarnings("unchecked")
	private void buildHexagons() {

		hexagons = new Hexagon2[molecule.getNbHexagons()];
		hexagonsCoords = new Couple[molecule.getNbHexagons()];
		centersCoords = new Couple[molecule.getNbHexagons()];
		int[][] dualGraph = molecule.getDualGraph();

		int[] checkedHexagons = new int[molecule.getNbHexagons()];

		ArrayList<Integer> candidates = new ArrayList<Integer>();
		candidates.add(0);

		checkedHexagons[0] = 1;
		hexagonsCoords[0] = new Couple<Integer, Integer>(0, 0);

		centersCoords[0] = new Couple<Double, Double>(40.0, 40.0);

		while (candidates.size() > 0) {

			int candidate = candidates.get(0);

			for (int i = 0; i < 6; i++) {

				int n = dualGraph[candidate][i];
				if (n != -1) {
					if (checkedHexagons[n] == 0) {

						int x = hexagonsCoords[candidate].getX();
						int y = hexagonsCoords[candidate].getY();

						double xCenter = centersCoords[candidate].getX();
						double yCenter = centersCoords[candidate].getY();

						if (i == 0) {

							x += 0;
							y += -1;

							xCenter += 26.0;
							yCenter -= 43.5;
						}

						else if (i == 1) {

							x += 1;
							y += 0;

							xCenter += 52.0;
							yCenter += 0.0;
						}

						else if (i == 2) {

							x += 1;
							y += 1;

							xCenter += 26.0;
							yCenter += 43.5;
						}

						else if (i == 3) {

							x += 0;
							y += 1;

							xCenter -= 26.0;
							yCenter += 43.5;
						}

						else if (i == 4) {

							x += -1;
							y += 0;

							xCenter -= 52.0;
							yCenter += 0.0;
						}

						else if (i == 5) {

							x += -1;
							y += -1;

							xCenter -= 26.0;
							yCenter -= 43.5;
						}

						checkedHexagons[n] = 1;
						hexagonsCoords[n] = new Couple<Integer, Integer>(x, y);
						centersCoords[n] = new Couple<Double, Double>(xCenter, yCenter);
						candidates.add(n);
					}
				}
			}

			candidates.remove(candidates.get(0));
		}

		ArrayList<ArrayList<Double>> points = new ArrayList<ArrayList<Double>>();
		for (int i = 0; i < centersCoords.length; i++) {

			points.add(getHexagonPoints(centersCoords[i].getX(), centersCoords[i].getY()));
		}

		double xMin = Double.MAX_VALUE;
		double xMax = Double.MIN_VALUE;

		double yMin = Double.MAX_VALUE;
		double yMax = Double.MIN_VALUE;

		for (int i = 0; i < points.size(); i++) {

			ArrayList<Double> point = points.get(i);

			for (int j = 0; j < point.size(); j++) {

				double u = point.get(j);

				if (j % 2 == 0) { // x
					if (u < xMin)
						xMin = u;

					if (u > xMax)
						xMax = u;
				}

				else { // y
					if (u < yMin)
						yMin = u;

					if (u > yMax)
						yMax = u;
				}
			}
		}

		xShift = 0;
		yShift = 0;

		if (xMin < 0)
			xShift = -xMin + 15.0;

		if (yMin < 0)
			yShift = -yMin + 15.0;

		for (int i = 0; i < points.size(); i++) {

			ArrayList<Double> point = points.get(i);
			for (int j = 0; j < point.size(); j++) {

				if (j % 2 == 0) // x
					point.set(j, point.get(j) + xShift);

				else // y
					point.set(j, point.get(j) + yShift);
			}
		}

		width = xMax + Math.abs(xMin) + 15.0;
		height = yMax + Math.abs(yMin) + 15.0;
		this.resize(width, height);

		for (int i = 0; i < centersCoords.length; i++) {
			hexagons[i] = new Hexagon2(hexagonsCoords[i], points.get(i));
		}
	}

	private ArrayList<Double> getHexagonPoints(double xCenter, double yCenter) {

		ArrayList<Double> points = new ArrayList<Double>();

		points.add(xCenter);
		points.add(yCenter - 29.5);

		points.add(xCenter + 26.0);
		points.add(yCenter - 14.0);

		points.add(xCenter + 26.0);
		points.add(yCenter + 14.0);

		points.add(xCenter);
		points.add(yCenter + 29.5);

		points.add(xCenter - 26.0);
		points.add(yCenter + 14.0);

		points.add(xCenter - 26.0);
		points.add(yCenter - 14.0);

		return points;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	private void drawHexagons() {
		for (int i = 0; i < hexagons.length; i++) {
			Hexagon2 hexagon = hexagons[i];
			this.getChildren().add(hexagon);

			Text text = createText(Integer.toString(i));
			text.setX(centersCoords[i].getX() + xShift - 5.0);
			text.setY(centersCoords[i].getY() + yShift + 5.0);

			this.getChildren().add(text);
		}
	}

	private Text createText(String string) {
		Text text = new Text(string);
		text.setBoundsType(TextBoundsType.VISUAL);
		text.setStyle("-fx-font-family: \"Verdana\";" + "-fx-font-size: 16px;");

		return text;
	}
}
