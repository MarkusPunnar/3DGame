package engine.render;

public class RenderRequest {

    private RequestType requestType;
    private RequestInfo requestInfo;

    public RenderRequest(RequestType requestType, RequestInfo requestInfo) {
        this.requestType = requestType;
        this.requestInfo = requestInfo;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public RequestInfo getRequestInfo() {
        return requestInfo;
    }
}
