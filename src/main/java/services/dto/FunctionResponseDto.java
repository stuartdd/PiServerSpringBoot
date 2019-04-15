package services.dto;

import java.util.Map;

public class FunctionResponseDto {
    private final String response;
    private final Map<String , String> map;

    public FunctionResponseDto(String response, Map<String, String> map) {
        this.response = response;
        this.map = map;
    }

    public String getResponse() {
        return response;
    }

    public Map<String, String> getMap() {
        return map;
    }
}
