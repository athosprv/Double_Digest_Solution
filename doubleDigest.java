/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author ageorgi2
 */
public class doubleDigest {

    private static ArrayList<Integer> matchedX = new ArrayList<Integer>();
    private static int numRecursiveRuns = 0;
    private static Exception Exception;

    /**
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        ArrayList<Integer> x1 = new ArrayList<Integer>(), x2 = new ArrayList<Integer>(), frags = new ArrayList<Integer>();

        //  X1, and X2 which are the original Sequences.
        //      Elements can be inserted or removed to test for multiple cases.
        x1.addAll((List<Integer>) Arrays.asList(0, 10, 15));
        x2.addAll((List<Integer>) Arrays.asList(0, 4, 7, 9, 13, 15));

        System.out.println("Initial Sequence");
        System.out.println("----------------");
        System.out.println("X1: " + x1);
        System.out.println("X2: " + x2);
        // Generating X from x1, x2.
        ArrayList<Integer> X = generateX(x1, x2);
        System.out.println("X': " + X);
        //  Generating Sequence segments for the initial sequence X.
        frags = generateSegments(X);
        System.out.println("Fragments of X': " + frags);
        // Generating DX from X.
        ArrayList<Integer> DX = generateDX(X);
        System.out.println("DX': " + DX);
        //  Generating distinct elements for current DX.
        ArrayList<Integer> elements = getDistinctElements(DX);
        System.out.println("Distinct " + elements.size() + " Elements': " + elements + "\n");


        System.out.println("Brute Force");
        System.out.println("-----------");
        // Getting a new X'' by a recursive Brute force approach and timing the run.
        long start = System.currentTimeMillis();
        try { generateBruteForceSolution(DX, X.size()); } catch (Exception e) { }
        long elapsedTime = System.currentTimeMillis() - start;
        System.out.println("X'': " + matchedX);
        frags = generateSegments(matchedX);
        //  Printing information for current X''.
        System.out.println("Fragments of X'': " + frags);
        System.out.println("Recursive calls: " + numRecursiveRuns);
        System.out.println("Elapsed Time: " + elapsedTime / 1000F + " Seconds");
        numRecursiveRuns = 0;
        System.out.println("");


        System.out.println("Branch and Bound");
        System.out.println("----------------");
        // Getting a new X'' by a Branch and Bound approach and timing the run.
        start = System.currentTimeMillis();
        try {geberateBranchAndBoundSolution(DX);} catch (Exception e) {}
        elapsedTime = System.currentTimeMillis() - start;
        System.out.println("X'': " + matchedX);
        frags = generateSegments(matchedX);
        //  Printing information for current X''.
        System.out.println("Fragments of X'': " + frags);
        System.out.println("Recursive calls: " + numRecursiveRuns);
        System.out.println("Elapsed Time: " + elapsedTime / 1000F + " Seconds");
    }

    /*  generateX(ArrayList<Integer> x1, ArrayList<Integer> x2)
     *
     *  @Purpose:    Generates a List containing the combination of cuts from 2 lists.
     *
     *  @param ArrayList<Integer> x1:   First list to be passed in.
     *  @param ArrayList<Integer> x2:   Second list to be passed in.
     *
     *  @Return:    ArrayList<Integer> X which contains the cuts.
     */
    private static ArrayList<Integer> generateX(ArrayList<Integer> x1, ArrayList<Integer> x2) {
        ArrayList<Integer> X = new ArrayList<Integer>();
        while (true) {
            if (!x1.isEmpty() && !x2.isEmpty()) {
                if (x1.get(0) <= x2.get(0)) {
                    if (!X.contains(x1.get(0))) {
                        X.add(x1.get(0));
                    }
                    x1.remove(0);
                } else if (x1.get(0) > x2.get(0)) {
                    if (!X.contains(x2.get(0))) {
                        X.add(x2.get(0));
                    }
                    x2.remove(0);
                }

            } else if (!x1.isEmpty() && x2.isEmpty()) {
                while (!x1.isEmpty()) {
                    if (!X.contains(x1.get(0))) {
                        X.add(x1.get(0));
                    }
                    x1.remove(0);
                }
                return X;
            } else {
                while (!x2.isEmpty()) {
                    if (!X.contains(x2.get(0))) {
                        X.add(x2.get(0));
                    }
                    x2.remove(0);
                }

                return X;
            }

        }
    }
    /*  generateDX(ArrayList<Integer> XP)
     *
     *  @Purpose:    Returns a List containing the DX elements generated by X.
     *
     *  @param ArrayList<Integer> XP:   First list to be passed in.
     *
     *  @Return:    ArrayList<Integer> newDX = DX.
     */

