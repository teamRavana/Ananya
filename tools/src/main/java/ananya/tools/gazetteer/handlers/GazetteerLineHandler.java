package ananya.tools.gazetteer.handlers;

import java.util.List;

public interface GazetteerLineHandler {

    /**
     *  Handle a line of a gazetteer file
     * @param line line of the file to be processed
     * @return List of strings of particular gazetteer type extracted from the line
     */
    public List<String> handleGazetteerLine(String line);
}
