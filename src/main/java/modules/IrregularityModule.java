package modules;

import java.util.ArrayList;

import org.chocosolver.solver.constraints.extension.Tuples;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

import generator.GeneralModel;
import view.generator.GeneratorCriterion;
import view.generator.GeneratorCriterion.Subject;

public class IrregularityModule extends Module {

	private int[][] dualGraph;

	private BoolVar[][] xN;
	private BoolVar zero;

	private IntVar[] NULL, SOLOS, DUOS, TRIOS, QUATUORS;

	private IntVar N0, N1, N2, N3, N4;

	private IntVar XI;
	private IntVar V1, V2;

	private ArrayList<GeneratorCriterion> criterions;

	public IrregularityModule(GeneralModel generalModel, ArrayList<GeneratorCriterion> criterions) {
		super(generalModel);
		this.criterions = criterions;
	}

	@Override
	public void buildVariables() {

		zero = generalModel.getProblem().boolVar(false);

		buildDualGraph();
		buildXN();

		NULL = new IntVar[generalModel.getDiameter() * generalModel.getDiameter()];
		SOLOS = new IntVar[generalModel.getDiameter() * generalModel.getDiameter()];
		DUOS = new IntVar[generalModel.getDiameter() * generalModel.getDiameter()];
		TRIOS = new IntVar[generalModel.getDiameter() * generalModel.getDiameter()];
		QUATUORS = new IntVar[generalModel.getDiameter() * generalModel.getDiameter()];

		for (int index = 0; index < generalModel.getDiameter() * generalModel.getDiameter(); index++) {

			if (generalModel.getCorrespondancesHexagons()[index] != -1) {
				NULL[index] = generalModel.getProblem().intVar("NULL_" + index, new int[] { 0, 1 });
				SOLOS[index] = generalModel.getProblem().intVar("SOLOS_" + index, new int[] { 0, 1, 2 });
				DUOS[index] = generalModel.getProblem().intVar("DUOS_" + index, new int[] { 0, 2 });
				TRIOS[index] = generalModel.getProblem().intVar("TRIOS_" + index, new int[] { 0, 3 });
				QUATUORS[index] = generalModel.getProblem().intVar("QUATUORS_" + index, new int[] { 0, 4 });
			}

			else {
				NULL[index] = generalModel.getProblem().intVar("NULL_" + index, 0);
				SOLOS[index] = generalModel.getProblem().intVar("SOLOS_" + index, 0);
				DUOS[index] = generalModel.getProblem().intVar("DUOS_" + index, 0);
				TRIOS[index] = generalModel.getProblem().intVar("TRIOS_" + index, 0);
				QUATUORS[index] = generalModel.getProblem().intVar("QUATUORS_" + index, 0);
			}
		}

		N0 = generalModel.getProblem().intVar("N0", 0, generalModel.getDiameter() * generalModel.getDiameter() + 1);
		N1 = generalModel.getProblem().intVar("N1", 0, 2 * generalModel.getDiameter() * generalModel.getDiameter() + 1);
		N2 = generalModel.getProblem().intVar("N2", 0, 2 * generalModel.getDiameter() * generalModel.getDiameter() + 1);
		N3 = generalModel.getProblem().intVar("N3", 0, 3 * generalModel.getDiameter() * generalModel.getDiameter() + 1);
		N4 = generalModel.getProblem().intVar("N4", 0, 4 * generalModel.getDiameter() * generalModel.getDiameter() + 1);

	}

