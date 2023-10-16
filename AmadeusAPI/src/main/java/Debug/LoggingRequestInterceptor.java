package Debug;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        // Log request details
        System.out.println("Request URI: " + request.getURI());
        System.out.println("Request Method: " + request.getMethod());
        // Log other details as needed

        // Continue with the request execution
        return execution.execute(request, body);
    }
}
