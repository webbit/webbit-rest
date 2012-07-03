package crud;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;
import org.webbitserver.handler.EmbeddedResourceHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.webbitserver.rest.Rest.params;

public class JMustacheView implements HttpHandler {
    private final String root;
    private final String templatePath;

    public JMustacheView(String root, String templatePath) {
        this.root = root;
        this.templatePath = templatePath;
    }

    @Override
    public void handleHttpRequest(HttpRequest request, HttpResponse response, HttpControl control) throws Exception {
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("uri", params(request));

        HttpHandler delegate = new EmbeddedJmustacheHandler(root, context);
        request.uri(templatePath);
        delegate.handleHttpRequest(request, response, control);
    }

    private static class EmbeddedJmustacheHandler extends EmbeddedResourceHandler {
        private final Mustache.Compiler mf = Mustache.compiler();
        private final Object context;

        public EmbeddedJmustacheHandler(String root, Object context) {
            super(root);
            this.context = context;
        }

        @Override
        protected IOWorker createIOWorker(HttpRequest request, HttpResponse response, HttpControl control) {
            return new JMustacheWorker(request, response, control, context);
        }

        protected class JMustacheWorker extends ResourceWorker {
            protected JMustacheWorker(HttpRequest request, HttpResponse response, HttpControl control, Object context) {
                super(request, response, control);
            }

            // TODO: Rather than overriding this I'd rather supply a TemplateEngine to
            // the constructor of webbit's AbstractResourceHandler. Webbit would supply
            // a default one (that does nothing - just renders a file as-is).
            // Also need to figure out how to easily create a context on each request.
            // Maybe the simplest option is to use filter chaining and set the context on
            // the request as a TEMPLATE_CONTEXT object.
            @Override
            protected ByteBuffer read(int length, InputStream in) throws IOException {
                Template t = mf.compile(new InputStreamReader(in, "UTF-8"));
                String result = t.execute(context);
                return ByteBuffer.wrap(result.getBytes("UTF-8"));
            }
        }
    }
}
