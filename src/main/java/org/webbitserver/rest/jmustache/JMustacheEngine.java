package org.webbitserver.rest.jmustache;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import org.webbitserver.handler.AbstractResourceHandler;
import org.webbitserver.handler.EmbeddedResourceHandler;
import org.webbitserver.handler.StaticFileHandler;
import org.webbitserver.handler.TemplateEngine;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class JMustacheEngine implements TemplateEngine {
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private final Mustache.Compiler compiler = Mustache.compiler();
    private final boolean reloading;
    private final Map<String, Template> templates = new HashMap<String, Template>();

    public static AbstractResourceHandler fromStaticFiles(String path) {
        return new StaticFileHandler(path, new JMustacheEngine(true));
    }

    public static AbstractResourceHandler fromEmbeddedResources(String path) {
        return new EmbeddedResourceHandler(path, new JMustacheEngine(false));
    }

    public JMustacheEngine(boolean reloading) {
        this.reloading = reloading;
    }

    @Override
    public ByteBuffer process(int length, InputStream template, String path, Object context) throws IOException {
        Template t = template(template, path);
        return ByteBuffer.wrap(t.execute(context).getBytes(UTF8));
    }

    private Template template(InputStream template, String path) {
        if (reloading) {
            return compiler.compile(new InputStreamReader(template, UTF8));
        } else {
            Template t = templates.get(path);
            if (t == null) {
                t = compiler.compile(new InputStreamReader(template, UTF8));
                templates.put(path, t);
            }
            return t;
        }
    }
}