    private static ArrayList<Integer> generateDX(ArrayList<Integer> XP) {
        ArrayList<Integer> newDX = new ArrayList<Integer>();

        for (int i = 0; i < XP.size(); i++) {
            for (int j = i + 1; j < XP.size(); j++) {
                newDX.add(XP.get(j) - XP.get(i));
            }
        }
        Collections.sort(newDX);
        return newDX;
    }

    /*  generateBruteForceSolution(ArrayList<Integer> DX, int n) throws Exception
     *
     *  @Purpose:    Performs the Double Digest algorithm with a recursive Brute Force Approach.
     *
     *  @param ArrayList<Integer> DX:   List to be passed in.
     *  @param int n:   length of the answer sequence.
     */
    private static void generateBruteForceSolution(ArrayList<Integer> DX, Integer n) throws Exception {
        //  We get the Distinct elements of DX to use when applying the algorithm (Used for convenience).
        ArrayList<Integer> elements = getDistinctElements(DX);
        //  Total number of elements combinations will be computed on.
        Integer numElements = n - 2;
        //  The largest value is saved.
        Integer M = DX.get(DX.size() - 1);
        //  The largest value is removed.
        elements.remove((Integer) M);
        //  Recursive Brute Force method to return the X'
        getNextX(DX, M, elements, 0, numElements, new int[numElements]);
    }

    /*  getNextX(ArrayList<Integer> DX, int M, ArrayList<Integer> items, int n, int k, int[] chosenK) throws Exception
     *
     *  @Purpose:    Recursively solves the Brute Force approach by generating all combinations of possible sequences.
     *
     *  @param ArrayList<Integer> DX:   List to be passed in.
     *  @param int M:   Largest element in DX.
     *  @param ArrayList<Integer> elements:    Holds dinstinct elements from DX.
     *  @param int n:   Variable used as a counter.
     *  @param int k:   Holds the number of elements from which a possible sequence will be formed.
     *  @param int[] chosenElements:    Array that holds the current selection of Distinct elements
    from which a new DX will be generated.
     */
    private static void getNextX(ArrayList<Integer> DX, Integer M, ArrayList<Integer> elements,
                                 Integer n, Integer k, int[] chosenElements) throws Exception {
        //  Counts the Recursive runs of this method.
        numRecursiveRuns++;

        if (k == 0) {
            //  The code below will generate a new DX from X and compare it to our original DX.
            ArrayList<Integer> newDX = new ArrayList<Integer>();
            ArrayList<Integer> newX = new ArrayList<Integer>();
            for (Integer s : chosenElements) {
                newX.add(s);
            }
            newX.add(0);
            newX.add(M);

            Collections.sort(newX);

            newDX = generateDX((ArrayList<Integer>) newX);
            //  If both DX are the same, solution is saved and exception is thrown.
            if (DX.equals(newDX)) {
                matchedX = newX;
                //  An exception is thrown to prevent the method from calculating unnecessary
                //      recursive computations once the solution has been found. In fewer words,
                //      the recursive stack is trashed.
                throw Exception;
            }
        } else {
            // Loop to keep generating combinations.
            for (int i = n; i <= elements.size() - k; i++) {
                chosenElements[chosenElements.length - k] = elements.get(i);
                getNextX(DX, M, elements, i + 1, k - 1, chosenElements);
            }
        }
    }


    /*  getDistinctElements(ArrayList<Integer> newDX)
     *
     *  @Purpose:    Isolates the Distinct elements in a given DX.
     *
     *  @param ArrayList<Integer> newDX:   List to be passed in.
     *
     *  @Return:    ArrayList<Integer> X which contains the cuts.
     */
    private static ArrayList<Integer> getDistinctElements(ArrayList<Integer> newDX) {
        ArrayList<Integer> newX = new ArrayList<Integer>();

        for (int i = 0; i < newDX.size(); i++) {
            if (!newX.contains(newDX.get(i))) {
                newX.add(newDX.get(i));
            }
        }

        return newX;
    }


    /*  geberateBranchAndBoundSolution(ArrayList<Integer> DX) throws Exception
     *
     *  @Purpose:    Method to call 'PLACE' which provides a Branch and Bound solution.
     *
     *  @param ArrayList<Integer> DX:   List to be passed in.
     */
    private static void geberateBranchAndBoundSolution(ArrayList<Integer> DX) throws Exception {
        //  Max number from DX is saved.
        Integer Max = DX.get(DX.size() - 1);
        //  Max is removed
        DX.remove((Integer) Max);

        ArrayList<Integer> X = new ArrayList<Integer>();
        //  0 and Max is added to our new X.
        X.addAll((List<Integer>) Arrays.asList(0, Max));

        PLACE(DX, X, Max);
    }

