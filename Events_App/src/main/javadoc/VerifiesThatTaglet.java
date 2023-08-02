import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.DocTreeVisitor;
import com.sun.source.doctree.UnknownBlockTagTree;
import com.sun.source.util.DocTreeScanner;
import jdk.javadoc.doclet.Taglet;

import javax.lang.model.element.Element;
import java.util.List;
import java.util.Set;

/**
 * A taglet that implements the "verifies.that" tag in JavaDoc.
 * It shows a bullet point list of preconditions that a method checks.
 */
@SuppressWarnings("unused")
public class VerifiesThatTaglet implements Taglet {
    private static final String TAG_NAME = "verifies.that";
    private static final String HEADER = "Verifies that:";
    private static final DocTreeVisitor<String, Void> visitor = new DocTreeScanner<>() {
        @Override
        public String visitUnknownBlockTag(UnknownBlockTagTree node, Void unused) {
            if (!TAG_NAME.equals(node.getTagName())) {
                return super.visitUnknownBlockTag(node, unused);
            }

            StringBuilder builder = new StringBuilder();
            for (DocTree subTag : node.getContent()) {
                builder.append(subTag.toString());
            }
            return builder.toString();
        }
    };

    @Override
    public Set<Location> getAllowedLocations() {
        return Set.of(Location.METHOD);
    }

    @Override
    public boolean isInlineTag() {
        return false;
    }

    @Override
    public String getName() {
        return TAG_NAME;
    }

    @Override
    public String toString(List<? extends DocTree> tags, Element element) {
        StringBuilder builder = new StringBuilder();
        builder.append("<dt><b>");
        builder.append(HEADER);
        builder.append("</b></dt>");

        builder.append("<dd><ul>");
        for (DocTree tag : tags) {
            builder.append("<li>");
            builder.append(processTag(tag));
            builder.append("</li>");
        }
        builder.append("</ul></dd>");

        return builder.toString();
    }

    private String processTag(DocTree tree) {
        return tree.accept(visitor, null);
    }
}
