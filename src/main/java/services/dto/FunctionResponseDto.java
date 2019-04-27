package services.dto;

import org.apache.commons.lang3.StringUtils;
import tools.StringTools;

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

    public byte[] getBytes() {
        if (StringUtils.isBlank(getResponse())) {
            return new byte[] {};
        }
        return getResponse().getBytes(StringTools.DEFAULT_CHARSET);
    }

    public String getContentType() {
        String ct = getMap().get("Content-Type");
        if (StringUtils.isBlank(ct)) {
            return "text/plain";
        }
        return ct;
    }
}
