package sql;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;

import molecules.Molecule;
import parsers.GraphParser;
import spectrums.ResultLogFile;

public class SelectQueryContent {

	private int idMolecule;
	private String moleculeName;
	private int nbHexagons;
	private int nbCarbons;
	private int nbHydrogens;

	private double irregularity;
	private int idGaussianResult;
	private ArrayList<Double> finalEnergies;
	private ArrayList<Double> frequencies;
	private ArrayList<Double> intensities;
	private double zeroPointEnergy;

	/*
	 * Constructor
	 */

	public SelectQueryContent(int idMolecule, String moleculeName, int nbHexagons, int nbCarbons, int nbHydrogens,
			double irregularity, int idGaussianResult, ArrayList<Double> finalEnergies, ArrayList<Double> frequencies,
			ArrayList<Double> intensities, double zeroPointEnergy) {

		this.idMolecule = idMolecule;
		this.moleculeName = moleculeName;
		this.nbHexagons = nbHexagons;
		this.nbCarbons = nbCarbons;
		this.nbHydrogens = nbHydrogens;
		this.irregularity = irregularity;
		this.idGaussianResult = idGaussianResult;
		this.finalEnergies = finalEnergies;
		this.frequencies = frequencies;
		this.intensities = intensities;
		this.zeroPointEnergy = zeroPointEnergy;
	}

	/*
	 * Getters
	 */

	public int getIdMolecule() {
		return idMolecule;
	}

	public String getMoleculeName() {
		return moleculeName;
	}

	public int getNbHexagons() {
		return nbHexagons;
	}

	public int getNbHydrogens() {
		return nbHydrogens;
	}

	public double getIrregularity() {
		return irregularity;
	}

	public int getIdGaussianResult() {
		return idGaussianResult;
	}

	public ArrayList<Double> getFinalEnergies() {
		return finalEnergies;
	}

	public ArrayList<Double> getFrequencies() {
		return frequencies;
	}

	public ArrayList<Double> getIntensities() {
		return intensities;
	}

	public double getZeroPointEnergy() {
		return zeroPointEnergy;
	}

	public int getNbCarbons() {
		return nbCarbons;
	}

	/*
	 * Class methods
	 */

	@SuppressWarnings("rawtypes")
	public static SelectQueryContent buildQueryContent(Map result) {

		int idMolecule = (int) ((double) result.get("id"));
		String name = (String) result.get("name");
		int nbHexagons = (int) ((double) result.get("nbHexagons"));
		int nbCarbons = (int) ((double) result.get("nbCarbons"));
		int nbHydrogens = (int) ((double) result.get("nbHydrogens"));
		double irregularity = (double) result.get("irregularity");

		int idSpectrum = (int) ((double) result.get("idSpectrum"));
		String frequenciesString = (String) result.get("frequencies");
		String intensitiesString = (String) result.get("intensities");
		String finalEnergiesString = (String) result.get("finalEnergies");
		double zeroPointEnergy = (double) result.get("zeroPointEnergy");

		String[] splittedFrequencies = frequenciesString.split("\\s+");
		ArrayList<Double> frequencies = new ArrayList<>();

		for (String frequency : splittedFrequencies)
			frequencies.add(Double.parseDouble(frequency));

		String[] splittedIntensities = intensitiesString.split("\\s+");
		ArrayList<Double> intensities = new ArrayList<>();

		for (String intensity : splittedIntensities)
			intensities.add(Double.parseDouble(intensity));

		String[] splittedEnergies = finalEnergiesString.split("\\s+");
		ArrayList<Double> finalEnergies = new ArrayList<>();

		for (String energy : splittedEnergies)
			finalEnergies.add(Double.parseDouble(energy));

		return new SelectQueryContent(idMolecule, name, nbHexagons, nbCarbons, nbHydrogens, irregularity, idSpectrum,
				finalEnergies, frequencies, intensities, zeroPointEnergy);
	}

	public static SelectQueryContent buildQueryContent(ArrayList<String> result) {

		int idMolecule = -1;
		String moleculeName = "";
		int nbHexagons = -1;
		int nbCarbons = -1;
		int nbHydrogens = -1;
		double irregularity = -1.0;

		int idGaussianResult = -1;
		ArrayList<Double> finalEnergies = new ArrayList<>();
		ArrayList<Double> frequencies = new ArrayList<>();
		ArrayList<Double> intensities = new ArrayList<>();
		double zeroPointEnergy = -1.0;

		for (String line : result) {

			String[] splittedLine = line.split(Pattern.quote(" = "));

			if (splittedLine[0].equals("id_molecule"))
				idMolecule = Integer.parseInt(splittedLine[1]);

			else if (splittedLine[0].equals("molecule_name"))
				moleculeName = splittedLine[1];

			else if (splittedLine[0].equals("nb_hexagons"))
				nbHexagons = Integer.parseInt(splittedLine[1]);

			else if (splittedLine[0].equals("nb_carbons"))
				nbCarbons = Integer.parseInt(splittedLine[1]);

			else if (splittedLine[0].equals("nb_hydrogens"))
				nbHydrogens = Integer.parseInt(splittedLine[1]);

			else if (splittedLine[0].equals("irregularity"))
				irregularity = Double.parseDouble(splittedLine[1]);

			else if (splittedLine[0].equals("id_gaussian_result"))
				idGaussianResult = Integer.parseInt(splittedLine[1]);

			else if (splittedLine[0].equals("final_energies")) {

				String[] splittedResult = splittedLine[1].split("\\s+");

				for (String value : splittedResult)
					finalEnergies.add(Double.parseDouble(value));
			}

			else if (splittedLine[0].equals("frequencies")) {

				String[] splittedResult = splittedLine[1].split("\\s+");

				for (String value : splittedResult)
					frequencies.add(Double.parseDouble(value));
			}

			else if (splittedLine[0].equals("intensities")) {

				String[] splittedResult = splittedLine[1].split("\\s+");

				for (String value : splittedResult)
					intensities.add(Double.parseDouble(value));
			}

			else if (splittedLine[0].equals("zero_point_energy"))
				zeroPointEnergy = Double.parseDouble(splittedLine[1]);
		}

		return new SelectQueryContent(idMolecule, moleculeName, nbHexagons, nbCarbons, nbHydrogens, irregularity,
				idGaussianResult, finalEnergies, frequencies, intensities, zeroPointEnergy);
	}

	public Molecule buildMolecule() throws IOException {
		return GraphParser.parseBenzenoidCode(moleculeName);
	}

	public ResultLogFile buildResultLogFile() {
		return new ResultLogFile("unknown.log", frequencies, intensities, finalEnergies, zeroPointEnergy);
	}
}