	@Override
	public void postConstraints() {

		/*
		 * Table constraints
		 */

		Tuples table = buildTable();

		for (int line = 0; line < generalModel.getCoordsMatrix().length; line++) {
			for (int column = 0; column < generalModel.getCoordsMatrix()[line].length; column++) {
				if (generalModel.getCoordsMatrix()[line][column] != -1) {

					int index = generalModel.getCoordsMatrix()[line][column];
					IntVar[] nH = xN[index];
					IntVar[] tuple = new IntVar[] { nH[0], nH[1], nH[2], nH[3], nH[4], nH[5], NULL[index], SOLOS[index],
							DUOS[index], TRIOS[index], QUATUORS[index] };

					generalModel.getProblem().ifThenElse(
							generalModel.getProblem().arithm(generalModel.getWatchedGraphVertices()[index], "=", 1),
							generalModel.getProblem().table(tuple, table, "CT+"),
							generalModel.getProblem().sum(new IntVar[] { NULL[index], SOLOS[index], DUOS[index],
									TRIOS[index], QUATUORS[index] }, "=", 0));
				}
			}
		}

		/*
		 * Sum constraints for N0, N1, N2, N3, N4
		 */

		generalModel.getProblem().sum(NULL, "=", N0).post();
		generalModel.getProblem().sum(SOLOS, "=", N1).post();
		generalModel.getProblem().sum(DUOS, "=", N2).post();
		generalModel.getProblem().sum(TRIOS, "=", N3).post();
		generalModel.getProblem().sum(QUATUORS, "=", N4).post();

		/*
		 * XI constraints
		 */

		XI = generalModel.getProblem().intVar("XI", 0, 1000, true);
		V1 = generalModel.getProblem().intVar("V1", 0, 160 * generalModel.getDiameter() * generalModel.getDiameter(),
				true);
		V2 = generalModel.getProblem().intVar("V2", 0, 160 * generalModel.getDiameter() * generalModel.getDiameter(),
				true);

		generalModel.getProblem().sum(new IntVar[] { N3, N4 }, "=", V1).post();
		generalModel.getProblem().sum(new IntVar[] { N1, N2, N3, N4 }, "=", V2).post();

		IntVar num = generalModel.getProblem().intVar("num", 0,
				800 * generalModel.getDiameter() * generalModel.getDiameter(), true);
		generalModel.getProblem().times(V1, 100, num).post();

		generalModel.getProblem().div(num, V2, XI).post();

		/*
		 * Parameters constraints
		 */

		for (GeneratorCriterion criterion : criterions) {

			if (criterion.getSubject() == Subject.XI) {
				double decimalValue = Double.parseDouble(criterion.getValue());
				int finalValue = (int) (decimalValue * 100);
				generalModel.getProblem().arithm(XI, criterion.getOperatorString(), finalValue).post();
			}

			else {

				IntVar subject = null;
				String operator = criterion.getOperatorString();
				int value = Integer.parseInt(criterion.getValue());

				switch (criterion.getSubject()) {

				case N0:
					subject = N0;
					break;

				case N1:
					subject = N1;
					break;

				case N2:
					subject = N2;
					break;

				case N3:
					subject = N3;
					break;

				case N4:
					subject = N4;
					break;

				default:
					break;
				}

				generalModel.getProblem().arithm(subject, operator, value).post();
			}
		}

	}

	@Override
	public void addWatchedVariables() {
		generalModel.addWatchedVariable(N0);
		generalModel.addWatchedVariable(N1);
		generalModel.addWatchedVariable(N2);
		generalModel.addWatchedVariable(N3);
		generalModel.addWatchedVariable(N4);
		generalModel.addWatchedVariable(XI);
	}

	private void buildXN() {

		xN = new BoolVar[generalModel.getDiameter() * generalModel.getDiameter()][6];

		for (int line = 0; line < dualGraph.length; line++) {
			for (int column = 0; column < dualGraph[line].length; column++) {
				if (dualGraph[line][column] != -1) {
					xN[line][column] = generalModel.getWatchedGraphVertices()[dualGraph[line][column]];
				} else {
					xN[line][column] = zero;
				}
			}
		}
	}

	private void buildDualGraph() {

		dualGraph = new int[generalModel.getDiameter() * generalModel.getDiameter()][6];

		for (int i = 0; i < dualGraph.length; i++) {
			for (int j = 0; j < dualGraph[i].length; j++) {
				dualGraph[i][j] = -1;
			}
		}

		for (int line = 0; line < generalModel.getCoordsMatrix().length; line++) {
			for (int column = 0; column < generalModel.getCoordsMatrix()[line].length; column++) {

				if (generalModel.getCoordsMatrix()[line][column] != -1) {

					int index = generalModel.getCoordsMatrix()[line][column];

					// High-Right
					if (line > 0)
						dualGraph[index][0] = generalModel.getCoordsMatrix()[line - 1][column];

					// Right
					if (column < generalModel.getCoordsMatrix()[line].length - 1)
						dualGraph[index][1] = generalModel.getCoordsMatrix()[line][column + 1];

					// Down-Right
					if (line < generalModel.getCoordsMatrix()[line].length - 1
							&& column < generalModel.getCoordsMatrix()[line].length - 1)
						dualGraph[index][2] = generalModel.getCoordsMatrix()[line + 1][column + 1];

					// Down-Left
					if (line < generalModel.getCoordsMatrix()[line].length - 1)
						dualGraph[index][3] = generalModel.getCoordsMatrix()[line + 1][column];

					// Left
					if (column > 0)
						dualGraph[index][4] = generalModel.getCoordsMatrix()[line][column - 1];

					// High-Left
					if (line > 0 && column > 0)
						dualGraph[index][5] = generalModel.getCoordsMatrix()[line - 1][column - 1];
				}
			}
		}
	}

