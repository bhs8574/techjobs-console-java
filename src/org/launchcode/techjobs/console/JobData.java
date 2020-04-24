package org.launchcode.techjobs.console;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

/**
 * Created by LaunchCode
 */
public class JobData {

    private static final String DATA_FILE = "resources/job_data.csv";
    private static Boolean isDataLoaded = false;

    private static ArrayList<HashMap<String, String>> allJobs;

    /**
     * Fetch list of all values from loaded data,
     * without duplicates, for a given column.
     *
     * @param field The column to retrieve values from
     * @return List of all of the values of the given field
     */
    public static ArrayList<String> findAll(String field) {

        // load data, if not already loaded
        loadData();

        ArrayList<String> values = new ArrayList<>();

        for (HashMap<String, String> row : allJobs) {
            String aValue = row.get(field);

            if (!values.contains(aValue)) {
                values.add(aValue);
            }
        }

        //Added a quick Collections.sort() to our ArrayList of Strings so that
        //when "When a user asks for a list of employers, locations, position types, etc."
        //The results are sorted alphabetically.
        Collections.sort(values);
        return values;
    }

    /**
     * Fetch a copy list of all job values from loaded data,
     *
     * @return A deep copy of the ArrayList of HashMaps that contains all job listings
     */
    public static ArrayList<HashMap<String, String>> findAll() {
        //Initialize a new ArrayList to hold the copy of allJobs
        ArrayList<HashMap<String, String>> copyOfAllJobs = new ArrayList<>();

        // load data, if not already loaded
        loadData();

        //Iterate over all Jobs, one job entry at a time.
        for(HashMap<String, String> job : allJobs) {
            //Create a new HashMap to load a copy of the current job into
            HashMap<String, String> jobCopy = new HashMap<>();

            /*
            Loop through all key sets in the current job and add them one
            key/value pair at a time to jobCopy using .put().  I believe it
            is okay it do it this way, because at this point we have created new
            objects to return of all data structures in allJobs that are mutable.
            Since String are immutable, it should be okay if references to them are shared.
             */
            for(Map.Entry<String, String> jobEntry : job.entrySet()) {
                jobCopy.put(jobEntry.getKey(), jobEntry.getValue());
            }

            //Add our new copy of the current job to copyOfAllJobs
            copyOfAllJobs.add(jobCopy);
        }

        //Finally, return our copy of allJobs!
        return copyOfAllJobs;

    }

    /**
     * Returns results of search the jobs data by key/value, using
     * inclusion of the search term.
     *
     * For example, searching for employer "Enterprise" will include results
     * with "Enterprise Holdings, Inc".
     *
     * @param column   Column that should be searched.
     * @param value Value of teh field to search for
     * @return List of all jobs matching the criteria
     */
    public static ArrayList<HashMap<String, String>> findByColumnAndValue(String column, String value) {

        // load data, if not already loaded
        loadData();

        ArrayList<HashMap<String, String>> jobs = new ArrayList<>();

        for (HashMap<String, String> row : allJobs) {

            String aValue = row.get(column);

            //toLowerCase() added to aValue and value to make the search case insensitive
            if (aValue.toLowerCase().contains(value.toLowerCase())) {
                jobs.add(row);
            }
        }

        return jobs;
    }


    /**
     * Returns results of search the jobs data by key/value, using
     * inclusion of the search term over all columns.
     *
     * For example, searching for "Java", it will return a list of every job
     * that includes the case insensitive term java anywhere in any column of
     * the listing.
     *
     * @param value Value of the field to search for
     * @return List of all jobs matching the criteria
     */
    public static ArrayList<HashMap<String, String>> findByValue (String value) {
        //create a new empty ArrayList called jobs.
        ArrayList<HashMap<String, String>> jobs = new ArrayList<>();

        //Load data, if not already loaded.  Should not do anything if the flag is activated.
        loadData();

        /*
        loop through each entry in the ArrayList allJobs and store each job that matches the search term
        in the ArrayList created above called jobs.  We check each entry that matches the search term to
        ensure it is not already added to jobs.  This is possible for us because when we load in the data,
        we store it in the allJobs ArrayList, which can be accessed and used by methods of this class.
        Because it is private we don't have to worry about methods from other classes interacting with
        allJobs outside of these class methods and causing us problems.

        Unless of course, as pointed out by the bonus mission, we return a reference to allJobs without making
        a deep copy, which was an issue I overlooked until the bonus mission pointed it out and asked me to fix it.
         */
        for (HashMap<String, String> job : allJobs) {

            //Loop through all columns of the current job with a for-each loop
            for (Map.Entry<String, String> entry: job.entrySet() ) {
                //Check to see if any values in the job listing contain the search term.
                //The toLowerCase() method is applied to entry and value to ensure case insensitivity.
                if (entry.getValue().toLowerCase().contains(value.toLowerCase())) {
                    //If the search term is found, check to see if the job is already part of jobs
                    if (!jobs.contains(job)) {
                        //If we've gotten this far, the job contains the search term and is not
                        //already in our ArrayList, so we add it.
                        jobs.add(job);
                    }
                }
            }
        }

        //Here we return our ArrayList of search results.
        return jobs;
    }

    /**
     * Read in data from a CSV file and store it in a list
     */
    private static void loadData() {

        // Only load data once
        if (isDataLoaded) {
            return;
        }

        try {

            // Open the CSV file and set up pull out column header info and records
            Reader in = new FileReader(DATA_FILE);
            CSVParser parser = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
            List<CSVRecord> records = parser.getRecords();
            Integer numberOfColumns = records.get(0).size();
            String[] headers = parser.getHeaderMap().keySet().toArray(new String[numberOfColumns]);

            allJobs = new ArrayList<>();

            // Put the records into a more friendly format
            for (CSVRecord record : records) {
                HashMap<String, String> newJob = new HashMap<>();

                for (String headerLabel : headers) {
                    newJob.put(headerLabel, record.get(headerLabel));
                }

                allJobs.add(newJob);
            }

            // flag the data as loaded, so we don't do it twice
            isDataLoaded = true;

        } catch (IOException e) {
            System.out.println("Failed to load job data");
            e.printStackTrace();
        }
    }

}
