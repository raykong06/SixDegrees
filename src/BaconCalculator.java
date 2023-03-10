import java.util.Collections;
import java.util.Scanner;
import java.util.ArrayList;

public class BaconCalculator {

    private Scanner scanner = new Scanner(System.in);
    private ArrayList<SimpleMovie> movies = MovieDatabaseBuilder.getMovieDB("src/movie_data");
    private ArrayList<SimpleMovie> moviesSortedLargeCast = MovieDatabaseBuilder.getSimpleMovieDB("src/output.txt");
    private ArrayList<String> secondDegreeActors = MovieDatabaseBuilder.getStringFile("src/second_degree_actors.txt");
    private ArrayList<SimpleMovie> secondDegreeMovies = MovieDatabaseBuilder.getSimpleMovieDB("src/second_degree_movies.txt");
    private ArrayList<SimpleMovie> thirdDegreeMovies = MovieDatabaseBuilder.getSimpleMovieDB("src/third_degree_movies.txt");
    private String inputActor;
    private int degree;
    private ArrayList<String> connectedActors;
    private ArrayList<String> connectedMovies;
    private ArrayList<String> allActors;
    private ArrayList<String> kevinBaconCastmates;
    private ArrayList<SimpleMovie> kevinBaconCastmatesMovies;

    public BaconCalculator()
    {
        setAllActors();
    }

    public void printListWithNumbers(ArrayList<String> list)
    {
        int i = 1;
        for (String str : list)
        {
            System.out.print("" + i + ". ");
            System.out.println(str);
            i++;
        }
    }

    public void mainMenu()
    {
        String mainChoice = "";
        while (!mainChoice.equals("q"))
        {
            System.out.print("--------- Bacon Calculator ---------" +
                    "\nEnter an actor's name or (q) to quit: ");
            mainChoice = scanner.nextLine();
            if (!mainChoice.equals("q"))
            {
                inputActor = mainChoice;
                ArrayList<String> matches = new ArrayList<String>();
                for (int i = 0; i < allActors.size(); i++)
                {
                    String addActor = allActors.get(i);
                    boolean inList = false;
                    for (int j = 0; j < matches.size(); j++)
                    {
                        if (matches.get(j).equals(addActor))
                        {
                            inList = true;
                            j = matches.size();
                        }
                    }
                    if (addActor.indexOf(inputActor) > -1 && !inList)
                    {
                        matches.add(addActor);
                    }
                }

                sortStringResults(matches);
                printListWithNumbers(matches);

                if (matches.size() < 1)
                {
                    System.out.println("Your search for an actor had no matches within the database.");
                }
                else
                {
                    System.out.println("\nWhich actor do you want to pick?");
                    System.out.print("Enter a number: ");
                    int choice = scanner.nextInt();

                    inputActor = matches.get(choice - 1);
                    System.out.println("\nActor chosen: " + matches.get(choice - 1));

                    calculateBacon();

                    if (degree < 0)
                    {
                        System.out.println("The actor could not be found in 3 degrees or less.");
                    }
                    else
                    {
                        System.out.print(inputActor + " -> ");
                        for (int i = 0; i < connectedMovies.size(); i++)
                        {
                            System.out.print(connectedMovies.get(i));
                            System.out.print(" -> ");
                            if (i < connectedActors.size())
                            {
                                System.out.print(connectedActors.get(i));
                                System.out.print(" -> ");
                            }
                        }
                        System.out.println("Kevin Bacon");
                        System.out.println("Bacon Number of: " + degree);
                    }
                    scanner.nextLine();
                }

                System.out.println();
            }
        }
        System.out.println("\n" +
                "-----------------------------------------" +
                "\nThank you for using the Bacon Calculator!");
    }

