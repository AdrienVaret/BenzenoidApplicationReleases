package generator.fragments;

import java.util.ArrayList;

import utils.Couple;

public class FragmentOccurences {

	private ArrayList<Integer []> occurences;
	private ArrayList<Couple<Integer, Integer> []> coordinates;
	
	private ArrayList<ArrayList<Integer>> allOutterHexagons;
	private ArrayList<ArrayList<Integer>> allPresentHexagons;
	private ArrayList<ArrayList<Integer>> allAbsentHexagons;
	private ArrayList<ArrayList<Integer>> allUnknownHexagons;
	
	public FragmentOccurences() {
		occurences = new ArrayList<>();
		coordinates = new ArrayList<>();
		allOutterHexagons = new ArrayList<>();
		allPresentHexagons = new ArrayList<>();
		allAbsentHexagons = new ArrayList<>();
		allUnknownHexagons = new ArrayList<>();
	}

	public ArrayList<Integer[]> getOccurences() {
		return occurences;
	}

	public ArrayList<Couple<Integer, Integer>[]> getCoordinates() {
		return coordinates;
	}
	
	public ArrayList<ArrayList<Integer>> getAllOutterHexagons() {
		return allOutterHexagons;
	}

	public ArrayList<ArrayList<Integer>> getAllPresentHexagons() {
		return allPresentHexagons;
	}

	public ArrayList<ArrayList<Integer>> getAllAbsentHexagons() {
		return allAbsentHexagons;
	}

	public ArrayList<ArrayList<Integer>> getAllUnknownHexagons() {
		return allUnknownHexagons;
	}

	public void addOccurence(Integer [] occurence) {
		occurences.add(occurence);
	}
	
	public void addCoordinate(Couple<Integer, Integer> [] coordinate) {
		coordinates.add(coordinate);
	}
	
	public void addOutterHexagons(ArrayList<Integer> hexagons) {
		allOutterHexagons.add(hexagons);
	}
	
	public void addPresentHexagons(ArrayList<Integer> hexagons) {
		allPresentHexagons.add(hexagons);
	}
	
	public void addAbsentHexagons(ArrayList<Integer> hexagons) {
		allAbsentHexagons.add(hexagons);
	}
	
	public void addUnknownHexagons(ArrayList<Integer> hexagons) {
		allUnknownHexagons.add(hexagons);
	}
	private void addAllOccurences(ArrayList<Integer []> occurences) {
		this.occurences.addAll(occurences);
	}
	
	private void addAllCoordinates(ArrayList<Couple<Integer, Integer> []> coordinates) {
		this.coordinates.addAll(coordinates);
	}
	
	public void addAll(FragmentOccurences fragmentOccurences) {
		this.addAllOccurences(fragmentOccurences.getOccurences());
		this.addAllCoordinates(fragmentOccurences.getCoordinates());
		allOutterHexagons.addAll(fragmentOccurences.getAllOutterHexagons());
		allPresentHexagons.addAll(fragmentOccurences.getAllPresentHexagons());
		allAbsentHexagons.addAll(fragmentOccurences.getAllAbsentHexagons());
		allUnknownHexagons.addAll(fragmentOccurences.getAllUnknownHexagons());
	}
	
	public int size() {
		return occurences.size();
	}
	
	public boolean occurencesContains(Integer [] occurence) {
		
		
		for (Integer [] occurence2 : occurences)
			if (occurence.equals(occurence2))
				return true;
		
		return false;
	}
}