    /*  PLACE(ArrayList<Integer> DX, ArrayList<Integer> X, int Max) throws Exception
     *
     *  @Purpose:    Recursively solves the Branch and Bound approach by bypassing invalid sequence combinations.
     *
     *  @param ArrayList<Integer> DX:   List to be passed in that holds a DX.
     *  @param ArrayList<Integer> X:   List to be passed in that holds a possible answer.
     *  @param int Max:   Holds the current Max.
     */
    private static void PLACE(ArrayList<Integer> DX, ArrayList<Integer> X, Integer Max) throws Exception {
        numRecursiveRuns++;

        //  When DX becomes empty a solution has been found.
        if (DX.isEmpty()) {
            matchedX = X;
            //  We sort the answer.
            Collections.sort(matchedX);
            //  Again, an exception is thrown to prevent the method from calculating unnecessary
            //      recursive computations once the solution has been found.
            throw Exception;
        }
        //  We get  a new Max
        int M = DX.get(DX.size() - 1);
        ArrayList<Integer> lengths = getLengths(M, X);

        //  The following code will execute if the current Max is valid to be added in X.
        if (isSubset(lengths, DX)) {
            X.add(M);
            remove(DX, lengths);
            PLACE(DX, X, Max);
            X.remove((Integer) M);
            DX.addAll(lengths);
            Collections.sort(DX);
        }
        lengths = getLengths(Max - M, X);
        //  The following code will execute if (original Max - current Max) is valid to be added in X.
        if (isSubset(lengths, DX)) {
            X.add(Max - M);
            remove(DX, lengths);
            PLACE(DX, X, Max);
            X.remove((Integer) (Max - M));
            DX.addAll(lengths);
            Collections.sort(DX);
        }
    }


    /*  getLengths(int M, ArrayList<Integer> X)
     *
     *  @Purpose:    Generates a list which contains the length of each element after substracting it from the Max.
     *
     *  @param int M:   the current Max.
     *  @param ArrayList<Integer> X:   List to be passed in.
     *
     *  @Return:    ArrayList<Integer> lengths which contains the lengths of all elements from the max.
     */
    private static ArrayList<Integer> getLengths(Integer M, ArrayList<Integer> X) {
        ArrayList<Integer> lengths = new ArrayList<Integer>();

        for (Integer i : X) {
            lengths.add(Math.abs(i - M));
        }
        return lengths;
    }


    /*  isSubset(ArrayList<Integer> lengths, ArrayList<Integer> DX)
     *
     *  @Purpose:    Specifies if one list is a subset of the other.
     *
     *  @param ArrayList<Integer> lengths:   List that holds the lengths.
     *  @param ArrayList<Integer> DX:   The superset list DX.
     *
     *  @Return: @param boolean true/false.
     */
    private static boolean isSubset(ArrayList<Integer> lengths, ArrayList<Integer> DX) {
        ArrayList<Integer> newDX = DX;

        for (Integer i : lengths) {
            if (!newDX.contains(i)) {
                return false;
            }
            newDX.remove(i);
        }
        return true;
    }
    /*  remove(ArrayList<Integer> DX, ArrayList<Integer> lengths)
     *
     *  @Purpose:    Removes all the elements from one list that exist in the other.
     *
     *  @param ArrayList<Integer> lengths:   List that holds the lengths.
     *  @param ArrayList<Integer> DX:   The superset list DX.
     */

    private static void remove(ArrayList<Integer> DX, ArrayList<Integer> lengths) {
        for (Integer i : lengths) {
            DX.remove(i);
        }

    }
    /*  generateSegments(ArrayList<Integer> X)
     *
     *  @Purpose:    Generates the segments of a list that contains cuts.
     *
     *  @param ArrayList<Integer> X:   List of cuts.
     *
     *  @Return:    ArrayList<Integer> segments which contains the segments.
     */
    private static ArrayList<Integer> generateSegments(ArrayList<Integer> X) {
        ArrayList<Integer> segments = new ArrayList<Integer>();

        for (Integer i = X.size() - 1; i > 0; i--) {
            segments.add((Integer) X.get(i) - X.get(i - 1));
        }
        return segments;
    }
}