    public void calculateBacon()
    {
        oneDegreeOfBacon();
        connectedMovies = new ArrayList<String>();
        connectedActors = new ArrayList<String>();
        boolean foundActor = false;

        if (inputActor.equals("Kevin Bacon")) // degree 0
        {
            degree = 0;
            foundActor = true;
        }
        if (!foundActor) // degree 1
        {
            int actorIndex = runBinarySearch(kevinBaconCastmates, inputActor);
            if (actorIndex > -1)
            {
                degree = 1;
                connectedMovies.add(kevinBaconCastmatesMovies.get(actorIndex).getTitle());
                foundActor = true;
            }
        }
        if (!foundActor) // degree 2
        {
            for (int i = 0; i < secondDegreeMovies.size(); i++)
            {
                SimpleMovie currentMovie = secondDegreeMovies.get(i);
                ArrayList<String> currentMovieCast = currentMovie.getActors();
                if (currentMovieCast.contains(inputActor))
                {
                    for (int j = 0; j < currentMovieCast.size(); j++)
                    {
                        String currentActor = currentMovieCast.get(j);
                        if (kevinBaconCastmates.contains(currentActor))
                        {
                            degree = 2;
                            foundActor = true;

                            String degreeOneActor = currentActor;
                            String degreeOneMovie = "";

                            for (int k = 0; k < kevinBaconCastmatesMovies.size(); k++)
                            {
                                ArrayList<String> cast = kevinBaconCastmatesMovies.get(k).getActors();
                                if (cast.contains(degreeOneActor))
                                {
                                    degreeOneMovie = kevinBaconCastmatesMovies.get(k).getTitle();
                                    k = kevinBaconCastmatesMovies.size();
                                }
                            }

                            connectedMovies.add(currentMovie.getTitle()); // Input actor --> this movie -->
                            connectedActors.add(degreeOneActor); // degree 1 actor -->

                            connectedMovies.add(degreeOneMovie); // degree 1 movie --> Kevin Bacon

                            i = secondDegreeMovies.size();
                            j = currentMovieCast.size();
                        }
                    }
                }
            }
        }

        if (!foundActor) // degree 3
        {
            // check arraylist of third degree movies to see which specific movie both were cast in
            for (int i = 0; i < thirdDegreeMovies.size(); i++) {
                SimpleMovie currentMovie = thirdDegreeMovies.get(i);
                ArrayList<String> currentMovieCast = currentMovie.getActors();
                if (currentMovieCast.contains(inputActor))
                {
                    for (int j = 0; j < currentMovieCast.size(); j++)
                    {
                        String currentActor = currentMovieCast.get(j);
                        if (secondDegreeActors.contains(currentActor)) //&& !currentActor.equals(inputActor))
                        {
                            degree = 3;
                            foundActor = true;

                            //int index = secondDegreeActors.indexOf(currentActor);
                            String degreeTwoActor = currentActor; //secondDegreeActors.get(index);
                            String degreeTwoMovie = "";

                            String degreeOneActor = "";
                            for (int k = 0; k < secondDegreeMovies.size(); k++)
                            {
                                ArrayList<String> cast = secondDegreeMovies.get(k).getActors();
                                boolean hasDegreeTwoActor = false;
                                if (cast.contains(degreeTwoActor))
                                {
                                    hasDegreeTwoActor = true;
                                }
                                boolean hasDegreeOneActor = false;
                                for (int l = 0; l < kevinBaconCastmates.size(); l++)
                                {
                                    degreeOneActor = kevinBaconCastmates.get(l);
                                    if (cast.contains(kevinBaconCastmates.get(l)))
                                    {
                                        hasDegreeOneActor = true;
                                        l = kevinBaconCastmates.size();
                                    }
                                }
                                if (hasDegreeOneActor && hasDegreeTwoActor)
                                {
                                    degreeTwoMovie = secondDegreeMovies.get(k).getTitle();
                                    k = secondDegreeMovies.size();
                                }
                            }

                            connectedMovies.add(currentMovie.getTitle()); // Input actor --> this movie -->
                            connectedActors.add(degreeTwoActor); // degree 2 actor -->

                            connectedMovies.add(degreeTwoMovie); // degree 2 corresponding movie -->
                            connectedActors.add(degreeOneActor); // degree 1 actor -->

                            int degreeOneIndex = runBinarySearch(kevinBaconCastmates, degreeOneActor);
                            connectedMovies.add(kevinBaconCastmatesMovies.get(degreeOneIndex).getTitle()); // degree 1 movie --> Kevin Bacon

                            i = thirdDegreeMovies.size();
                            j = currentMovieCast.size();
                        }
                    }
                }
            }
        }

        if (!foundActor)
        {
            degree = -1;
        }
    }

    private void setAllActors()
    {
        allActors = new ArrayList<String>();
        for (int i = 0; i < movies.size(); i++)
        {
            SimpleMovie currentMovie = movies.get(i);
            ArrayList<String> currentMovieCast = currentMovie.getActors();

            for (int j = 0; j < currentMovieCast.size(); j++) {
                String currentCastMember = currentMovieCast.get(j);
                boolean inList = false;

                allActors.add(currentCastMember);

            }
        }
    }

    private void oneDegreeOfBacon()
    {
        kevinBaconCastmates = new ArrayList<String>();
        kevinBaconCastmatesMovies = new ArrayList<SimpleMovie>();
        for (int i = 0; i < movies.size(); i++)
        {
            SimpleMovie currentMovie = movies.get(i);
            ArrayList<String> currentMovieCast = currentMovie.getActors();
            boolean bacon = false;
            for (int k = 0; k < currentMovieCast.size(); k++)
            {
                if (currentMovieCast.get(k).equals("Kevin Bacon"))
                {
                    bacon = true;
                    k = currentMovieCast.size();
                }
            }
            if (bacon)
            {
                for (int j = 0; j < currentMovieCast.size(); j++)
                {
                    String currentCastMember = currentMovieCast.get(j);
                    boolean inList = false;

                    int index = runBinarySearch(kevinBaconCastmates, currentCastMember);

                    if (index == -1 && !currentCastMember.equals("Kevin Bacon"))
                    {
                        inList = false;
                    }
                    else
                    {
                        inList = true;
                    }
                    if (!inList)
                    {
                        kevinBaconCastmates.add(currentCastMember);
                        sortStringResults(kevinBaconCastmates);

                        int addMovieIndex = runBinarySearch(kevinBaconCastmates, currentCastMember);
                        kevinBaconCastmatesMovies.add(addMovieIndex, currentMovie);
                    }
                }
            }
        }
    }

    private void sortStringResults(ArrayList<String> listToSort)
    {
        Collections.sort(listToSort);
    }

    private int runBinarySearch(ArrayList<String> sortedArray, String compare)
    {
        int low = 0;
        int high = sortedArray.size() - 1;

        int index = -1;

        while (low <= high)
        {
            int mid = low + ((high - low) / 2);
            if (sortedArray.get(mid).compareTo(compare) < 0)
            {
                low = mid + 1;
            }
            else if (sortedArray.get(mid).compareTo(compare) > 0)
            {
                high = mid - 1;
            }
            else if (sortedArray.get(mid).compareTo(compare) == 0)
            {
                index = mid;
                low = high + 1;
            }
        }
        return index;
    }
}