	private static Tuples buildTable() {
		Tuples table = new Tuples(true);

		table.add(1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0);
		table.add(0, 1, 0, 1, 0, 1, 1, 0, 0, 0, 0);

		table.add(1, 1, 0, 1, 1, 0, 1, 0, 0, 0, 0);
		table.add(0, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0);
		table.add(1, 0, 1, 1, 0, 1, 1, 0, 0, 0, 0);

		table.add(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4);
		table.add(0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 4);
		table.add(0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 4);
		table.add(0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 4);
		table.add(0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 4);
		table.add(0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 4);

		table.add(1, 1, 0, 0, 0, 0, 0, 0, 0, 3, 0);
		table.add(0, 1, 1, 0, 0, 0, 0, 0, 0, 3, 0);
		table.add(0, 0, 1, 1, 0, 0, 0, 0, 0, 3, 0);
		table.add(0, 0, 0, 1, 1, 0, 0, 0, 0, 3, 0);
		table.add(0, 0, 0, 0, 1, 1, 0, 0, 0, 3, 0);
		table.add(1, 0, 0, 0, 0, 1, 0, 0, 0, 3, 0);

		table.add(1, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0);
		table.add(0, 1, 0, 0, 1, 0, 0, 2, 0, 0, 0);
		table.add(0, 0, 1, 0, 0, 1, 0, 2, 0, 0, 0);

		table.add(1, 0, 1, 0, 0, 0, 0, 0, 2, 0, 0);
		table.add(0, 1, 0, 1, 0, 0, 0, 0, 2, 0, 0);
		table.add(0, 0, 1, 0, 1, 0, 0, 0, 2, 0, 0);
		table.add(0, 0, 0, 1, 0, 1, 0, 0, 2, 0, 0);
		table.add(1, 0, 0, 0, 1, 0, 0, 0, 2, 0, 0);
		table.add(0, 1, 0, 0, 0, 1, 0, 0, 2, 0, 0);

		table.add(1, 1, 1, 0, 0, 0, 0, 0, 2, 0, 0);
		table.add(0, 1, 1, 1, 0, 0, 0, 0, 2, 0, 0);
		table.add(0, 0, 1, 1, 1, 0, 0, 0, 2, 0, 0);
		table.add(0, 0, 0, 1, 1, 1, 0, 0, 2, 0, 0);
		table.add(1, 0, 0, 0, 1, 1, 0, 0, 2, 0, 0);
		table.add(1, 1, 0, 0, 0, 1, 0, 0, 2, 0, 0);

		table.add(1, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0);
		table.add(0, 1, 1, 0, 1, 0, 0, 1, 0, 0, 0);
		table.add(0, 0, 1, 1, 0, 1, 0, 1, 0, 0, 0);
		table.add(1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0);
		table.add(0, 1, 0, 0, 1, 1, 0, 1, 0, 0, 0);
		table.add(1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 0);

		table.add(1, 1, 1, 1, 0, 0, 0, 1, 0, 0, 0);
		table.add(0, 1, 1, 1, 1, 0, 0, 1, 0, 0, 0);
		table.add(0, 0, 1, 1, 1, 1, 0, 1, 0, 0, 0);
		table.add(1, 0, 0, 1, 1, 1, 0, 1, 0, 0, 0);
		table.add(1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 0);
		table.add(1, 1, 1, 0, 0, 1, 0, 1, 0, 0, 0);

		table.add(1, 1, 1, 0, 1, 0, 1, 0, 0, 0, 0);
		table.add(0, 1, 1, 1, 0, 1, 1, 0, 0, 0, 0);
		table.add(1, 0, 1, 1, 1, 0, 1, 0, 0, 0, 0);
		table.add(0, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0);
		table.add(1, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0);
		table.add(1, 1, 0, 1, 0, 1, 1, 0, 0, 0, 0);

		table.add(1, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0);
		table.add(0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0);
		table.add(1, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0);
		table.add(1, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0);
		table.add(1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0);
		table.add(1, 1, 1, 1, 0, 1, 1, 0, 0, 0, 0);

		table.add(0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 0);
		table.add(0, 1, 0, 1, 1, 0, 0, 1, 0, 0, 0);
		table.add(0, 1, 1, 0, 0, 1, 0, 1, 0, 0, 0);
		table.add(1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0);
		table.add(1, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0);
		table.add(1, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0);

		table.add(1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0);
		table.add(0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0);

		return table;
	}

	@Override
	public void changeSolvingStrategy() {

	}

	@Override
	public void changeWatchedGraphVertices() {

	}

	@Override
	public void setPriority() {
		priority = 1;
	}

	@Override
	public String toString() {
		return "IrregularityModule";
	}
}
