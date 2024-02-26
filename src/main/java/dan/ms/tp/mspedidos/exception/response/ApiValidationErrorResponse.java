package dan.ms.tp.mspedidos.exception.response;

import java.util.List;

public class ApiValidationErrorResponse {
    public Integer status;
    public String title;
    public List<String> errors;
}
