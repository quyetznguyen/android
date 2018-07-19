package space.ntq.tutopassport.entites;

import java.util.List;
import java.util.Map;

public class ApiError {
    String message;
    Map<String, List> errors;

    public String getMessage() {
        return message;
    }

    public Map<String, List> getErrors() {
        return errors;
    }
}
